/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.base.internal.di.modules;

import android.content.Context;
import android.support.annotation.Nullable;

import javax.inject.Singleton;

import dagger.Lazy;
import dagger.Module;
import dagger.Provides;
import de.elanev.studip.android.app.data.datamodel.Server;
import de.elanev.studip.android.app.data.net.services.CustomJsonConverterApiService;
import de.elanev.studip.android.app.data.net.services.DiscoveryRouteJsonConverterFactory;
import de.elanev.studip.android.app.data.net.services.OfflineCacheInterceptor;
import de.elanev.studip.android.app.data.net.services.RewriteCacheControlInterceptor;
import de.elanev.studip.android.app.data.net.services.StudIpLegacyApiService;
import de.elanev.studip.android.app.data.net.sync.SyncHelper;
import de.elanev.studip.android.app.util.Prefs;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;
import se.akerfeldt.okhttp.signpost.SigningInterceptor;

/**
 * @author joern
 */
@Module
public class NetworkModule {
  private static final long CACHE_SIZE = 10 * 1024 * 1024;

  @Provides public Server provideServer(Context context) {
    return Prefs.getInstance(context)
        .getServer(context);
  }

  @Provides @Singleton public CustomJsonConverterApiService provideCustomJsonConverterApiService(
      @Nullable Server server) {
    return new CustomJsonConverterApiService(server, new DiscoveryRouteJsonConverterFactory());
  }

  @Provides @Singleton public StudIpLegacyApiService provideApiService(Retrofit retrofit,
      Prefs prefs) {
    return new StudIpLegacyApiService(retrofit, prefs);
  }

  @Provides @Singleton public SyncHelper provideSyncHelper(Context context,
      Lazy<StudIpLegacyApiService> apiService,
      Lazy<CustomJsonConverterApiService> customJsonConverterApiService) {
    return new SyncHelper(context, apiService, customJsonConverterApiService);
  }

  @Provides @Singleton public Cache providesOkHttpCache(Context context) {
    return new Cache(context.getCacheDir(), CACHE_SIZE);
  }

  @Provides @Singleton public HttpLoggingInterceptor providesLoggingInterceptor() {
    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

    return loggingInterceptor;
  }

  @Provides @Singleton public RewriteCacheControlInterceptor providesRewriteCacheControlInterceptor() {
    return new RewriteCacheControlInterceptor();
  }

  @Provides @Singleton public OfflineCacheInterceptor providesOfflineCacheInterceptor() {
    return new OfflineCacheInterceptor();
  }

  @Provides @Singleton public SigningInterceptor providesSignInterceptor(Server server) {
    OkHttpOAuthConsumer oAuthConsumer = new OkHttpOAuthConsumer(server.getConsumerKey(),
        server.getConsumerSecret());
    oAuthConsumer.setTokenWithSecret(server.getAccessToken(), server.getAccessTokenSecret());

    return new SigningInterceptor(oAuthConsumer);
  }

  @Provides @Singleton public OkHttpClient providesOkHttpClient(Cache cache,
      HttpLoggingInterceptor loggingInterceptor, SigningInterceptor signingInterceptor) {

    return new OkHttpClient.Builder().addInterceptor(loggingInterceptor)
        .addInterceptor(signingInterceptor)
        .cache(cache)
        .build();
  }

  @Provides @Singleton JacksonConverterFactory providesJacksonConverterFactory() {
    return JacksonConverterFactory.create();
  }

  @Provides @Singleton RxJavaCallAdapterFactory providesRxJavaCallAdapterFactory() {
    return RxJavaCallAdapterFactory.create();
  }

  @Provides @Singleton public Retrofit providesRetrofit(Server server, OkHttpClient client,
      JacksonConverterFactory jacksonConverterFactory,
      RxJavaCallAdapterFactory rxJavaCallAdapterFactory) {

    return new Retrofit.Builder().baseUrl(server.getApiUrl())
        .addConverterFactory(jacksonConverterFactory)
        .addCallAdapterFactory(rxJavaCallAdapterFactory)
        .client(client)
        .build();
  }

}
