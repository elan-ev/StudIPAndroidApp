/*
 * Copyright (c) 2015 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.frontend.planner;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import de.elanev.studip.android.app.MainActivity;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.util.Prefs;

/**
 * @author joern
 */
public class PlannerActivity extends MainActivity {

  public static final String PLANNER_VIEW_TIMETABLE = "planner-view-timetable";
  public static final String PLANNER_VIEW_LIST = "planner-view-list";
  private static final String PLANNER_PREFERRED_VIEW = "planner-preferred-view";
  private static final String FRAGMENT_TAG = "planner-fragment";
  private int mOrientation;

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
        scrollToCurrentTime();
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

    mOrientation = getResources().getConfiguration().orientation;
    String preferredView = Prefs.getInstance(this)
        .getPreferredPlannerView(mOrientation);

    overridePendingTransition(0, 0);
    if (savedInstanceState == null) {
      initFragment(preferredView);
    }
  }

  private void initFragment(String preferredView) {
    Bundle args = new Bundle();
    args.putString(PLANNER_PREFERRED_VIEW, preferredView);

    FragmentManager fragmentManager = getSupportFragmentManager();
    Fragment fragment;
    if (TextUtils.equals(preferredView, PLANNER_VIEW_LIST)) {
      fragment = PlannerListFragment.newInstance(args);
    } else {
      fragment = PlannerTimetableFragment.newInstance(args);
    }

    fragmentManager.beginTransaction()
        .add(R.id.content_frame, fragment, FRAGMENT_TAG)
        .commit();
  }

  private void scrollToCurrentTime() {
    FragmentManager fm = getSupportFragmentManager();
    PlannerFragment fragment = (PlannerFragment) fm.findFragmentByTag(FRAGMENT_TAG);

    if (fragment != null) {
      fragment.scrollToCurrentTime();
    }
  }

  private void shouldSwitchFragment(int plannerView) {
    FragmentManager fm = getSupportFragmentManager();
    Fragment fragment = fm.findFragmentByTag(FRAGMENT_TAG);
    switch (plannerView) {
      case R.id.planner_list:
        Prefs.getInstance(this)
            .setPlannerPreferredView(mOrientation, PLANNER_VIEW_LIST);
        if (fragment == null || !(fragment instanceof PlannerListFragment)) {
          fragment = PlannerListFragment.newInstance(new Bundle());
        }
        break;
      case R.id.planner_timetable:
        Prefs.getInstance(this)
            .setPlannerPreferredView(mOrientation, PLANNER_VIEW_TIMETABLE);
        if (fragment == null || !(fragment instanceof PlannerTimetableFragment)) {
          fragment = PlannerTimetableFragment.newInstance(new Bundle());
        }
        break;
    }
    fm.beginTransaction()
        .replace(R.id.content_frame, fragment, FRAGMENT_TAG)
        .commit();
  }
}
