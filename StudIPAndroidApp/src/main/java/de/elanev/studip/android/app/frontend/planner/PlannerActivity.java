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

  @Override protected int getCurrentNavDrawerItem() {
    return R.id.navigation_planner;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    int orientation = getResources().getConfiguration().orientation;
    String preferredView = Prefs.getInstance(this)
        .getPreferredPlannerView(orientation);

    initFragment(preferredView);
    overridePendingTransition(0, 0);
  }

  private void initFragment(String preferredView) {
    Bundle args = new Bundle();
    args.putString(PLANNER_PREFERRED_VIEW, preferredView);

    Fragment fragment;
    if (TextUtils.equals(preferredView, PLANNER_VIEW_LIST)) {
      fragment = PlannerListFragment.newInstance(args);
    } else {
      fragment = PlannerTimetableFragment.newInstance(args);
    }

    FragmentManager fragmentManager = getSupportFragmentManager();
    fragmentManager.beginTransaction()
        .add(R.id.content_frame, fragment, "planner-fragment")
        .commit();
  }
}
