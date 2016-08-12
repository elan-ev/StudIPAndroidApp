/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.user.interal.di;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.elanev.studip.android.app.base.internal.di.PerActivity;
import de.elanev.studip.android.app.user.data.repository.UserDataRepository;
import de.elanev.studip.android.app.user.domain.GetUserDetails;
import de.elanev.studip.android.app.user.domain.UserRepository;

/**
 * @author joern
 */
@Module
public class UserModule {
  final String userId;

  public UserModule(String userId) {
    this.userId = userId;
  }

  @PerActivity @Provides GetUserDetails providesGetUserDetailsUseCase(
      UserRepository userRepository) {
    return new GetUserDetails(userId, userRepository);
  }
}
