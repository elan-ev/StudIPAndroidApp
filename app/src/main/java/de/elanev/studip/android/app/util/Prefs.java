/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import de.elanev.studip.android.app.planner.presentation.view.PlannerActivity;
import timber.log.Timber;


/**
 * Helper class for accessing the shared preferences of the app easier. It offers an
 * interface for all shared preference options needed in this app.
 *
 * @author joern
 */
public class Prefs {
  private static final String APP_PREFS_NAME = "prefs";
  private static final String API_SETTINGS_STRING = "apiSettingsString";
  private static final String PLANNER_PREFERRED_PORTRAIT_VIEW = "plannerPreferredPortraitView";
  private static final String PLANNER_PREFERRED_LANDSCAPE_VIEW = "plannerPreferredLandscapeView";
  private static final String PLANNER_PREFERRED_TIMETABLE_DAYS_COUNT = "plannerPreferredTimetableViewDayCount";
  private static final String CURRENT_USER_ID = "current-user-id";
  private static final String APP_SIGNED_IN = "app-signed-in";
  private static final String ENDPOINT_EMAIL = "app-endpoint-email";
  private static final String ENDPOINT_NAME = "app-endpoint-name";
  private static final String ENDPOINT_BASE_URL = "app-endpoint-base-url";
  private SharedPreferences mPrefs;

  public Prefs(Context context) {
    this.mPrefs = context.getSharedPreferences(APP_PREFS_NAME, Context.MODE_PRIVATE);
  }

  /*
   * Clears the SharedPreferences
   */
  public void clearPrefs() {
    Timber.i("Clearing prefs!");
    mPrefs.edit()
        .clear()
        .apply();
  }

  //TODO: Use for sign in tutorial
  //  /**
  //   * Checks if the app was started before. If it was not started before it will return true
  //   *
  //   * @return true if the current start is the first start of the app on the current device, else
  //   * false
  //   */
  //  public boolean isFirstStart() {
  //    return mPrefs.getBoolean(APP_FIRST_START, true);
  //  }
  //
  //  /**
  //   * Set the app as started. This will cause the isFirstStart() method to return false.
  //   */
  //  public void setAppStarted() {
  //    mPrefs.edit()
  //        .putBoolean(APP_FIRST_START, false)
  //        .apply();
  //  }

  // TODO: Do we really need this anymore?
  //  /**
  //   * Checks if insecure credentials from earlier versions of the app exist.
  //   *
  //   * @return true if there are insecure credentials form earlier versions,
  //   * otherwise false
  //   */
  //  public boolean legacyDataExists() {
  //    String accessToken = mPrefs.getString("accessToken", null);
  //    String accessTokenSecret = mPrefs.getString("accessTokenSecret", null);
  //    String serverName = mPrefs.getString("serverName", null);
  //    String serverUrl = mPrefs.getString("serverUrl", null);
  //    String serverKey = mPrefs.getString("serverKey", null);
  //    String serverSecret = mPrefs.getString("serverSecret", null);
  //
  //    return accessToken != null || accessTokenSecret != null || serverName != null
  //        || serverUrl != null || serverKey != null || serverSecret != null;
  //  }

  /**
   * Returns the stored API settings JSON String representation
   *
   * @return JSON String of the API settings
   */
  public String getApiSettings() {
    return mPrefs.getString(API_SETTINGS_STRING, "");
  }

  /**
   * Saves a String representation of the API settings in the shared preferences.
   *
   * @param apiSettings JSON String representation of the API settings to save
   */
  public void setApiSettings(String apiSettings) {
    mPrefs.edit()
        .putString(API_SETTINGS_STRING, apiSettings)
        .apply();
  }

  //TODO: Use this in for checking if user allow mobile usage for heavy downloads
  //  /**
  //   * Returns whether the user allowed downloading via mobile data connection previously
  //   *
  //   * @return true if the user allowed downloading via mobile data, false otherwise
  //   */
  //  public boolean isAllowMobileData() {
  //    return mPrefs.getBoolean(ALLOW_MOBILE_DATA, false);
  //  }
  //
  //  /**
  //   * Store the users decision to allow downloading via mobile data connection
  //   *
  //   * @param isAllowed the value to store
  //   */
  //  public void setAllowMobile(boolean isAllowed) {
  //    mPrefs.edit()
  //        .putBoolean(ALLOW_MOBILE_DATA, isAllowed)
  //        .apply();
  //  }

  /**
   * Returns the users preferred planner view based on the passed orientation of the devices. We
   * store
   * the preferred view of the landscape and portrait orientation.
   *
   * @param orientation orientation for which the preferred view should be returned
   * @return the preferred view of the planner for the passed orientation
   */
  public String getPreferredPlannerView(int orientation) {
    String preferredView;
    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
      preferredView = mPrefs.getString(PLANNER_PREFERRED_PORTRAIT_VIEW,
          PlannerActivity.PLANNER_VIEW_LIST);
    } else {
      preferredView = mPrefs.getString(PLANNER_PREFERRED_LANDSCAPE_VIEW,
          PlannerActivity.PLANNER_VIEW_TIMETABLE);
    }

    return preferredView;
  }

  /**
   * Stores the preferred planner view for the passed orientation. We store the preferred view for
   * landscape and portrait orientation.
   *
   * @param orientation the orientation for which the preferred view should be stored
   * @param view        the preferred view for the passed orientation
   */
  public void setPlannerPreferredView(int orientation, String view) {
    if (orientation == Configuration.ORIENTATION_PORTRAIT) {
      mPrefs.edit()
          .putString(PLANNER_PREFERRED_PORTRAIT_VIEW, view)
          .apply();
    } else {
      mPrefs.edit()
          .putString(PLANNER_PREFERRED_LANDSCAPE_VIEW, view)
          .apply();
    }
  }

  /**
   * Returns the user's preferred count of days which should be displayed in the timetable view of
   * the planner.
   *
   * @return The count of days which should be displayed in the timetable view.
   */
  public int getPreferredPlannerTimetableViewDayCount() {
    return mPrefs.getInt(PLANNER_PREFERRED_TIMETABLE_DAYS_COUNT, 1);
  }

  /**
   * Stores the users preferred count of days which should be displayed in the timetable view of
   * the planner.
   *
   * @param count The user's preferred count of days.
   */
  public void setPrefPlannerTimetableViewDayCount(int count) {
    mPrefs.edit()
        .putInt(PLANNER_PREFERRED_TIMETABLE_DAYS_COUNT, count)
        .apply();
  }

  public String getCurrentUserId() {
    return mPrefs.getString(CURRENT_USER_ID, "");
  }

  public void setCurrentUserId(String userId) {
    mPrefs.edit()
        .putString(CURRENT_USER_ID, userId.trim())
        .apply();
  }

  public boolean isAppAuthorized() {
    return mPrefs.getBoolean(APP_SIGNED_IN, false);
  }

  public void setAppAuthorized(boolean auth) {
    mPrefs.edit()
        .putBoolean(APP_SIGNED_IN, auth)
        .apply();
  }

  public String getEndpointEmail() {
    return mPrefs.getString(ENDPOINT_EMAIL, "");
  }

  public void setEndpointEmail(String email) {
    mPrefs.edit()
        .putString(ENDPOINT_EMAIL, email)
        .apply();
  }

  public String getEndpointName() {
    return mPrefs.getString(ENDPOINT_NAME, "");
  }

  public void setEndpointName(String endpointName) {
    mPrefs.edit()
        .putString(ENDPOINT_NAME, endpointName)
        .apply();
  }

  public String getBaseUrl() {
    return mPrefs.getString(ENDPOINT_BASE_URL, "");
  }

  public void setBaseUrl(String baseUrl) {
    mPrefs.edit()
        .putString(ENDPOINT_BASE_URL, baseUrl)
        .apply();
  }
}
