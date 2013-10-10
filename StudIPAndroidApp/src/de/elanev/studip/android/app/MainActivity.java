/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.sherlock.navigationdrawer.compat.SherlockActionBarDrawerToggle;

import de.elanev.studip.android.app.backend.db.AbstractContract;
import de.elanev.studip.android.app.backend.net.oauth.SignInActivity;
import de.elanev.studip.android.app.frontend.AboutFragment;
import de.elanev.studip.android.app.frontend.contacts.ContactsGroupsFragment;
import de.elanev.studip.android.app.frontend.courses.CoursesFragment;
import de.elanev.studip.android.app.frontend.messages.MessagesListFragment;
import de.elanev.studip.android.app.frontend.news.NewsListFragment;
import de.elanev.studip.android.app.util.Prefs;

/**
 * @author joern
 *         <p/>
 *         Activity holding the navigation drawer and content frame.
 *         It manages the navigation and content fragments.
 */
public class MainActivity extends SherlockFragmentActivity {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final String ACTIVE_NAVIGATION_ITEM = "active_navi_item";
    private static int mPosition = 0;
    public DrawerLayout mDrawerLayout;
    public ListView mDrawerListView;
    public SherlockActionBarDrawerToggle mDrawerToggle;

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerListView = (ListView) findViewById(R.id.left_drawer);

        mDrawerToggle = new SherlockActionBarDrawerToggle(this, mDrawerLayout,
                R.drawable.ic_navigation_drawer, R.string.open_drawer,
                R.string.close_drawer) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                // creates call to onPrepareOptionsMenu()
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerLayout.setDrawerShadow(R.drawable.shadow_right,
                GravityCompat.START);

        mDrawerListView.setAdapter(getNewMenuAdapter());
        mDrawerListView.setOnItemClickListener(new DrawerItemClickListener());

        if (savedInstanceState == null)
            changeFragment(mPosition);
        else
            changeFragment(savedInstanceState.getInt(ACTIVE_NAVIGATION_ITEM));

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.actionbarsherlock.app.SherlockFragmentActivity#onPostCreate(android
     * .os.Bundle)
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.actionbarsherlock.app.SherlockFragmentActivity#onSaveInstanceState(android
     * .os.Bundle)
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(ACTIVE_NAVIGATION_ITEM, mPosition);
        super.onSaveInstanceState(outState);
    }

    /*
    * (non-Javadoc)
    *
    * @see
    * com.actionbarsherlock.app.SherlockFragmentActivity#onConfigurationChanged
    * (android.content.res.Configuration)
    */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.actionbarsherlock.app.SherlockFragmentActivity#onOptionsItemSelected
     * (com.actionbarsherlock.view.MenuItem)
     */
    @Override
    public boolean onOptionsItemSelected(
            com.actionbarsherlock.view.MenuItem item) {

        // ABS specific drawer open and close code
        if (item.getItemId() == android.R.id.home) {

            if (mDrawerLayout.isDrawerOpen(mDrawerListView)) {
                mDrawerLayout.closeDrawer(mDrawerListView);
            } else {
                mDrawerLayout.openDrawer(mDrawerListView);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    /*
     * Changes the fragment of this activity
     *
     * @param Fragment the fragment to change to
     */
    private void changeFragment(int position) {
        if (position != ListView.INVALID_POSITION) {
            MenuItem item = (MenuItem) mDrawerListView.getItemAtPosition(position);
            Fragment frag = null;
            String fragTag = null;

            if (item != null) {
                switch (item.id) {
                    case R.id.navigation_news:
                        fragTag = NewsListFragment.class.getName();
                        frag = findFragment(fragTag);
                        if (frag == null) {
                            frag = new NewsListFragment();
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
                    // case SETTINGS_MENU_ITEM:
                    // TODO: Settings for prefetch behavior, Stud.IP installation, appearance
                    // break;
                    // case HELP_MENU_ITEM:
                    // TODO: Information about how to use the app
                    // break;
                    case R.id.navigation_information:
                        fragTag = AboutFragment.class.getName();
                        frag = findFragment(fragTag);
                        if (frag == null) {
                            frag = new AboutFragment();
                        }
                        break;
                    case R.id.navigation_feedback:
                        Intent intent = new Intent(Intent.ACTION_SENDTO,
                                Uri.fromParts("mailto",
                                        getString(R.string.feedback_form_email),
                                        null));
                        intent.putExtra(Intent.EXTRA_SUBJECT,
                                getString(R.string.feedback_form_subject));
                        intent.putExtra(
                                Intent.EXTRA_TEXT,
                                String.format(
                                        getString(R.string.feedback_form_message_template),
                                        Build.VERSION.SDK_INT,
                                        BuildConfig.VERSION_NAME,
                                        BuildConfig.VERSION_CODE,
                                        BuildConfig.BUILD_TIME));

                        startActivity(Intent.createChooser(intent,
                                getString(R.string.feedback_form_action)));
                        return;
                    case R.id.navigation_logout:
                        logout();
                        mDrawerLayout.closeDrawers();
                        return;
                }
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_frame, frag, fragTag).commit();

                mDrawerListView.setItemChecked(position, true);
                mDrawerLayout.closeDrawers();

            }
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

    /*
     * Deletes the preferences and database to logout of the service
     */
    private void logout() {
        // Clear the app preferences
        Prefs.getInstance(this).clearPrefs();

        // Delete the app database
        getContentResolver().delete(AbstractContract.BASE_CONTENT_URI, null,
                null);

        // Resetting the UI
        mPosition = 0;

        // Start an intent so show the sign in screen
        Intent intent = new Intent(this, SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    /*
     * Creates a new MenuAdapter with the defined items
     */
    private MenuAdapter getNewMenuAdapter() {
        MenuAdapter adapter = new MenuAdapter(this);

        // Only show the menu items if the user is authorized with the API
        if (Prefs.getInstance(this).isAppAuthorized()) {
            adapter.add(
                    new MenuItem(
                            R.id.navigation_news,
                            R.drawable.ic_menu_news,
                            getString(R.string.News)));
            adapter.add(
                    new MenuItem(
                            R.id.navigation_courses,
                            R.drawable.ic_menu_courses,
                            getString(R.string.Courses)));
            adapter.add(
                    new MenuItem(
                            R.id.navigation_messages,
                            R.drawable.ic_menu_messages,
                            getString(R.string.Messages)));
            adapter.add(
                    new MenuItem(
                            R.id.navigation_contacts,
                            R.drawable.ic_menu_community,
                            getString(R.string.Contacts)));
            adapter.add(
                    new MenuItem(
                            R.id.navigation_feedback,
                            R.drawable.ic_menu_feedback,
                            getString(R.string.Feedback)));
            adapter.add(
                    new MenuItem(
                            R.id.navigation_information,
                            R.drawable.ic_menu_info,
                            getString(R.string.about_studip_mobile)));
            adapter.add(
                    new MenuItem(
                            R.id.navigation_logout,
                            R.drawable.ic_menu_logout,
                            getString(R.string.Logout)));
        }
        return adapter;
    }

    /*
     * Implementation of a ListVoew OnItemClickListener for the Navigation
     * Drawer items
     */
    private class DrawerItemClickListener implements
            ListView.OnItemClickListener {

        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {

            mPosition = position;
            changeFragment(position);
        }
    }

    /*
     * Represents a menu item and encapsulates the needed informations
     */
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
    public class MenuAdapter extends ArrayAdapter<MenuItem> {

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
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.list_item_menu, null);
            }
            ImageView icon = (ImageView) convertView
                    .findViewById(R.id.menuItemImage);
            icon.setImageResource(getItem(position).iconRes);
            TextView title = (TextView) convertView
                    .findViewById(R.id.menuItemText);
            title.setText(getItem(position).tag);

            return convertView;
        }

    }

}
