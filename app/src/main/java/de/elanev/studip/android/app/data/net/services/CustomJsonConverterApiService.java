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

  public CustomJsonConverterApiService(Retrofit retrofit) {

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
