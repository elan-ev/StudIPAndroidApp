/*
 * Copyright (c) 2015 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package de.elanev.studip.android.app;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import de.elanev.studip.android.app.backend.db.AbstractContract;
import de.elanev.studip.android.app.backend.db.UsersContract;
import de.elanev.studip.android.app.backend.net.SyncHelper;
import de.elanev.studip.android.app.frontend.contacts.ContactsGroupsFragment;
import de.elanev.studip.android.app.frontend.courses.CoursesFragment;
import de.elanev.studip.android.app.frontend.messages.MessagesListFragment;
import de.elanev.studip.android.app.frontend.news.NewsTabsFragment;
import de.elanev.studip.android.app.frontend.planer.PlannerFragment;
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
    NavigationView.OnNavigationItemSelectedListener {
  public static final String TAG = MainActivity.class.getSimpleName();
  public static final String ACTIVE_NAVIGATION_ITEM = "active_navi_item";
  private static int mPosition = 0;
  public DrawerLayout mDrawerLayout;
  public ActionBarDrawerToggle mDrawerToggle;
  private String mUserId;
  private boolean isPaused;
  private Toolbar mToolbar;
  private CoordinatorLayout mCoordinatorLayout;
  private NavigationView mNavigationView;

  @Override public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    mDrawerToggle.onConfigurationChanged(newConfig);
  }

  @Override protected void onPause() {
    super.onPause();
    isPaused = true;
  }

  @Override protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    // Sync the toggle state after onRestoreInstanceState has occurred.
    mDrawerToggle.syncState();
  }

  @Override protected void onSaveInstanceState(Bundle outState) {
    outState.putInt(ACTIVE_NAVIGATION_ITEM, mPosition);
    super.onSaveInstanceState(outState);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);

    return super.onCreateOptionsMenu(menu);
  }

  @Override public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (mDrawerToggle.onOptionsItemSelected(item)) return true;

    switch (item.getItemId()) {
      case R.id.menu_feedback:
        StuffUtil.startFeedback(this, Prefs.getInstance(this).getServer());
        return true;
      case R.id.menu_about:
        StuffUtil.startAbout(this);
        return true;
      case R.id.menu_sign_out:
        logout();
        return true;
    }

    return super.onOptionsItemSelected(item);
  }

  /*
     * Deletes the preferences and database to logout of the service
     */
  private void logout() {
    //Cancel all pending network requests
    StudIPApplication.getInstance().cancelAllPendingRequests(SyncHelper.TAG);

    // Resetting the SyncHelper
    SyncHelper.getInstance(this).resetSyncHelper();

    // Clear the app preferences
    Prefs.getInstance(this).clearPrefs();

    // Delete the app database
    getContentResolver().delete(AbstractContract.BASE_CONTENT_URI, null, null);

    StuffUtil.startSignInActivity(this);
    finish();
  }

  @Override public void onBackPressed() {
    if (!ApiUtils.isOverApi11()) {
      return;
    }
    super.onBackPressed();
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (isFinishing()) {
      return;
    }

    if (!Prefs.getInstance(this).isAppAuthorized()) {
      StuffUtil.startSignInActivity(this);
      finish();
      return;
    }

    // Verify that the forum routes are still activated
    //TODO: Move this and other API checks to a Service or something, but not here
    SyncHelper.getInstance(this).requestApiRoutes(null);
    SyncHelper.getInstance(this).getSettings(null);

    setContentView(R.layout.activity_new_main);

    mUserId = Prefs.getInstance(this).getUserId();

    initToolbar();
    initNavigation();

    //    if (savedInstanceState == null) changeFragment(mPosition);
    //    else changeFragment(savedInstanceState.getInt(ACTIVE_NAVIGATION_ITEM));
  }

  private void initNavigation() {
    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

    mDrawerToggle = new ActionBarDrawerToggle(MainActivity.this,
        mDrawerLayout,
        R.string.open_drawer,
        R.string.close_drawer);
    mDrawerLayout.setDrawerListener(mDrawerToggle);

    mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.root_layout);
    mNavigationView = (NavigationView) findViewById(R.id.navigation);
    mNavigationView.setNavigationItemSelectedListener(this);
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

  /*
   * Searches for a fragment for the given tag
   *
   * @return the found fragment or null
   *
   */
  private Fragment findFragment(String tag) {
    return getSupportFragmentManager().findFragmentByTag(tag);
  }

  @Override protected void onStart() {
    super.onStart();
    isPaused = false;
  }

  @Override public boolean onNavigationItemSelected(android.view.MenuItem menuItem) {
    menuItem.setChecked(true);
    Fragment frag;
    String fragTag;

    switch (menuItem.getItemId()) {
      case R.id.navigation_news:
        fragTag = NewsTabsFragment.class.getName();
        frag = findFragment(fragTag);
        if (frag == null) {
          frag = new NewsTabsFragment();
        }
        break;
      case R.id.navigation_courses:
        fragTag = CoursesFragment.class.getName();
        frag = findFragment(fragTag);
        if (frag == null) {
          frag = new CoursesFragment();
        }
        break;
      case R.id.navigation_messages:
        fragTag = MessagesListFragment.class.getName();
        frag = findFragment(fragTag);
        if (frag == null) {
          frag = new MessagesListFragment();
        }
        break;
      case R.id.navigation_contacts:
        fragTag = ContactsGroupsFragment.class.getName();
        frag = findFragment(fragTag);
        if (frag == null) {
          frag = new ContactsGroupsFragment();
        }
        break;
      case R.id.navigation_planner:
        fragTag = PlannerFragment.class.getName();
        frag = findFragment(fragTag);
        if (frag == null) {
          frag = new PlannerFragment();
        }
        break;
      case R.id.navigation_profile:
        if (mUserId != null) {
          Intent intent = new Intent(this, UserDetailsActivity.class);
          intent.putExtra(UsersContract.Columns.USER_ID, mUserId);
          startActivity(intent);
        }
      default:
        frag = new NewsTabsFragment();
    }
    changeFragment(frag);
    mDrawerLayout.closeDrawers();
    return false;
  }


  private void changeFragment(Fragment frag) {
    FragmentManager fragmentManager = getSupportFragmentManager();
    fragmentManager.beginTransaction()
        .replace(R.id.content_frame, frag, frag.getClass().getName())
        .commit();
  }


}
