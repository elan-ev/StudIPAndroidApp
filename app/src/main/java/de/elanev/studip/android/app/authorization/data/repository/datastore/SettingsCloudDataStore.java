/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.data.repository.datastore;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.authorization.data.entity.SettingsEntity;
import de.elanev.studip.android.app.data.net.services.StudIpLegacyApiService;
import rx.Observable;

/**
 * @author joern
 */
@Singleton
public class SettingsCloudDataStore implements SettingsDataStore {
  private final StudIpLegacyApiService apiService;

  @Inject SettingsCloudDataStore(StudIpLegacyApiService apiService) {this.apiService = apiService;}

  @Override public Observable<SettingsEntity> getSettings() {
    return apiService.getSettings();
  }

}
