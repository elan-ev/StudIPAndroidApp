/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.auth;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import de.elanev.studip.android.app.AbstractStudIPApplication;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.StartupActivity;
import de.elanev.studip.android.app.data.datamodel.Server;
import de.elanev.studip.android.app.data.datamodel.User;
import de.elanev.studip.android.app.data.net.sync.SyncHelper;
import de.elanev.studip.android.app.news.presentation.NewsActivity;
import de.elanev.studip.android.app.util.ApiUtils;
import de.elanev.studip.android.app.util.Prefs;
import timber.log.Timber;

/**
 * @author joern
 */
public class SignInSyncFragment extends Fragment implements SyncHelper.SyncHelperCallbacks {
  public static final String REQUEST_TOKEN_RECEIVED = "onRequestTokenReceived";
  private boolean mCoursesSynced = false;
  private boolean mSemestersSynced = false;
  private boolean mMessagesSynced = false;
  private boolean mContactsSynced = false;
  private boolean mNewsSynced = false;
  private boolean mUsersSynced = false;
  private boolean mInstitutesSynced = false;

  private View mProgressInfo;
  private TextView mSyncStatusTextView;
  private Toolbar mToolbar;
  private ImageView mLogoImageView;
  private Server mSelectedServer;
  @Inject SyncHelper mSyncHelper;
  @Inject Prefs prefs;
  private SignInSyncListener mSyncCallbacks;
  private boolean mAuthSuccess;
  private SignInListener signInListener;
  private SignInSuccessListener signInSuccessListener;

  public SignInSyncFragment() {}

  /**
   * Instantiates a new ServerListFragment.
   *
   * @return A new ServerListFragment instance
   */
  public static SignInSyncFragment newInstance() {
    return new SignInSyncFragment();
  }

  interface SignInSuccessListener {
    void onSignInSuccess();
    void onSignInError(Exception e);
  }

  /**
   * Instantiates a new ServerListFragment.
   *
   * @return A new ServerListFragment instance
   */
  public static SignInSyncFragment newInstance(Bundle args) {
    SignInSyncFragment fragment = new SignInSyncFragment();
    fragment.setArguments(args);

    return fragment;
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);

    try {
      mSyncCallbacks = (SignInSyncListener) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(
          activity.toString() + " must implement OnRequestTokenReceiveListener and OnAuthListener");
    }

    if (activity instanceof SignInListener) {
      this.signInListener = (SignInListener) activity;
    }
    if (activity instanceof SignInSuccessListener) {
      this.signInSuccessListener = (SignInSuccessListener) activity;
    }
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ((AbstractStudIPApplication) getActivity().getApplication()).getAppComponent()
        .inject(this);

    if (prefs.isAppAuthorized(getContext())) {
      mSelectedServer = prefs.getServer(getContext());

      if (mSelectedServer == null) {
        mSyncCallbacks.onSignInSyncError(new Exception("Server not found"));
      }
    }
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_sign_in, container, false);

    mToolbar = (Toolbar) v.findViewById(R.id.toolbar);

    mProgressInfo = v.findViewById(R.id.progress_info);
    mSyncStatusTextView = (TextView) v.findViewById(R.id.sync_status);
    mLogoImageView = (ImageView) v.findViewById(R.id.sign_in_imageview);
    return v;
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setHasOptionsMenu(true);

    initToolbar();

    if (mLogoImageView != null) {
      Picasso.with(getActivity())
          .load(R.drawable.logo)
          .config(Bitmap.Config.RGB_565)
          .fit()
          .centerCrop()
          .noFade()
          .into(mLogoImageView);
    }
    // Check if unsecured server credentials exist
    if (prefs.isAppAuthorized(getContext())) {
      performPrefetchSync();
    }

  }

  public void initToolbar() {
    ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeButtonEnabled(true);
    }
  }

  /* Simply triggers the prefetching at the SyncHelper */
  private void performPrefetchSync() {
    showProgressInfo(true);
    mSyncHelper.requestApiRoutes(this);
  }

  /* Hides the progess indicator and sets the login form as visible */
  private void showProgressInfo(boolean show) {
    if (getActivity() != null && isAdded()) {
      if (show) {
        mProgressInfo.setVisibility(View.VISIBLE);
      } else {
        mProgressInfo.setVisibility(View.GONE);
      }
    }
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putBoolean(REQUEST_TOKEN_RECEIVED, mAuthSuccess);
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.menu_sign_in, menu);

    // Remove search view, since there is nothing to search for
    MenuItem searchItem = menu.findItem(R.id.search_studip);
    if (searchItem != null) {
      menu.removeItem(R.id.search_studip);
    }
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {

    switch (item.getItemId()) {
      // Respond to the action bar's Up/Home button
      case android.R.id.home:
        mSyncCallbacks.onSignInSyncCanceled();

        return true;
      case R.id.menu_feedback:
        if (signInListener != null) {
          signInListener.onFeedbackSelected();
        }

        return true;
      case R.id.menu_about:
        if (signInListener != null) {
          signInListener.onAboutSelected();
        }

        return true;
      default:
    }

    return super.onOptionsItemSelected(item);
  }

  @Override public void onSyncStarted() {}

  @Override public void onSyncStateChange(int status) {
    switch (status) {
      case SyncHelper.SyncHelperCallbacks.STARTED_SEMESTER_SYNC:
        Timber.d("Synching semesters");
        mSyncStatusTextView.setText(R.string.syncing_semesters);
        break;
      case SyncHelper.SyncHelperCallbacks.STARTED_COURSES_SYNC:
        Timber.d("Synching courses");
        mSyncStatusTextView.setText(R.string.syncing_courses);
        break;
      case SyncHelper.SyncHelperCallbacks.STARTED_NEWS_SYNC:
        Timber.d("Synching news");
        mSyncStatusTextView.setText(R.string.syncing_news);
        break;
      case SyncHelper.SyncHelperCallbacks.STARTED_CONTACTS_SYNC:
        Timber.d("Synching contacts");
        mSyncStatusTextView.setText(R.string.syncing_contacts);
        break;
      case SyncHelper.SyncHelperCallbacks.STARTED_MESSAGES_SYNC:
        Timber.d("Synching messages");
        mSyncStatusTextView.setText(R.string.syncing_messages);
        break;
      case SyncHelper.SyncHelperCallbacks.STARTED_USER_SYNC:
        Timber.d("Synching settings");
        mSyncStatusTextView.setText("Synchronizing settings");
        break;
      case SyncHelper.SyncHelperCallbacks.STARTED_INSTITUTES_SYNC:
        Timber.d("Synching institutes");
        mSyncStatusTextView.setText(R.string.syncing_institutes);
        break;
    }
  }

  @Override public void onSyncFinished(int status) {
    switch (status) {
      case SyncHelper.SyncHelperCallbacks.FINISHED_ROUTES_SYNC:
        Timber.d("Finished synching routes");
        mSyncHelper.getSettings(this);
        break;
      case SyncHelper.SyncHelperCallbacks.FINISHED_SETTINGS_SYNC:
        Timber.d("Finished synching settings");
        mSyncHelper.requestCurrentUserInfo(this);
        break;
      case SyncHelper.SyncHelperCallbacks.FINISHED_USER_SYNC:
        Timber.d("Finished synching user infos");
        User currentUser = User.fromJson(prefs.getUserInfo());
        if (currentUser != null) {
          mSyncHelper.requestInstitutesForUserID(currentUser.userId, this);
        } else {
          mSyncCallbacks.onSignInSyncError(new Exception("Current user info not found"));
        }
        break;
      case SyncHelper.SyncHelperCallbacks.FINISHED_INSTITUTES_SYNC:
        Timber.d("Finished synching institutes");
        mInstitutesSynced = true;
        mSyncHelper.performSemestersSync(this);
        break;
      case SyncHelper.SyncHelperCallbacks.FINISHED_SEMESTER_SYNC:
        Timber.d("Finished synching semesters");
        mSemestersSynced = true;
        mSyncHelper.performCoursesSync(this);
        break;
      case SyncHelper.SyncHelperCallbacks.FINISHED_COURSES_SYNC:
        Timber.d("Finished synching courses");
        mCoursesSynced = true;
        mSyncHelper.performNewsSync(this);
        break;
      case SyncHelper.SyncHelperCallbacks.FINISHED_NEWS_SYNC:
        Timber.d("Finished synching news");
        mNewsSynced = true;
        mMessagesSynced = true;
        mSyncHelper.performContactsSync(this);
        break;
      case SyncHelper.SyncHelperCallbacks.FINISHED_CONTACTS_SYNC:
        Timber.d("Finished synching contacts");
        mContactsSynced = true;
        mUsersSynced = true;
        finishSync();
        break;
    }


  }

  private void finishSync() {
    if (mContactsSynced && mMessagesSynced && mCoursesSynced && mNewsSynced && mUsersSynced
        && mSemestersSynced && mInstitutesSynced) {

      mCoursesSynced = false;
      mContactsSynced = false;
      mMessagesSynced = false;
      mNewsSynced = false;
      mUsersSynced = false;
      mSemestersSynced = false;
      mInstitutesSynced = false;

      prefs.setServer(mSelectedServer, getContext());
      if (this.signInSuccessListener != null) {
        this.signInSuccessListener.onSignInSuccess();
      }
    }
  }

  @Override public void onSyncError(int status, String errorMsg, int errorCode) {
    if (getActivity() == null || errorCode == 404) {
      return;
    }
    Timber.e("Sync error %d. Message: %s . StatusCode: %d", status, errorMsg, errorCode);

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

    Toast.makeText(getContext(), finalErrorMessage, Toast.LENGTH_LONG)
        .show();

    mSyncCallbacks.onSignInSyncError(new Exception(finalErrorMessage));
  }


  public interface SignInSyncListener {
    void onSignInSyncCanceled();
    void onSignInSyncError(Exception e);
  }

}
