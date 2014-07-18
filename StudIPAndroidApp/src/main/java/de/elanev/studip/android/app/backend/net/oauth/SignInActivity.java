/*
 * Copyright (c) 2014 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package de.elanev.studip.android.app.backend.net.oauth;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.util.ApiUtils;

/**
 * Activity for handling the full sign in and authorization process. It triggers
 * the prefetching after authorization.
 *
 * @author joern
 */
public class SignInActivity extends SherlockFragmentActivity implements
    WebAuthFragment.OnWebViewAuthListener, SignInFragment.OnRequestTokenReceived {

  private static final String TAG = SignInActivity.class.getSimpleName();

  /* Disable back button on older devices */
  @Override public void onBackPressed() {
    if (!ApiUtils.isOverApi11()) {
      return;
    }
    super.onBackPressed();
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.setContentView(R.layout.content_frame);

    if (savedInstanceState == null) {
      SignInFragment signInFragment = SignInFragment.newInstance();
      getSupportFragmentManager().beginTransaction()
          .add(R.id.content_frame, signInFragment, SignInFragment.class.getName())
          .commit();
    }
  }

  @Override public void onAuthSuccess() {
    FragmentManager fm = getSupportFragmentManager();
    Fragment frag = fm.findFragmentByTag(SignInFragment.class.getName());
    if (frag == null) {
      frag = SignInFragment.newInstance();
      fm.beginTransaction()
          .replace(R.id.content_frame, frag, SignInFragment.class.getName())
          .commit();
    } else {
      Fragment webAuthFrag = fm.findFragmentByTag(WebAuthFragment.class.getName());
      fm.beginTransaction().remove(webAuthFrag).attach(frag).commit();
    }

  }

  @Override public void onAuthCancelled() {
    FragmentManager fm = getSupportFragmentManager();
    SignInFragment frag = SignInFragment.newInstance();
    fm.beginTransaction()
        .replace(R.id.content_frame, frag, SignInFragment.class.getName())
        .commit();
  }

  @Override public void requestTokenReceived(String authUrl) {
    Bundle args = new Bundle();
    args.putString(WebAuthFragment.AUTH_URL, authUrl);
    WebAuthFragment frag = WebAuthFragment.newInstance(args);
    Fragment signInFrag = getSupportFragmentManager().findFragmentByTag(SignInFragment.class.getName());
    getSupportFragmentManager().beginTransaction()
        .detach(signInFrag)
        .add(R.id.content_frame, frag, WebAuthFragment.class.getName())
        .commitAllowingStateLoss();
  }


}
