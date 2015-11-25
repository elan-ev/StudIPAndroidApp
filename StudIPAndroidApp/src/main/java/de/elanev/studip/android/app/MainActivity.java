/*
 * Copyright (c) 2015 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package de.elanev.studip.android.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.elanev.studip.android.app.backend.datamodel.Server;
import de.elanev.studip.android.app.backend.datamodel.User;
import de.elanev.studip.android.app.backend.db.AbstractContract;
import de.elanev.studip.android.app.backend.db.UsersContract;
import de.elanev.studip.android.app.backend.net.SyncHelper;
import de.elanev.studip.android.app.frontend.contacts.ContactsActivity;
import de.elanev.studip.android.app.frontend.courses.CoursesActivity;
import de.elanev.studip.android.app.frontend.messages.MessagesActivity;
import de.elanev.studip.android.app.frontend.news.NewsActivity;
import de.elanev.studip.android.app.frontend.planer.PlanerActivity;
import de.elanev.studip.android.app.util.ApiUtils;
import de.elanev.studip.android.app.util.Prefs;
import de.elanev.studip.android.app.util.StuffUtil;
import de.elanev.studip.android.app.widget.UserDetailsActivity;

/**
 * @author joern
 *         <p/>
 *         Activity holding the navigation drawer and content frame.
 *         It manages the navigation and content fragments.
 */
public class MainActivity extends AppCompatActivity implements
    NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
  public static final String TAG = MainActivity.class.getSimpleName();
  private static final String ACTIVE_NAVIGATION_ITEM = "active_navi_item";
  private static int mSelectedNavItem = R.id.navigation_news;
  private DrawerLayout mDrawerLayout;
  private ActionBarDrawerToggle mDrawerToggle;
  private String mUserId;
  private User mCurrentUser;
  private boolean isPaused;
  private NavigationView mNavigationView;
  private View mHeaderView;

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);

    return super.onCreateOptionsMenu(menu);
  }

  @Override public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (mDrawerToggle.onOptionsItemSelected(item)) return true;

    switch (item.getItemId()) {
      case R.id.menu_feedback:
        StuffUtil.startFeedback(this, Prefs.getInstance(this)
            .getServer());
        return true;
      case R.id.menu_about:
        StuffUtil.startAbout(this);
        return true;
      case R.id.menu_sign_out:
        logout();
        return true;
      case R.id.menu_invite:
        startInviteIntent(createInviteIntent());
        return true;
    }

    return super.onOptionsItemSelected(item);
  }

  /*
   * Deletes the preferences and database to logout of the service
   */
  private void logout() {
    //Cancel all pending network requests
    StudIPApplication.getInstance()
        .cancelAllPendingRequests(SyncHelper.TAG);

    // Resetting the SyncHelper
    SyncHelper.getInstance(this)
        .resetSyncHelper();

    // Clear the app preferences
    Prefs.getInstance(this)
        .clearPrefs();

    // Delete the app database
    getContentResolver().delete(AbstractContract.BASE_CONTENT_URI, null, null);

    StuffUtil.startSignInActivity(this);
    finish();
  }

  private void startInviteIntent(Intent intent) {
    // Check if intent resolves to an activity to prevent ActivityNotFoundException
    if (intent.resolveActivity(getPackageManager()) != null) {
      startActivity(intent);
    }
  }

  private Intent createInviteIntent() {

    Server server = Prefs.getInstance(this)
        .getServer();
    String inviteText = "";
    String inviteTextHtml = "";
    if (server == null) {
      inviteText = String.format(getString(R.string.invite_text), "");
      inviteTextHtml = String.format(getString(R.string.invite_text_html), "");
    } else {
      inviteText = String.format(getString(R.string.invite_text), server.getName());
      inviteTextHtml = String.format(getString(R.string.invite_text_html), server.getName());
    }

    return ShareCompat.IntentBuilder.from(this)
        .setSubject(getString(R.string.invite_subject))
        .setText(inviteText)
        .setHtmlText(inviteTextHtml)
        .setType("text/plain")
        .createChooserIntent();
  }

  @Override public void onBackPressed() {
    if (!ApiUtils.isOverApi11()) {
      return;
    }
    super.onBackPressed();
  }

  @Override protected void onPause() {
    super.onPause();
    isPaused = true;
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    outState.putInt(ACTIVE_NAVIGATION_ITEM, mSelectedNavItem);
    super.onSaveInstanceState(outState);
  }

  @Override protected void onStart() {
    super.onStart();
    isPaused = false;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (isFinishing()) {
      return;
    }

    if (!Prefs.getInstance(this)
        .isAppAuthorized()) {
      StuffUtil.startSignInActivity(this);
      finish();
      return;
    }

    // Verify that the forum routes are still activated
    //TODO: Move this and other API checks to a Service or something, but not here
    SyncHelper.getInstance(this)
        .requestApiRoutes(null);
    SyncHelper.getInstance(this)
        .getSettings(null);
    SyncHelper.getInstance(this)
        .requestCurrentUserInfo(null);

    mCurrentUser = User.fromJson(Prefs.getInstance(this)
        .getUserInfo());
    if (mCurrentUser != null) {
      mUserId = mCurrentUser.userId;
    }

    initNavigation();

//    if (savedInstanceState == null) {
//      navigateTo(mSelectedNavItem);
//      mNavigationView.getMenu()
//          .findItem(mSelectedNavItem)
//          .setChecked(true);
//    } else {
//      navigateTo(mSelectedNavItem);
//      mNavigationView.getMenu()
//          .findItem(savedInstanceState.getInt(ACTIVE_NAVIGATION_ITEM))
//          .setChecked(true);
//    }
  }

  @Override protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    // Sync the toggle state after onRestoreInstanceState has occurred.
    initNavigation();
    mDrawerToggle.syncState();
  }

  @Override public void setContentView(@LayoutRes int layoutResID) {
    super.setContentView(layoutResID);
    initToolbar();
  }

//  @Override public void onConfigurationChanged(Configuration newConfig) {
//    super.onConfigurationChanged(newConfig);
//    mDrawerToggle.onConfigurationChanged(newConfig);
//  }

  private void initToolbar() {
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setHomeButtonEnabled(true);
    }
  }

  private void initNavigation() {
    mSelectedNavItem = getCurrentNavDrawerItem();
    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

    if (mDrawerLayout == null) {
      Log.d(TAG, "No drawer found");
      return;
    }

    mDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, mDrawerLayout,
        R.string.open_drawer, R.string.close_drawer);
    mDrawerLayout.setDrawerListener(mDrawerToggle);

    mNavigationView = (NavigationView) findViewById(R.id.navigation);
    mHeaderView = findViewById(R.id.navigation_header);
    mHeaderView.setOnClickListener(this);
    mNavigationView.setNavigationItemSelectedListener(this);

    setNavHeaderInformation();
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

  private void navigateTo(int itemId) {

    switch (itemId) {
      case R.id.navigation_news:
        startActivity(new Intent(this, NewsActivity.class));
        return;
      case R.id.navigation_courses:
        startActivity(new Intent(this, CoursesActivity.class));
        return;

      case R.id.navigation_messages:
        startActivity(new Intent(this, MessagesActivity.class));
        return;

      case R.id.navigation_contacts:
        startActivity(new Intent(this, ContactsActivity.class));
        return;

      case R.id.navigation_planner:
        startActivity(new Intent(this, PlanerActivity.class));
        return;

      default:
        startActivity(new Intent(this, NewsActivity.class));
        return;
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

  protected int getCurrentNavDrawerItem() {
    return R.id.navigation_invalid;
  }

  @Override public boolean onNavigationItemSelected(android.view.MenuItem menuItem) {
    if (!isPaused) {
      menuItem.setChecked(true);
      mSelectedNavItem = menuItem.getItemId();
      navigateTo(mSelectedNavItem);
      mDrawerLayout.closeDrawers();
    }

    return false;
  }

  @Override public void onClick(View v) {
    if (mUserId != null && !isPaused) {
      Intent intent = new Intent(this, UserDetailsActivity.class);
      intent.putExtra(UsersContract.Columns.USER_ID, mUserId);
      startActivity(intent);
      mDrawerLayout.closeDrawers();
    }
  }
}
