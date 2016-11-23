/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.contacts.data.repository.datastore;

import java.util.List;

import de.elanev.studip.android.app.contacts.data.entity.ContactGroupEntity;
import de.elanev.studip.android.app.data.net.services.StudIpLegacyApiService;
import rx.Observable;

/**
 * @author joern
 */

public class ContactsCloudDataStore implements ContactsDataStore {

  private final StudIpLegacyApiService apiService;

  ContactsCloudDataStore(StudIpLegacyApiService apiService) {
    this.apiService = apiService;
  }

  @Override public Observable<List<ContactGroupEntity>> contactGroupEntityList() {
    return apiService.getContactGroups();
  }
}
