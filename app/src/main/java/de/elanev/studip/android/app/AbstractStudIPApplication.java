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

/**
 * @author joern
 */
public abstract class AbstractStudIPApplication extends Application {
  private static AbstractStudIPApplication mInstance;

  public static synchronized AbstractStudIPApplication getInstance() {
    return mInstance;
  }

  @TargetApi(Build.VERSION_CODES.HONEYCOMB) @Override public void onCreate() {
    super.onCreate();

    // create instance
    mInstance = this;
  }
}
