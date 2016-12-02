/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.internal.di;

import dagger.Component;
import de.elanev.studip.android.app.base.internal.di.PerActivity;
import de.elanev.studip.android.app.base.internal.di.components.ApplicationComponent;
import de.elanev.studip.android.app.courses.presentation.view.CourseAttendeesFragment;
import de.elanev.studip.android.app.courses.presentation.view.CourseOverviewFragment;
import de.elanev.studip.android.app.courses.presentation.view.CourseScheduleFragment;
import de.elanev.studip.android.app.courses.presentation.view.CoursesFragment;

/**
 * @author joern
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {CoursesModule.class})
public interface CoursesComponent {

  void inject(CoursesFragment target);

  void inject(CourseOverviewFragment target);

  void inject(CourseScheduleFragment target);

  void inject(CourseAttendeesFragment target);
}
