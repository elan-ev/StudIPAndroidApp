/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

/**
 * Source: https://gist.github.com/JakeWharton/5616899#gistcomment-1259033
 */
package de.elanev.studip.android.app.backend.net.util;

import com.android.volley.toolbox.HurlStack;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.OkUrlFactory;

/**
 * An {@link com.android.volley.toolbox.HttpStack HttpStack} implementation which
 * uses OkHttp as its transport.
 */
public class OkHttpStack extends HurlStack {
  private final OkUrlFactory mFactory;

  public OkHttpStack() {
    this(new OkHttpClient());
  }

  public OkHttpStack(OkHttpClient client) {
    if (client == null) {
      throw new NullPointerException("Client must not be null.");
    }
    mFactory = new OkUrlFactory(client);
  }

  @Override protected HttpURLConnection createConnection(URL url) throws IOException {
    return mFactory.open(url);
  }
}