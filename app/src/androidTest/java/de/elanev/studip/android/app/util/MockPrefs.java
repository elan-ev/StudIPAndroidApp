/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.util;

import android.content.Context;

import de.elanev.studip.android.app.planner.presentation.view.PlannerActivity;

/**
 * @author joern
 */

public class MockPrefs extends Prefs {

  public MockPrefs(Context context) {
    super(context);
  }

  @Override public String getPreferredPlannerView(int orientation) {
    return PlannerActivity.PLANNER_VIEW_LIST;
  }

  @Override public void setPlannerPreferredView(int orientation, String view) {}

  @Override public int getPreferredPlannerTimetableViewDayCount() {
    return 1;
  }

  @Override public void setPrefPlannerTimetableViewDayCount(int count) {}
}
