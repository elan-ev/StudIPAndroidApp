/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.domain.usecase;

import java.util.List;

import javax.inject.Inject;

import de.elanev.studip.android.app.authorization.domain.EndpointsRepository;
import de.elanev.studip.android.app.authorization.domain.model.Endpoint;
import de.elanev.studip.android.app.base.UseCase;
import de.elanev.studip.android.app.base.domain.executor.PostExecutionThread;
import de.elanev.studip.android.app.base.domain.executor.ThreadExecutor;
import rx.Observable;

/**
 * @author joern
 */

public class GetEndpointsList extends UseCase<List<Endpoint>> {
  private final EndpointsRepository endpointsRepository;

  @Inject GetEndpointsList(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread,
      EndpointsRepository endpointsRepository) {
    super(threadExecutor, postExecutionThread);

    this.endpointsRepository = endpointsRepository;
  }

  @Override public Observable<List<Endpoint>> buildUseCaseObservable(boolean forceUpdate) {
    return this.endpointsRepository.endpoints();
  }
}
