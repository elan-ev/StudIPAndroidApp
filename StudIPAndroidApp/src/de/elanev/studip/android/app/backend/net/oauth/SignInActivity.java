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
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.net.Server;
import de.elanev.studip.android.app.backend.net.SyncHelper;
import de.elanev.studip.android.app.frontend.news.NewsViewActivity;
import de.elanev.studip.android.app.frontend.util.BaseSlidingFragmentActivity;
import de.elanev.studip.android.app.util.Prefs;
import de.elanev.studip.android.app.util.VolleyHttp;

public class SignInActivity extends BaseSlidingFragmentActivity {

	/**
	 * @param titleRes
	 */
	public SignInActivity() {
		super(R.string.Authorize);
	}

	private static final String TAG = SignInActivity.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setContentView(R.layout.content_frame);
		init(this);
		FragmentManager fm = getSupportFragmentManager();
		SignInFragment frag = null;
		if (savedInstanceState == null) {
			frag = (SignInFragment) SignInFragment.instantiate(this,
					SignInFragment.class.getName());
		} else {
			frag = (SignInFragment) fm.findFragmentByTag(SignInFragment.class
					.getName());
		}
		fm.beginTransaction()
				.add(R.id.content_frame, frag, SignInFragment.class.getName())
				.commit();
	}

	/**
	 * Initialize the app
	 */
	private void init(Context context) {
		/*
		 * Clear shared prefs for debugging
		 */
		// Prefs.getInstance(getApplicationContext()).clearPrefs();
		VolleyHttp.init(context);
	}

	/**
	 * @author joern
	 * 
	 */
	public static class SignInFragment extends SherlockListFragment {
		private Context mContext;
		private ArrayAdapter<Server> mAdapter;
		private boolean mSignInFormVisible = false;
		private ProgressBar mProgressBar;
		private View mSignInForm;

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
		 */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			mContext = getActivity();

			int res = R.layout.list_item_single_text;
			if (!isOverAPI11()) {
				res = android.R.layout.simple_list_item_checked;
			}
			mAdapter = new ServerAdapter(mContext, res, getItems());

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.support.v4.app.ListFragment#onCreateView(android.view.
		 * LayoutInflater, android.view.ViewGroup, android.os.Bundle)
		 */
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.fragment_sign_in, null);

			mProgressBar = (ProgressBar) v
					.findViewById(R.id.sign_in_progressbar);
			mSignInForm = v.findViewById(R.id.sign_in_form);

			return v;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
		 */
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			setListAdapter(mAdapter);
			mAdapter.notifyDataSetChanged();
			getListView().setSelector(R.drawable.list_item_selector);

			if (Prefs.getInstance(mContext).getServer() != null
					&& Prefs.getInstance(mContext).isAppAuthorized()) {
				connect();
			} else {
				showLoginForm();
				Button signInButton = (Button) getView().findViewById(
						R.id.sign_in_button);
				signInButton.setOnClickListener(new OnClickListener() {
					// @Override
					public void onClick(View v) {
						hideLoginForm();
						connect();
					}
				});
			}

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.support.v4.app.Fragment#onResume()
		 */
		@Override
		public void onResume() {
			super.onResume();

			if (Prefs.getInstance(mContext).getServer() != null
					|| Prefs.getInstance(mContext).isAppAuthorized()) {
				showLoginForm();
			}

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.support.v4.app.Fragment#onActivityResult(int, int,
		 * android.content.Intent)
		 */
		@Override
		public void onActivityResult(int requestCode, int resultCode,
				Intent intent) {
			super.onActivityResult(requestCode, resultCode, intent);

			if (resultCode == RESULT_OK) {
				Log.d(TAG, "ACCESS TOKEN FLAG SET");
				Bundle extras = intent.getExtras();
				String token = extras.getString("token");
				String tokenSecret = extras.getString("tokenSecret");
				Log.d(TAG, token + ", " + tokenSecret);
				Log.i(TAG, OAuthConnector.getConsumer().getToken() + " "
						+ OAuthConnector.getConsumer().getTokenSecret());
				new AccessTokenTask().execute();
			}
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.support.v4.app.ListFragment#onListItemClick(android.widget
		 * .ListView , android.view.View, int, long)
		 */
		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			super.onListItemClick(l, v, position, id);

			if (!isOverAPI11()) {
				getListView().setItemChecked(position, true);
			}

			Prefs.getInstance(mContext).setServer((Server) v.getTag());

		}

		private void showLoginForm() {
			if (!mSignInFormVisible) {
				mSignInForm.setVisibility(View.VISIBLE);
				mProgressBar.setVisibility(View.GONE);
				mSignInFormVisible = true;
			}
		}

		private void hideLoginForm() {
			if (mSignInFormVisible) {
				mSignInForm.setVisibility(View.GONE);
				mProgressBar.setVisibility(View.VISIBLE);
				mSignInFormVisible = false;
			}
		}

		private void performPrefetchSync() {
			if (SyncHelper.getInstance(mContext).prefetch()) {
				Intent intent = new Intent(mContext, NewsViewActivity.class);

				if (isOverAPI11()) {
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				} else {
					getActivity().finish();
				}
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				startActivity(intent);
			}
		}

		private void connect() {
			if (Prefs.getInstance(mContext).getServer() != null) {
				OAuthConnector.init(Prefs.getInstance(mContext).getServer());

				if (!Prefs.getInstance(mContext).isAppAuthorized()) {
					new RequestTokenTask().execute();

				} else {

					String accessToken = Prefs.getInstance(mContext)
							.getAccessToken();
					String accessSecret = Prefs.getInstance(mContext)
							.getAccessTokenSecret();

					if (accessToken != null && accessSecret != null) {

						OAuthConnector
								.setAccessToken(accessToken, accessSecret);

						performPrefetchSync();
					} else {
						showLoginForm();
					}
				}
			} else {
				showLoginForm();
			}

		}

		private boolean isOverAPI11() {
			return (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB);
		}

		private Server[] getItems() {
			/*
			 * WARNING: you need your own TempServerDeclares Class in the
			 * de.elanev.studip.android.app.util package see:
			 * de.elanev.studip.android.app.util.TempServerDeclaresExample
			 */
			return TempServerDeclares.servers;
		}

		private class ServerAdapter extends ArrayAdapter<Server> {
			private Context context;
			private int textViewResourceId;
			private Server[] data = null;

			/**
			 * @param context
			 * @param textViewResourceId
			 */
			public ServerAdapter(Context context, int textViewResourceId,
					Server[] data) {
				super(context, textViewResourceId);
				this.context = context;
				this.textViewResourceId = textViewResourceId;
				this.data = data;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see android.widget.ArrayAdapter#getView(int, android.view.View,
			 * android.view.ViewGroup)
			 */
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {

				if (convertView == null) {
					LayoutInflater inflater = ((Activity) context)
							.getLayoutInflater();
					convertView = inflater.inflate(textViewResourceId, parent,
							false);

				}
				Server server = data[position];
				((TextView) convertView.findViewById(android.R.id.text1))
						.setText(server.NAME);
				convertView.setTag(server);
				return convertView;
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see android.widget.ArrayAdapter#getCount()
			 */
			@Override
			public int getCount() {
				return data.length;
			}
		}

		public class RequestTokenTask extends
				AsyncTask<String, Integer, String> {

			@Override
			protected void onPostExecute(String result) {
				super.onPostExecute(result);

				if (result != null) {
					Log.d("Verbinung", "request Token geholt");
					Log.d("sAuthUrl", result);
					int requestCode = 0;
					Intent intent = new Intent(mContext, WebViewActivity.class);
					intent.putExtra("sAuthUrl", result);
					startActivityForResult(intent, requestCode);
				} else {
					Toast.makeText(mContext,
							getString(R.string.something_went_wrong),
							Toast.LENGTH_LONG).show();
				}
			}

			@Override
			protected String doInBackground(String... params) {
				try {
					return OAuthConnector.getProvider().retrieveRequestToken(
							OAuthConnector.getConsumer(), "");
				} catch (OAuthMessageSignerException e) {
					e.printStackTrace();
				} catch (OAuthNotAuthorizedException e) {
					e.printStackTrace();
				} catch (OAuthExpectationFailedException e) {
					e.printStackTrace();
				} catch (OAuthCommunicationException e) {
					e.printStackTrace();
				}

				return null;
			}
		}

		public class AccessTokenTask extends AsyncTask<String, Integer, String> {

			@Override
			protected String doInBackground(String... arg0) {
				OAuthConsumer consumer = OAuthConnector.getConsumer();
				OAuthProvider provider = OAuthConnector.getProvider();

				Log.i(TAG,
						consumer.getToken() + " " + consumer.getTokenSecret());

				try {
					provider.retrieveAccessToken(consumer, null);
					Prefs.getInstance(mContext).setAccessToken(
							consumer.getToken());
					Prefs.getInstance(mContext).setAccessTokenSecret(
							consumer.getTokenSecret());

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

					performPrefetchSync();
				} else {
					Toast.makeText(mContext,
							getString(R.string.something_went_wrong),
							Toast.LENGTH_LONG).show();

				}
			}
		}

	}
}