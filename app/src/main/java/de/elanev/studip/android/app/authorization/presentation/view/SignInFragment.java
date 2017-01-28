/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.presentation.view;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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

import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.elanev.studip.android.app.AbstractStudIPApplication;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.authorization.internal.di.component.AuthComponent;
import de.elanev.studip.android.app.authorization.internal.di.component.DaggerAuthComponent;
import de.elanev.studip.android.app.authorization.internal.di.modules.AuthModule;
import de.elanev.studip.android.app.authorization.presentation.presenter.SignInPresenter;
import de.elanev.studip.android.app.base.presentation.view.BaseMvpFragment;
import timber.log.Timber;

/**
 * @author joern
 */
public class SignInFragment extends BaseMvpFragment<SignInView, SignInPresenter> implements
    SignInView, StudIPAuthWebViewClient.WebAuthStatusListener {
  public static final String ENDPOINT_ID = "endpoint-id";
  @Inject SignInPresenter presenter;
  @BindView(R.id.progress_info) View mProgressInfo;
  @BindView(R.id.toolbar) Toolbar mToolbar;
  @BindView(R.id.sync_status) TextView mSyncStatusTextView;
  @BindView(R.id.sign_in_imageview) ImageView mLogoImageView;
  private OnAuthListener mOnAuthListener;
  private SignInListener signInListener;
  private AlertDialog mWebAuthDialog;
  private AuthComponent authComponent;
  private String endpointId;

  public SignInFragment() {
    setRetainInstance(true);
  }

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

  @Override public SignInPresenter createPresenter() {
    return this.presenter;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Bundle args = getArguments();
    if (args == null || args.isEmpty()) {
      throw new IllegalStateException("Fragment arg must not be null");
    }
    this.endpointId = args.getString(ENDPOINT_ID);

    initInjector();
  }

  private void initInjector() {
    this.authComponent = DaggerAuthComponent.builder()
        .applicationComponent(
            ((AbstractStudIPApplication) getActivity().getApplication()).getAppComponent())
        .authModule(new AuthModule(endpointId))
        .build();
    this.authComponent.inject(this);
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setHasOptionsMenu(true);

    initToolbar();

    if (mLogoImageView != null) {
      Picasso.with(getContext())
          .load(R.drawable.logo)
          .config(Bitmap.Config.RGB_565)
          .fit()
          .centerCrop()
          .noFade()
          .into(mLogoImageView);
    }

    // Check if unsecured server credentials exist
    // TODO: Move to startup code
    //    if (mPrefs.legacyDataExists()) {
    //      destroyInsecureCredentials();
    //    }
    //    private void destroyInsecureCredentials() {
    //      Timber.i("Insecure credentials found, deleting...");
    //      // Clear the app preferences
    //      mPrefs.clearPrefs();
    //    }

    presenter.startAuthProcess();
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

  public void initToolbar() {
    ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
    ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeButtonEnabled(true);
    }
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_sign_in, container, false);
    ButterKnife.bind(this, v);

    return v;
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

  @Override public void onWebAuthSuccess() {
    mWebAuthDialog.dismiss();
    this.presenter.signInUser();
  }

  @Override public void onWebAuthCanceled() {
    mWebAuthDialog.dismiss();
    mOnAuthListener.onAuthCanceled();
  }

  @Override public void showError(Throwable e) {
    Timber.e(e.getLocalizedMessage(), e);
    showToast(e.getLocalizedMessage());
    mOnAuthListener.onAuthCanceled();
  }

  @Override public void showAuthDialog(String authUrl) {
    mWebAuthDialog = createSignInDialog(authUrl);
    mWebAuthDialog.show();
  }

  private AlertDialog createSignInDialog(String authUrl) {
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
    dialogBuilder.setNegativeButton(android.R.string.cancel,
        (dialog, which) -> mOnAuthListener.onAuthCanceled());
    dialogBuilder.setOnCancelListener(dialog -> mOnAuthListener.onAuthCanceled());

    return dialogBuilder.create();
  }

  @Override public void authSuccess() {
    mOnAuthListener.onAuthSuccess();
  }

  @Override public void showLoading() {
    mSyncStatusTextView.setText(R.string.authentication);
    showProgressInfo(true);
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
}
