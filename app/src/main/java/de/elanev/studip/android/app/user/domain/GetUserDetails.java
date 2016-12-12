/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.user.domain;

import javax.inject.Inject;

import de.elanev.studip.android.app.base.UseCase;
import de.elanev.studip.android.app.base.domain.executor.PostExecutionThread;
import de.elanev.studip.android.app.base.domain.executor.ThreadExecutor;
import rx.Observable;

/**
 * @author joern
 */
public class GetUserDetails extends UseCase<User> {

  private final String userId;
  private final UserRepository userRepository;

  @Inject public GetUserDetails(String userId, UserRepository userRepository,
      ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
    super(threadExecutor, postExecutionThread);

    this.userId = userId;
    this.userRepository = userRepository;
  }

  @Override protected Observable<User> buildUseCaseObservable(boolean forceUpdate) {
    return this.userRepository.user(this.userId);
  }
}
