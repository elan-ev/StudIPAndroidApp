/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.net.oauth;

import oauth.signpost.OAuthConsumer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.util.Prefs;

public class WebViewActivity extends SherlockActivity {

	public static final String TAG = WebViewActivity.class.getSimpleName();

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayShowHomeEnabled(true);
		actionBar.setIcon(R.drawable.left_indicator);

		setTitle(android.R.string.cancel);
		setContentView(R.layout.webview_view);

		WebView webView = (WebView) this.findViewById(R.id.webView);
		webView.setWebViewClient(new LoginWebViewClient(this));
		WebSettings webViewSettings = webView.getSettings();
		webViewSettings.setSavePassword(false);
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		String authUrl = extras.getString("sAuthUrl");
		webView.loadUrl(authUrl);

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

	class LoginWebViewClient extends WebViewClient {

		public final String TAG = LoginWebViewClient.class.getCanonicalName();
		Activity activity;

		public LoginWebViewClient(Activity activity) {
			this.activity = activity;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			if (url.contains("user")) {
				Log.i(TAG, "AUTHURL" + url);
				Prefs.getInstance(getApplicationContext()).setAuthorized(true);

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
