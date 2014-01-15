/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
/**
 *
 */
package de.elanev.studip.android.app.util;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;

import de.elanev.studip.android.app.backend.datamodel.Server;
import de.elanev.studip.android.app.backend.db.AuthenticationContract;


/**
 * @author joern
 */
public class Prefs {
    // TODO do DB operations in AsyncTask
    private static final String APP_PREFS_NAME = "prefs";
    private static final String APP_FIRST_START = "appFirstStart";
    private static final String APP_SECURE_START = "appDBCrypted";
    private static final String APP_IS_AUTHORIZED = "appIsAuthorized";
    private static final String TAG = Prefs.class.getSimpleName();
    private static Prefs sInstance;
    private Context mContext;
    private SharedPreferences mPrefs;
    private Server mCachedServer;

    private Prefs() {
    }

    private Prefs(Context context) {
        this.mContext = context;
        this.mPrefs = context.getSharedPreferences(APP_PREFS_NAME, Context.MODE_PRIVATE);
    }

    /**
     * Returns an instance of the Prefs for easily accessing several stored preferences
     *
     * @param context The context that is used to access the preferences
     * @return An instance of the Prefs
     */
    public static synchronized Prefs getInstance(Context context) {
        if (sInstance == null)
            sInstance = new Prefs(context);

        return sInstance;
    }

    /*
     * Clears the SharedPreferences
     */
    public void clearPrefs() {
        mPrefs.edit().clear().commit();
    }

    /*
     * Returns a Server object containing the necessary information to communicate with the api
     */
    public Server getServer() {

        if (mCachedServer == null) {
            String[] projection = new String[]{
                    AuthenticationContract.Columns.Authentication.SERVER_NAME,
                    AuthenticationContract.Columns.Authentication.SERVER_CONTACT_EMAIL,
                    AuthenticationContract.Columns.Authentication.SERVER_KEY,
                    AuthenticationContract.Columns.Authentication.SERVER_SECRET,
                    AuthenticationContract.Columns.Authentication.SERVER_URL,
                    AuthenticationContract.Columns.Authentication.ACCESS_TOKEN,
                    AuthenticationContract.Columns.Authentication.ACCESS_TOKEN_SECRET
            };

            Cursor c = mContext.getContentResolver()
                    .query(AuthenticationContract.CONTENT_URI, projection, null, null, null);

            c.moveToFirst();
            if (c.getCount() > 0) {
                String serverName = c.getString(c.getColumnIndex(AuthenticationContract.Columns.Authentication.SERVER_NAME));
                String serverContact = c.getString(c.getColumnIndex(AuthenticationContract.Columns.Authentication.SERVER_CONTACT_EMAIL));
                String serverKey = c.getString(c.getColumnIndex(AuthenticationContract.Columns.Authentication.SERVER_KEY));
                String serverSecret = c.getString(c.getColumnIndex(AuthenticationContract.Columns.Authentication.SERVER_SECRET));
                String serverUrl = c.getString(c.getColumnIndex(AuthenticationContract.Columns.Authentication.SERVER_URL));
                String accessToken = c.getString(c.getColumnIndex(AuthenticationContract.Columns.Authentication.ACCESS_TOKEN));
                String accessSecret = c.getString(c.getColumnIndex(AuthenticationContract.Columns.Authentication.ACCESS_TOKEN_SECRET));


                this.mCachedServer = new Server(serverName, serverKey, serverSecret, serverUrl, serverContact, accessToken, accessSecret);
            }
            c.close();
        }

        return mCachedServer;
    }

    /**
     * Stores the Server object permanently for later usage
     *
     * @param server The Server object to store permanently
     */
    public void setServer(Server server) {
        ContentValues values = new ContentValues();
        values.put(AuthenticationContract.Columns.Authentication.ACCESS_TOKEN, server.getAccessToken());
        values.put(AuthenticationContract.Columns.Authentication.ACCESS_TOKEN_SECRET, server.getAccessTokenSecret());
        values.put(AuthenticationContract.Columns.Authentication.SERVER_NAME, server.getName());
        values.put(AuthenticationContract.Columns.Authentication.SERVER_URL, server.getBaseUrl());
        values.put(AuthenticationContract.Columns.Authentication.SERVER_CONTACT_EMAIL, server.getContactEmail());
        values.put(AuthenticationContract.Columns.Authentication.SERVER_KEY, server.getConsumerKey());
        values.put(AuthenticationContract.Columns.Authentication.SERVER_SECRET, server.getConsumerSecret());

        Uri returnUri = mContext.getContentResolver().insert(AuthenticationContract.CONTENT_URI, values);

        if (Long.parseLong(returnUri.getLastPathSegment()) != -1)
            mCachedServer = server;
    }

    /*
     * Returns true if the app was previously set as authorized with the api
     *
     * @return True if the app is authorized, false if the app is not authorized
     */
    public boolean isAppAuthorized() {
        return mPrefs.getBoolean(APP_IS_AUTHORIZED, false);
    }

    /**
     * Sets the app authorized with the api
     *
     * @param value True if the app is authorized, false if not
     */
    public void setAppAuthorized(boolean value) {
        mPrefs.edit().putBoolean(APP_IS_AUTHORIZED, value).commit();
    }

    /**
     * Checks if the app was started before. If it was not started bevor it will return true
     *
     * @return true if the current start is the first start of the app on the current device, else
     * false
     */
    public boolean isFirstStart() {
        return mPrefs.getBoolean(APP_FIRST_START, true);
    }

    /**
     * Set the app as started. This will cause the isFirstStart() method to return false.
     */
    public void setAppStarted() {
        mPrefs.edit().putBoolean(APP_FIRST_START, false).commit();
    }

    /**
     * Temporary method to check if the app credentials have been secured. If the update to a
     * secured version of the app just happened this method will return false. If it returns true
     * the app credentials were secured before.
     *
     * @return false if the app credentials where not secured before, else true
     */
    public boolean isSecureStarted() {
        return mPrefs.getBoolean(APP_SECURE_START, false);
    }

    /**
     * Sets this app to secure indicating that the app credentials have been secured. This will cause
     * the isSecureStarted() method to return true.
     */
    public void setSecureStarted() {
        mPrefs.edit().putBoolean(APP_SECURE_START, true).commit();
    }

}
