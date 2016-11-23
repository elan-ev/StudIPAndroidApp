/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.data.repository.datastore;

import android.support.annotation.NonNull;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.data.net.services.StudIpLegacyApiService;

/**
 * @author joern
 */
@Singleton
public class NewsDataStoreFactory {
  private final StudIpLegacyApiService apiService;

  @Inject NewsDataStoreFactory(@NonNull StudIpLegacyApiService apiService) {
    this.apiService = apiService;
  }

  public NewsDataStore create() {
    return new CloudNewsDataStore(apiService);
  }
}
