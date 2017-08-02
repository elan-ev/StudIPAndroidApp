/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.domain.usecase;

import javax.inject.Inject;

import de.elanev.studip.android.app.authorization.domain.AuthService;
import de.elanev.studip.android.app.authorization.domain.EndpointsRepository;
import de.elanev.studip.android.app.base.UseCase;
import de.elanev.studip.android.app.base.domain.executor.PostExecutionThread;
import de.elanev.studip.android.app.base.domain.executor.ThreadExecutor;
import de.elanev.studip.android.app.base.internal.di.PerActivity;
import rx.Observable;

/**
 * @author joern
 */
@PerActivity
public class RequestUserAuth extends UseCase<String> {
  private final String endpointId;
  private final AuthService authService;
  private final EndpointsRepository endpointsRepository;

  @Inject public RequestUserAuth(String endpointId, AuthService authService,
      EndpointsRepository endpointsRepository, ThreadExecutor threadExecutor,
      PostExecutionThread postExecutionThread) {
    super(threadExecutor, postExecutionThread);

    this.endpointId = endpointId;
    this.authService = authService;
    this.endpointsRepository = endpointsRepository;
  }

  @Override public Observable<String> buildUseCaseObservable(boolean forceUpdate) {
    return this.endpointsRepository.endpoint(endpointId)
        .flatMap(authService::auth);
  }
}
