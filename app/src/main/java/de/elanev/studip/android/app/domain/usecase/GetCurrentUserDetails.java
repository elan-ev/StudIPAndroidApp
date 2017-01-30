/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.domain.usecase;

import javax.inject.Inject;

import de.elanev.studip.android.app.base.UseCase;
import de.elanev.studip.android.app.base.domain.executor.PostExecutionThread;
import de.elanev.studip.android.app.base.domain.executor.ThreadExecutor;
import de.elanev.studip.android.app.user.domain.User;
import de.elanev.studip.android.app.user.domain.UserRepository;
import de.elanev.studip.android.app.util.Prefs;
import rx.Observable;

/**
 * @author joern
 */

public class GetCurrentUserDetails extends UseCase<User> {
  private final Prefs prefs;
  private final UserRepository userRepository;

  @Inject public GetCurrentUserDetails(UserRepository userRepository, ThreadExecutor threadExecutor,
      PostExecutionThread postExecutionThread, Prefs prefs) {
    super(threadExecutor, postExecutionThread);
    this.prefs = prefs;
    this.userRepository = userRepository;
  }

  @Override protected Observable<User> buildUseCaseObservable(boolean forceUpdate) {
    String currentUserId = prefs.getCurrentUserId();

    return userRepository.user(currentUserId, forceUpdate);
  }
}
