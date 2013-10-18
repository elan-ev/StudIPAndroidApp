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

import de.elanev.studip.android.app.backend.net.Server;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

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
    private static final String APP_FIRST_START = "appFirstStart";
    private static Prefs mInstance;
    private static Context mContext;


    private Prefs() {
    }

    private Prefs(Context context) {
        mContext = context;
    }

    public static Prefs getInstance(Context context) {
        if (mInstance == null)
            mInstance = new Prefs(context);

        return mInstance;
    }

    private static SharedPreferences getPrefs() {
        return mContext.getSharedPreferences(APP_PREFS_NAME,
                Activity.MODE_PRIVATE);
    }

    /*
     * Clear preferences for debuging
     */
    public void clearPrefs() {
        SharedPreferences.Editor prefsEditor = getPrefs().edit();
        prefsEditor.clear();
        prefsEditor.apply();
    }

    /*
     * Getting and setting server preferences
     */
    public Server getServer() {
        Server server = null;
        String serverName = getPrefs().getString(SERVER_NAME, null);
        String serverUrl = getPrefs().getString(SERVER_URL, null);
        String serverKey = getPrefs().getString(SERVER_KEY, null);
        String serverSecret = getPrefs().getString(SERVER_SECRET, null);
        if (serverName != null && serverUrl != null && serverKey != null
                && serverSecret != null) {
            server = new Server(serverName, serverKey, serverSecret, serverUrl);
        }

        return server;
    }

    public void setServer(Server value) {
        SharedPreferences.Editor prefsEditor = getPrefs().edit();
        prefsEditor.putString(SERVER_NAME, value.getName());
        prefsEditor.putString(SERVER_URL, value.getBaseUrl());
        prefsEditor.putString(SERVER_KEY, value.getConsumerKey());
        prefsEditor.putString(SERVER_SECRET, value.getConsumerSecret());
        prefsEditor.apply();
    }

    /*
     * Getting and setting access token preferences
     */
    public String getAccessToken() {

        return getPrefs().getString(ACCESS_TOKEN, null);
    }

    public void setAccessToken(String value) {
        SharedPreferences.Editor prefsEditor = getPrefs().edit();
        prefsEditor.putString(ACCESS_TOKEN, value);
        prefsEditor.apply();
    }

    public String getAccessTokenSecret() {

        return getPrefs().getString(ACCESS_TOKEN_SECRET, null);
    }

    public void setAccessTokenSecret(String value) {
        SharedPreferences.Editor prefsEditor = getPrefs().edit();
        prefsEditor.putString(ACCESS_TOKEN_SECRET, value);
        prefsEditor.apply();
    }

    /*
     * Getting and setting authorization preferences
     */
    public Boolean isAppAuthorized() {
        return (getAccessToken() != null && getAccessTokenSecret() != null);
    }

    public boolean isFirstStart() {
        return getPrefs().getBoolean(APP_FIRST_START, true);
    }

    public void setAppStarted() {
        SharedPreferences.Editor prefsEditor = getPrefs().edit();
        prefsEditor.putBoolean(APP_FIRST_START, false);
        prefsEditor.apply();
    }
}
