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

import de.elanev.studip.android.app.StudIPApplication;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author joern
 */
public class OfflineCacheInterceptor implements Interceptor {
  @Override public Response intercept(Chain chain) throws IOException {
    Request request = chain.request();

    if (!StudIPApplication.hasNetwork()) {
      CacheControl cacheControl = new CacheControl.Builder().maxStale(7, TimeUnit.DAYS)
          .build();

      request = request.newBuilder()
          .cacheControl(cacheControl)
          .build();
    }

    return chain.proceed(request);
  }
}
