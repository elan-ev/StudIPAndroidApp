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

import de.elanev.studip.android.app.authorization.data.entity.SettingsEntity;
import de.elanev.studip.android.app.authorization.data.entity.SettingsEntityDataMapper;
import de.elanev.studip.android.app.authorization.data.repository.datastore.SettingsCloudDataStore;
import de.elanev.studip.android.app.authorization.data.repository.datastore.SettingsPrefsDataStore;
import de.elanev.studip.android.app.authorization.domain.SettingsRepository;
import de.elanev.studip.android.app.authorization.domain.model.Settings;
import rx.Observable;

/**
 * @author joern
 */
@Singleton
public class SettingsDataRepository implements SettingsRepository {
  private final SettingsCloudDataStore cloudDataStore;
  private final SettingsPrefsDataStore prefsDataStore;
  private final SettingsEntityDataMapper dataMapper;

  @Inject public SettingsDataRepository(SettingsCloudDataStore cloudDataStore,
      SettingsPrefsDataStore prefsDataStore, SettingsEntityDataMapper dataMapper) {
    this.cloudDataStore = cloudDataStore;
    this.prefsDataStore = prefsDataStore;
    this.dataMapper = dataMapper;
  }

  @Override public Observable<Settings> studipSettings(boolean forceUpdate) {
    Observable<SettingsEntity> cloudDataObs = cloudDataStore.getSettings()
        .doOnNext(settings -> prefsDataStore.store(settings));
    Observable<SettingsEntity> localDataObs = prefsDataStore.getSettings();

    return localDataObs.exists(settingsEntity -> settingsEntity != null)
        .flatMap(isInDb -> (isInDb && !forceUpdate) ? localDataObs : cloudDataObs)
        .map(dataMapper::transform);
  }
}
