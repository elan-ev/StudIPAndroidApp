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
import de.elanev.studip.android.app.authorization.domain.AuthService;
import de.elanev.studip.android.app.authorization.domain.AuthorizationRepository;
import de.elanev.studip.android.app.authorization.domain.usecase.LogoutUser;
import de.elanev.studip.android.app.authorization.domain.usecase.RequestUserAuth;
import de.elanev.studip.android.app.authorization.domain.usecase.SignInUser;
import de.elanev.studip.android.app.base.UseCase;
import de.elanev.studip.android.app.base.domain.executor.PostExecutionThread;
import de.elanev.studip.android.app.base.domain.executor.ThreadExecutor;
import de.elanev.studip.android.app.base.internal.di.PerActivity;

/**
 * @author joern
 */
@Module
public class AuthModule {
  private String endpointId = "";

  public AuthModule() {}

  public AuthModule(String endpointId) {
    this.endpointId = endpointId;
  }

  @Provides @PerActivity @Named("logoutUser") UseCase providesLogoutUseCase(LogoutUser logoutUser) {
    return logoutUser;
  }

  @Provides @PerActivity @Named("requestUserAuth") UseCase<String> providesRequestUserAuthUseCase(
      AuthService authService, AuthorizationRepository repository, ThreadExecutor threadExecutor,
      PostExecutionThread postExecutionThread) {
    return new RequestUserAuth(endpointId, authService, repository, threadExecutor,
        postExecutionThread);
  }

  @Provides @PerActivity @Named("signInUser") UseCase providesSignInUserUseCase(
      SignInUser signInUser) {
    return signInUser;
  }
}
