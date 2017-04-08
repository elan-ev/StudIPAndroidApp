/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.base.internal.di.component;

import de.elanev.studip.android.app.authorization.data.entity.CredentialsEntityDataMapper;
import de.elanev.studip.android.app.authorization.domain.model.OAuthCredentials;
import de.elanev.studip.android.app.base.internal.di.modules.NetworkModule;
import de.elanev.studip.android.app.base.network.NoOpInterceptor;
import de.elanev.studip.android.app.util.Prefs;
import io.realm.RealmConfiguration;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import se.akerfeldt.okhttp.signpost.SigningInterceptor;

/**
 * @author joern
 */
public class MockNetworkModule extends NetworkModule {
  @Override public OAuthCredentials provideOAuthCredentials(RealmConfiguration realmConfiguration,
      CredentialsEntityDataMapper mapper) {
    return new OAuthCredentials();
  }

  @Override public SigningInterceptor provideSignInterceptor(OAuthCredentials credentials) {
    return new NoOpInterceptor();
  }

  @Override public Retrofit provideRetrofit(Prefs prefs, OkHttpClient client,
      JacksonConverterFactory jacksonConverterFactory,
      RxJavaCallAdapterFactory rxJavaCallAdapterFactory) {

    return new Retrofit.Builder().baseUrl("http://localhost")
        .build();
  }
}
