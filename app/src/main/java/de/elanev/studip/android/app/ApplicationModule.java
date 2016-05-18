/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app;

import android.app.Application;
import android.content.Context;
import android.support.annotation.Nullable;

import javax.inject.Singleton;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import de.elanev.studip.android.app.data.datamodel.Server;
import de.elanev.studip.android.app.data.net.services.CustomJsonConverterApiService;
import de.elanev.studip.android.app.data.net.services.DiscoveryRouteJsonConverterFactory;
import de.elanev.studip.android.app.data.net.services.StudIpLegacyApiService;
import de.elanev.studip.android.app.data.net.sync.SyncHelper;
import de.elanev.studip.android.app.util.Prefs;

/**
 * @author joern
 */
@Module
public class ApplicationModule {

  private Application mApplication;

  public ApplicationModule(Application application) {
    this.mApplication = application;
  }

  @Provides @Singleton public Context provideContext() {
    return mApplication;
  }

  @Provides @Nullable public Server provideServer(Context context) {
    return Prefs.getInstance(context)
        .getServer();
  }

  @Provides @Singleton public Prefs providePrefs(Context context) {
    return Prefs.getInstance(context);
  }

  @Provides @Singleton public CustomJsonConverterApiService
  provideCustomJsonConverterApiService(
      @Nullable Server server) {
    return new CustomJsonConverterApiService(server, new DiscoveryRouteJsonConverterFactory());
  }

  @Provides @Singleton public StudIpLegacyApiService provideApiService(Context context,
     @Nullable Server server) {
    return new StudIpLegacyApiService(server, context);
  }

  @Provides @Singleton public SyncHelper provideSyncHelper(Context context,
      Lazy<StudIpLegacyApiService> apiService,
      Lazy<CustomJsonConverterApiService> customJsonConverterApiService) {
    return new SyncHelper(context, apiService, customJsonConverterApiService);
  }


}
