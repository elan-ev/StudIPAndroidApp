/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.auth;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import de.elanev.studip.android.app.R;

/**
 * Activity holding a WebView which prompts the user the OAuth permission.
 *
 * @author joern
 */
public class WebViewActivity extends AppCompatActivity {

    public static final String TAG = WebViewActivity.class.getSimpleName();
    private WebView mWebview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle(android.R.string.cancel);

        mWebview = (WebView) this.findViewById(R.id.webView);

        // If instance state was saved, restore it
        if (savedInstanceState != null) {
            mWebview.restoreState(savedInstanceState);
        } else {
            mWebview.setWebViewClient(new LoginWebViewClient());
            Intent intent = getIntent();
            Bundle extras = intent.getExtras();
            String authUrl = extras.getString("sAuthUrl");
            mWebview.loadUrl(authUrl);
        }

    }



    /*
    * Prevent the user from pressing the back button
    */
    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mWebview.saveState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpTo(this, new Intent(this, SignInActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     * WebviewClient which overrides the onPageStarted method to intercept the
     * OAuth result
     */
    private class LoginWebViewClient extends WebViewClient {

        public final String TAG = LoginWebViewClient.class.getCanonicalName();

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (url.contains("user")) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }

        }

    }
}
