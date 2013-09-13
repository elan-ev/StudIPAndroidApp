/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.frontend.courses;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;

import com.actionbarsherlock.view.MenuItem;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.db.CoursesContract;
import de.elanev.studip.android.app.frontend.util.TabbedFragmentActivity;

public class CourseViewActivity extends TabbedFragmentActivity {
	ViewPager mPager;
	BasePagerTabsAdapter mPagerAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.actionbarsherlock.app.SherlockFragmentActivity#onOptionsItemSelected
	 * (com.actionbarsherlock.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}

		return super.onOptionsItemSelected(item);
	}
}
