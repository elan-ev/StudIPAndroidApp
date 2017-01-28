/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.data.repository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.authorization.data.entity.EndpointsEntityDataMapper;
import de.elanev.studip.android.app.authorization.data.repository.datastore.EndpointsDataStore;
import de.elanev.studip.android.app.authorization.data.repository.datastore.EndpointsFileDataStore;
import de.elanev.studip.android.app.authorization.domain.EndpointsRepository;
import de.elanev.studip.android.app.authorization.domain.model.Endpoint;
import rx.Observable;

/**
 * @author joern
 */
@Singleton
public class EndpointsDataRepository implements EndpointsRepository {
  private final EndpointsDataStore endpointsDataStore;
  private final EndpointsEntityDataMapper dataMapper;

  @Inject public EndpointsDataRepository(EndpointsFileDataStore endpointsDataStore,
      EndpointsEntityDataMapper dataMapper) {
    this.endpointsDataStore = endpointsDataStore;
    this.dataMapper = dataMapper;
  }

  @Override public Observable<Endpoint> endpoint(String endpointId) {
    return this.endpointsDataStore.getEndpoint(endpointId)
        .map(dataMapper::transform);
  }

  @Override public Observable<List<Endpoint>> endpoints() {
    return this.endpointsDataStore.getEndpoints()
        .map(dataMapper::transform);
  }
}
