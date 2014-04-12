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
    private static final String APP_SYMC_COMPLETE = "appSyncComplete";
    private static final String USER_ID = "userId";
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
        this.mCachedServer = null;
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
     * Returns true if the app was previously authorized with an API and the
     * credentials are correctly set in the secured database.
     *
     * @return True if the app is authorized, false if the app is not authorized
     */
    public boolean isAppAuthorized() {
        Server server = getServer();
        if (server != null) {
            if (server.getAccessToken() == null
                    || server.getAccessTokenSecret() == null) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
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
     * Checks if insecure credentials from earlier versions of the app exist.
     *
     * @return true if there are insecure credentials form earlier versions,
     * otherwise false
     */
    public boolean legacyDataExists() {
        String accessToken = mPrefs.getString("accessToken", null);
        String accessTokenSecret = mPrefs.getString("accessTokenSecret", null);
        String serverName = mPrefs.getString("serverName", null);
        String serverUrl = mPrefs.getString("serverUrl", null);
        String serverKey = mPrefs.getString("serverKey", null);
        String serverSecret = mPrefs.getString("serverSecret", null);

        if (accessToken != null
                || accessTokenSecret != null
                || serverName != null
                || serverUrl != null
                || serverKey != null
                || serverSecret != null) {

            return true;

        } else {

            return false;

        }
    }

    //DEBUG Only for credential migration testing
    public void simulateOldPrefs(Server server) {
        mPrefs.edit()
                .putString("accessToken", server.getAccessToken())
                .commit();
        mPrefs.edit()
                .putString("accessTokenSecret", server.getAccessTokenSecret())
                .commit();
        mPrefs.edit()
                .putString("serverName", server.getName())
                .commit();
        mPrefs.edit()
                .putString("serverUrl", server.getBaseUrl())
                .commit();
        mPrefs.edit()
                .putString("serverKey", server.getConsumerKey())
                .commit();
        mPrefs.edit()
                .putString("serverSecret", server.getConsumerSecret())
                .commit();
    }

    public void setAppSynced() {
        mPrefs.edit()
                .putBoolean(APP_SYMC_COMPLETE, true)
                .commit();
    }

    public boolean isAppSynced() {
        return mPrefs.getBoolean(APP_SYMC_COMPLETE, false);
    }

    public String getUserId() {
        return mPrefs.getString(USER_ID, null);
    }

    public void setUserId(String userId) {
        mPrefs.edit()
                .putString(USER_ID, userId)
                .commit();
    }

}
