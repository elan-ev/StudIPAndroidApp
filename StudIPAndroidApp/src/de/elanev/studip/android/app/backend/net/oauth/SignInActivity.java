/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.net.oauth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
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

import com.actionbarsherlock.app.SherlockFragmentActivity;

import de.elanev.studip.android.app.MainActivity;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.net.Server;
import de.elanev.studip.android.app.backend.net.SyncHelper;
import de.elanev.studip.android.app.backend.net.util.NetworkUtils;
import de.elanev.studip.android.app.util.ApiUtils;
import de.elanev.studip.android.app.util.Prefs;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

/**
 * Activity for handling the full sign in and authorization process. It triggers
 * the prefetching after authorization.
 *
 * @author joern
 */
public class SignInActivity extends SherlockFragmentActivity {

    private static final String TAG = SignInActivity.class.getSimpleName();

    /*
     * (non-Javadoc)
     *
     * @see
     * de.elanev.studip.android.app.frontend.util.BaseSlidingFragmentActivity
     * #onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        this.setContentView(R.layout.content_frame);

		/*
         * Clear shared prefs for debugging
		 */
        // Prefs.getInstance(getApplicationContext()).clearPrefs();

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

        getSupportActionBar().hide();
    }

    /*
     * Prevent the user from pressing the back button
     */
    @Override
    public void onBackPressed() {
        return;
    }

    /**
     * The fragment that is holding the actual sign in and authorization logic.
     *
     * @author joern
     */
    public static class SignInFragment extends ListFragment {
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
            if (!ApiUtils.isOverApi11()) {
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
                performPrefetchSync();

            } else {
                showLoginForm();
                Button signInButton = (Button) getView().findViewById(
                        R.id.sign_in_button);
                signInButton.setOnClickListener(new OnClickListener() {

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

            if (!ApiUtils.isOverApi11()) {
                getListView().setItemChecked(position, true);
            }

            Prefs.getInstance(mContext).setServer((Server) v.getTag());

        }

        /*
         * Hides the progess indicator and sets the login form as visible
         */
        private void showLoginForm() {
            if (!mSignInFormVisible) {
                mSignInForm.setVisibility(View.VISIBLE);
                mProgressBar.setVisibility(View.GONE);
                mSignInFormVisible = true;
            }
        }

        /*
         * Hides the login form and sets the progress indicator as visible
         */
        private void hideLoginForm() {
            if (mSignInFormVisible) {
                mSignInForm.setVisibility(View.GONE);
                mProgressBar.setVisibility(View.VISIBLE);
                mSignInFormVisible = false;
            }
        }

        /*
         * Simply triggers the prefetching at the SyncHelper
         */
        private void performPrefetchSync() {
            SyncHelper.getInstance(mContext).prefetch(this);
        }

        /**
         * Starts the next activity after prefetching
         */
        public void startNewsActivity() {
            if (getActivity() == null)
                return;

            Intent intent = new Intent(mContext, MainActivity.class);

            if (ApiUtils.isOverApi11()) {
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            } else {
                getActivity().finish();
            }

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
        }

        /*
         * Checks if the server is set correctly, if the device is online and
         * triggers the request of an new access token pair or sets it in the
         * OAuthConnector if it's already saved.
         */
        private boolean connect() {
            if (Prefs.getInstance(mContext).getServer() != null) {

                OAuthConnector.init(Prefs.getInstance(mContext).getServer());

                if (NetworkUtils.getConnectivityStatus(mContext) != NetworkUtils.NOT_CONNECTED) {

                    if (!Prefs.getInstance(mContext).isAppAuthorized()) {
                        new RequestTokenTask().execute();

                    } else {

                        String accessToken = Prefs.getInstance(mContext)
                                .getAccessToken();
                        String accessSecret = Prefs.getInstance(mContext)
                                .getAccessTokenSecret();

                        if (accessToken != null && accessSecret != null) {
                            OAuthConnector.setAccessToken(accessToken,
                                    accessSecret);

                            return true;
                        }
                    }
                } else {
                    Toast.makeText(mContext, "Not connected", Toast.LENGTH_LONG)
                            .show();
                    showLoginForm();
                }
            } else {
                showLoginForm();
            }
            return false;

        }

        /*
         * Returns the list auf saved servers
         */
        private Server[] getItems() {
			/*
			 * WARNING: you need your own TempServerDeclares Class in the
			 * de.elanev.studip.android.app.util package see:
			 * de.elanev.studip.android.app.util.TempServerDeclaresExample
			 */
            return TempServerDeclares.servers;
        }

        /*
         * Array adapter class which holds and displays the saved servers
         */
        private class ServerAdapter extends ArrayAdapter<Server> {
            private Context context;
            private int textViewResourceId;
            private Server[] data = null;

            /**
             * Public constructor which takes the context, viewRessource and
             * server data and initializes it.
             *
             * @param context            the execution context
             * @param textViewResourceId the view resource id for displaying the servers
             * @param data               an array with servers
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

        /*
         * AsyncTask for requesting the request token from the API
         */
        private class RequestTokenTask extends
                AsyncTask<String, Integer, String> {

            /*
             * (non-Javadoc)
             *
             * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
             */
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

            /*
             * (non-Javadoc)
             *
             * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
             */
            @Override
            protected void onPostExecute(String result) {
                if (getActivity() == null)
                    return;

                // If the RequestToken request was successful, show the
                // permission prompt
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
                    showLoginForm();
                }
            }
        }

        /*
         * AsyncTask for requesting the access token from the API
         */
        private class AccessTokenTask extends
                AsyncTask<String, Integer, String> {

            /*
             * (non-Javadoc)
             *
             * @see android.os.AsyncTask#onPreExecute()
             */
            @Override
            protected void onPreExecute() {
                hideLoginForm();
            }

            /*
             * (non-Javadoc)
             *
             * @see android.os.AsyncTask#doInBackground(java.lang.Object[])
             */
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

            /*
             * (non-Javadoc)
             *
             * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
             */
            @Override
            protected void onPostExecute(String result) {
                if (getActivity() == null)
                    return;

                // If the access token was requested successfully, start the
                // prefetching
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
