/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.presentation.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import de.elanev.studip.android.app.AbstractStudIPApplication;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.authorization.internal.di.component.AuthComponent;
import de.elanev.studip.android.app.authorization.internal.di.component.DaggerAuthComponent;
import de.elanev.studip.android.app.authorization.internal.di.modules.AuthModule;
import de.elanev.studip.android.app.authorization.presentation.model.EndpointModel;
import de.elanev.studip.android.app.base.internal.di.components.ApplicationComponent;
import de.elanev.studip.android.app.base.internal.di.components.HasComponent;
import de.elanev.studip.android.app.base.presentation.view.activity.BaseActivity;
import de.elanev.studip.android.app.news.presentation.NewsActivity;

/**
 * Activity for handling the full sign in and authorization process. It triggers
 * the prefetch after authorization.
 *
 * @author joern
 */
public class SignInActivity extends BaseActivity implements ServerListFragment.EndpointListListener,
    OnAuthListener, SignInListener, HasComponent<AuthComponent> {

  static final String SELECTED_SERVER = "selected_server";
  static final String AUTH_SUCCESS = "auth_success";
  private AuthComponent component;

  public static Intent getCallingIntent(Context context) {
    return new Intent(context, SignInActivity.class);
  }

  //  void showTutorial() {
  //    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
  //
  //    SignInTutorialDialog tutorialDialog = new SignInTutorialDialog();
  //    tutorialDialog.show(ft, "dialog");
  //  }

  @Override public void onCreate(Bundle savedInstanceState) {
    initInjector();
    super.onCreate(savedInstanceState);
    this.setContentView(R.layout.activity_signin);
    setTitle(R.string.app_name);

    if (savedInstanceState == null) {
      ServerListFragment serverListFragment = ServerListFragment.newInstance();
      getSupportFragmentManager().beginTransaction()
          .add(R.id.content_frame, serverListFragment, ServerListFragment.class.getName())
          .commit();
    }
    //TODO: Reactivate tutorial when the slides are ready
    //    boolean isFirstStart = Prefs.getInstance(this)
    //        .isFirstStart();
    //
    //    // For show tutorial for debugging purposes
    //    if (BuildConfig.DEBUG) isFirstStart = true;
    //    if (isFirstStart) {
    //
    //      // If this is the first start, show the tutorial DialogFragment
    //      showTutorial();
    //    }
    //    Prefs.getInstance(this)
    //        .setAppStarted();
  }

  private void initInjector() {
    ApplicationComponent applicationComponent = ((AbstractStudIPApplication) getApplication()).getAppComponent();
    this.component = DaggerAuthComponent.builder()
        .applicationComponent(applicationComponent)
        .authModule(new AuthModule())
        .build();
  }

  @Override public void onAuthCanceled() {
    attachSignInServerListFragment();
  }

  @Override public void onAuthSuccess() {
    navigateToNews();
  }

  private void navigateToNews() {
    Intent intent = NewsActivity.getCallingIntent(this);

    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

    startActivity(intent);
  }

  private void attachSignInServerListFragment() {
    FragmentManager fm = getSupportFragmentManager();
    Fragment fragment = fm.findFragmentByTag(ServerListFragment.class.getName());

    if (fragment == null) {
      fragment = ServerListFragment.newInstance();
    }

    fm.beginTransaction()
        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        .replace(R.id.content_frame, fragment, ServerListFragment.class.getName())
        .commit();
  }

  @Override public void onFeedbackSelected() {
    navigator.navigateToFeedback(this);
  }

  @Override public void onAboutSelected() {
    navigator.navigateToAbout(this);
  }

  @Override public AuthComponent getComponent() {
    return this.component;
  }

  @Override public void onEndpointSelected(EndpointModel endpointModel) {
    FragmentManager fm = getSupportFragmentManager();
    Bundle args = new Bundle();
    args.putString(SignInFragment.ENDPOINT_ID, endpointModel.getId());
    SignInFragment frag = SignInFragment.newInstance(args);

    FragmentTransaction ft = fm.beginTransaction()
        .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    ft.replace(R.id.content_frame, frag, SignInFragment.class.getName())
        .commit();
  }
}
