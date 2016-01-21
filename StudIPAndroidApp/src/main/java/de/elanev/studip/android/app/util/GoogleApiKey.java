/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

/**
 * @author joern
 */
public class GoogleApiKey {
  public static final String TAG = GoogleApiKey.class.getSimpleName();

  private static final String API_KEY_IDENTIFIER = "com.google.android.urlshortener.API_KEY";

  public static String getApiKeyFromManifest(Context context) {
    String apiKey = null;

    try {
      ApplicationInfo applicationInfo = context.getPackageManager()
          .getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
      Bundle bundle = applicationInfo.metaData;
      apiKey = bundle.getString(API_KEY_IDENTIFIER);
    } catch (PackageManager.NameNotFoundException e) {
      Log.e(TAG, "Failed to load meta-data, NameNotFound: " + e.getMessage());
    } catch (NullPointerException e) {
      Log.e(TAG, "Failed to load meta-data, NullPointer: " + e.getMessage());
    }

    return apiKey;
  }
}
