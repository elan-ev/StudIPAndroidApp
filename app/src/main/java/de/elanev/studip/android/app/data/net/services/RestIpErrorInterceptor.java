/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.data.net.services;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author joern
 */
public class RestIpErrorInterceptor implements Interceptor {

  @Override public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();
    boolean isUserRequest = request.url()
        .toString()
        .contains("user");

    Response response = chain.proceed(chain.request());

    if (isUserRequest && response.code() == 400) {
      Response emptyResponse = new Response.Builder().build();

      return emptyResponse;
    }

    return response;
  }

}
