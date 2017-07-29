/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.presentation.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.elanev.studip.android.app.AbstractStudIPApplication;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.base.internal.di.components.HasComponent;
import de.elanev.studip.android.app.base.presentation.view.BaseLceFragment;
import de.elanev.studip.android.app.base.presentation.view.activity.BaseActivity;
import de.elanev.studip.android.app.courses.internal.di.CoursesComponent;
import de.elanev.studip.android.app.courses.internal.di.CoursesModule;
import de.elanev.studip.android.app.courses.internal.di.DaggerCoursesComponent;
import de.elanev.studip.android.app.courses.presentation.model.CourseModulesModel;
import de.elanev.studip.android.app.courses.presentation.model.CourseUserModel;
import de.elanev.studip.android.app.forums.ForumCategoriesListFragment;
import de.elanev.studip.android.app.user.presentation.view.UserDetailsActivity;

/**
 * Activity for displaying a ViewPager with tabs for course overview,
 * schedule, participants and documents.
 */
public class CourseViewActivity extends BaseActivity implements HasComponent<CoursesComponent>,
    CourseAttendeesFragment.CourseUsersListListener,
    BaseLceFragment.OnComponentNotFoundErrorListener {
  public static final String COURSE_ID = "course-id";
  public static final String COURSE_MODULES = "course-modules";
  @BindView(R.id.pager) ViewPager mPager;
  @BindView(R.id.sliding_tabs) TabLayout mTabLayout;
  @BindView(R.id.toolbar) Toolbar toolbar;

  private CourseModulesModel modules = new CourseModulesModel();
  private CoursesComponent component;
  private String courseId;
  private Bundle args;

  public static Intent getCallingIntent(Context context) {
    return new Intent(context, CourseViewActivity.class);
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.viewpager_with_toolbar);

    args = getIntent().getExtras();
    modules = (CourseModulesModel) args.getSerializable(COURSE_MODULES);
    courseId = args.getString(COURSE_ID);

    initInjector();

    ButterKnife.bind(this);
    setUpToolbar();
    setUpViewPager();
  }

  private void initInjector() {
    this.component = DaggerCoursesComponent.builder()
        .applicationComponent(((AbstractStudIPApplication) getApplication()).getAppComponent())
        .coursesModule(new CoursesModule(courseId))
        .build();
  }

  private void setUpToolbar() {
    setSupportActionBar(toolbar);
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setHomeButtonEnabled(true);
      actionBar.setDisplayHomeAsUpEnabled(true);
    }

  }

  private void setUpViewPager() {
    mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
    FragmentsAdapter mPagerAdapter = new FragmentsAdapter(getSupportFragmentManager(), getTabs());
    mPager.setAdapter(mPagerAdapter);
    mTabLayout.setupWithViewPager(mPager);
  }

  private ArrayList<Tab> getTabs() {
    ArrayList<Tab> tabs = new ArrayList<>();
    // Add the tabs to the PagerAdapter, if activated.
    tabs.add(new Tab(getString(R.string.Overview), CourseOverviewFragment.class, args));

    if (modules.isSchedule()) {
      tabs.add(new Tab(getString(R.string.Schedule), CourseScheduleFragment.class, args));
    }
    if (modules.isParticipants()) {
      tabs.add(new Tab(getString(R.string.Participants), CourseAttendeesFragment.class, args));
    }
    if (modules.isForum()) {
      tabs.add(new Tab(getString(R.string.forum), ForumCategoriesListFragment.class, args));
    }
    if (modules.isDocuments()) {
      tabs.add(new Tab(getString(R.string.Documents), CourseDocumentsFragment.class, args));
    }
    if (modules.isRecordings()) {
      tabs.add(new Tab(getString(R.string.Recordings), CourseRecordingsFragment.class, args));
    }
    if (modules.isUnizensus()) {
      tabs.add(new Tab(getString(R.string.unizensus), CourseUnicensusFragment.class, args));
    }

    return tabs;
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

  @Override public CoursesComponent getComponent() {
    return this.component;
  }

  @Override public void onCourseUserClicked(CourseUserModel courseUserModel) {

    String userId = courseUserModel.getUserId();

    if (!TextUtils.isEmpty(userId)) {
      Intent intent = new Intent(this, UserDetailsActivity.class);
      Bundle args = new Bundle();
      args.putString(UserDetailsActivity.USER_ID, userId);
      intent.putExtras(args);

      startActivity(intent);
    }
  }

  @Override public void onComponentNotFound() {
    Toast.makeText(this, R.string.unknown_error, Toast.LENGTH_SHORT)
        .show();
    finish();
  }

  public static class FragmentsAdapter extends FragmentPagerAdapter {

    private ArrayList<Tab> mTabs = new ArrayList<>();

    FragmentsAdapter(FragmentManager fm, ArrayList<Tab> tabs) {
      super(fm);
      mTabs = tabs;
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
