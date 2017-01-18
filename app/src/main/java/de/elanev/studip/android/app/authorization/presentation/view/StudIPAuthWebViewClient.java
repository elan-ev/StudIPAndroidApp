/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.presentation.view;

import android.graphics.Bitmap;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import timber.log.Timber;

/**
 * @author joern
 */

class StudIPAuthWebViewClient extends WebViewClient {
  private WebAuthStatusListener mWebAuthCallbacks;

  public StudIPAuthWebViewClient(WebAuthStatusListener webAuthCallbacks) {
    mWebAuthCallbacks = webAuthCallbacks;
  }

  @Override public void onPageStarted(WebView view, String url, Bitmap favicon) {
    Timber.d("URL loaded: %s", url);
    if (url.contains("user")) {
      if (mWebAuthCallbacks != null) {
        mWebAuthCallbacks.onWebAuthSuccess();
      }
    } else if (url.contains("restipplugin/oauth/oob")) {
      if (mWebAuthCallbacks != null) {
        mWebAuthCallbacks.onWebAuthCanceled();
      }
    }
  }

  interface WebAuthStatusListener {
    void onWebAuthSuccess();

    void onWebAuthCanceled();
  }

}
