/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.frontend.news;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.db.CoursesContract;
import de.elanev.studip.android.app.frontend.util.TabbedSlidingFragmentActivity;

/**
 * @author joern
 * 
 */
public class NewsViewActivity extends TabbedSlidingFragmentActivity {

	ViewPager mPager;
	BasePagerTabsAdapter mPagerAdapter;

	public NewsViewActivity() {
		super(R.string.News);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ViewPager, PagetAdapter setup
		mPager = new ViewPager(this);
		mPager.setId("VP".hashCode());
		setContentView(mPager);
		mPagerAdapter = new BasePagerTabsAdapter(this,
				getSupportFragmentManager(), mPager, mActionbar);
		mPagerAdapter.addTab(mActionbar.newTab(), R.string.Global,
				GlobalNewsFragment.class, null);
		mPagerAdapter.addTab(mActionbar.newTab(), R.string.Courses,
				CourseNewsFragment.class, null);

		// Setting Activity title
		setTitle(getIntent().getStringExtra(
				CoursesContract.Columns.Courses.COURSE_TITLE));

	}

}
