/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.base.internal.di.module;

import android.app.Application;

import dagger.Module;
import de.elanev.studip.android.app.base.internal.di.modules.ApplicationModule;

/**
 * @author joern
 */
@Module
public class ApplicationTestModule extends ApplicationModule {
  public ApplicationTestModule(Application application) {
    super(application);
  }
}
