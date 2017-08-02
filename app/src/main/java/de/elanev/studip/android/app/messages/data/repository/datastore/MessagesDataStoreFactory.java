/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.messages.data.repository.datastore;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.base.data.net.StudIpLegacyApiService;

/**
 * @author joern
 */
@Singleton
public class MessagesDataStoreFactory {

  private final StudIpLegacyApiService apiService;

  @Inject public MessagesDataStoreFactory(StudIpLegacyApiService apiService) {
    this.apiService = apiService;
  }

  public MessagesDataStore create() {
    return new MessagesCloudDataStore(apiService);
  }
}
