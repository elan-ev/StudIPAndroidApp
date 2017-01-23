/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.data.repository;


import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.authorization.data.repository.datastore.RealmAuthorizationDataStore;
import de.elanev.studip.android.app.authorization.domain.AuthorizationRepository;
import de.elanev.studip.android.app.authorization.domain.model.Endpoint;
import de.elanev.studip.android.app.authorization.domain.model.OAuthCredentials;
import rx.Observable;

/**
 * @author joern
 */
@Singleton
public class AuthorizationDataRepository implements AuthorizationRepository {
  private final RealmAuthorizationDataStore realDataStore;
  private final AuthorizationEntityDataMapper authDataMapper;

  @Inject public AuthorizationDataRepository(RealmAuthorizationDataStore realDataStore,
      AuthorizationEntityDataMapper authDataMapper) {
    this.realDataStore = realDataStore;
    this.authDataMapper = authDataMapper;
  }

  @Override public void saveCredentials(OAuthCredentials credentials) {
    this.realDataStore.saveCredentials(authDataMapper.transform(credentials));
  }

  @Override public OAuthCredentials getCredentials() {
    return authDataMapper.transform(this.realDataStore.getCredentials());
  }

  @Override public Observable<Void> clearCredentials() {
    this.realDataStore.clearCredentials();
    return Observable.just(null);
  }

  @Override public Observable<Endpoint> endpoint(String endpointId) {
    return this.realDataStore.getEndpoint(endpointId).map(authDataMapper::transform);
  }
}
