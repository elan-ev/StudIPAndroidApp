/*
 * Copyright (c) 2014 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.backend.net.oauth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import de.elanev.studip.android.app.MainActivity;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.StudIPApplication;
import de.elanev.studip.android.app.backend.datamodel.Server;
import de.elanev.studip.android.app.backend.datamodel.Servers;
import de.elanev.studip.android.app.backend.datamodel.User;
import de.elanev.studip.android.app.backend.db.AbstractContract;
import de.elanev.studip.android.app.backend.db.DatabaseHandler;
import de.elanev.studip.android.app.backend.net.SyncHelper;
import de.elanev.studip.android.app.backend.net.util.JacksonRequest;
import de.elanev.studip.android.app.backend.net.util.NetworkUtils;
import de.elanev.studip.android.app.util.ApiUtils;
import de.elanev.studip.android.app.util.Prefs;
import de.elanev.studip.android.app.util.ServerData;
import de.elanev.studip.android.app.util.StuffUtil;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

/**
 * Created by joern on 03.05.14.
 */

/**
 * The fragment that is holding the actual sign in and authorization logic.
 *
 * @author joern
 */
public class SignInFragment extends SherlockListFragment implements SyncHelper.SyncHelperCallbacks, OAuthConnector.OAuthCallbacks {
  public static final String REQEUEST_TOKEN_RECEIVED = "reqeuestTokenReceived";
  public static final String AUTH_CANCELED = "authCanceled";
  private static final String TAG = SignInFragment.class.getSimpleName();
  private boolean mCoursesSynced = false;
  private boolean mSemestersSynced = false;
  private boolean mMessagesSynced = false;
  private boolean mContactsSynced = false;
  private boolean mNewsSynced = false;
  private boolean mUsersSynced = false;
  private boolean mInstitutesSynced = false;
  private Animation slideUpIn;
  private Context mContext;
  private Server mSelectedServer;
  private ArrayAdapter<Server> mAdapter;
  private boolean mSignInFormVisible = false;
  private boolean mMissingBoxShown = false;
  private View mSignInForm;
  private View mProgressInfo;
  private View mInfoBoxView;
  private TextView mSyncStatusTextView;
  private TextView mInfoBoxTextView;
  private ListView mListView;
  private boolean mRequestTokenReceived = false;

  private View.OnClickListener mMissingServerOnClickListener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      String email = getString(R.string.feedback_form_developer_mail);
      StuffUtil.startFeedback(getActivity(), email);
    }
  };
  private OnRequestTokenReceived mCallbacks;

  public SignInFragment() {}

  /**
   * Instantiates a new SignInFragment
   *
   * @return A new SignInFragment instance
   */
  public static SignInFragment newInstance() {
    SignInFragment fragment = new SignInFragment();

    return fragment;
  }

  /**
   * Instantiates a new SignInFragment
   *
   * @return A new SignInFragment instance
   */
  public static SignInFragment newInstance(Bundle args) {
    SignInFragment fragment = new SignInFragment();
    fragment.setArguments(args);
    return fragment;
  }


  @Override public View onCreateView(LayoutInflater inflater,
      ViewGroup container,
      Bundle savedInstanceState) {
    Log.i(TAG, "onCreateView Called!");
    View v = inflater.inflate(R.layout.fragment_sign_in, null);

    mProgressInfo = v.findViewById(R.id.progress_info);
    mSignInForm = v.findViewById(R.id.sign_in_form);
    mListView = (ListView) v.findViewById(android.R.id.list);
    mSyncStatusTextView = (TextView) v.findViewById(R.id.sync_status);
    mInfoBoxView = v.findViewById(R.id.info_box);
    mInfoBoxTextView = (TextView) v.findViewById(R.id.info_box_message);
    return v;
  }

  @Override public void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);

    if (position != ListView.INVALID_POSITION && position <= l.getCount()) {
      if (!ApiUtils.isOverApi11()) {
        getListView().setItemChecked(position, true);
      }

      mSelectedServer = mAdapter.getItem(position);
    }

  }

  @Override public void onCreate(Bundle savedInstanceState) {
    Log.i(TAG, "onCreate Called!");
    super.onCreate(savedInstanceState);

    mContext = getActivity();
    Bundle args = getArguments();
    if (args != null) {
      boolean resetFlag = args.getBoolean(AUTH_CANCELED);
      if (resetFlag) {
        resetSignInActivityState();
      }
    }
    int res = R.layout.list_item_single_text;
    if (!ApiUtils.isOverApi11()) {
      res = android.R.layout.simple_list_item_checked;
    }
    mAdapter = new ServerAdapter(mContext, res, getItems().getServers());
    slideUpIn = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up_in);
    setHasOptionsMenu(true);
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    Log.i(TAG, "onActivityCreated Called!");
    super.onActivityCreated(savedInstanceState);
    getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    getSherlockActivity().getSupportActionBar().setHomeButtonEnabled(false);
    getSherlockActivity().setTitle(R.string.app_name);
    hideLoginForm();
    if (savedInstanceState != null) {
      mRequestTokenReceived = savedInstanceState.getBoolean(REQEUEST_TOKEN_RECEIVED);
    }

    Prefs prefs = Prefs.getInstance(getActivity());
    // Check if unsecured server credentials exist
    if (prefs.legacyDataExists()) {
      destroyInsecureCredentials();

    } else if (prefs.isAppAuthorized()) {
      Log.i(TAG, "Valid secured credentials found");
      if (prefs.isAppSynced()) {
        Log.i(TAG, "App synced starting..");
        startMainActivity();
        return;
      } else {
        performPrefetchSync();
        return;
      }

    } else if (mRequestTokenReceived) {
      OAuthConnector.with(prefs.getServer()).getAccessToken(this);
      return;
    }

    setListAdapter(mAdapter);
    mAdapter.notifyDataSetChanged();
    mListView.setSelector(R.drawable.list_item_selector);


    showLoginForm();
    Button signInButton = (Button) getView().findViewById(R.id.sign_in_button);
    signInButton.setOnClickListener(new View.OnClickListener() {

      public void onClick(View v) {
        if (mSelectedServer != null) {
          Prefs.getInstance(getActivity()).setServer(mSelectedServer);
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

  @Override public void onStart() {
    super.onStart();

    if (!mMissingBoxShown) {
      mInfoBoxView.startAnimation(slideUpIn);
      mMissingBoxShown = true;
    }
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putBoolean(REQEUEST_TOKEN_RECEIVED, mRequestTokenReceived);
  }

  /* Hides the login form and sets the progress indicator as visible */
  private void hideLoginForm() {
    if (getActivity() != null && isAdded()) {
      mSignInForm.setVisibility(View.GONE);
      mProgressInfo.setVisibility(View.VISIBLE);
      mSignInFormVisible = false;
      mInfoBoxView.setOnClickListener(null);
      mInfoBoxTextView.setText(R.string.sync_notice);
    }
  }

  private void destroyInsecureCredentials() {
    Log.i(TAG, "Insecure credentials found, deleting...");
    // Encrypt legacy database
    DatabaseHandler.deleteLegacyDatabase(getActivity());
    // Delete the app database
    getActivity().getContentResolver().delete(AbstractContract.BASE_CONTENT_URI, null, null);
    // Clear the app preferences
    Prefs.getInstance(getActivity()).clearPrefs();
  }

  /**
   * Starts the next activity after prefetching
   */
  public void startMainActivity() {
    Intent intent = new Intent(getActivity(), MainActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(intent);

    Log.i(TAG, "Starting news Activity...");
    if (!ApiUtils.isOverApi11()) {
      getActivity().finish();
    }
  }

  /* Simply triggers the prefetching at the SyncHelper */
  private void performPrefetchSync() {
    String userId = Prefs.getInstance(mContext).getUserId();
    SyncHelper.getInstance(mContext).requestInstitutesForUserID(userId, this);
  }

  /* Hides the progess indicator and sets the login form as visible */
  private void showLoginForm() {
    if (getActivity() != null && isAdded()) {
      mSignInForm.setVisibility(View.VISIBLE);
      mProgressInfo.setVisibility(View.GONE);
      mSignInFormVisible = true;
      mInfoBoxView.setOnClickListener(mMissingServerOnClickListener);
      mInfoBoxTextView.setText(R.string.missing_studip_message);
    }
  }

  private void authorize() {
    if (NetworkUtils.getConnectivityStatus(mContext) == NetworkUtils.NOT_CONNECTED) {
      Toast.makeText(mContext, "Not connected", Toast.LENGTH_LONG).show();
      return;
    }
    hideLoginForm();
    getActivity().getContentResolver().delete(AbstractContract.BASE_CONTENT_URI, null, null);
    Server server = Prefs.getInstance(mContext).getServer();
    OAuthConnector.with(server).getRequestToken(this);
  }

  private void resetSignInActivityState() {
    //Cancel all pending network requests
    StudIPApplication.getInstance().cancelAllPendingRequests(SyncHelper.TAG);

    // Resetting the SyncHelper
    SyncHelper.getInstance(mContext).resetSyncHelper();

    // Clear the app preferences
    Prefs.getInstance(mContext).clearPrefs();

    // Delete the app database
    mContext.getContentResolver() //
        .delete(AbstractContract.BASE_CONTENT_URI, null, null);

    mCoursesSynced = false;
    mContactsSynced = false;
    mMessagesSynced = false;
    mNewsSynced = false;
    mUsersSynced = false;
    mSemestersSynced = false;
    mInstitutesSynced = false;
    mRequestTokenReceived = false;
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
    if (servers == null) servers = new Servers(new Server[0]);

    return servers;
  }

  @Override public void onAttach(Activity activity) {
    Log.i(TAG, "onAttach Called!");
    super.onAttach(activity);
    try {
      mCallbacks = (OnRequestTokenReceived) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(activity.toString() + "must implement OnWebViewAuthListener");
    }
  }

  @Override public void onDetach() {
    Log.i(TAG, "onDetach Called!");
    super.onDetach();
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.main, menu);
    menu.removeItem(R.id.menu_sign_out);

    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {

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
      default:
    }

    return super.onOptionsItemSelected(item);
  }

  @Override public void onSyncStarted() {}

  @Override public void onSyncStateChange(int status) {
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
      case SyncHelper.SyncHelperCallbacks.STARTED_INSTITUTES_SYNC:
        mSyncStatusTextView.setText(R.string.syncing_institutes);
        break;
    }
  }

  @Override public void onSyncFinished(int status) {
    switch (status) {
      case SyncHelper.SyncHelperCallbacks.FINISHED_SEMESTER_SYNC:
        mSemestersSynced = true;
        SyncHelper.getInstance(mContext).performCoursesSync(this);
        break;
      case SyncHelper.SyncHelperCallbacks.FINISHED_COURSES_SYNC:
        mCoursesSynced = true;
        SyncHelper.getInstance(mContext).performNewsSync(this);
        break;
      case SyncHelper.SyncHelperCallbacks.FINISHED_NEWS_SYNC:
        mNewsSynced = true;
        SyncHelper.getInstance(mContext).performMessagesSync(this);
        break;
      case SyncHelper.SyncHelperCallbacks.FINISHED_MESSAGES_SYNC:
        mMessagesSynced = true;
        SyncHelper.getInstance(mContext).performContactsSync(this, null);
        break;
      case SyncHelper.SyncHelperCallbacks.FINISHED_CONTACTS_SYNC:
        mContactsSynced = true;
        mUsersSynced = true;
        //          SyncHelper.getInstance(mContext).performPendingUserSync(this);
        break;
      case SyncHelper.SyncHelperCallbacks.FINISHED_USER_SYNC:
        break;
      case SyncHelper.SyncHelperCallbacks.FINISHED_INSTITUTES_SYNC:
        mInstitutesSynced = true;
        SyncHelper.getInstance(mContext).performSemestersSync(this);
        break;
    }

    if (mContactsSynced && mMessagesSynced && mCoursesSynced && mNewsSynced && mUsersSynced &&
        mSemestersSynced && mInstitutesSynced) {

      mCoursesSynced = false;
      mContactsSynced = false;
      mMessagesSynced = false;
      mNewsSynced = false;
      mUsersSynced = false;
      mSemestersSynced = false;
      mInstitutesSynced = false;

      Prefs.getInstance(mContext).setAppSynced();
      if (getActivity() != null) {
        startMainActivity();
      }
      return;
    }
  }

  @Override public void onSyncError(int status, VolleyError error) {
    Log.wtf(TAG, "Sync error " + error.getLocalizedMessage());

    if (getActivity() == null || error.networkResponse.statusCode == 404) return;

    String genericErrorMessage = getString(R.string.sync_error_generic);
    String finalErrorMessage;
    switch (status) {
      case SyncHelper.SyncHelperCallbacks.ERROR_CONTACTS_SYNC:
        finalErrorMessage = String.format(genericErrorMessage, getString(R.string.contacts));
        break;
      case SyncHelper.SyncHelperCallbacks.ERROR_USER_SYNC:
        finalErrorMessage = String.format(genericErrorMessage,
            getString(R.string.user_profile_data));
        break;
      case SyncHelper.SyncHelperCallbacks.ERROR_SEMESTER_SYNC:
        finalErrorMessage = String.format(genericErrorMessage, getString(R.string.semesters));
        break;
      case SyncHelper.SyncHelperCallbacks.ERROR_NEWS_SYNC:
        finalErrorMessage = String.format(genericErrorMessage, getString(R.string.news));
        break;
      case SyncHelper.SyncHelperCallbacks.ERROR_COURSES_SYNC:
        finalErrorMessage = String.format(genericErrorMessage, getString(R.string.courses));
        break;
      case SyncHelper.SyncHelperCallbacks.ERROR_INSTITUTES_SYNC:
        finalErrorMessage = String.format(genericErrorMessage, getString(R.string.institutes));
        break;
      case SyncHelper.SyncHelperCallbacks.ERROR_MESSAGES_SYNC:
        finalErrorMessage = String.format(genericErrorMessage, getString(R.string.messages));
        break;
      default:
        finalErrorMessage = getString(R.string.sync_error_default);
    }

    StringBuilder builder = new StringBuilder(finalErrorMessage);

    String errMesg = error.getLocalizedMessage();
    if (errMesg != null) {
      builder.append(errMesg);
    } else {
      builder.append("HTTP-Code: ").append(error.networkResponse.statusCode);
    }

    Toast.makeText(mContext, builder.toString(), Toast.LENGTH_LONG).show();

    resetSignInActivityState();
    showLoginForm();
  }

  @Override public void onRequestTokenReceived(String authUrl) {
    mRequestTokenReceived = true;
    mCallbacks.requestTokenReceived(authUrl);
  }

  @Override public void onAccessTokenReceived(String token, String tokenSecret) {
    //DEBUG Testing old credential migration behavior
    //Prefs.getInstance(mContext).simulateOldPrefs(mSelectedServer);
    //getActivity().finish();

    mSelectedServer.setAccessToken(token);
    mSelectedServer.setAccessTokenSecret(tokenSecret);
    Prefs.getInstance(mContext).setServer(mSelectedServer);

    String url = String.format(getString(R.string.restip_user), mSelectedServer.getApiUrl());

    JacksonRequest<User> request = new JacksonRequest<User>(url,
        User.class,
        null,
        new Response.Listener<User>() {
          @Override public void onResponse(User response) {
            Prefs.getInstance(getActivity()).setUserId(response.user_id);
            performPrefetchSync();
          }
        },
        new Response.ErrorListener() {
          @Override public void onErrorResponse(VolleyError error) {
            Log.wtf(TAG, "Error requesting user details: " + error.getLocalizedMessage() +
                "ErrorCode: " + error.networkResponse.statusCode);

            // Build error message
            String genericMessage = getString(R.string.sync_error_generic);
            String errorPosition = getString(R.string.your_user_data);
            StringBuilder sb = new StringBuilder(String.format(genericMessage, errorPosition));
            String err = error.getLocalizedMessage();
            if (err != null) {
              sb.append(err);
            } else {
              sb.append("ErrorCode: ").append(error.networkResponse.statusCode);
            }


            // Toast error and display login form
            Toast.makeText(getActivity(), sb.toString(), Toast.LENGTH_LONG).show();
            resetSignInActivityState();
            showLoginForm();
          }
        },
        Request.Method.GET
    );

    try {
      OAuthConnector.with(mSelectedServer).sign(request);
      StudIPApplication.getInstance().addToRequestQueue(request, TAG);
    } catch (OAuthCommunicationException e) {
      e.printStackTrace();
    } catch (OAuthExpectationFailedException e) {
      e.printStackTrace();
    } catch (OAuthMessageSignerException e) {
      e.printStackTrace();
    } catch (OAuthNotAuthorizedException e) {
      e.printStackTrace();
    }

  }

  @Override public void onRequestTokenRequestError(OAuthError e) {
    Toast.makeText(mContext, e.errorMessage, Toast.LENGTH_LONG).show();
    showLoginForm();
  }

  @Override public void onAccesTokenRequestError(OAuthError e) {
    Toast.makeText(mContext, e.errorMessage, Toast.LENGTH_LONG).show();
    showLoginForm();
  }

  public interface OnRequestTokenReceived {
    public void requestTokenReceived(String authUrl);
  }

  /* Array adapter class which holds and displays the saved servers */
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
    public ServerAdapter(Context context, int textViewResourceId, Server[] data) {
      super(context, textViewResourceId);
      this.context = context;
      this.textViewResourceId = textViewResourceId;
      this.data = data;
    }

    @Override public int getCount() {
      return data.length;
    }

    @Override public Server getItem(int position) {
      return data[position];
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
      if (convertView == null) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        convertView = inflater.inflate(textViewResourceId, parent, false);
      }

      Server server = data[position];
      ((TextView) convertView.findViewById(android.R.id.text1)).setText(server.getName());
      convertView.setTag(server);
      return convertView;
    }
  }
}
