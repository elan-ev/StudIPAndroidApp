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
import android.os.Bundle;

import de.elanev.studip.android.app.backend.datamodel.Server;
import de.elanev.studip.android.app.backend.db.AuthenticationContract;


/**
 * @author joern
 */
public class Prefs {
    // TODO do DB operations in AsyncTask
    private static final String APP_PREFS_NAME = "prefs";
    public static final String ACCESS_TOKEN = "accessToken";
    public static final String ACCESS_TOKEN_SECRET = "accessTokenSecret";
    private static final String SERVER_NAME = "serverName";
    private static final String SERVER_URL = "serverUrl";
    private static final String SERVER_KEY = "serverKey";
    private static final String SERVER_SECRET = "serverSecret";
    private static final String SERVER_CONTACT_EMAIL = "serverContactEmail";
    private static final String APP_FIRST_START = "appFirstStart";
    private static final String APP_SECURE_START = "appDBCrypted";
    private static final String APP_IS_AUTHORIZED = "appIsAuthorized";
    private static final String TAG = Prefs.class.getSimpleName();
    private static Prefs mInstance;
    private static Context mContext;
    private static SharedPreferences mPrefs;

    private static final String[] PROJECTION = new String[]{
            AuthenticationContract.Columns.Authentication.ACCESS_TOKEN,
            AuthenticationContract.Columns.Authentication.ACCESS_TOKEN_SECRET,
    };


    private Prefs() {
    }

    private Prefs(Context context) {
        mContext = context;
    }

    public static synchronized Prefs getInstance(Context context) {
        if (mInstance == null)
            mInstance = new Prefs(context);

        if (mPrefs == null)
            mInstance.mPrefs = mContext.getSharedPreferences(APP_PREFS_NAME, Context.MODE_PRIVATE);

        return mInstance;
    }

    /*
     * Clear preferences
     */
    public void clearPrefs() {
        mPrefs.edit().clear().commit();
    }

    /*
     * Getting and setting server preferences
     */
    public Server getServer() {
        Server server = null;

        String[] projection = new String[]{
                AuthenticationContract.Columns.Authentication.SERVER_NAME,
                AuthenticationContract.Columns.Authentication.SERVER_CONTACT_EMAIL,
                AuthenticationContract.Columns.Authentication.SERVER_KEY,
                AuthenticationContract.Columns.Authentication.SERVER_SECRET,
                AuthenticationContract.Columns.Authentication.SERVER_URL
        };

        Cursor c = mContext.getContentResolver()
                .query(AuthenticationContract.CONTENT_URI, projection, null, null, null);

        c.moveToFirst();
        if (c.getCount() > 0) {
            String serverName = c.getString(c.getColumnIndex(AuthenticationContract.Columns.Authentication.SERVER_NAME));
            String serverCon
            tact = c.getString(c.getColumnIndex(AuthenticationContract.Columns.Authentication.SERVER_CONTACT_EMAIL));
            String serverKey = c.getString(c.getColumnIndex(AuthenticationContract.Columns.Authentication.SERVER_KEY));
            String serverSecret = c.getString(c.getColumnIndex(AuthenticationContract.Columns.Authentication.SERVER_SECRET));
            String serverUrl = c.getString(c.getColumnIndex(AuthenticationContract.Columns.Authentication.SERVER_URL));

            server = new Server(serverName, serverKey, serverSecret, serverUrl, serverContact);
        }
        c.close();

        return server;
    }

    /*
     * Getting and setting access token preferences
     */
    public Bundle getAccessToken() {
        Bundle tokens = null;
        String[] projection = new String[]{
                AuthenticationContract.Columns.Authentication.ACCESS_TOKEN,
                AuthenticationContract.Columns.Authentication.ACCESS_TOKEN_SECRET
        };

        Cursor c = mContext.getContentResolver()
                .query(AuthenticationContract.CONTENT_URI, projection, null, null, null);

        c.moveToFirst();
        if (c.getCount() > 0) {
            String accessToken = c.getString(c.getColumnIndex(AuthenticationContract.Columns.Authentication.ACCESS_TOKEN));
            String accessSecret = c.getString(c.getColumnIndex(AuthenticationContract.Columns.Authentication.ACCESS_TOKEN_SECRET));
            tokens = new Bundle();
            tokens.putString(ACCESS_TOKEN, accessToken);
            tokens.putString(ACCESS_TOKEN_SECRET, accessSecret);
        }
        c.close();

        return tokens;
    }

    /**
     * Writes the new access tokens alongside the corresponding server information into the database
     *
     * @param server the Server object corresponding to the access tokens
     * @param tokens a Bundle containing the access tokens
     */
    public void setAccessToken(Server server, Bundle tokens) {
        ContentValues values = new ContentValues();
        values.put(AuthenticationContract.Columns.Authentication.ACCESS_TOKEN, tokens.getString(ACCESS_TOKEN));
        values.put(AuthenticationContract.Columns.Authentication.ACCESS_TOKEN_SECRET, tokens.getString(ACCESS_TOKEN_SECRET));
        values.put(AuthenticationContract.Columns.Authentication.SERVER_NAME, server.getName());
        values.put(AuthenticationContract.Columns.Authentication.SERVER_URL, server.getBaseUrl());
        values.put(AuthenticationContract.Columns.Authentication.SERVER_CONTACT_EMAIL, server.getContactEmail());
        values.put(AuthenticationContract.Columns.Authentication.SERVER_KEY, server.getConsumerKey());
        values.put(AuthenticationContract.Columns.Authentication.SERVER_SECRET, server.getConsumerSecret());
    }

    /*
     * Getting and setting authorization preferences
     *
     * @return true if the app is authorized, false if the app is not authorized
     */
    public boolean isAppAuthorized() {
        return mPrefs.getBoolean(APP_IS_AUTHORIZED, false);
    }

    /**
     * Sets the isAuthorized preference in the SharedPrefs
     *
     * @param value true if the app is authorized, false if not
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
