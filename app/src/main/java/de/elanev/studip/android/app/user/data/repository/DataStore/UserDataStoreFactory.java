/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.user.data.repository.DataStore;

import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.base.data.net.StudIpLegacyApiService;

/**
 * @author joern
 */
@Singleton
public class UserDataStoreFactory {
  private final StudIpLegacyApiService apiService;

  @Inject public UserDataStoreFactory(@NonNull StudIpLegacyApiService apiService) {
    this.apiService = apiService;
  }

  public UserDataStore create() {
    return new UserCloudDataStore(apiService);
  }
}