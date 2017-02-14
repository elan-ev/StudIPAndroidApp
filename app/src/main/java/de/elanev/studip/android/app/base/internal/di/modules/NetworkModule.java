/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.base.internal.di.modules;

import android.annotation.SuppressLint;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.elanev.studip.android.app.authorization.data.entity.CredentialsEntityDataMapper;
import de.elanev.studip.android.app.authorization.data.entity.OAuthCredentialsEntity;
import de.elanev.studip.android.app.authorization.data.repository.CustomJsonConverterApiService;
import de.elanev.studip.android.app.authorization.domain.model.OAuthCredentials;
import de.elanev.studip.android.app.base.data.net.StudIpLegacyApiService;
import de.elanev.studip.android.app.util.Prefs;
import io.realm.Realm;
import io.realm.RealmConfiguration;
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

  @SuppressLint("NewApi") @Provides public OAuthCredentials provideOAuthCredentials(
      RealmConfiguration realmConfiguration, CredentialsEntityDataMapper mapper) {
    try (Realm realm = Realm.getInstance(realmConfiguration)) {
      OAuthCredentialsEntity entity = realm.where(OAuthCredentialsEntity.class)
          .findFirst();

      if (entity != null) {
        return mapper.transform(realm.copyFromRealm(entity));
      } else {
        return null;
      }
    }
  }

  @Provides @Singleton public CustomJsonConverterApiService provideCustomJsonConverterApiService(
      Retrofit retrofit) {
    return new CustomJsonConverterApiService(retrofit);
  }

  @Provides @Singleton public StudIpLegacyApiService provideApiService(Retrofit retrofit) {
    return new StudIpLegacyApiService(retrofit);
  }

  @Provides @Singleton public Cache provideOkHttpCache(Context context) {
    return new Cache(context.getCacheDir(), CACHE_SIZE);
  }

  @Provides @Singleton public HttpLoggingInterceptor provideLoggingInterceptor() {
    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

    return loggingInterceptor;
  }

  @Provides @Singleton public SigningInterceptor provideSignInterceptor(
      OAuthCredentials credentials) {
    OkHttpOAuthConsumer oAuthConsumer = new OkHttpOAuthConsumer(credentials.getEndpoint()
        .getConsumerKey(), credentials.getEndpoint()
        .getConsumerSecret());
    oAuthConsumer.setTokenWithSecret(credentials.getAccessToken(),
        credentials.getAccessTokenSecret());

    return new SigningInterceptor(oAuthConsumer);
  }

  @Provides @Singleton public OkHttpClient provideOkHttpClient(Cache cache,
      HttpLoggingInterceptor loggingInterceptor, SigningInterceptor signingInterceptor) {

    return new OkHttpClient.Builder().addInterceptor(loggingInterceptor)
        .addInterceptor(signingInterceptor)
        .cache(cache)
        .build();
  }

  @Provides @Singleton public JacksonConverterFactory provideJacksonConverterFactory() {
    return JacksonConverterFactory.create();
  }

  @Provides @Singleton public RxJavaCallAdapterFactory provideRxJavaCallAdapterFactory() {
    return RxJavaCallAdapterFactory.create();
  }

  @Provides @Singleton public Retrofit provideRetrofit(Prefs prefs, OkHttpClient client,
      JacksonConverterFactory jacksonConverterFactory,
      RxJavaCallAdapterFactory rxJavaCallAdapterFactory) {

    return new Retrofit.Builder().baseUrl(prefs.getBaseUrl())
        .addConverterFactory(jacksonConverterFactory)
        .addCallAdapterFactory(rxJavaCallAdapterFactory)
        .client(client)
        .build();
  }

}
