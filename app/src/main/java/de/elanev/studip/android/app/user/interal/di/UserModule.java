/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.user.interal.di;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import de.elanev.studip.android.app.base.UseCase;
import de.elanev.studip.android.app.base.domain.executor.PostExecutionThread;
import de.elanev.studip.android.app.base.domain.executor.ThreadExecutor;
import de.elanev.studip.android.app.base.internal.di.PerActivity;
import de.elanev.studip.android.app.domain.usecase.GetCurrentUserDetails;
import de.elanev.studip.android.app.user.domain.GetUserDetails;
import de.elanev.studip.android.app.user.domain.User;
import de.elanev.studip.android.app.user.domain.UserRepository;

/**
 * @author joern
 */
@Module
public class UserModule {
  String userId;

  public UserModule() {}

  public UserModule(String userId) {
    this.userId = userId;
  }

  @PerActivity @Provides UseCase provideGetUserDetailsUseCase(UserRepository userRepository,
      ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
    return new GetUserDetails(userId, userRepository, threadExecutor, postExecutionThread);
  }

  @PerActivity @Provides @Named("getCurrentUserDetails") UseCase<User> provideGetCurrentUserDetailsUseCase(
      GetCurrentUserDetails getCurrentUserDetails) {
    return getCurrentUserDetails;
  }
}
