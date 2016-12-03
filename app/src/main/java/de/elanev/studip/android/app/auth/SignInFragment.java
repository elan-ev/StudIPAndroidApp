/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.auth;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import de.elanev.studip.android.app.AbstractStudIPApplication;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.data.datamodel.Server;
import de.elanev.studip.android.app.data.datamodel.User;
import de.elanev.studip.android.app.data.net.services.StudIpLegacyApiService;
import de.elanev.studip.android.app.util.Prefs;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * @author joern
 */
public class SignInFragment extends Fragment implements OAuthConnector.OAuthCallbacks,
    StudIPAuthWebViewClient.WebAuthStatusListener {
  public static final String REQUEST_TOKEN_RECEIVED = "onRequestTokenReceived";
  @Inject Prefs mPrefs;
  @Inject StudIpLegacyApiService apiService;
  private View mProgressInfo;
  private TextView mSyncStatusTextView;
  private boolean mRequestTokenReceived = false;
  private Context mContext;
  private Toolbar mToolbar;
  private ImageView mLogoImageView;
  private Server mSelectedServer;
  private OnAuthListener mOnAuthListener;
  private AlertDialog mWebAuthDialog;
  private SignInListener signInListener;

  public SignInFragment() {}

  /**
   * Instantiates a new SignInFragment.
   *
   * @return A new ServerListFragment instance
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

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);

    try {
      mOnAuthListener = (OnAuthListener) activity;
    } catch (ClassCastException e) {
      throw new ClassCastException(
          activity.toString() + " must implement OnRequestTokenReceiveListener and OnAuthListener");
    }

    if (activity instanceof SignInListener) {
      this.signInListener = (SignInListener) activity;
    }
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ((AbstractStudIPApplication) getActivity().getApplication()).getAppComponent()
        .inject(this);

    mContext = getActivity();
    Bundle args = getArguments();

    if (args == null || args.isEmpty()) {
      throw new IllegalStateException("Fragment arg must not be null");
    }

    mSelectedServer = (Server) args.getSerializable(SignInActivity.SELECTED_SERVER);
    mRequestTokenReceived = args.getBoolean(SignInActivity.AUTH_SUCCESS);
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
    if (mPrefs.legacyDataExists()) {
      destroyInsecureCredentials();
    }

    startRequestTokenRequest();
  }

  public void initToolbar() {
    ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeButtonEnabled(true);
    }
  }

  private void destroyInsecureCredentials() {
    Timber.i("Insecure credentials found, deleting...");
    // Clear the app preferences
    mPrefs.clearPrefs();
  }

  private void startRequestTokenRequest() {
    mSyncStatusTextView.setText(R.string.authentication);
    showProgressInfo(true);
    OAuthConnector.with(mSelectedServer)
        .getRequestToken(this);
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
    outState.putBoolean(REQUEST_TOKEN_RECEIVED, mRequestTokenReceived);
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
        mOnAuthListener.onAuthCanceled();

        return true;
      case R.id.menu_feedback:
        if (signInListener != null) {
          this.mPrefs.setServer(mSelectedServer);
          this.signInListener.onFeedbackSelected();
        }

        return true;
      case R.id.menu_about:
        if (signInListener != null) {
          this.signInListener.onAboutSelected();
        }

        return true;
      default:
    }

    return super.onOptionsItemSelected(item);
  }

  @Override public void onRequestTokenReceived(String authUrl) {
    if (getActivity() == null) {
      return;
    }

    mRequestTokenReceived = true;

    //TODO: Start loading the side hidden and the animate the fully loaded dialog into the window
    // or show intermediate progress indicator while loading the site an then switch to the WebView
    mWebAuthDialog = createSignInDialog(authUrl);
    mWebAuthDialog.show();
  }

  public AlertDialog createSignInDialog(String authUrl) {
    // Hack for enabling keyboard input in WebViews inside AlertDialogs
    // Wrap a invisible EditText and the WebView in a LinearLayout,
    // this will mysteriously enable the keyboard
    // Source: http://stackoverflow.com/a/7253971
    // FIXME: Maybe a better hack or way?
    LinearLayout wrapper = new LinearLayout(getActivity());
    EditText keyboardHack = new EditText(getActivity());
    keyboardHack.setVisibility(View.GONE);

    WebView webView = new WebView(getActivity());
    webView.loadUrl(authUrl);
    webView.setWebViewClient(new StudIPAuthWebViewClient(this));
    //    WebSettings webSettings = webView.getSettings();
    //    webSettings.setJavaScriptEnabled(true);

    wrapper.setOrientation(LinearLayout.VERTICAL);
    wrapper.addView(webView, LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.MATCH_PARENT);
    wrapper.addView(keyboardHack, LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT);

    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext(),
        R.style.AppCompatAlertDialogStyle);
    dialogBuilder.setTitle(R.string.authentication);
    dialogBuilder.setView(wrapper);
    dialogBuilder.setCancelable(true);
    dialogBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
      @Override public void onClick(DialogInterface dialog, int which) {
        mOnAuthListener.onAuthCanceled();
      }
    });
    dialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {
      @Override public void onCancel(DialogInterface dialog) {
        mOnAuthListener.onAuthCanceled();
      }
    });

    return dialogBuilder.create();
  }

  @Override public void onAccessTokenReceived(String token, String tokenSecret) {
    //DEBUG Testing old credential migration behavior
    //Prefs.getInstance(mContext).simulateOldPrefs(mSelectedServer);
    //getActivity().finish();

    if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(tokenSecret)) {
      mSelectedServer.setAccessToken(token);
      mSelectedServer.setAccessTokenSecret(tokenSecret);

      mPrefs.setServer(mSelectedServer);

      requestUserProfile();
    } else {
      mOnAuthListener.onAuthCanceled();
    }

  }

  private void requestUserProfile() {
    Subscription subscription = apiService.getCurrentUserInfo()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<User>() {
          @Override public void onCompleted() {
            mOnAuthListener.onAuthSuccess(mSelectedServer);
          }

          @Override public void onError(Throwable e) {
            if (e != null && e.getLocalizedMessage() != null) {
              Timber.e(e, e.getLocalizedMessage());

              mOnAuthListener.onAuthCanceled();
            }
          }

          @Override public void onNext(User user) {
            mPrefs.setUserInfo(User.toJson(user));
          }
        });
  }

  @Override public void onRequestTokenRequestError(OAuthConnector.OAuthCallbacks.OAuthError e) {
    Timber.e("RequestToken error: %s", e.errorMessage);
    Toast.makeText(mContext, "RequestToken error: " + e.errorMessage, Toast.LENGTH_LONG)
        .show();

    mOnAuthListener.onAuthCanceled();
  }

  @Override public void onAccessTokenRequestError(OAuthConnector.OAuthCallbacks.OAuthError e) {
    Timber.e("RequestToken error: %s", e.errorMessage);
    Toast.makeText(mContext, "AccessToken error: " + e.errorMessage, Toast.LENGTH_LONG)
        .show();

    mOnAuthListener.onAuthCanceled();
  }

  @Override public void onWebAuthSuccess() {
    mWebAuthDialog.dismiss();

    onAuthSuccess(mSelectedServer);
  }

  private void onAuthSuccess(Server server) {

    OAuthConnector.with(server)
        .getAccessToken(this);
  }

  @Override public void onWebAuthCanceled() {
    mWebAuthDialog.dismiss();
    mOnAuthListener.onAuthCanceled();
  }


}
