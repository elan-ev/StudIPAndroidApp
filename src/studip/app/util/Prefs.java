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
package studip.app.util;

import studip.app.backend.net.Server;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author joern
 * 
 */
public class Prefs {
    private static Prefs mInstance;
    private static Context mContext;
    private static final String APP_PREFS_NAME = "prefs";
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String ACCESS_TOKEN_SECRET = "accessTokenSecret";
    private static final String SERVER_NAME = "serverName";
    private static final String SERVER_URL = "serverUrl";
    private static final String SERVER_KEY = "serverKey";
    private static final String SERVER_SECRET = "serverSecret";
    private static final String APP_AUTHORIZED = "appAuthorized";

    public static Prefs getInstance(Context context) {
	if (mInstance == null)
	    mInstance = new Prefs(context);

	return mInstance;
    }

    private Prefs() {
    }

    private Prefs(Context context) {
	mContext = context;
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
	prefsEditor.putString(SERVER_NAME, value.NAME);
	prefsEditor.putString(SERVER_URL, value.BASE_URL);
	prefsEditor.putString(SERVER_KEY, value.CONSUMER_KEY);
	prefsEditor.putString(SERVER_SECRET, value.CONSUMER_SECRET);
	prefsEditor.apply();
    }

    /*
     * Getting and setting access token preferences
     */
    public String getAccessToken() {

	return getPrefs().getString(ACCESS_TOKEN, null);
    }

    public String getAccessTokenSecret() {

	return getPrefs().getString(ACCESS_TOKEN_SECRET, null);
    }

    public void setAccessToken(String value) {
	SharedPreferences.Editor prefsEditor = getPrefs().edit();
	prefsEditor.putString(ACCESS_TOKEN, value);
	prefsEditor.apply();
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

	return getPrefs().getBoolean(APP_AUTHORIZED, false);
    }

    public void setAuthorized(Boolean value) {
	SharedPreferences.Editor prefsEditor = getPrefs().edit();
	prefsEditor.putBoolean(APP_AUTHORIZED, value);
	prefsEditor.apply();
    }

}
