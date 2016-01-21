/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.widget;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;

import de.elanev.studip.android.app.R;

/**
 * Activity holding a WebView which shows the site passed
 *
 * @author joern
 */
public class WebViewActivity extends AppCompatActivity {

  public static final String TAG = WebViewActivity.class.getSimpleName();
  public static final String URL = "de.elanev.studip.android.app.URL";
  public static final String TITLE_RES = "de.elanev.studip.android.app.TITLE_RES";

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.webview_view);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    // Get Intent data or return if intent is null
    Intent intent = getIntent();
    if (intent == null) {
      return;
    }

    Bundle extras = intent.getExtras();

    int titleRes = extras.getInt(TITLE_RES);
    String url = extras.getString(URL);
    setTitle(titleRes);

    WebView webView = (WebView) findViewById(R.id.webView);
    webView.loadUrl(url);

  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      // Respond to the action bar's Up/Home button
      case android.R.id.home:
        onBackPressed();
        return true;
      default:
    }
    return super.onOptionsItemSelected(item);
  }

}
