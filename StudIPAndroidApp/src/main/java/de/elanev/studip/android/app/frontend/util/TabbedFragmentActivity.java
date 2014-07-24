/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.frontend.util;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import java.util.ArrayList;

/**
 * @author joern
 */
public class TabbedFragmentActivity extends ActionBarActivity {
    public static final String ACTIVE_TAB = "activeTab";
    protected ActionBar mActionbar;

    /*
     * (non-Javadoc)
     *
     * @see
     * de.elanev.studip.android.app.frontend.util.BaseSlidingFragmentActivity
     * #onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mActionbar = getSupportActionBar();

        // Setting ABS to tabs mode
        mActionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mActionbar.setHomeButtonEnabled(true);

        if (savedInstanceState != null) {
            int selectedTab = savedInstanceState.getInt(ACTIVE_TAB);
            if (mActionbar.getNavigationItemCount() > 0)
                mActionbar.setSelectedNavigationItem(selectedTab);
        }
    }

    /*
     * Save the active tab when the activiy gets destroyed
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(ACTIVE_TAB, mActionbar.getSelectedNavigationIndex());
        super.onSaveInstanceState(outState);
    }

    /**
     * The basic page adapter which holds the tabs and provides the needed callbacks
     */
    public static class BasePagerTabsAdapter extends FragmentPagerAdapter
            implements ActionBar.TabListener, ViewPager.OnPageChangeListener {

        private final ViewPager mViewPager;
        private final Context mContext;
        private final ActionBar mActionBar;
        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

        /**
         * Constructs the BasePagerTabsAdapter and sets the needed fields
         *
         * @param activity  the instantiating activity
         * @param fm        a reference to the FragmentManager
         * @param pager     the ViewPager created in the activity
         * @param actionbar a reference to the ActionBar
         */
        public BasePagerTabsAdapter(Activity activity, FragmentManager fm,
                                    ViewPager pager, ActionBar actionbar) {
            super(fm);
            mContext = activity;
            mViewPager = pager;
            mActionBar = actionbar;
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
        }

        /**
         * Adds a new tab to the adapter to display it in the activity
         *
         * @param tab      a reference to the tab
         * @param iconRes  the icon res of the tab
         * @param titleRes the title res of the tab
         * @param clss     the class of the tabs fragment
         * @param args     optional arguments Bundle for the tab
         */
        public void addTab(ActionBar.Tab tab, int iconRes, int titleRes,
                           Class<?> clss, Bundle args) {
            TabInfo info = new TabInfo(clss, args);
            tab.setTag(info);
            tab.setText(titleRes);
            tab.setTabListener(this);
            tab.setIcon(iconRes);
            mTabs.add(info);
            mActionBar.addTab(tab);
            notifyDataSetChanged();
        }

        /**
         * Returns the count of the tabs in te adapter
         * @return count of tabs
         */
        @Override
        public int getCount() {
            return mTabs.size();
        }

        /**
         * Returns the Fragment at the passed adapter position
         * @param position the position in the adapter
         * @return the Fragment at the position in the adapter
         */
        @Override
        public Fragment getItem(int position) {
            TabInfo info = mTabs.get(position);
            return Fragment.instantiate(mContext, info.clss.getName(),
                    info.args);
        }

        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
        }

        public void onPageSelected(int position) {
            mActionBar.setSelectedNavigationItem(position);
        }

        public void onPageScrollStateChanged(int state) {
        }

        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            Object tag = tab.getTag();
            for (int i = 0; i < mTabs.size(); i++) {
                if (mTabs.get(i) == tag) {
                    mViewPager.setCurrentItem(i);
                }
            }
        }

        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
        }

        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
        }

      static final class TabInfo {
            private final Class<?> clss;
            private final Bundle args;

            TabInfo(Class<?> _class, Bundle _args) {
                clss = _class;
                args = _args;
            }
        }

    }
}
