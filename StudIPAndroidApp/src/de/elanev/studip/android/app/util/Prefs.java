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

import android.content.Context;
import android.content.SharedPreferences;

import com.mndfcktory.android.encryptedUserprefs.SecurePreferences;

import de.elanev.studip.android.app.backend.datamodel.Server;


/**
 * @author joern
 */
public class Prefs {
    private static final String APP_PREFS_NAME = "prefs";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String ACCESS_TOKEN_SECRET = "accessTokenSecret";
    private static final String SERVER_NAME = "serverName";
    private static final String SERVER_URL = "serverUrl";
    private static final String SERVER_KEY = "serverKey";
    private static final String SERVER_SECRET = "serverSecret";
    private static final String SERVER_CONTACT_EMAIL = "serverContactEmail";
    private static final String APP_FIRST_START = "appFirstStart";
    private static final String APP_SECURE_START = "appSecureStart";
    private static final String TAG = Prefs.class.getSimpleName();
    private static Prefs mInstance;
    private static Context mContext;
    private static SecurePreferences mPrefs;


    private Prefs() {
    }

    private Prefs(Context context) {
        mContext = context;
    }

    public static Prefs getInstance(Context context) {
        if (mInstance == null)
            mInstance = new Prefs(context);

        if (mPrefs == null)
            mPrefs = new SecurePreferences(mContext,
                    APP_PREFS_NAME,
                    Config.PRIVATE_KEY,
                    Installation.id(mContext),
                    true);

        return mInstance;
    }

    /*
     * Clear preferences
     */
    public void clearPrefs() {
        mPrefs.clear();
    }

    /*
     * Getting and setting server preferences
     */
    public Server getServer() {
        Server server = null;
        String serverName = mPrefs.getString(SERVER_NAME);
        String serverUrl = mPrefs.getString(SERVER_URL);
        String serverKey = mPrefs.getString(SERVER_KEY);
        String serverSecret = mPrefs.getString(SERVER_SECRET);
        String contactEmail = mPrefs.getString(SERVER_CONTACT_EMAIL);
        if (serverName != null && serverUrl != null && serverKey != null
                && serverSecret != null) {
            server = new Server(serverName, serverKey, serverSecret, serverUrl, contactEmail);
        }

        return server;
    }

    public void setServer(Server value) {
        mPrefs.putString(SERVER_NAME, value.getName());
        mPrefs.putString(SERVER_URL, value.getBaseUrl());
        mPrefs.putString(SERVER_KEY, value.getConsumerKey());
        mPrefs.putString(SERVER_SECRET, value.getConsumerSecret());
        mPrefs.putString(SERVER_CONTACT_EMAIL, value.getContactEmail());
    }

    /*
     * Getting and setting access token preferences
     */
    public String getAccessToken() {

        return mPrefs.getString(ACCESS_TOKEN);
    }

    public void setAccessToken(String value) {
        mPrefs.putString(ACCESS_TOKEN, value);
    }

    public String getAccessTokenSecret() {
        return mPrefs.getString(ACCESS_TOKEN_SECRET);
    }

    public void setAccessTokenSecret(String value) {
        mPrefs.putString(ACCESS_TOKEN_SECRET, value);
    }

    /*
     * Getting and setting authorization preferences
     */
    public Boolean isAppAuthorized() {
        return (getAccessToken() != null && getAccessTokenSecret() != null);
    }

    public boolean isFirstStart() {
        return mContext
                .getSharedPreferences(APP_PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(APP_FIRST_START, true);
    }

    public void setAppStarted() {
        SharedPreferences.Editor prefsEditor = mContext
                .getSharedPreferences(APP_PREFS_NAME, Context.MODE_PRIVATE)
                .edit();

        prefsEditor.putBoolean(APP_FIRST_START, false)
                .commit();
    }

    public boolean isSecureStarted() {
        return mPrefs.getBoolean(APP_SECURE_START, false);
    }

    public void setSecureStarted() {
        mPrefs.putBoolean(APP_SECURE_START, true);
    }
}
