/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.planner.internal.di;

import dagger.Component;
import de.elanev.studip.android.app.base.internal.di.PerActivity;
import de.elanev.studip.android.app.base.internal.di.components.ApplicationComponent;
import de.elanev.studip.android.app.planner.presentation.view.PlannerListFragment;
import de.elanev.studip.android.app.planner.presentation.view.PlannerTimetableFragment;

/**
 * @author joern
 */
@PerActivity
@Component(dependencies = {ApplicationComponent.class}, modules = {PlannerModule.class})
public interface PlannerComponent {
  void inject(PlannerListFragment target);

  void inject(PlannerTimetableFragment target);
}
