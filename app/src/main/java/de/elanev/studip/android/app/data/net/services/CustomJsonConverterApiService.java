/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.data.net.services;

import de.elanev.studip.android.app.BuildConfig;
import de.elanev.studip.android.app.data.datamodel.Routes;
import de.elanev.studip.android.app.data.datamodel.Server;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.http.GET;
import rx.Observable;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;
import se.akerfeldt.okhttp.signpost.SigningInterceptor;

/**
 * A Retrofit ApiService which can use custom JSON converters to be able to parse somewhat
 * strange API Response JSON-Formats.
 *
 * @author joern
 */
public class CustomJsonConverterApiService {

  private SpecialRestServiceForWrongJson mService;

  public CustomJsonConverterApiService(Server server, Converter.Factory converterFactory) {

    if (converterFactory == null) {
      throw new IllegalStateException("Converter.Factory must not be null!");
    }

    // Begin building the OkHttp3 client
    OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

    // Create OkHttp3 SignPost interceptor and add it to the OkHttp3 client
    OkHttpOAuthConsumer oAuthConsumer = new OkHttpOAuthConsumer(server.getConsumerKey(),
        server.getConsumerSecret());
    oAuthConsumer.setTokenWithSecret(server.getAccessToken(), server.getAccessTokenSecret());
    clientBuilder.addInterceptor(new SigningInterceptor(oAuthConsumer));

    // Set log request log level based on BuildConfig
    HttpLoggingInterceptor.Level logLevel = (BuildConfig.DEBUG)
        ? HttpLoggingInterceptor.Level.BODY
        : HttpLoggingInterceptor.Level.BASIC;

    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    logging.setLevel(logLevel);
    clientBuilder.addInterceptor(logging);

    // Add the necessary RestIpApiErrorInterceptor
    clientBuilder.addInterceptor(new RestIpErrorInterceptor());

    // Begin creating the Retrofit2 client
    Retrofit.Builder retrofitBuilder = new Retrofit.Builder();

    // Add API URL, JacksonConverter and the previously created OkHttp3 client
    retrofitBuilder.baseUrl(server.getApiUrl())
        .addConverterFactory(converterFactory)
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .client(clientBuilder.build());

    // Build Retrofit
    Retrofit retrofit = retrofitBuilder.build();

    // Create an instance of our RestIPLegacyService API interface.
    mService = retrofit.create(SpecialRestServiceForWrongJson.class);
  }

  public Observable<Routes> discoverApi() {
    return mService.discoverApi();
  }

  public interface SpecialRestServiceForWrongJson {
    @GET("discovery") Observable<Routes> discoverApi();
  }
}
