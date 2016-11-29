/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.presentation.view;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.courses.presentation.model.CourseModulesModel;
import de.elanev.studip.android.app.data.db.CoursesContract;
import de.elanev.studip.android.app.forums.ForumCategoriesListFragment;
import de.elanev.studip.android.app.util.Prefs;

/**
 * Activity for displaying a ViewPager with tabs for course overview,
 * schedule, participants and documents.
 */
public class CourseViewActivity extends AppCompatActivity {
  public static final String COURSE_ID = "course_id";
  public static final String COURSE_MODULES = "course_modules";
  private static final String INTENT_EXTRAS = "intent_extras";
  static Bundle sExtras;
  static String sTitle;
  private ViewPager mPager;
  private TabLayout mTabLayout;
  private FragmentsAdapter mPagerAdapter;
  private CourseModulesModel modules = new CourseModulesModel();

  public static Intent getCallingIntent(Context context) {
    return new Intent(context, CourseViewActivity.class);
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.viewpager_with_toolbar);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setHomeButtonEnabled(true);
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

    mPager = (ViewPager) findViewById(R.id.pager);
    mTabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
    mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

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

      modules = (CourseModulesModel) sExtras.getSerializable(COURSE_MODULES);
    } else {
      finish();
      return;
    }

    mPagerAdapter = new FragmentsAdapter(getSupportFragmentManager(), getTabs());
    mPager.setAdapter(mPagerAdapter);
    mTabLayout.setupWithViewPager(mPager);
  }

  private ArrayList<Tab> getTabs() {
    ArrayList<Tab> tabs = new ArrayList<>();
    // Add the tabs to the PagerAdapter, if activated.
    tabs.add(new Tab(getString(R.string.Overview), CourseOverviewFragment.class, sExtras));

    if (modules.isSchedule()) {
      tabs.add(new Tab(getString(R.string.Schedule), CourseScheduleFragment.class, sExtras));
    }
    if (modules.isParticipants()) {
      tabs.add(new Tab(getString(R.string.attendees), CourseAttendeesFragment.class, sExtras));
    }
    if (modules.isForum() && Prefs.getInstance(this)
        .isForumActivated()) {
      tabs.add(new Tab(getString(R.string.forum), ForumCategoriesListFragment.class, sExtras));
    }
    if (modules.isDocuments() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
      tabs.add(new Tab(getString(R.string.Documents), CourseDocumentsFragment.class, sExtras));
    }
    if (modules.isRecordings()) {
      tabs.add(new Tab(getString(R.string.Recordings), CourseRecordingsFragment.class, sExtras));
    }
    if (modules.isUnizensus()) {
      tabs.add(new Tab(getString(R.string.unizensus), CourseUnizensusFragment.class, sExtras));
    }

    return tabs;
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    outState.putBundle(INTENT_EXTRAS, sExtras);
    super.onSaveInstanceState(outState);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      // Respond to the action bar's Up/Home button
      case android.R.id.home:
        NavUtils.navigateUpFromSameTask(this);
        return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override public void onBackPressed() {
    String activeFragmentTag = "android:switcher:" + R.id.pager + ":" + mPager.getCurrentItem();
    FragmentManager fragmentManager = getSupportFragmentManager();

    Fragment fragment = fragmentManager.findFragmentByTag(activeFragmentTag);
    if (fragment != null && fragment instanceof CourseDocumentsFragment) {
      if (((CourseDocumentsFragment) fragment).onBackPressed()) {
        return;
      } else {
        super.onBackPressed();
      }
    }

    super.onBackPressed();
  }

  public static class FragmentsAdapter extends FragmentPagerAdapter {

    private ArrayList<Tab> mTabs = new ArrayList<Tab>();

    public FragmentsAdapter(FragmentManager fm) {
      super(fm);
    }

    public FragmentsAdapter(FragmentManager fm, ArrayList<Tab> tabs) {
      super(fm);
      mTabs = tabs;
    }

    public void addTab(Tab tab) {
      mTabs.add(tab);
    }

    @Override public Fragment getItem(int position) {
      Tab tab = mTabs.get(position);

      try {
        Method m = tab.clss.getMethod("newInstance", Bundle.class);

        return (Fragment) m.invoke(null, tab.args);
      } catch (NoSuchMethodException e) {
        e.printStackTrace();
      } catch (InvocationTargetException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }

      return CourseOverviewFragment.newInstance(tab.args);
    }

    @Override public int getCount() {
      return mTabs.size();
    }

    @Override public CharSequence getPageTitle(int position) {
      return mTabs.get(position).title;
    }
  }

  static final class Tab {
    CharSequence title;
    Class<?> clss;
    Bundle args;

    public Tab(CharSequence title, Class clss, Bundle args) {
      this.title = title;
      this.clss = clss;
      this.args = args;
    }
  }

}
