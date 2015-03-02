/*
 * Copyright (c) 2015 ELAN e.V.
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
import android.text.TextUtils;
import android.view.MenuItem;

import java.util.List;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.datamodel.Course;
import de.elanev.studip.android.app.backend.db.CoursesContract;
import de.elanev.studip.android.app.frontend.forums.ForumCategoriesListFragment;
import de.elanev.studip.android.app.frontend.util.TabbedFragmentActivity;

/**
 * Activity for displaying a ViewPager with tabs for course overview,
 * schedule, participants and documents.
 */
public class CourseViewActivity extends TabbedFragmentActivity {
  private static final String INTENT_EXTRAS = "intent_extras";
  static Bundle sExtras;
  static String sTitle;
  ViewPager mPager;
  BasePagerTabsAdapter mPagerAdapter;
  Course.Modules mModules = new Course.Modules();

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    // Get intent data
    if (savedInstanceState != null) {
      sExtras = savedInstanceState.getBundle(INTENT_EXTRAS);
    } else {
      Bundle intentExtras = getIntent().getExtras();
      if (intentExtras != null) {
        sExtras = intentExtras;
      }
    }

    if (sExtras != null) {
      sTitle = sExtras.getString(CoursesContract.Columns.Courses.COURSE_TITLE);
      setTitle(sTitle);

      String modulesJson = sExtras.getString(CoursesContract.Columns.Courses.COURSE_MODULES);
      if (!TextUtils.isEmpty(modulesJson)) {
        mModules = Course.Modules.fromJson(modulesJson);
      }
    }
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
        sExtras);


    if (mModules.schedule) {
      mPagerAdapter.addTab(mActionbar.newTab(),
          R.drawable.ic_action_schedule,
          R.string.Schedule,
          CourseScheduleFragment.class,
          sExtras);
    }
    if (mModules.participants) {
      mPagerAdapter.addTab(mActionbar.newTab(),
          R.drawable.ic_action_attendees,
          R.string.attendees,
          CourseAttendeesFragment.class,
          sExtras);
    }
    if (mModules.forum) {
      mPagerAdapter.addTab(mActionbar.newTab(),
          R.drawable.ic_action_forum,
          R.string.forum,
          ForumCategoriesListFragment.class,
          sExtras);
    }
    if (mModules.documents) {
      mPagerAdapter.addTab(mActionbar.newTab(),
          R.drawable.ic_action_files,
          R.string.Documents,
          CourseDocumentsFragment.class,
          sExtras);
    }
    if (mModules.recordings) {
      mPagerAdapter.addTab(mActionbar.newTab(),
          R.drawable.ic_action_recordings,
          R.string.Recordings,
          CourseRecordingsFragment.class,
          sExtras);
    }
    if (mModules.unizensus) {
      mPagerAdapter.addTab(mActionbar.newTab(),
          R.drawable.ic_action_unizensus,
          R.string.unizensus,
          CourseUnizensusFragment.class,
          sExtras);
    }


  }

  @Override public void onSaveInstanceState(Bundle outState) {
    outState.putBundle(INTENT_EXTRAS, sExtras);
    super.onSaveInstanceState(outState);
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
