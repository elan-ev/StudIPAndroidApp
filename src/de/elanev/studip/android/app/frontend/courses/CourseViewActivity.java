/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.frontend.courses;

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
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.db.CoursesContract;
import de.elanev.studip.android.app.frontend.util.BaseSlidingFragmentActivity;

public class CourseViewActivity extends BaseSlidingFragmentActivity {

	/**
	 * @param titleRes
	 */
	public CourseViewActivity() {
		super(R.string.Courses);
	}

	public String mCourse = null;
	public static ActionBar mActionbar = null;
	public static final String ACTIVE_TAB = "activeTab";
	ViewPager mPager;
	CoursePagerTabsAdapter mPagerAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActionbar = getSupportActionBar();
		mCourse = getIntent().getStringExtra(CoursesContract.Columns.COURSE_ID);
		setContentView(R.layout.pager);

		mPager = new ViewPager(this);
		mPager.setId("VP".hashCode());
		setContentView(mPager);

		mActionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		setTitle(getIntent().getStringExtra(
				CoursesContract.Columns.COURSE_TITLE));
		mPagerAdapter = new CoursePagerTabsAdapter(this,
				getSupportFragmentManager(), mPager);
		Bundle args = new Bundle();
		args.putString("cid", mCourse);
		mPagerAdapter.addTab(mActionbar.newTab(), R.string.appointments,
				CourseEventsFragment.class, args);
		mPagerAdapter.addTab(mActionbar.newTab(), R.string.attendees,
				CourseAttendeesFragment.class, args);
		mPagerAdapter.addTab(mActionbar.newTab(), R.string.Documents,
				CourseDocumentsFragment.class, args);

		if (savedInstanceState != null) {
			mActionbar.setSelectedNavigationItem(savedInstanceState.getInt(
					"tab", 0));
		}

		if (savedInstanceState != null) {
			this.getSupportActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(ACTIVE_TAB));
		}

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putInt(ACTIVE_TAB, this.getSupportActionBar()
				.getSelectedNavigationIndex());
		super.onSaveInstanceState(outState);
	}

	public static class CoursePagerTabsAdapter extends FragmentPagerAdapter
			implements ActionBar.TabListener, ViewPager.OnPageChangeListener {

		private final ViewPager mViewPager;
		private final Context mContext;
		private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

		static final class TabInfo {
			private final Class<?> clss;
			private final Bundle args;

			TabInfo(Class<?> _class, Bundle _args) {
				clss = _class;
				args = _args;
			}
		}

		public CoursePagerTabsAdapter(Activity activity, FragmentManager fm,
				ViewPager pager) {
			super(fm);
			mContext = activity;
			mViewPager = pager;
			mViewPager.setAdapter(this);
			mViewPager.setOnPageChangeListener(this);
		}

		public void addTab(ActionBar.Tab tab, int titleRes, Class<?> clss,
				Bundle args) {
			TabInfo info = new TabInfo(clss, args);
			tab.setTag(info);
			tab.setText(titleRes);
			tab.setTabListener(this);
			mTabs.add(info);
			mActionbar.addTab(tab);
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
			mActionbar.setSelectedNavigationItem(position);
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
