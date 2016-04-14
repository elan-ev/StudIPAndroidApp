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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.elanev.studip.android.app.backend.datamodel.Postbox;
import de.elanev.studip.android.app.backend.datamodel.Server;
import de.elanev.studip.android.app.backend.datamodel.User;
import de.elanev.studip.android.app.backend.db.AbstractContract;
import de.elanev.studip.android.app.backend.db.UsersContract;
import de.elanev.studip.android.app.backend.net.SyncHelper;
import de.elanev.studip.android.app.backend.net.services.StudIpLegacyApiService;
import de.elanev.studip.android.app.frontend.contacts.ContactsActivity;
import de.elanev.studip.android.app.frontend.courses.CoursesActivity;
import de.elanev.studip.android.app.frontend.messages.MessagesActivity;
import de.elanev.studip.android.app.frontend.news.NewsActivity;
import de.elanev.studip.android.app.frontend.planner.PlannerActivity;
import de.elanev.studip.android.app.util.ApiUtils;
import de.elanev.studip.android.app.util.Prefs;
import de.elanev.studip.android.app.util.StuffUtil;
import de.elanev.studip.android.app.widget.UserDetailsActivity;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

/**
 * @author joern
 *         <p/>
 *         Activity holding the navigation drawer and content frame.
 *         It manages the navigation and content fragments.
 */
public class MainActivity extends AppCompatActivity implements
    NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
  private static final String TAG = MainActivity.class.getSimpleName();
  private static int mSelectedNavItem = R.id.navigation_invalid;
  private DrawerLayout mDrawerLayout;
  private ActionBarDrawerToggle mDrawerToggle;
  private String mUserId;
  private User mCurrentUser;
  private NavigationView mNavigationView;
  private View mHeaderView;
  private Handler mHandler;
  private CompositeSubscription mCompositeSubscription = new CompositeSubscription();
  private StudIpLegacyApiService mApiService;
  protected Toolbar mToolbar;

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

    if (!Prefs.getInstance(this)
        .isAppAuthorized()) {
      StuffUtil.startSignInActivity(this);
      finish();
      return;
    }
    mApiService = new StudIpLegacyApiService(Prefs.getInstance(this).getServer(), this);
    mHandler = new Handler();



    fetchPref();

    mCurrentUser = User.fromJson(Prefs.getInstance(this)
        .getUserInfo());
    if (mCurrentUser != null) {
      mUserId = mCurrentUser.userId;
    }

    initNavigation();
  }

  private void fetchPref() {
    // Verify that the forum routes are still activated
    //TODO: Move this and other API checks to a Service or something, but not here
    SyncHelper.getInstance(this)
        .requestApiRoutes(null);
    SyncHelper.getInstance(this)
        .getSettings(null);
    SyncHelper.getInstance(this)
        .requestCurrentUserInfo(null);
    mCompositeSubscription.add(mApiService.getMessageFolders(0, 10)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeOn(Schedulers.io())
        .subscribe(new Subscriber<Postbox>() {
          @Override public void onCompleted() {
            // Do something....
          }

          @Override public void onError(Throwable e) {
            Log.e(TAG, e.getLocalizedMessage());
          }

          @Override public void onNext(Postbox postbox) {
            Prefs.getInstance(MainActivity.this)
                .setPostbox(postbox);
          }
        }));
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
        Log.w(TAG, "No view with ID main_content to fade in.");
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
      intent.putExtra(UsersContract.Columns.USER_ID, mUserId);
      startActivity(intent);
      mDrawerLayout.closeDrawers();
    }
  }

  public interface OnShowProgressBarListener {
    public void onShowProgressBar(boolean show);
  }
}
