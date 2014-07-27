/*
 * Copyright (c) 2014 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package de.elanev.studip.android.app.frontend.courses;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import java.util.List;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.datamodel.Course;
import de.elanev.studip.android.app.backend.db.CoursesContract;
import de.elanev.studip.android.app.frontend.util.TabbedFragmentActivity;

/**
 * Activity for displaying a ViewPager with tabs for course overview,
 * schedule, participants and documents.
 */
public class CourseViewActivity extends TabbedFragmentActivity {
  ViewPager mPager;
  BasePagerTabsAdapter mPagerAdapter;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);


    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    // Get intent data
    Bundle intentExtras = getIntent().getExtras();

    // Get activated modules for course
    String modulesJson = intentExtras.getString(CoursesContract.Columns.Courses.COURSE_MODULES);
    Course.Modules modules = Course.Modules.fromJson(modulesJson);

    // ViewPager, PageAdapter setup
    mPager = new ViewPager(this);
    mPager.setId("VP".hashCode());
    setContentView(mPager);
    mPagerAdapter = new BasePagerTabsAdapter(this, getSupportFragmentManager(), mPager, mActionbar);

    // Add the tabs to the PagerAdapter, if activated.
    mPagerAdapter.addTab(mActionbar.newTab(),
        R.drawable.ic_action_seminar,
        R.string.Overview,
        CourseOverviewFragment.class,
        intentExtras);

    if (modules.schedule) {
      mPagerAdapter.addTab(mActionbar.newTab(),
          R.drawable.ic_action_schedule,
          R.string.Schedule,
          CourseScheduleFragment.class,
          intentExtras);
    }
    if (modules.participants) {
      mPagerAdapter.addTab(mActionbar.newTab(),
          R.drawable.ic_action_attendees,
          R.string.attendees,
          CourseAttendeesFragment.class,
          intentExtras);
    }
    if (modules.documents) {
      mPagerAdapter.addTab(mActionbar.newTab(),
          R.drawable.ic_action_files,
          R.string.Documents,
          CourseDocumentsFragment.class,
          intentExtras);
    }
    if (modules.recordings) {
      mPagerAdapter.addTab(mActionbar.newTab(),
          R.drawable.ic_action_recordings,
          R.string.Recordings,
          CourseRecordingsFragment.class,
          intentExtras);
    }

    // Setting Activity title
    setTitle(getIntent().getStringExtra(CoursesContract.Columns.Courses.COURSE_TITLE));

  }

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


  @Override
  public void onBackPressed() {
    if (!returnBackStackImmediate(getSupportFragmentManager())) {
      super.onBackPressed();
    }
  }

  // HACK: propagate back button press to child fragments.
  // (http://android.joao.jp/2013/09/back-stack-with-nested-fragments-back.html)
  // FIXME: Fix with either a better hack, or an official solution
  private boolean returnBackStackImmediate(FragmentManager fm) {
    List<Fragment> fList = fm.getFragments();
    Fragment fActive = mPagerAdapter.getItem(mPager.getCurrentItem());
    if (fActive instanceof CourseDocumentsFragment) {
      if (fList != null && fList.size() > 0) {
        for (Fragment f : fList) {
          if (f.getChildFragmentManager().getBackStackEntryCount() > 0) {
            return f.getChildFragmentManager().popBackStackImmediate() || returnBackStackImmediate(f
                .getChildFragmentManager());
          }
        }
      }
    }
    return false;
  }
}
