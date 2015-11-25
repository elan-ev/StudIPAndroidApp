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
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import de.elanev.studip.android.app.MainActivity;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.StudIPApplication;
import de.elanev.studip.android.app.backend.datamodel.Server;
import de.elanev.studip.android.app.backend.datamodel.Servers;
import de.elanev.studip.android.app.backend.datamodel.User;
import de.elanev.studip.android.app.backend.db.AbstractContract;
import de.elanev.studip.android.app.backend.db.DatabaseHandler;
import de.elanev.studip.android.app.backend.net.SyncHelper;
import de.elanev.studip.android.app.backend.net.util.NetworkUtils;
import de.elanev.studip.android.app.frontend.news.NewsActivity;
import de.elanev.studip.android.app.util.ApiUtils;
import de.elanev.studip.android.app.util.Prefs;
import de.elanev.studip.android.app.util.ServerData;
import de.elanev.studip.android.app.util.StuffUtil;

/**
 * The fragment that is holding the actual sign in and authorization logic.
 *
 * @author joern
 */
public class SignInFragment extends ListFragment implements SyncHelper.SyncHelperCallbacks,
    OAuthConnector.OAuthCallbacks {
  public static final String REQUEST_TOKEN_RECEIVED = "requestTokenReceived";
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
  private View mProgressInfo;
  private View mInfoBoxView;
  private TextView mSyncStatusTextView;
  private TextView mInfoBoxTextView;
  private ListView mListView;
  private boolean mRequestTokenReceived = false;

  private View.OnClickListener mMissingServerOnClickListener = new View.OnClickListener() {
    @Override public void onClick(View v) {
      Server s = new Server();
      s.setName("Stud.IP mobil developer");
      s.setContactEmail(getString(R.string.feedback_form_developer_mail));
      StuffUtil.startFeedback(getActivity(), s);
    }
  };

  private OnRequestTokenReceived mCallbacks;
  private ImageView mLogoImageView;

  public SignInFragment() {}

  /**
   * Instantiates a new SignInFragment.
   *
   * @return A new SignInFragment instance
   */
  public static SignInFragment newInstance() {
    return new SignInFragment();
  }

  /**
   * Instantiates a new SignInFragment.
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
    View v = inflater.inflate(R.layout.fragment_sign_in, container, false);

    mProgressInfo = v.findViewById(R.id.progress_info);
    mListView = (ListView) v.findViewById(android.R.id.list);
    mSyncStatusTextView = (TextView) v.findViewById(R.id.sync_status);
    mInfoBoxView = v.findViewById(R.id.info_box);
    mInfoBoxTextView = (TextView) v.findViewById(R.id.info_box_message);
    mLogoImageView = (ImageView) v.findViewById(R.id.sign_in_imageview);
    return v;
  }

  @Override public void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);

    if (position != ListView.INVALID_POSITION && position <= l.getCount()) {
      if (!ApiUtils.isOverApi11()) {
        getListView().setItemChecked(position, true);
      }

      mSelectedServer = mAdapter.getItem(position);
      if (mSelectedServer != null) {
        Prefs.getInstance(getActivity()).setServer(mSelectedServer);
        authorize(mSelectedServer);
      }
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
    ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(false);
    getActivity().setTitle(R.string.app_name);

    Picasso.with(getActivity())
        .load(R.drawable.logo)
        .config(Bitmap.Config.RGB_565)
        .fit()
        .centerCrop()
        .noFade()
        .into(mLogoImageView);

    hideLoginForm();
    if (savedInstanceState != null) {
      mRequestTokenReceived = savedInstanceState.getBoolean(REQUEST_TOKEN_RECEIVED);
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
    mListView.setTextFilterEnabled(true);

    showLoginForm();
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
    outState.putBoolean(REQUEST_TOKEN_RECEIVED, mRequestTokenReceived);
  }

  /* Hides the login form and sets the progress indicator as visible */
  private void hideLoginForm() {
    if (getActivity() != null && isAdded()) {
      mListView.setVisibility(View.GONE);
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
   * Starts the next activity after prefetching.
   */
  public void startMainActivity() {
    Intent intent = new Intent(getActivity(), NewsActivity.class);
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
    User currentUser = User.fromJson(Prefs.getInstance(mContext).getUserInfo());

    if (currentUser != null) {
      SyncHelper.getInstance(mContext)
          .requestInstitutesForUserID(currentUser.userId, this);
      SyncHelper.getInstance(mContext)
          .requestApiRoutes(this);
    } else {
      showLoginForm();
    }

  }

  /* Hides the progess indicator and sets the login form as visible */
  private void showLoginForm() {
    if (getActivity() != null && isAdded()) {
      mListView.setVisibility(View.VISIBLE);
      mProgressInfo.setVisibility(View.GONE);
      mSignInFormVisible = true;
      mInfoBoxView.setOnClickListener(mMissingServerOnClickListener);
      mInfoBoxTextView.setText(R.string.missing_studip_message);
    }
  }

  private void authorize(Server selectedServer) {
    if (NetworkUtils.getConnectivityStatus(mContext) == NetworkUtils.NOT_CONNECTED) {
      Toast.makeText(mContext, "Not connected", Toast.LENGTH_LONG).show();
      return;
    }
    hideLoginForm();

    OAuthConnector.with(selectedServer).getRequestToken(this);
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

    inflater.inflate(R.menu.menu_sign_in, menu);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      MenuItem searchItem = menu.findItem(R.id.search_studip);
      SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
      SearchView.OnQueryTextListener queryTextListener = new SearchView.OnQueryTextListener() {
        @Override public boolean onQueryTextSubmit(String s) {
          return false;
        }

        @Override public boolean onQueryTextChange(String s) {
          if (TextUtils.isEmpty(s)) {
            mListView.clearTextFilter();
          } else {
            mListView.setFilterText(s);
          }

          return true;
        }
      };
      searchView.setOnQueryTextListener(queryTextListener);
    }
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {

    switch (item.getItemId()) {
      case R.id.menu_feedback:
        if (mSelectedServer != null) {
          StuffUtil.startFeedback(getActivity(), mSelectedServer);
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
        performPrefetchSync();
        break;
      case SyncHelper.SyncHelperCallbacks.FINISHED_INSTITUTES_SYNC:
        mInstitutesSynced = true;
        SyncHelper.getInstance(mContext).performSemestersSync(this);
        break;
    }

    if (mContactsSynced && mMessagesSynced && mCoursesSynced && mNewsSynced && mUsersSynced
        && mSemestersSynced && mInstitutesSynced) {

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
    }
  }

  @Override public void onSyncError(int status, String errorMsg, int errorCode) {
    if (getActivity() == null || errorCode == 404) {
      return;
    }
    Log.wtf(TAG, "Sync error " + status + ". Message: " + errorMsg + ". StatusCode: " + errorCode);

    String errorMessage;
    String defaultError = getString(R.string.sync_error_default);
    String genericErrorMessage = getString(R.string.sync_error_generic);
    String finalErrorMessage;

    if (TextUtils.isEmpty(errorMsg) || errorCode == 0) {
      finalErrorMessage = defaultError;
    } else {
      switch (status) {
        case SyncHelper.SyncHelperCallbacks.ERROR_CONTACTS_SYNC:
          errorMessage = String.format(genericErrorMessage, getString(R.string.contacts));
          break;
        case SyncHelper.SyncHelperCallbacks.ERROR_USER_SYNC:
          errorMessage = String.format(genericErrorMessage, getString(R.string.user_profile_data));
          break;
        case SyncHelper.SyncHelperCallbacks.ERROR_SEMESTER_SYNC:
          errorMessage = String.format(genericErrorMessage, getString(R.string.semesters));
          break;
        case SyncHelper.SyncHelperCallbacks.ERROR_NEWS_SYNC:
          errorMessage = String.format(genericErrorMessage, getString(R.string.news));
          break;
        case SyncHelper.SyncHelperCallbacks.ERROR_COURSES_SYNC:
          errorMessage = String.format(genericErrorMessage, getString(R.string.courses));
          break;
        case SyncHelper.SyncHelperCallbacks.ERROR_INSTITUTES_SYNC:
          errorMessage = String.format(genericErrorMessage, getString(R.string.institutes));
          break;
        case SyncHelper.SyncHelperCallbacks.ERROR_MESSAGES_SYNC:
          errorMessage = String.format(genericErrorMessage, getString(R.string.messages));
          break;
        case SyncHelper.SyncHelperCallbacks.ERROR_ROUTES_SYNC:
          errorMessage = String.format(genericErrorMessage, "routes");
          break;
        default:
          errorMessage = getString(R.string.sync_error_default);
      }

      StringBuilder builder = new StringBuilder(errorMessage);
      finalErrorMessage = builder.append(errorMsg)
          .append("HTTP-Code: ")
          .append(errorCode)
          .toString();
    }

    Toast.makeText(mContext, finalErrorMessage, Toast.LENGTH_LONG)
        .show();

    resetSignInActivityState();
    showLoginForm();
  }

  @Override public void onRequestTokenReceived(String authUrl) {
    if (getActivity() == null || mCallbacks == null) {
      return;
    }

    mRequestTokenReceived = true;
    mCallbacks.requestTokenReceived(authUrl);
  }

  @Override public void onAccessTokenReceived(String token, String tokenSecret) {
    //DEBUG Testing old credential migration behavior
    //Prefs.getInstance(mContext).simulateOldPrefs(mSelectedServer);
    //getActivity().finish();

    if (!isAdded()) {
      return;
    }

    mSelectedServer.setAccessToken(token);
    mSelectedServer.setAccessTokenSecret(tokenSecret);
    Prefs.getInstance(mContext).setServer(mSelectedServer);
    SyncHelper.getInstance(mContext).requestApiRoutes(this);
    SyncHelper.getInstance(mContext).getSettings(this);
    SyncHelper.getInstance(mContext).requestCurrentUserInfo(this);
  }

  @Override public void onRequestTokenRequestError(OAuthError e) {
    Toast.makeText(mContext, "RequestToken error: " + e.errorMessage, Toast.LENGTH_LONG).show();
    showLoginForm();
  }

  @Override public void onAccesTokenRequestError(OAuthError e) {
    Toast.makeText(mContext, "AccessToken error: " + e.errorMessage, Toast.LENGTH_LONG).show();
    showLoginForm();
  }

  public interface OnRequestTokenReceived {
    void requestTokenReceived(String authUrl);
  }

  /* Array adapter class which holds and displays the saved servers */
  private class ServerAdapter extends ArrayAdapter<Server> implements Filterable {
    private Context mContext;
    private int mTextViewResourceId;
    private Server[] mData = null;
    private ServerFilter mFilter = null;
    private Server[] mOriginalData;
    private final Object lock = new Object();

    /**
     * Public constructor which takes the context, viewResource and
     * server data and initializes it.
     *
     * @param context            the execution context
     * @param textViewResourceId the view resource id for displaying the servers
     * @param data               an array with servers
     */
    public ServerAdapter(Context context, int textViewResourceId, Server[] data) {
      super(context, textViewResourceId);
      this.mContext = context;
      this.mTextViewResourceId = textViewResourceId;
      this.mData = data;
    }

    @Override public Filter getFilter() {
      if (mFilter == null) {
        return new ServerFilter();
      } else {
        return mFilter;
      }
    }

    @Override public int getCount() {
      return mData.length;
    }

    @Override public Server getItem(int position) {
      return mData[position];
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
      if (convertView == null) {
        LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
        convertView = inflater.inflate(mTextViewResourceId, parent, false);
      }

      Server server = mData[position];
      ((TextView) convertView.findViewById(android.R.id.text1)).setText(server.getName());
      convertView.setTag(server);
      return convertView;
    }

    private class ServerFilter extends Filter {

      @Override protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults result = new FilterResults();

        if (mOriginalData == null) {
          synchronized (lock) {
            mOriginalData = mData.clone();
          }
        }

        if (constraint.length() > 0) {
          String lowerCaseConstraint = constraint.toString().toLowerCase();
          ArrayList<Server> filtered = new ArrayList<Server>();
          Server[] values = mOriginalData;

          for (Server s : values) {
            if (s.getName().toLowerCase().contains(lowerCaseConstraint)) {
              filtered.add(s);
            }
          }
          result.count = filtered.size();
          result.values = filtered.toArray(new Server[filtered.size()]);

        } else {

          synchronized (lock) {
            Server[] serversArray = mOriginalData.clone();
            result.values = serversArray;
            result.count = serversArray.length;
          }
        }

        return result;
      }

      @Override protected void publishResults(CharSequence constraint, FilterResults results) {
        mData = (Server[]) results.values;

        if (results.count > 0) {
          notifyDataSetChanged();
        } else {
          notifyDataSetInvalidated();
        }
      }

    }
  }
}
