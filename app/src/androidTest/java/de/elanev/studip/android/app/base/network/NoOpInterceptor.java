/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.base.network;

import java.io.IOException;

import okhttp3.Response;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;
import se.akerfeldt.okhttp.signpost.SigningInterceptor;

/**
 * @author joern
 */

public class NoOpInterceptor extends SigningInterceptor {
  /**
   * Constructs a new {@code SigningInterceptor}.
   *
   * @param consumer the {@link OkHttpOAuthConsumer} used to sign the requests.
   */
  public NoOpInterceptor() {
    super(null);
  }

  @Override public Response intercept(Chain chain) throws IOException {
    return chain.proceed(chain.request());
  }
}
