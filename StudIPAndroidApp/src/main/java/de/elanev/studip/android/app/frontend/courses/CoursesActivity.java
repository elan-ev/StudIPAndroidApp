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

import de.elanev.studip.android.app.MainActivity;
import de.elanev.studip.android.app.R;

/**
 * @author joern
 */
public class CoursesActivity extends MainActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Bundle args = getFragmentArguments();
    Fragment fragment = CoursesFragment.newInstance(args);

    FragmentManager fragmentManager = getSupportFragmentManager();
    fragmentManager.beginTransaction()
        .add(R.id.content_frame, fragment, "courses-fragment")
        .commit();
  }

  @Override protected int getCurrentNavDrawerItem() {
    return R.id.navigation_courses;
  }
}
