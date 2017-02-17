/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app;

import de.elanev.studip.android.app.base.internal.di.component.MockApplicationModule;
import de.elanev.studip.android.app.base.internal.di.component.MockNetworkModule;
import de.elanev.studip.android.app.base.internal.di.components.ApplicationComponent;
import de.elanev.studip.android.app.base.internal.di.components.DaggerApplicationComponent;

/**
 * @author joern
 */

public class TestStudIPApplication extends StudIPApplication {

  @Override public ApplicationComponent buildAppComponent() {
    return DaggerApplicationComponent.builder()
        .applicationModule(new MockApplicationModule(TestStudIPApplication.this))
        .networkModule(new MockNetworkModule())
        .build();
  }
}
