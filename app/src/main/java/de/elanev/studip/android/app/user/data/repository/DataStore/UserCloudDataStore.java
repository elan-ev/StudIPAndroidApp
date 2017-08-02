/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.user.data.repository.DataStore;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.base.data.net.StudIpLegacyApiService;
import de.elanev.studip.android.app.user.data.entity.UserEntity;
import rx.Observable;

/**
 * @author joern
 */
@Singleton
public class UserCloudDataStore implements UserDataStore {
  private final StudIpLegacyApiService apiService;

  @Inject public UserCloudDataStore(StudIpLegacyApiService apiService) {
    this.apiService = apiService;
  }

  @Override public Observable<UserEntity> userEntity(String id) {
    return apiService.getUserEntity(id);
  }

  public Observable<UserEntity> currentUserEntity() {
    return apiService.getCurrentUser();
  }
}
