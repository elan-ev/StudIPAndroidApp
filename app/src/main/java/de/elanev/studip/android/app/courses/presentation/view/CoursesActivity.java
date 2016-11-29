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
import android.os.Bundle;

import de.elanev.studip.android.app.AbstractStudIPApplication;
import de.elanev.studip.android.app.MainActivity;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.base.internal.di.components.HasComponent;
import de.elanev.studip.android.app.courses.internal.di.CoursesComponent;
import de.elanev.studip.android.app.courses.internal.di.CoursesModule;
import de.elanev.studip.android.app.courses.internal.di.DaggerCoursesComponent;
import de.elanev.studip.android.app.courses.presentation.model.CourseModel;
import de.elanev.studip.android.app.courses.presentation.model.CourseModulesModel;

/**
 * @author joern
 */
public class CoursesActivity extends MainActivity implements HasComponent<CoursesComponent>,
    CoursesFragment.CourseListListener {

  private CoursesComponent coursesComponent;

  public static Intent getCallingIntent(Context context) {
    return new Intent(context, CoursesActivity.class);
  }

  @Override protected int getCurrentNavDrawerItem() {
    return R.id.navigation_courses;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initInjector();
    setContentView(R.layout.activity_course);

    overridePendingTransition(0, 0);
  }

  private void initInjector() {
    this.coursesComponent = DaggerCoursesComponent.builder()
        .applicationComponent(((AbstractStudIPApplication) getApplication()).getAppComponent())
        .coursesModule(new CoursesModule())
        .build();
  }

  @Override public CoursesComponent getComponent() {
    return this.coursesComponent;
  }

  @Override public void onCourseClicked(CourseModel course) {
    Bundle args = new Bundle();
    args.putString(CourseViewActivity.COURSE_ID, course.getCourseId());
    args.putSerializable(CourseViewActivity.COURSE_MODULES, course.getModules());
    Intent intent = CourseViewActivity.getCallingIntent(this);
    intent.putExtras(args);
    startActivity(intent);
  }
}
