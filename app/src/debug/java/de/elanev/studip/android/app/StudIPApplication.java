/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.StrictMode;

import com.squareup.picasso.Picasso;

import timber.log.Timber;

/**
 * Debug build variant Application class
 *
 * @author joern
 */
public class StudIPApplication extends AbstractStudIPApplication {


  @TargetApi(Build.VERSION_CODES.HONEYCOMB) @Override public void onCreate() {
    super.onCreate();

    // Enable StrictMode for debug builds
    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads()
        .detectDiskWrites()
        .detectAll()
        .penaltyLog()
        .build());
    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
    builder.detectLeakedSqlLiteObjects()
        .penaltyLog()
        .detectLeakedClosableObjects();
    StrictMode.setVmPolicy(builder.build());

    Picasso.with(this)
        .setIndicatorsEnabled(true);
    Picasso.with(this)
        .setLoggingEnabled(true);
    Timber.plant(new Timber.DebugTree() {

      @Override protected String createStackElementTag(StackTraceElement element) {
        return super.createStackElementTag(element) + ":" + element.getLineNumber();
      }
    });
  }


}
