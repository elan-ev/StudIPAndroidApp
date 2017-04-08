/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app;

import com.crashlytics.android.Crashlytics;

import de.elanev.studip.android.app.logging.ReleaseTimberTree;
import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 * Release build variant of the Application class
 *
 * @author joern
 */
public class StudIPApplication extends AbstractStudIPApplication {


  @Override public void onCreate() {
    super.onCreate();

    Fabric.with(this, new Crashlytics());
    Timber.plant(new ReleaseTimberTree());
  }
}


