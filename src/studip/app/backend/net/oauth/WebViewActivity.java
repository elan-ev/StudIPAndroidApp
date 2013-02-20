/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package studip.app.backend.net.oauth;

import StudIPApp.app.R;
import studip.app.util.Prefs;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class WebViewActivity extends Activity {

    private static final String TAG = WebViewActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	this.setContentView(R.layout.webview_view);

	WebView webView = (WebView) this.findViewById(R.id.webView);
	webView.setWebViewClient(new LoginWebViewClient(this));

	WebSettings webViewSettings = webView.getSettings();
	webViewSettings.setSavePassword(false);
	Intent intent = getIntent();
	Bundle extras = intent.getExtras();
	String authUrl = extras.getString("authUrl");
	Log.d(TAG, authUrl);
	webView.loadUrl(authUrl);

    }

    @Override
    public void onResume() {
	super.onResume();
    }

    class LoginWebViewClient extends WebViewClient {

	public final String TAG = LoginWebViewClient.class.getSimpleName();
	Activity activity;

	public LoginWebViewClient(Activity activity) {
	    this.activity = activity;
	}

	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
	    if (url.contains("user")) {
		Log.d(TAG, url);
		Prefs.getInstance(getApplicationContext()).setAuthorized(true);

		Intent intent = new Intent();
		intent.putExtra("token",
			OAuthConnector.getInstance().consumer.getToken());
		intent.putExtra("tokenSecret",
			OAuthConnector.getInstance().consumer.getTokenSecret());
		setResult(RESULT_OK, intent);
		finish();
	    }

	}

    }
}
