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
import de.elanev.studip.android.app.base.internal.di.components.ApplicationComponent;
import de.elanev.studip.android.app.base.internal.di.module.ApplicationTestModule;
import de.elanev.studip.android.app.base.internal.di.module.NetworkTestModule;

/**
 * @author joern
 */
@Singleton
@Component(modules = {ApplicationTestModule.class, NetworkTestModule.class})
public interface ApplicationTestComponent extends ApplicationComponent {}
