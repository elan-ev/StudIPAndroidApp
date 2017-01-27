/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.user.interal.di;

import dagger.Component;
import de.elanev.studip.android.app.NavHeaderFragment;
import de.elanev.studip.android.app.base.internal.di.PerActivity;
import de.elanev.studip.android.app.base.internal.di.components.ApplicationComponent;
import de.elanev.studip.android.app.user.presentation.view.UserDetailsFragment;

/**
 * @author joern
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {
    UserModule.class
})
public interface UserComponent {
  void inject(UserDetailsFragment target);

  void inject(NavHeaderFragment target);
}
