/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import de.elanev.studip.android.app.backend.db.AbstractContract;
import de.elanev.studip.android.app.backend.db.UsersContract;
import de.elanev.studip.android.app.backend.net.SyncHelper;
import de.elanev.studip.android.app.frontend.contacts.ContactsGroupsFragment;
import de.elanev.studip.android.app.frontend.courses.CoursesFragment;
import de.elanev.studip.android.app.frontend.messages.MessagesListFragment;
import de.elanev.studip.android.app.frontend.news.NewsListFragment;
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
public class MainActivity extends ActionBarActivity {
  public static final String TAG = MainActivity.class.getSimpleName();
  public static final String ACTIVE_NAVIGATION_ITEM = "active_navi_item";
  private static int mPosition = 0;
  public DrawerLayout mDrawerLayout;
  public ListView mDrawerListView;
  public ActionBarDrawerToggle mDrawerToggle;
  private String mUserId;
  private MenuAdapter mAdapter;
  private boolean isPaused;

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    mDrawerToggle.onConfigurationChanged(newConfig);
  }

  @Override
  protected void onPause() {
    super.onPause();
    isPaused = true;
  }

  @Override
  protected void onPostCreate(Bundle savedInstanceState) {
    super.onPostCreate(savedInstanceState);
    // Sync the toggle state after onRestoreInstanceState has occurred.
    mDrawerToggle.syncState();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    outState.putInt(ACTIVE_NAVIGATION_ITEM, mPosition);
    super.onSaveInstanceState(outState);
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.main, menu);

    return super.onCreateOptionsMenu(menu);
  }

  @Override public boolean onOptionsItemSelected(android.view.MenuItem item) {
    // ABS specific drawer open and close code
    if (item.getItemId() == android.R.id.home) {

      if (mDrawerLayout.isDrawerOpen(mDrawerListView)) {
        mDrawerLayout.closeDrawer(mDrawerListView);
      } else {
        mDrawerLayout.openDrawer(mDrawerListView);
      }
    }

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

  @Override
  public void onBackPressed() {
    if (!ApiUtils.isOverApi11()) {
      return;
    }
    super.onBackPressed();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (isFinishing()) {
      return;
    }

    if (!Prefs.getInstance(this).isAppAuthorized()) {
      StuffUtil.startSignInActivity(this);
      finish();
    }

    setContentView(R.layout.activity_main);
    mAdapter = getNewMenuAdapter();

    mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
    mDrawerListView = (ListView) findViewById(R.id.left_drawer);

    mDrawerToggle = new ActionBarDrawerToggle(this,
        mDrawerLayout,
        R.drawable.ic_navigation_drawer,
        R.string.open_drawer,
        R.string.close_drawer) {

      public void onDrawerOpened(View drawerView) {
        supportInvalidateOptionsMenu();
      }

      /** Called when a drawer has settled in a completely closed state. */
      public void onDrawerClosed(View view) {
        // creates call to onPrepareOptionsMenu()
        supportInvalidateOptionsMenu();
      }
    };

    // Set the drawer toggle as the DrawerListener
    mDrawerLayout.setDrawerListener(mDrawerToggle);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);

    mDrawerLayout.setDrawerShadow(R.drawable.shadow_right, GravityCompat.START);

    mDrawerListView.setAdapter(mAdapter);
    mDrawerListView.setOnItemClickListener(new DrawerItemClickListener());

    if (savedInstanceState == null) changeFragment(mPosition);
    else changeFragment(savedInstanceState.getInt(ACTIVE_NAVIGATION_ITEM));

    mUserId = Prefs.getInstance(this).getUserId();

  }

  /* Creates a new MenuAdapter with the defined items */
  private MenuAdapter getNewMenuAdapter() {
    MenuAdapter adapter = new MenuAdapter(this);

    // Only show the menu items if the user is authorized with the API
    if (Prefs.getInstance(this).isAppAuthorized()) {
      adapter.add(new MenuItem(R.id.navigation_news,
          R.drawable.ic_menu_news,
          getString(R.string.News)));
      adapter.add(new MenuItem(R.id.navigation_courses,
          R.drawable.ic_menu_courses,
          getString(R.string.Courses)));
      adapter.add(new MenuItem(R.id.navigation_messages,
          R.drawable.ic_menu_messages,
          getString(R.string.Messages)));
      adapter.add(new MenuItem(R.id.navigation_contacts,
          R.drawable.ic_menu_community,
          getString(R.string.Contacts)));
      adapter.add(new MenuItem(R.id.navigation_planner,
          R.drawable.ic_menu_planner,
          getString(R.string.Planner)));
      if (mUserId != null) {
        adapter.add(new MenuItem(R.id.navigation_profile,
            R.drawable.ic_menu_profile,
            getString(R.string.Profile)));
      }

    }
    return adapter;
  }

  /*
   * Changes the fragment of this activity
   *
   * @param Fragment the fragment to change to
   */
  private void changeFragment(int position) {
    if (position != ListView.INVALID_POSITION && !mAdapter.isEmpty()) {
      MenuItem item = (MenuItem) mDrawerListView.getItemAtPosition(position);
      Fragment frag = null;
      String fragTag = null;

      if (item != null) {
        switch (item.id) {
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
              fragTag = UserDetailsActivity.UserDetailsFragment.class.getName();
              frag = findFragment(fragTag);

              if (frag == null) {
                Bundle args = new Bundle();
                args.putString(UsersContract.Columns.USER_ID, mUserId);
                frag = UserDetailsActivity.UserDetailsFragment.newInstance(args);
              }
            } else {
              mDrawerLayout.closeDrawers();
              return;
            }
            break;
          default:
            frag = new NewsListFragment();
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, frag, fragTag).commit();

        mDrawerListView.setItemChecked(position, true);
        mDrawerLayout.closeDrawers();

      }
    } else {
      mDrawerListView.setAdapter(getNewMenuAdapter());
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

  @Override
  protected void onStart() {
    super.onStart();
    isPaused = false;
  }

  /* Implementation of a ListView OnItemClickListener for the Navigation Drawer items */
  private class DrawerItemClickListener implements ListView.OnItemClickListener {

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      if (isPaused) {
        // to prevent state loss do nothing when the activity is paused
        return;
      }
      mPosition = position;
      changeFragment(position);
    }
  }

  /* Represents a menu item and encapsulates the needed information */
  private class MenuItem {
    public int id;
    public String tag;
    public int iconRes;

    /**
     * Creates a new MenuItem
     *
     * @param iconRes res id for the item icon
     * @param tag     the item tag
     */
    public MenuItem(int id, int iconRes, String tag) {
      this.id = id;
      this.tag = tag;
      this.iconRes = iconRes;
    }
  }

  /**
   * An array adapter which holds MenuItems
   */
  public class MenuAdapter extends ArrayAdapter<MainActivity.MenuItem> {

    /**
     * Creates a new MenuArrayAdapter for the context
     *
     * @param context the execution context
     */
    public MenuAdapter(Context context) {
      super(context, 0);
    }

    /**
     * Returns the menu item view
     */
    public View getView(int position, View convertView, ViewGroup parent) {
      if (convertView == null) {
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_menu, null);
      }
      ImageView icon = (ImageView) convertView.findViewById(R.id.menuItemImage);
      icon.setImageResource(getItem(position).iconRes);
      TextView title = (TextView) convertView.findViewById(R.id.menuItemText);
      title.setText(getItem(position).tag);

      return convertView;
    }

  }

}
