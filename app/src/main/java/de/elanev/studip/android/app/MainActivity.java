/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ShareCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import de.elanev.studip.android.app.base.presentation.view.activity.BaseActivity;
import de.elanev.studip.android.app.contacts.presentation.ContactsActivity;
import de.elanev.studip.android.app.courses.CoursesActivity;
import de.elanev.studip.android.app.data.datamodel.User;
import de.elanev.studip.android.app.messages.presentation.view.MessagesActivity;
import de.elanev.studip.android.app.news.presentation.NewsActivity;
import de.elanev.studip.android.app.planner.presentation.view.PlannerActivity;
import de.elanev.studip.android.app.user.presentation.view.UserDetailsActivity;
import de.elanev.studip.android.app.util.ApiUtils;
import de.elanev.studip.android.app.util.Prefs;
import timber.log.Timber;

/**
 * @author joern
 *         <p/>
 *         Activity holding the navigation drawer and content frame.
 *         It manages the navigation and content fragments.
 */
public class MainActivity extends BaseActivity implements
    NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
  private static int mSelectedNavItem = R.id.navigation_invalid;
  protected Toolbar mToolbar;
  @Inject Prefs mPrefs;
  private DrawerLayout mDrawerLayout;
  private ActionBarDrawerToggle mDrawerToggle;
  private String mUserId;
  private User mCurrentUser;
  private NavigationView mNavigationView;
  private View mHeaderView;
  private Handler mHandler;

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);

    return super.onCreateOptionsMenu(menu);
  }

  @Override public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (mDrawerToggle.onOptionsItemSelected(item)) return true;

    switch (item.getItemId()) {
      case R.id.menu_feedback:
        this.navigator.navigateToFeedback(this);
        return true;
      case R.id.menu_about:
        navigator.navigateToAbout(this);
        return true;
      case R.id.menu_sign_out:
        logout();
        return true;
      case R.id.menu_invite:
        startInviteIntent();
        return true;
    }

    return super.onOptionsItemSelected(item);
  }

  /*
   * Deletes the preferences and database to logout of the service
   */
  private void logout() {
    new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
        .setTitle(R.string.Logout)
        .setMessage(R.string.logout_confirmation)
        .setPositiveButton(android.R.string.yes,
            (dialog, which) -> navigateToLogout())
        .setNegativeButton(android.R.string.no, null)
        .show();
  }

  private void navigateToLogout() {
    this.navigator.navigateToLogout(this);
    this.finish();
  }

  private void startInviteIntent() {
    Intent intent = ShareCompat.IntentBuilder.from(this)
        .setSubject(getString(R.string.invite_subject))
        .setText(getString(R.string.invite_text))
        .setHtmlText(getString(R.string.invite_text_html))
        .setType("text/plain")
        .createChooserIntent();

    // Check if intent resolves to an activity to prevent ActivityNotFoundException
    if (intent.resolveActivity(getPackageManager()) != null) {
      startActivity(intent);
    }
  }

  @Override public void onBackPressed() {
    if (!ApiUtils.isOverApi11()) {
      return;
    }
    super.onBackPressed();
  }

  @Override protected void onResume() {
    super.onResume();
    selectNavItem();
  }

  private void selectNavItem() {
    mNavigationView.getMenu()
        .findItem(getCurrentNavDrawerItem())
        .setChecked(true);
  }

  protected int getCurrentNavDrawerItem() {
    return R.id.navigation_invalid;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (isFinishing()) {
      return;
    }


    // Register Activity with the component
    ((AbstractStudIPApplication) getApplication()).getAppComponent()
        .inject(this);

    mHandler = new Handler();


    mCurrentUser = User.fromJson(mPrefs.getUserInfo());
    if (mCurrentUser != null) {
      mUserId = mCurrentUser.userId;
    }

    initNavigation();
  }

  private void initNavigation() {
    mSelectedNavItem = getCurrentNavDrawerItem();
    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

    if (mDrawerLayout == null) {
      Timber.w("No drawer found");

      return;
    }

    mDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, mDrawerLayout,
        R.string.open_drawer, R.string.close_drawer);
    mDrawerLayout.setDrawerListener(mDrawerToggle);

    mNavigationView = (NavigationView) findViewById(R.id.navigation);
    mHeaderView = LayoutInflater.from(this)
        .inflate(R.layout.nav_header, null);
    mHeaderView.setOnClickListener(this);
    mNavigationView.addHeaderView(mHeaderView);
    mNavigationView.setNavigationItemSelectedListener(this);

    setNavHeaderInformation();
    selectNavItem();
  }

  private void setNavHeaderInformation() {
    if (mCurrentUser != null) {
      TextView userNameTextView = (TextView) mHeaderView.findViewById(R.id.user_name);
      TextView userEmailTextView = (TextView) mHeaderView.findViewById(R.id.user_email);
      ImageView userImageView = (ImageView) mHeaderView.findViewById(R.id.user_image);

      userNameTextView.setText(mCurrentUser.getFullName());
      userEmailTextView.setText(mCurrentUser.email);

      Picasso.with(this)
          .load(mCurrentUser.avatarNormal)
          .fit()
          .centerCrop()
          .into(userImageView);
    } else {
      mHeaderView.setVisibility(View.GONE);
    }
  }

  @Override protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    // Sync the toggle state after onRestoreInstanceState has occurred.
    initNavigation();
    mDrawerToggle.syncState();

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
      // Fade content back in
      View content = findViewById(R.id.content_frame);
      if (content != null) {
        content.setAlpha(0);
        content.animate()
            .alpha(1)
            .setDuration(250);
      } else {
        Timber.w("No view with ID main_content to fade in.");
      }
    }
  }

  @Override public void setContentView(@LayoutRes int layoutResID) {
    super.setContentView(layoutResID);
    initToolbar();
  }

  private void initToolbar() {
    mToolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(mToolbar);

    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeButtonEnabled(true);
    }
  }

  protected Bundle getFragmentArguments() {
    Bundle args = new Bundle();
    Intent intent = getIntent();

    if (intent == null) {
      return args;
    }

    final Bundle intentExtras = intent.getExtras();
    if (intentExtras != null) {
      args.putAll(intentExtras);
    }

    return args;
  }

  @Override public boolean onNavigationItemSelected(final android.view.MenuItem menuItem) {
    menuItem.setChecked(true);

    mHandler.postDelayed(new Runnable() {
      @Override public void run() {
        navigateTo(menuItem.getItemId());
      }
    }, 250);

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
      // fade out the main content
      View content = findViewById(R.id.content_frame);
      if (content != null) {
        content.animate()
            .alpha(0)
            .setDuration(150);
      }
    }
    mDrawerLayout.closeDrawer(GravityCompat.START);

    return false;
  }

  private void navigateTo(int itemId) {

    switch (itemId) {
      case R.id.navigation_news:
        startActivityWithNewTask(new Intent(this, NewsActivity.class));
        return;
      case R.id.navigation_courses:
        startActivityWithNewTask(new Intent(this, CoursesActivity.class));
        return;

      case R.id.navigation_messages:
        startActivityWithNewTask(new Intent(this, MessagesActivity.class));
        return;

      case R.id.navigation_contacts:
        startActivityWithNewTask(new Intent(this, ContactsActivity.class));
        return;

      case R.id.navigation_planner:
        startActivityWithNewTask(new Intent(this, PlannerActivity.class));
        return;
    }
  }

  private void startActivityWithNewTask(Intent intent) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
      TaskStackBuilder builder = TaskStackBuilder.create(this);
      builder.addNextIntentWithParentStack(intent);
      builder.startActivities();
    } else {
      startActivity(intent);
      finish();
    }
  }

  @Override public void onClick(View v) {
    if (mUserId != null) {
      Intent intent = new Intent(this, UserDetailsActivity.class);
      Bundle args = new Bundle();
      args.putString(UserDetailsActivity.USER_ID, mUserId);
      intent.putExtras(args);
      startActivity(intent);
      mDrawerLayout.closeDrawers();
    }
  }

  public interface OnShowProgressBarListener {
    void onShowProgressBar(boolean show);
  }
}
