/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package de.elanev.studip.android.app.backend.net.oauth;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

import de.elanev.studip.android.app.R;
import oauth.signpost.OAuthConsumer;

/**
 * Activity holding a WebView which prompts the user the OAuth permission
 * 
 * @author joern
 */
public class WebViewActivity extends SherlockActivity {

	public static final String TAG = WebViewActivity.class.getSimpleName();
	private WebView mWebview;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		setTitle(android.R.string.cancel);
		setContentView(R.layout.webview_view);

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
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.actionbarsherlock.app.SherlockActivity#onSaveInstanceState(android
	 * .os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		mWebview.saveState(outState);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.actionbarsherlock.app.SherlockActivity#onOptionsItemSelected(android
	 * .view.MenuItem)
	 */
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

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.webkit.WebViewClient#onPageStarted(android.webkit.WebView,
		 * java.lang.String, android.graphics.Bitmap)
		 */
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			if (url.contains("user")) {
				Log.i(TAG, "AUTHURL" + url);

				Intent intent = new Intent();
				OAuthConsumer consumer = OAuthConnector.getConsumer();
				intent.putExtra("token", consumer.getToken());
				intent.putExtra("tokenSecret", consumer.getTokenSecret());
				Log.i(TAG,
						consumer.getToken() + " " + consumer.getTokenSecret());
				setResult(RESULT_OK, intent);
				finish();
			}

		}

	}
}
