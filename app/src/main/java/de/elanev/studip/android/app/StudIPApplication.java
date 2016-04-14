/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app;

import android.annotation.TargetApi;
import android.app.Application;
import android.os.Build;
import android.os.StrictMode;

import com.crashlytics.android.Crashlytics;
import com.squareup.picasso.Picasso;

import de.elanev.studip.android.app.util.ApiUtils;
import io.fabric.sdk.android.Fabric;

/**
 * Application class
 *
 * @author joern
 *         <p/>
 *         Extends the Application class to enable crash reports through
 *         Crashlytics
 */
public class StudIPApplication extends Application {

  public static String TAG = StudIPApplication.class.getSimpleName();
  private static StudIPApplication mInstance;

  public static synchronized StudIPApplication getInstance() {
    return mInstance;
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB) @Override public void onCreate() {
    super.onCreate();

    // create instance
    mInstance = this;

    // Trigger initialization of Crashlytics
    if (BuildConfig.USE_CRASHLYTICS) {
      Fabric.with(this, new Crashlytics());
    }

    // Enable StrictMode for debug builds
    if (BuildConfig.DEBUG) {
      StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads()
          .detectDiskWrites()
          .detectAll()
          .penaltyLog()
          .build());
      StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
      builder.detectLeakedSqlLiteObjects().penaltyLog().penaltyDeath();
      if (ApiUtils.isOverApi11()) {
        builder.detectLeakedClosableObjects();
      }
      StrictMode.setVmPolicy(builder.build());

      Picasso.with(this).setIndicatorsEnabled(true);
      Picasso.with(this).setLoggingEnabled(true);
    }
  }


}
