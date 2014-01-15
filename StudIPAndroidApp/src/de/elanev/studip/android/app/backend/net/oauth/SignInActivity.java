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
import android.os.Bundle;
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
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.VolleyError;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import de.elanev.studip.android.app.MainActivity;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.datamodel.Server;
import de.elanev.studip.android.app.backend.datamodel.Servers;
import de.elanev.studip.android.app.backend.db.AbstractContract;
import de.elanev.studip.android.app.backend.db.DatabaseHandler;
import de.elanev.studip.android.app.backend.net.SyncHelper;
import de.elanev.studip.android.app.backend.net.util.NetworkUtils;
import de.elanev.studip.android.app.util.ApiUtils;
import de.elanev.studip.android.app.util.Prefs;
import de.elanev.studip.android.app.util.ServerData;
import de.elanev.studip.android.app.util.StuffUtil;

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

        if (Prefs.getInstance(this).isAppAuthorized()) {
            // Check if unsecured data needs to be cleared
            if (!Prefs.getInstance(this).isSecureStarted()) {
                // Encrypt legacy database
                DatabaseHandler.deleteLegacyDatabase(this);
                // Delete the app database
                getContentResolver().delete(AbstractContract.BASE_CONTENT_URI, null, null);
                // Clear the app preferences
                Prefs.getInstance(this).clearPrefs();
                Prefs.getInstance(this).setSecureStarted();

            } else {
                startNewsActivity();
            }
        }

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
    public static class SignInFragment extends SherlockListFragment implements SyncHelper.SyncHelperCallbacks,
            OAuthConnector.OAuthCallbacks {
        Animation slideUpIn;
        private Context mContext;
        private Server mSelectedServer;
        private ArrayAdapter<Server> mAdapter;
        private boolean mSignInFormVisible = false;
        private boolean mMissingBoxShown = false;
        private View mSignInForm, mProgressInfo, mInfoBoxView;
        private TextView mSyncStatusTextView, mInfoBoxTextView;
        private ListView mListView;
        private OnClickListener mMissingServerOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {

                // Send missing server mails to the developers mail address
                StuffUtil.startFeedback(getActivity(), getString(R.string.feedback_form_developer_mail));
            }
        };

        public static SignInFragment newInstance() {
            SignInFragment fragment = new SignInFragment();

            return fragment;
        }

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

            setHasOptionsMenu(true);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_sign_in, null);

            mProgressInfo = v.findViewById(R.id.progress_info);
            mSignInForm = v.findViewById(R.id.sign_in_form);
            mInfoBoxTextView = (TextView) v.findViewById(R.id.info_box_message);
            mInfoBoxView = v.findViewById(R.id.info_box);
            mListView = (ListView) v.findViewById(android.R.id.list);

            mSyncStatusTextView = (TextView) v.findViewById(R.id.sync_status);
            // Set missing message text from html to get undline.. (stupid)
            ((TextView) v.findViewById(R.id.info_box_message))
                    .setText(Html.fromHtml(getString(R.string.missing_studip_message)));

            return v;
        }

        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            setListAdapter(mAdapter);
            mAdapter.notifyDataSetChanged();
            mListView.setSelector(R.drawable.list_item_selector);


            showLoginForm();
            Button signInButton = (Button) getView().findViewById(R.id.sign_in_button);
            signInButton.setOnClickListener(new OnClickListener() {

                public void onClick(View v) {
                    if (mSelectedServer != null) {
                        authorize();
                    }
                }
            });


            if (!mAdapter.isEmpty() && mSignInFormVisible) {
                mListView.setSelection(0);
                mListView.setItemChecked(0, true);
                mSelectedServer = mAdapter.getItem(0);
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

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            inflater.inflate(R.menu.main, menu);
            menu.removeItem(R.id.menu_sign_out);

            super.onCreateOptionsMenu(menu, inflater);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {

            switch (item.getItemId()) {
                case R.id.menu_feedback:
                    if (mSelectedServer != null) {
                        String contact_mail = mSelectedServer.getContactEmail();
                        StuffUtil.startFeedback(getActivity(), contact_mail);
                    }
                    return true;

                case R.id.menu_about:
                    StuffUtil.startAbout(getActivity());
                    return true;
            }

            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode,
                                     Intent intent) {
            super.onActivityResult(requestCode, resultCode, intent);

            if (resultCode == RESULT_OK) {
                Log.d(TAG, "ACCESS TOKEN FLAG SET");
                OAuthConnector.with(mSelectedServer).getAccessToken(this);
            }
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
            super.onListItemClick(l, v, position, id);

            if (position != ListView.INVALID_POSITION && position <= l.getCount()) {
                if (!ApiUtils.isOverApi11()) {
                    getListView().setItemChecked(position, true);
                }

                mSelectedServer = mAdapter.getItem(position);
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

        private void authorize() {
            if (NetworkUtils.getConnectivityStatus(mContext) == NetworkUtils.NOT_CONNECTED) {
                Toast.makeText(mContext, "Not connected", Toast.LENGTH_LONG).show();
                return;
            }

            hideLoginForm();
            OAuthConnector.with(mSelectedServer).getRequestToken(this);
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
            } catch (JsonParseException e) {
                e.printStackTrace();
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
                    SyncHelper.getInstance(mContext).performContactsSync(this, null);
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
                    SyncHelper.getInstance(mContext).performContactsSync(this, null);
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

        @Override
        public void onRequestTokenReceived(String authUrl) {
            Log.d("Verbinung", "request Token geholt");
            Log.d("sAuthUrl", authUrl);
            int requestCode = 0;
            Intent intent = new Intent(mContext, WebViewActivity.class);
            intent.putExtra("sAuthUrl", authUrl);
            startActivityForResult(intent, requestCode);
        }

        @Override
        public void onAccessTokenReceived(String token, String tokenSecret) {
            mSelectedServer.setAccessToken(token);
            mSelectedServer.setAccessTokenSecret(tokenSecret);
            Prefs.getInstance(mContext).setServer(mSelectedServer);
            Prefs.getInstance(mContext).setAppAuthorized(true);
            performPrefetchSync();
        }

        @Override
        public void onRequestTokenRequestError(OAuthError e) {
            Toast.makeText(mContext, e.errorMessage, Toast.LENGTH_LONG).show();
            showLoginForm();
        }

        @Override
        public void onAccesTokenRequestError(OAuthError e) {
            Toast.makeText(mContext, e.errorMessage, Toast.LENGTH_LONG).show();
            showLoginForm();
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
    }
}
