/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.user.presentation.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.elanev.studip.android.app.AbstractStudIPApplication;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.base.internal.di.components.HasComponent;
import de.elanev.studip.android.app.user.interal.di.DaggerUserComponent;
import de.elanev.studip.android.app.user.interal.di.UserComponent;
import de.elanev.studip.android.app.user.interal.di.UserModule;
import de.elanev.studip.android.app.user.presentation.model.UserModel;

/**
 * @author joern
 */
public class UserDetailsActivity extends AppCompatActivity implements HasComponent<UserComponent>,
    UserDetailsFragment.FabClickListener {

  public static final String USER_ID = "user-id";
  @BindView(R.id.toolbar) Toolbar toolbar;

  private String mUserId;
  private UserComponent userComponent;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.content_frame);

    ButterKnife.bind(this);

    this.iniInstance();
    this.initToolbar();
    this.initInjector();

    if (savedInstanceState == null) {
      this.addFragment();
    }
  }

  private void iniInstance() {
    Bundle args = getIntent().getExtras();
    if (args == null || !args.containsKey(USER_ID)) {
      throw new IllegalArgumentException("USER_ID must not be null");
    } else {
      this.mUserId = args.getString(USER_ID);
    }
  }

  private void initToolbar() {
    toolbar.setVisibility(View.GONE);
  }

  private void initInjector() {
    this.userComponent = DaggerUserComponent.builder()
        .applicationComponent(((AbstractStudIPApplication) getApplication()).getAppComponent())
        .userModule(new UserModule(mUserId))
        .build();
  }

  private void addFragment() {
    UserDetailsFragment userDetailsFragment = UserDetailsFragment.newInstance();
    getSupportFragmentManager().beginTransaction()
        .add(R.id.content_frame, userDetailsFragment, UserDetailsFragment.class.getName())
        .commit();
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      // Respond to the action bar's Up/Home button
      case android.R.id.home:
        // Since this activity can be called from different other
        // activities, we call the back button to move back in stack history
        onBackPressed();
        return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override public UserComponent getComponent() {
    return userComponent;
  }

  @Override public void onFabClicked(UserModel userModel) {
    Toast.makeText(this, "FabClicked", Toast.LENGTH_LONG)
        .show();
    //    User user = new User();
    //    user.userId = mUserId;
    //    user.forename = mFirstname;
    //    user.lastname = mLastname;
    //    user.titlePost = mTitlePost;
    //    user.titlePre = mTitlePre;
    //
    //    Bundle extras = new Bundle();
    //    extras.putInt(MessageComposeActivity.MESSAGE_ACTION_FLAG,
    //        MessageComposeActivity.MESSAGE_ACTION_REPLY);
    //    extras.putSerializable(MessageComposeActivity.MESSAGE_RECEIVER, user);
    //
    //
    //    Intent intent = new Intent(this, MessageComposeActivity.class);
    //    intent.putExtras(extras);
    //    startActivity(intent);
  }
}
