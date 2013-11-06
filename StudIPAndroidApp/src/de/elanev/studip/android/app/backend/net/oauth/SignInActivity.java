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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.android.volley.VolleyError;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import de.elanev.studip.android.app.BuildConfig;
import de.elanev.studip.android.app.MainActivity;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.datamodel.Server;
import de.elanev.studip.android.app.backend.datamodel.Servers;
import de.elanev.studip.android.app.backend.db.AbstractContract;
import de.elanev.studip.android.app.backend.net.SyncHelper;
import de.elanev.studip.android.app.backend.net.util.NetworkUtils;
import de.elanev.studip.android.app.util.ApiUtils;
import de.elanev.studip.android.app.util.Prefs;
import de.elanev.studip.android.app.util.ServerData;
import de.elanev.studip.android.app.util.StuffUtil;
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
    private static boolean mCoursesSynced = false;
    private static boolean mSemestersSynced = false;
    private static boolean mMessagesSynced = false;
    private static boolean mContactsSynced = false;
    private static boolean mNewsSynced = false;

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

        if (!Prefs.getInstance(this).isSecureStarted()) {
            // Delete the app database
            getContentResolver().delete(AbstractContract.BASE_CONTENT_URI, null, null);
            // Clear the app preferences
            Prefs.getInstance(this).clearPrefs();
            Prefs.getInstance(this).setSecureStarted();
        }

        if (!Prefs.getInstance(this).isFirstStart()) {
            startNewsActivity();
            return;
        }

		/*
         * Clear shared prefs for debugging
		 */
        // Prefs.getInstance(getApplicationContext()).clearPrefs();

        if (savedInstanceState == null) {
            SignInFragment signInFragment = SignInFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_frame, signInFragment, SignInFragment.class.getName())
                    .commit();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getSupportMenuInflater().inflate(R.menu.main, menu);
        menu.removeItem(R.id.menu_sign_out);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(
            com.actionbarsherlock.view.MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_feedback:
                StuffUtil.startFeedback(this);
                return true;

            case R.id.menu_about:
                StuffUtil.startAbout(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Starts the next activity after prefetching
     */
    public void startNewsActivity() {

        Intent intent = new Intent(this, MainActivity.class);

        if (ApiUtils.isOverApi11()) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        } else {
            finish();
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    /**
     * The fragment that is holding the actual sign in and authorization logic.
     *
     * @author joern
     */
    public static class SignInFragment extends ListFragment implements SyncHelper.SyncHelperCallbacks {
        Animation slideUpIn;
        private Context mContext;
        private ArrayAdapter<Server> mAdapter;
        private boolean mSignInFormVisible = false;
        private boolean mMissingBoxShown = false;
        private View mSignInForm, mProgressInfo, mInfoBoxView;
        private TextView mSyncStatusTextView, mInfoBoxTextView;
        private OnClickListener mMissingServerOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO,
                        Uri.fromParts("mailto",
                                getString(R.string.feedback_form_email),
                                null));
                intent.putExtra(Intent.EXTRA_SUBJECT,
                        "Feedback: Missing Stud.IP");
                intent.putExtra(
                        Intent.EXTRA_TEXT,
                        String.format(
                                getString(R.string.feedback_form_message_template),
                                Build.VERSION.SDK_INT,
                                BuildConfig.VERSION_NAME,
                                BuildConfig.VERSION_CODE,
                                BuildConfig.BUILD_TIME));

                startActivity(Intent.createChooser(intent,
                        getString(R.string.feedback_form_action)));
            }
        };

        public static SignInFragment newInstance() {
            SignInFragment fragment = new SignInFragment();

            return fragment;
        }

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
            mAdapter = new ServerAdapter(mContext, res, getItems().getServers());
            slideUpIn = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up_in);

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

            mProgressInfo = v.findViewById(R.id.progress_info);
            mSignInForm = v.findViewById(R.id.sign_in_form);
            mInfoBoxTextView = (TextView) v.findViewById(R.id.info_box_message);
            mInfoBoxView = v.findViewById(R.id.info_box);

            mSyncStatusTextView = (TextView) v.findViewById(R.id.sync_status);
            // Set missing message text from html to get undline.. (stupid)
            ((TextView) v.findViewById(R.id.info_box_message))
                    .setText(Html.fromHtml(getString(R.string.missing_studip_message)));

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
                View selectedView = getListView().getSelectedView();
                if (!mAdapter.isEmpty() && selectedView == null) {
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            getListView().performItemClick(
                                    getListView().getChildAt(0),
                                    0,
                                    getListView().getAdapter().getItemId(0));
                        }
                    });
                }
                showLoginForm();
                Button signInButton = (Button) getView().findViewById(R.id.sign_in_button);
                signInButton.setOnClickListener(new OnClickListener() {

                    public void onClick(View v) {
                        hideLoginForm();
                        connect();
                    }
                });
            }

        }

        @Override
        public void onStart() {
            super.onStart();

            if (!mMissingBoxShown) {
                getView().findViewById(R.id.info_box).startAnimation(slideUpIn);
                mMissingBoxShown = true;
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

            if (position != ListView.INVALID_POSITION && position <= l.getCount()) {
                if (!ApiUtils.isOverApi11()) {
                    getListView().setItemChecked(position, true);
                }

                Server server = mAdapter.getItem(position);

                Prefs.getInstance(mContext).setServer(server);
            }

        }

        /*
         * Hides the progess indicator and sets the login form as visible
         */
        private void showLoginForm() {
            if (!mSignInFormVisible) {
                mSignInForm.setVisibility(View.VISIBLE);
                mProgressInfo.setVisibility(View.GONE);
                mSignInFormVisible = true;
                mInfoBoxTextView.setText(Html.fromHtml(getString(R.string.missing_studip_message)));
                mInfoBoxView.setOnClickListener(mMissingServerOnClickListener);
            }
        }

        /*
         * Hides the login form and sets the progress indicator as visible
         */
        private void hideLoginForm() {
            if (mSignInFormVisible) {
                mSignInForm.setVisibility(View.GONE);
                mProgressInfo.setVisibility(View.VISIBLE);
                mSignInFormVisible = false;
                mInfoBoxTextView.setText(Html.fromHtml(getString(R.string.sync_notice)));
                mInfoBoxView.setOnClickListener(null);
            }
        }

        /*
         * Simply triggers the prefetching at the SyncHelper
         */
        private void performPrefetchSync() {
            SyncHelper.getInstance(mContext).performSemestersSync(this);
            Prefs.getInstance(mContext).setAppStarted();
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
         * Returns the list auf saved servers. This method expects to find a correct formatted
         * servers.json file in the Android assets folder
         */
        private Servers getItems() {
            ObjectMapper mapper = new ObjectMapper();
            Servers servers = null;
            try {
                servers = mapper.readValue(ServerData.serverJson, Servers.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

            // If something went wrong, return an empty array
            if (servers == null)
                servers = new Servers(new Server[0]);

            return servers;

        }

        @Override
        public void onSyncStarted() {

        }

        @Override
        public void onSyncStateChange(int status) {
            switch (status) {
                case SyncHelper.SyncHelperCallbacks.STARTED_SEMESTER_SYNC:
                    mSyncStatusTextView.setText(R.string.syncing_semesters);
                    break;
                case SyncHelper.SyncHelperCallbacks.STARTED_COURSES_SYNC:
                    mSyncStatusTextView.setText(R.string.syncing_courses);
                    break;
                case SyncHelper.SyncHelperCallbacks.STARTED_NEWS_SYNC:
                    mSyncStatusTextView.setText(R.string.syncing_news);
                    break;
                case SyncHelper.SyncHelperCallbacks.STARTED_CONTACTS_SYNC:
                    mSyncStatusTextView.setText(R.string.syncing_contacts);
                    break;
                case SyncHelper.SyncHelperCallbacks.STARTED_MESSAGES_SYNC:
                    mSyncStatusTextView.setText(R.string.syncing_messages);
                    break;
                case SyncHelper.SyncHelperCallbacks.STARTED_USER_SYNC:
                    mSyncStatusTextView.setText(R.string.syncing_users);
                    break;
            }
        }

        @Override
        public void onSyncFinished(int status) {
            switch (status) {
                case SyncHelper.SyncHelperCallbacks.FINISHED_SEMESTER_SYNC:
                    mSemestersSynced = true;
                    SyncHelper.getInstance(mContext).performCoursesSync(this);
                    break;
                case SyncHelper.SyncHelperCallbacks.FINISHED_COURSES_SYNC:
                    mCoursesSynced = true;
                    SyncHelper.getInstance(mContext).performContactsSync(this);
                    break;
                case SyncHelper.SyncHelperCallbacks.FINISHED_NEWS_SYNC:
                    mNewsSynced = true;
                    SyncHelper.getInstance(mContext).performMessagesSync(this);
                    break;
                case SyncHelper.SyncHelperCallbacks.FINISHED_MESSAGES_SYNC:
                    mMessagesSynced = true;
                    break;
                case SyncHelper.SyncHelperCallbacks.FINISHED_CONTACTS_SYNC:
                    mContactsSynced = true;
                    break;
                case SyncHelper.SyncHelperCallbacks.FINISHED_USER_SYNC:
                    mCoursesSynced = false;
                    mContactsSynced = false;
                    mMessagesSynced = false;
                    mNewsSynced = false;
                    if (getActivity() != null) {
                        ((SignInActivity) getActivity()).startNewsActivity();
                    }
                    return;
            }

            if (mContactsSynced && mMessagesSynced && mCoursesSynced && mNewsSynced)
                SyncHelper.getInstance(mContext).performPendingUserSync(this);

        }

        @Override
        public void onSyncError(int status, VolleyError error) {
            switch (status) {
                case SyncHelper.SyncHelperCallbacks.ERROR_SEMESTER_SYNC:
                    SyncHelper.getInstance(mContext).performCoursesSync(this);
                    break;
                case SyncHelper.SyncHelperCallbacks.ERROR_COURSES_SYNC:
                    SyncHelper.getInstance(mContext).performContactsSync(this);
                    break;
                case SyncHelper.SyncHelperCallbacks.ERROR_NEWS_SYNC:
                    SyncHelper.getInstance(mContext).performMessagesSync(this);
                    break;
                case SyncHelper.SyncHelperCallbacks.ERROR_MESSAGES_SYNC:
                case SyncHelper.SyncHelperCallbacks.ERROR_CONTACTS_SYNC:
                    SyncHelper.getInstance(mContext).performPendingUserSync(this);
                    break;
                default:
                    mCoursesSynced = false;
                    mContactsSynced = false;
                    mMessagesSynced = false;
                    mNewsSynced = false;
                    if (getActivity() != null) {
                        ((SignInActivity) getActivity()).startNewsActivity();
                    }
                    return;
            }
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

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                if (convertView == null) {
                    LayoutInflater inflater = ((Activity) context)
                            .getLayoutInflater();
                    convertView = inflater.inflate(textViewResourceId, parent, false);
                }

                Server server = data[position];
                ((TextView) convertView.findViewById(android.R.id.text1))
                        .setText(server.getName());
                convertView.setTag(server);
                return convertView;
            }

            @Override
            public int getCount() {
                return data.length;
            }

            @Override
            public Server getItem(int position) {
                return data[position];
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
