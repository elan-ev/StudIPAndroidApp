/*
 * Copyright (c) 2015 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.frontend.planer;

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
public class PlanerActivity extends MainActivity {

  public static final String PLANER_VIEW_TIMETABLE = "planer-view-timetable";
  public static final String PLANER_VIEW_LIST = "planer-view-list";
  private static final String PLANER_PREFERRED_VIEW = "preferred-view";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    int orientation = getResources().getConfiguration().orientation;
    String preferredView = Prefs.getInstance(this)
        .getPreferredPlanerView(orientation);

    initFragment(preferredView);
  }

  private void initFragment(String preferredView) {
    Bundle args = new Bundle();
    args.putString(PLANER_PREFERRED_VIEW, preferredView);

    Fragment fragment;
    if (TextUtils.equals(preferredView, PLANER_VIEW_LIST)) {
      fragment = PlannerFragment.newInstance(args);
    } else {
      fragment = TimetableFragment.newInstance(args);
    }

    FragmentManager fragmentManager = getSupportFragmentManager();
    fragmentManager.beginTransaction()
        .add(R.id.content_frame, fragment, "planner-fragment")
        .commit();
  }

  @Override protected int getCurrentNavDrawerItem() {
    return R.id.navigation_planner;
  }
}
