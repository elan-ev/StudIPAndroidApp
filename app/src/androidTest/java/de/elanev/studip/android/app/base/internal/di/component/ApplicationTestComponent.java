/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.base.internal.di.component;

import javax.inject.Singleton;

import dagger.Component;
import de.elanev.studip.android.app.base.internal.di.modules.ApplicationModule;
import de.elanev.studip.android.app.base.internal.di.modules.NetworkModule;
import de.elanev.studip.android.app.news.presentation.NewsActivityTest;

/**
 * @author joern
 */
@Singleton
@Component(modules = {ApplicationModule.class, NetworkModule.class})
public interface ApplicationTestComponent {
  void inject(NewsActivityTest target);
}
