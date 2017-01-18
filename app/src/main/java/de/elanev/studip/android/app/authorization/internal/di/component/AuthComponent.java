/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.internal.di.component;

import dagger.Component;
import de.elanev.studip.android.app.authorization.internal.di.modules.AuthModule;
import de.elanev.studip.android.app.authorization.presentation.view.LogoutActivity;
import de.elanev.studip.android.app.authorization.presentation.view.SignInFragment;
import de.elanev.studip.android.app.base.internal.di.PerActivity;
import de.elanev.studip.android.app.base.internal.di.components.ApplicationComponent;

/**
 * @author joern
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {AuthModule.class})
public interface AuthComponent {

  void inject(LogoutActivity target);

  void inject(SignInFragment target);
}
