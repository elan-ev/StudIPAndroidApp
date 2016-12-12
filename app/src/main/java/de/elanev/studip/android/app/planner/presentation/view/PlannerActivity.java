/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.planner.presentation.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import javax.inject.Inject;

import de.elanev.studip.android.app.AbstractStudIPApplication;
import de.elanev.studip.android.app.MainActivity;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.base.internal.di.components.HasComponent;
import de.elanev.studip.android.app.courses.presentation.view.CourseViewActivity;
import de.elanev.studip.android.app.planner.internal.di.DaggerPlannerComponent;
import de.elanev.studip.android.app.planner.internal.di.PlannerComponent;
import de.elanev.studip.android.app.planner.internal.di.PlannerModule;
import de.elanev.studip.android.app.planner.presentation.model.EventModel;
import de.elanev.studip.android.app.util.Prefs;

/**
 * @author joern
 */
public class PlannerActivity extends MainActivity implements HasComponent<PlannerComponent>,
    PlannerEventListener {
  public static final String PLANNER_VIEW_TIMETABLE = "planner-view-timetable";
  public static final String PLANNER_VIEW_LIST = "planner-view-list";
  private static final String PLANNER_PREFERRED_VIEW = "planner-preferred-view";
  private static final String FRAGMENT_TAG = "planner-fragment";
  @Inject Prefs prefs;
  private int mOrientation;
  private PlannerComponent plannerComponent;

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.planner_activity_menu, menu);

    return super.onCreateOptionsMenu(menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.isCheckable() && item.isChecked()) {
      item.setChecked(false);
    } else {
      item.setChecked(true);
    }

    switch (item.getItemId()) {
      case R.id.planner_goto_today:
        onScrollToCurrent();
        return true;
      case R.id.planner_timetable:
        shouldSwitchFragment(R.id.planner_timetable);
        return true;
      case R.id.planner_list:
        shouldSwitchFragment(R.id.planner_list);
        return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override protected int getCurrentNavDrawerItem() {
    return R.id.navigation_planner;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    initInjector();

    mOrientation = getResources().getConfiguration().orientation;
    String preferredView = prefs.getPreferredPlannerView(mOrientation);

    overridePendingTransition(0, 0);
    if (savedInstanceState == null) {
      initFragment(preferredView);
    }
  }

  private void initInjector() {
    getApplicationComponent().inject(this);
    this.plannerComponent = DaggerPlannerComponent.builder()
        .applicationComponent(((AbstractStudIPApplication) getApplication()).getAppComponent())
        .plannerModule(new PlannerModule())
        .build();
  }

  private void initFragment(String preferredView) {
    Bundle args = new Bundle();
    args.putString(PLANNER_PREFERRED_VIEW, preferredView);
    Fragment fragment;
    FragmentManager fragmentManager = getSupportFragmentManager();
    if (TextUtils.equals(preferredView, PLANNER_VIEW_LIST)) {
      fragment = PlannerListFragment.newInstance(args);
    } else {
      fragment = PlannerTimetableFragment.newInstance();
    }

    fragmentManager.beginTransaction()
        .add(R.id.content_frame, fragment, FRAGMENT_TAG)
        .commit();
  }

  private void onScrollToCurrent() {
    FragmentManager fm = getSupportFragmentManager();
    PlannerScrollToCurrentListener fragment = (PlannerScrollToCurrentListener) fm.findFragmentByTag(
        FRAGMENT_TAG);

    if (fragment != null) {
      fragment.onScrollToCurrent();
    }
  }

  private void shouldSwitchFragment(int plannerView) {
    FragmentManager fm = getSupportFragmentManager();
    Fragment fragment = fm.findFragmentByTag(FRAGMENT_TAG);
    switch (plannerView) {
      case R.id.planner_list:
        prefs.setPlannerPreferredView(mOrientation, PLANNER_VIEW_LIST);
        if (fragment == null || !(fragment instanceof PlannerListFragment)) {
          fragment = PlannerListFragment.newInstance(new Bundle());
        }
        break;
      case R.id.planner_timetable:
        prefs.setPlannerPreferredView(mOrientation, PLANNER_VIEW_TIMETABLE);
        if (fragment == null || !(fragment instanceof PlannerTimetableFragment)) {
          fragment = PlannerTimetableFragment.newInstance();
        }
        break;
    }
    fm.beginTransaction()
        .replace(R.id.content_frame, fragment, FRAGMENT_TAG)
        .commit();
  }

  @Override public PlannerComponent getComponent() {
    return plannerComponent;
  }

  @Override public void onPlannerEventSelected(EventModel model) {
    //TODO: Create real EventActivity and start this instead
    Intent intent = new Intent(this, CourseViewActivity.class);
    intent.putExtra(CourseViewActivity.COURSE_ID, model.getCourse()
        .getCourseId());
    intent.putExtra(CourseViewActivity.COURSE_MODULES, model.getCourse()
        .getModules()
        .getAsJson());
    startActivity(intent);
  }
}
