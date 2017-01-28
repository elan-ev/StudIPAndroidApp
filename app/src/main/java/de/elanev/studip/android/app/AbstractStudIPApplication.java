/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import de.elanev.studip.android.app.base.internal.di.components.ApplicationComponent;
import de.elanev.studip.android.app.base.internal.di.components.DaggerApplicationComponent;
import de.elanev.studip.android.app.base.internal.di.modules.ApplicationModule;
import io.realm.Realm;

/**
 * @author joern
 */
public abstract class AbstractStudIPApplication extends Application {
  private static AbstractStudIPApplication mInstance;
  private ApplicationComponent mApplicationComponent;

  public static synchronized AbstractStudIPApplication getInstance() {
    return mInstance;
  }

  public static boolean hasNetwork() {
    return mInstance.checkNetworkConnectivity();
  }

  private boolean checkNetworkConnectivity() {
    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
    return networkInfo != null && networkInfo.isConnected();
  }

  @Override public void onCreate() {
    super.onCreate();
    buildAppComponent();

    // create instance
    mInstance = this;

    Realm.init(this);
  }

  public void buildAppComponent() {
    mApplicationComponent = DaggerApplicationComponent.builder()
        .applicationModule(new ApplicationModule(this))
        .build();
  }

  public ApplicationComponent getAppComponent() {
    return mApplicationComponent;
  }
}
