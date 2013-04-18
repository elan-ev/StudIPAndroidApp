/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.net.oauth;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.net.ChooseServerActivity;
import de.elanev.studip.android.app.backend.net.Server;
import de.elanev.studip.android.app.frontend.news.NewsViewActivity;
import de.elanev.studip.android.app.util.Prefs;

public class SignInActivity extends FragmentActivity {

	private static final String TAG = SignInActivity.class.getSimpleName();

	public Server mSelectedServer;
	public ProgressBar mProgressBar;
	public OAuthConnector mConnector;
	public Button mSignInButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, " onCreate");
		this.setContentView(R.layout.choose_server_view);
		mProgressBar = (ProgressBar) findViewById(R.id.chooseServerProgressBar);
		mSignInButton = (Button) findViewById(R.id.signinbutton);
		mConnector = OAuthConnector.getInstance();

		mSignInButton.setVisibility(View.GONE);
		mProgressBar.setVisibility(View.VISIBLE);

		((Button) this.findViewById(R.id.signinbutton))
				.setOnClickListener(new OnClickListener() {
					// @Override
					public void onClick(View v) {
						signInButtonPressed(v);
					}
				});

	}

	@Override
	protected void onResume() {
		super.onResume();

		mProgressBar.setVisibility(View.VISIBLE);
		if (mSelectedServer == null) {
			setSelectedServer();
		}

		if (Prefs.getInstance(getApplicationContext()).isAppAuthorized()) {
			connect();
		} else {
			mSignInButton.setVisibility(View.VISIBLE);
			mProgressBar.setVisibility(View.GONE);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);

		if (resultCode == RESULT_OK) {
			Log.d(TAG, "ACCESS TOKEN FLAG SET");
			Bundle extras = intent.getExtras();
			String token = extras.getString("token");
			String tokenSecret = extras.getString("tokenSecret");
			Log.d(TAG, token + ", " + tokenSecret);
			mConnector.consumer.setTokenWithSecret(token, tokenSecret);
			requestAccessToken();
		}
	}

	private void setSelectedServer() {
		mSelectedServer = Prefs.getInstance(getApplicationContext())
				.getServer();
		if (mSelectedServer == null) {
			this.startActivity(new Intent(SignInActivity.this,
					ChooseServerActivity.class));
		}
	}

	public void signInButtonPressed(View v) {
		if (mSelectedServer != null) {
			connect();
		}
	}

	private void connect() {
		if (mConnector.provider == null && mConnector.consumer == null) {
			mConnector.init(mSelectedServer);
		}
		if (!Prefs.getInstance(getApplicationContext()).isAppAuthorized()) {
			getRequestToken();
		} else if (Prefs.getInstance(getApplicationContext()).getAccessToken() != null
				&& Prefs.getInstance(getApplicationContext())
						.getAccessTokenSecret() != null) {
			mConnector.accessToken = Prefs.getInstance(getApplicationContext())
					.getAccessToken();
			mConnector.accessSecret = Prefs
					.getInstance(getApplicationContext())
					.getAccessTokenSecret();

			if (mConnector.accessToken != null
					&& mConnector.accessSecret != null) {

				Log.d("Verbinung", "access Token GELADEN");
				Log.d("accessToken", mConnector.accessToken);
				Log.d("accessSecret", mConnector.accessSecret);

				mConnector.setAccessToken(mConnector.accessToken,
						mConnector.accessSecret);
				// this.startActivity(new Intent(this,
				// AbstractFragmentActivity.class));
				this.startActivity(new Intent(this, NewsViewActivity.class));
				this.finish();
			}
		}

	}

	private void requestAccessToken() {
		AccessTokenTask caller = new AccessTokenTask(SignInActivity.this,
				mConnector.provider, mConnector.consumer);
		caller.execute();
	}

	private void getRequestToken() {
		RequestTokenTask caller = new RequestTokenTask(SignInActivity.this,
				mConnector.provider, mConnector.consumer);
		caller.execute();
	}

	public class RequestTokenTask extends AsyncTask<String, Integer, String> {
		private Activity mContext;
		private OAuthProvider mProvider;
		private OAuthConsumer mConsumer;

		public RequestTokenTask(Activity ctx, OAuthProvider provider,
				OAuthConsumer consumer) {
			this.mContext = ctx;
			this.mProvider = provider;
			this.mConsumer = consumer;
		}

		@Override
		protected void onPreExecute() {
			mSignInButton.setVisibility(View.GONE);
			mProgressBar.setVisibility(ProgressBar.VISIBLE);

		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			if (result != null) {
				Log.d("Verbinung", "request Token geholt");
				Log.d("authUrl", result);
				int requestCode = 0;
				Intent intent = new Intent(mContext, WebViewActivity.class);
				intent.putExtra("authUrl", result);
				mContext.startActivityForResult(intent, requestCode);
			}
		}

		@Override
		protected String doInBackground(String... params) {
			String result = null;
			try {
				result = mProvider.retrieveRequestToken(mConsumer, "");
			} catch (OAuthMessageSignerException e) {
				e.printStackTrace();
			} catch (OAuthNotAuthorizedException e) {
				e.printStackTrace();
			} catch (OAuthExpectationFailedException e) {
				e.printStackTrace();
			} catch (OAuthCommunicationException e) {
				e.printStackTrace();
			}

			Log.v(TAG, "Auth URL " + result);
			return result;
		}
	}

	public class AccessTokenTask extends AsyncTask<String, Integer, String> {
		private Activity mContext;
		private OAuthProvider mProvider;
		private OAuthConsumer mConsumer;

		public AccessTokenTask(Activity ctx, OAuthProvider provider,
				OAuthConsumer consumer) {
			this.mContext = ctx;
			this.mProvider = provider;
			this.mConsumer = consumer;
		}

		@Override
		protected String doInBackground(String... arg0) {
			Log.d(TAG,
					String.format("%s, %s", mConsumer.getToken(),
							mConsumer.getTokenSecret()));
			try {
				mProvider.retrieveAccessToken(mConsumer, null);
				mConnector.accessToken = mConsumer.getToken();
				mConnector.accessSecret = mConsumer.getTokenSecret();
				Prefs.getInstance(getApplicationContext()).setAccessToken(
						mConsumer.getToken());
				Prefs.getInstance(getApplicationContext())
						.setAccessTokenSecret(mConsumer.getTokenSecret());
				Log.v(TAG, "Access token geholt!");
				Log.v(TAG, "AccessToken " + mConnector.accessToken
						+ " AccessSecret " + mConnector.accessSecret);
				return "SUCCESS";
			} catch (OAuthMessageSignerException e) {
				e.printStackTrace();
			} catch (OAuthNotAuthorizedException e) {
				e.printStackTrace();
			} catch (OAuthExpectationFailedException e) {
				e.printStackTrace();
			} catch (OAuthCommunicationException e) {
				e.printStackTrace();
			}
			return "FAIL";
		}

		@Override
		protected void onPostExecute(String result) {
			if (result.equals("SUCCESS")) {
				// Intent intent = new Intent(mContext,
				// AbstractFragmentActivity.class);
				// intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				// | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				// intent.putExtra("frag", CourseNewsFragment.class.getName());
				// mContext.startActivity(intent);

				Intent intent = new Intent(mContext, NewsViewActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_SINGLE_TOP);
				// intent.putExtra("frag", CourseNewsFragment.class.getName());
				mContext.startActivity(intent);
			}
		}

	}
}
