/*
 * Copyright (c) 2014 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

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
 * Helper singleton class for accessing the shared preferences of the app easier. It offers an
 * interface for all shared preference options needed in this app.
 *
 * @author joern
 */
public class Prefs {
  // TODO do DB operations in AsyncTask
  private static final String APP_PREFS_NAME = "prefs";
  private static final String APP_FIRST_START = "appFirstStart";
  private static final String APP_SYMC_COMPLETE = "appSyncComplete";
  private static final String USER_ID = "userId";
  private static final String TAG = Prefs.class.getSimpleName();
  private static final String CURRENT_SEMESTER_ID = "currentSemesterId";
  private static final String RECORDINGS_ENABLED = "recordingsEnabled";
  private static final String FORUM_IS_ACTIVATED = "activeRoutes";
  private static final String API_SETTINGS_STRING = "apiSettingsString";
  private static Prefs sInstance;
  private Context mContext;
  private SharedPreferences mPrefs;
  private volatile Server mCachedServer;

  private Prefs() {
  }

  private Prefs(Context context) {
    this.mContext = context.getApplicationContext();
    this.mPrefs = context.getSharedPreferences(APP_PREFS_NAME, Context.MODE_PRIVATE);
  }

  /**
   * Returns an instance of the Prefs for easily accessing several stored preferences
   *
   * @param context The context that is used to access the preferences
   * @return An instance of the Prefs
   */
  public static synchronized Prefs getInstance(Context context) {
    if (sInstance == null) sInstance = new Prefs(context);

    return sInstance;
  }

  /*
   * Clears the SharedPreferences
   */
  public void clearPrefs() {
    mPrefs.edit().clear().apply();
    this.mCachedServer = null;
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
      if (server.getAccessToken() == null || server.getAccessTokenSecret() == null) {
        return false;
      } else {
        return true;
      }
    } else {
      return false;
    }
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


        this.mCachedServer = new Server(serverName,
            serverKey,
            serverSecret,
            serverUrl,
            serverContact,
            accessToken,
            accessSecret);
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
    values.put(AuthenticationContract.Columns.Authentication.ACCESS_TOKEN_SECRET,
        server.getAccessTokenSecret());
    values.put(AuthenticationContract.Columns.Authentication.SERVER_NAME, server.getName());
    values.put(AuthenticationContract.Columns.Authentication.SERVER_URL, server.getBaseUrl());
    values.put(AuthenticationContract.Columns.Authentication.SERVER_CONTACT_EMAIL,
        server.getContactEmail());
    values.put(AuthenticationContract.Columns.Authentication.SERVER_KEY, server.getConsumerKey());
    values.put(AuthenticationContract.Columns.Authentication.SERVER_SECRET,
        server.getConsumerSecret());

    Uri returnUri = mContext.getContentResolver()
        .insert(AuthenticationContract.CONTENT_URI, values);

    if (Long.parseLong(returnUri.getLastPathSegment()) != -1) {
      mCachedServer = server;
    } else {
      mCachedServer = null;
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
    mPrefs.edit().putBoolean(APP_FIRST_START, false).apply();
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

    if (accessToken != null || accessTokenSecret != null || serverName != null ||
        serverUrl != null || serverKey != null || serverSecret != null) {

      return true;

    } else {

      return false;

    }
  }

  /**
   * Takes a Server object and creates the shared preferences of the old and insecure way for
   * saving the authorized sever credentials.
   *
   * @param server Server object to create the old credentials for.
   * @deprecated debug only, for credential migration testing.
   */
  public void simulateOldPrefs(Server server) {
    mPrefs.edit().putString("accessToken", server.getAccessToken()).apply();
    mPrefs.edit().putString("accessTokenSecret", server.getAccessTokenSecret()).apply();
    mPrefs.edit().putString("serverName", server.getName()).apply();
    mPrefs.edit().putString("serverUrl", server.getBaseUrl()).apply();
    mPrefs.edit().putString("serverKey", server.getConsumerKey()).apply();
    mPrefs.edit().putString("serverSecret", server.getConsumerSecret()).apply();
  }

  /**
   * Sets the preference that indicates that the initial sync operation was successful.
   */
  public void setAppSynced() {
    mPrefs.edit().putBoolean(APP_SYMC_COMPLETE, true).apply();
  }

  /**
   * Returns true if the initial sync operation was successful. Otherwise it will return false.
   *
   * @return true if the initial sync operation was successful. Otherwise false.
   */
  public boolean isAppSynced() {
    return mPrefs.getBoolean(APP_SYMC_COMPLETE, false);
  }

  /**
   * Returns the Stud.IP internal user id String.
   *
   * @return Stud.IP user id String.
   */
  public String getUserId() {
    return mPrefs.getString(USER_ID, null);
  }

  /**
   * Saves the users Stud.IP id String in the preferences.
   *
   * @param userId the current users Stud.IP user id String.
   */
  public void setUserId(String userId) {
    mPrefs.edit().putString(USER_ID, userId).apply();
  }

  /**
   * Returns the Stud.IP id String of the current semester
   *
   * @return Stud.IP id String of the current semester.
   */
  public String getCurrentSemesterId() {
    return mPrefs.getString(CURRENT_SEMESTER_ID, null);
  }

  /**
   * Saves the Stud.IP id String of the current semester in the shared preferences.
   *
   * @param semesterId Stud.IP id String of the current semester.
   */
  public void setCurrentSemesterId(String semesterId) {
    mPrefs.edit().putString(CURRENT_SEMESTER_ID, semesterId).apply();
  }

  /**
   * Saves the wether the forum is activated or not.
   *
   * @param value Indicates an activated forum.
   */
  public void setForumIsActivated(boolean value) {
    mPrefs.edit().putBoolean(FORUM_IS_ACTIVATED, value).apply();
  }

  /**
   * Checks if the forum route is activated on the API.
   *
   * @return true if the forum route is active on the API, false otherwise
   */
  public boolean isForumActivated() {
    return mPrefs.getBoolean(FORUM_IS_ACTIVATED, false);
  }

  /**
   * Saves a String representation of the API settings in the shared preferences.
   *
   * @param apiSettings JSON String representation of the API settings to save
   */
  public void setApiSettings(String apiSettings) {
    mPrefs.edit().putString(API_SETTINGS_STRING, apiSettings).apply();
  }

  /**
   * Returns the stored API settings JSON String representation
   *
   * @return JSON String of the API settings
   */
  public String getApiSettings() {
    return mPrefs.getString(API_SETTINGS_STRING, "");
  }
}
