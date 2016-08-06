/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.data.net.services;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * @author joern
 */
public class RewriteCacheControlInterceptor implements Interceptor {
  private static final String CACHE_CONTROL_HEADER = "Cache-Control";

  @Override public Response intercept(Chain chain) throws IOException {
    Response response = chain.proceed(chain.request());
    CacheControl.Builder cacheControlBuilder = new CacheControl.Builder();
    //FIXME: The cache-control headers from the API should be used, but they need some work
    //    if (response.cacheControl() == null) {
    //      cacheControlBuilder.maxAge(2, TimeUnit.MINUTES);
    //    }
    // Instead cache everything for two minutes

    // We want messages not to be cached to simulate a realtime delivery
    if (!chain.request().url().toString().contains("messages")) {
      cacheControlBuilder.maxAge(30, TimeUnit.MINUTES);
    }

    return response.newBuilder()
        .header(CACHE_CONTROL_HEADER, cacheControlBuilder.build()
            .toString())
        .build();
  }
}
