/*
 * Copyright (c) 2015 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.backend.net.services;

import android.content.Context;

import org.apache.http.HttpStatus;

import de.elanev.studip.android.app.backend.datamodel.Routes;
import de.elanev.studip.android.app.backend.datamodel.Server;
import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.Converter;
import retrofit.http.GET;
import rx.Observable;
import se.akerfeldt.signpost.retrofit.RetrofitHttpOAuthConsumer;
import se.akerfeldt.signpost.retrofit.SigningOkClient;

/**
 * A Retrofit ApiService which can use custom JSON converters to be able to parse somewhat
 * strange API Response JSON-Formats.
 *
 * @author joern
 */
public class CustomJsonConverterApiService {

  private SpecialRestServiceForWrongJson mService;

  public CustomJsonConverterApiService(Context context, Server server, Converter converter) {

    if (context == null) {
      throw new IllegalStateException("Converter must not be null!");
    }
    RetrofitHttpOAuthConsumer oAuthConsumer = new RetrofitHttpOAuthConsumer(server.getConsumerKey(),
        server.getConsumerSecret());
    oAuthConsumer.setTokenWithSecret(server.getAccessToken(), server.getAccessTokenSecret());

    RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(server.getApiUrl())
        .setLogLevel(RestAdapter.LogLevel.FULL)
        .setConverter(converter)
        .setClient(new SigningOkClient(oAuthConsumer))
        .setErrorHandler(new ErrorHandler() {
          @Override public Throwable handleError(RetrofitError cause) {
            Response response = cause.getResponse();
            if (response.getUrl().contains("user")
                && cause.getResponse().getStatus() == HttpStatus.SC_NOT_FOUND) {
              return new StudIpLegacyApiService.UserNotFoundException(cause);
            }
            return cause;
          }
        })
        .build();

    mService = restAdapter.create(SpecialRestServiceForWrongJson.class);
  }

  public Observable<Routes> discoverApi() {
    return mService.discoverApi();
  }

  public interface SpecialRestServiceForWrongJson {
    @GET("/discovery") Observable<Routes> discoverApi();
  }
}
