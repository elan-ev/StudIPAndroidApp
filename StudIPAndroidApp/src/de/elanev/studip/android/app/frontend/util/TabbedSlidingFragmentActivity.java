/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.frontend.util;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

/**
 * @author joern
 * 
 */
public class TabbedSlidingFragmentActivity extends BaseSlidingFragmentActivity {
	public static final String ACTIVE_TAB = "activeTab";

	/**
	 * @param titleRes
	 */
	public TabbedSlidingFragmentActivity(int titleRes) {
		super(titleRes);
	}

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

		// Setting ABS to tabs mode
		mActionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		if (savedInstanceState != null) {
			int selectedTab = savedInstanceState.getInt(ACTIVE_TAB);
			if (mActionbar.getNavigationItemCount() > 0)
				mActionbar.setSelectedNavigationItem(selectedTab);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(ACTIVE_TAB, mActionbar.getSelectedNavigationIndex());
		super.onSaveInstanceState(outState);
	}

	public static class BasePagerTabsAdapter extends FragmentPagerAdapter
			implements ActionBar.TabListener, ViewPager.OnPageChangeListener {

		private final ViewPager mViewPager;
		private final Context mContext;
		private final ActionBar mActionBar;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

		static final class TabInfo {
			private final Class<?> clss;
			private final Bundle args;

			TabInfo(Class<?> _class, Bundle _args) {
				clss = _class;
				args = _args;
			}
		}

		public BasePagerTabsAdapter(Activity activity, FragmentManager fm,
				ViewPager pager, ActionBar actionbar) {
			super(fm);
			mContext = activity;
			mViewPager = pager;
			mActionBar = actionbar;
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);
		}

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

		@Override
		public int getCount() {
			return mTabs.size();
		}

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
			switch (position) {
			case 0:
				((SlidingFragmentActivity) mContext).getSlidingMenu()
						.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
				break;
			default:
				((SlidingFragmentActivity) mContext).getSlidingMenu()
						.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
				break;
			}
			mActionBar.setSelectedNavigationItem(position);
		}

		public void onPageScrollStateChanged(int state) {
		}

		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			Object tag = tab.getTag();
			for (int i = 0; i < mTabs.size(); i++) {
				if (mTabs.get(i) == tag) {
					mViewPager.setCurrentItem(i);
				}
			}
		}

		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		}

		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}

	}
}
