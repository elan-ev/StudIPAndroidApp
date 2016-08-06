/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.base.internal.di.modules;

import android.app.Application;
import android.content.ContentResolver;
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
import de.elanev.studip.android.app.news.data.repository.NewsDataRepository;
import de.elanev.studip.android.app.news.domain.NewsRepository;
import de.elanev.studip.android.app.util.Prefs;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * @author joern
 */
@Module
public class ApplicationModule {

  private Application mApplication;

  public ApplicationModule(Application application) {
    this.mApplication = application;
  }

  // Android
  @Provides @Singleton public Context provideContext() {
    return mApplication;
  }

  @Provides @Singleton public ContentResolver provideContentResolver(Context context) {
    return context.getContentResolver();
  }

  @Provides @Singleton public Prefs providePrefs(Context context) {
    return Prefs.getInstance(context);
  }



  @Provides @Singleton public NewsRepository provideNewsRepository(
      NewsDataRepository newsDataRepository) {
    return newsDataRepository;
  }

}
