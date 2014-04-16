/*
 * Copyright (c) 2014 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.widget;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

import de.elanev.studip.android.app.R;

/**
 * Activity holding a WebView which shows the site passed
 *
 * @author joern
 */
public class WebViewActivity extends SherlockActivity {

    public static final String TAG = WebViewActivity.class.getSimpleName();
    public static final String URL = "url";
    public static final String TITLE_RES = "title_res";
    private WebView mWebview;
    private String mUrl;
    private int mTitleRes;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Set up layout
        setContentView(R.layout.webview_view);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(mTitleRes);

        // Get Intent data
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        mUrl = extras.getString(URL);
        mTitleRes = extras.getInt(TITLE_RES);

        // Do the loading
        mWebview = (WebView) this.findViewById(R.id.webView);
        mWebview.loadUrl(mUrl);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
