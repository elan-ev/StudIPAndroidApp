/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.internal.di.modules;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import de.elanev.studip.android.app.authorization.domain.LogoutUser;
import de.elanev.studip.android.app.base.UseCase;

/**
 * @author joern
 */
@Module
public class AuthModule {
  @Provides @Named("logoutUser") UseCase providesLogoutUseCase(LogoutUser logoutUser) {
    return logoutUser;
  }
}
