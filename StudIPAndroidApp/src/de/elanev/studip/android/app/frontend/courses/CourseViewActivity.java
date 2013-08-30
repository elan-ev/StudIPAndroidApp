/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.frontend.courses;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.app.ActionBar;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.db.CoursesContract;
import de.elanev.studip.android.app.frontend.util.TabbedSlidingFragmentActivity;

public class CourseViewActivity extends TabbedSlidingFragmentActivity {
	public String mCourse = null;
	ViewPager mPager;
	BasePagerTabsAdapter mPagerAdapter;

	/**
	 * @param titleRes
	 */
	public CourseViewActivity() {
		super(R.string.Courses);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setting ABS to tabs mode
		mActionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Get intent data
		Bundle intentExtras = getIntent().getExtras();

		// ViewPager, PagetAdapter setup
		mPager = new ViewPager(this);
		mPager.setId("VP".hashCode());
		setContentView(mPager);
		mPagerAdapter = new BasePagerTabsAdapter(this,
				getSupportFragmentManager(), mPager, mActionbar);
		mPagerAdapter.addTab(mActionbar.newTab(), R.drawable.ic_action_seminar,
				R.string.Overview, CourseOverviewFragment.class, intentExtras);
		mPagerAdapter.addTab(mActionbar.newTab(),
				R.drawable.ic_action_schedule, R.string.Schedule,
				CourseScheduleFragment.class, intentExtras);
		mPagerAdapter.addTab(mActionbar.newTab(),
				R.drawable.ic_action_attendees, R.string.attendees,
				CourseAttendeesFragment.class, intentExtras);
		mPagerAdapter
				.addTab(mActionbar.newTab(), R.drawable.ic_action_files,
						R.string.Documents, CourseDocumentsFragment.class,
						intentExtras);

		// Setting Activity title
		setTitle(getIntent().getStringExtra(
				CoursesContract.Columns.Courses.COURSE_TITLE));

	}
}
