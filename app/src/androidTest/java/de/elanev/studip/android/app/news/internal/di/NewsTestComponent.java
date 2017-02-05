/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.internal.di;

import dagger.Component;
import de.elanev.studip.android.app.base.internal.di.PerActivity;
import de.elanev.studip.android.app.base.internal.di.component.ApplicationTestComponent;
import de.elanev.studip.android.app.news.presentation.NewsActivityTest;

/**
 * @author joern
 */
@PerActivity
@Component(dependencies = ApplicationTestComponent.class, modules = {NewsTestModule.class})
public interface NewsTestComponent extends NewsComponent {
  void inject(NewsActivityTest target);
}
