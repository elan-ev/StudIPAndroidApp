/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.data.repository;


import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.authorization.data.entity.SettingsEntity;
import de.elanev.studip.android.app.authorization.data.repository.datastore.CloudAuthorizationDataStore;
import de.elanev.studip.android.app.authorization.data.repository.datastore.FileAuthorizationDataStore;
import de.elanev.studip.android.app.authorization.data.repository.datastore.RealmAuthorizationDataStore;
import de.elanev.studip.android.app.authorization.domain.AuthorizationRepository;
import de.elanev.studip.android.app.authorization.domain.model.Endpoint;
import de.elanev.studip.android.app.authorization.domain.model.OAuthCredentials;
import de.elanev.studip.android.app.authorization.domain.model.Settings;
import de.elanev.studip.android.app.util.Prefs;
import de.elanev.studip.android.app.util.TextTools;
import rx.Observable;
import timber.log.Timber;

/**
 * @author joern
 */
@Singleton
public class AuthorizationDataRepository implements AuthorizationRepository {
  private final RealmAuthorizationDataStore realmDataStore;
  private final FileAuthorizationDataStore fileDataStore;
  private final AuthorizationEntityDataMapper authDataMapper;
  private final CloudAuthorizationDataStore cloudDataStore;
  private final Prefs prefs;
  private final ObjectMapper mapper;

  @Inject public AuthorizationDataRepository(RealmAuthorizationDataStore realmDataStore,
      FileAuthorizationDataStore fileDataStore, AuthorizationEntityDataMapper authDataMapper,
      CloudAuthorizationDataStore cloudDataStore, Prefs prefs, ObjectMapper mapper) {
    this.realmDataStore = realmDataStore;
    this.fileDataStore = fileDataStore;
    this.authDataMapper = authDataMapper;
    this.cloudDataStore = cloudDataStore;
    this.prefs = prefs;
    this.mapper = mapper;
  }

  @Override public void saveCredentials(OAuthCredentials credentials) {
    this.realmDataStore.saveCredentials(authDataMapper.transform(credentials));
  }

  @Override public OAuthCredentials getCredentials() {
    return authDataMapper.transform(this.realmDataStore.getCredentials());
  }

  @Override public Observable<Void> clearCredentials() {
    this.realmDataStore.clearCredentials();
    return Observable.just(null);
  }

  @Override public Observable<Endpoint> endpoint(String endpointId) {
    return this.realmDataStore.getEndpoint(endpointId)
        .map(authDataMapper::transform);
  }

  @Override public Observable<List<Endpoint>> endpoints() {
    return fileDataStore.getEndpoints()
        .doOnNext(realmDataStore::save)
        .map(authDataMapper::transform);
  }

  @Override public Observable<Settings> studipSettings(boolean forceUpdate) {
    Observable<SettingsEntity> cloudDataObs = cloudDataStore.getSettings()
        .doOnNext(settings -> {
          try {
            String settingsJson = mapper.writeValueAsString(settings);
            prefs.setApiSettings(settingsJson);
          } catch (Exception e) {
            Timber.e(e.getLocalizedMessage(), e);
          }
        });

    Observable<SettingsEntity> localDataObs = Observable.fromCallable(() -> {
      String settingsJson = prefs.getApiSettings();
      if (!TextTools.isEmpty(settingsJson)) {
        SettingsEntity settings = null;
        try {
          settings = mapper.readValue(settingsJson, SettingsEntity.class);
        } catch (Exception e) {
          Timber.e(e.getLocalizedMessage(), e);
          throw e;
        }
        return settings;
      }

      return null;
    });

    return localDataObs.exists(settingsEntity -> settingsEntity != null)
        .flatMap(isInDb -> (isInDb && !forceUpdate) ? localDataObs : cloudDataObs)
        .map(authDataMapper::transform);
  }
}
