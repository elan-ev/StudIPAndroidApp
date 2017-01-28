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

import de.elanev.studip.android.app.authorization.data.entity.CredentialsEntityDataMapper;
import de.elanev.studip.android.app.authorization.data.repository.datastore.CredentialsRealmDataStore;
import de.elanev.studip.android.app.authorization.domain.CredentialsRepository;
import de.elanev.studip.android.app.authorization.domain.model.OAuthCredentials;
import rx.Observable;

/**
 * @author joern
 */
@Singleton
public class CredentialsDataRepository implements CredentialsRepository {
  private final CredentialsRealmDataStore realmDataStore;
  private final CredentialsEntityDataMapper authDataMapper;

  @Inject public CredentialsDataRepository(CredentialsRealmDataStore realmDataStore,
      CredentialsEntityDataMapper dataMapper) {
    this.realmDataStore = realmDataStore;
    this.authDataMapper = dataMapper;
  }

  @Override public void save(OAuthCredentials credentials) {
    this.realmDataStore.save(authDataMapper.transform(credentials));
  }

  @Override public OAuthCredentials credentials() {
    return authDataMapper.transform(this.realmDataStore.getCredentials());
  }

  @Override public Observable<Void> clear() {
    this.realmDataStore.clearCredentials();
    return Observable.just(null);
  }
}
