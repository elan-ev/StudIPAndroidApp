/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.contacts.data.repository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.contacts.data.repository.datastore.ContactsDataStoreFactory;
import de.elanev.studip.android.app.contacts.domain.ContactGroup;
import de.elanev.studip.android.app.contacts.domain.ContactsRepository;
import rx.Observable;

/**
 * @author joern
 */
@Singleton
public class ContactsDataRepository implements ContactsRepository {

  private final ContactsDataStoreFactory dataStoreFactory;
  private final ContactsEntityDataMapper dataMapper;

  @Inject ContactsDataRepository(ContactsDataStoreFactory contactsDataStoreFactory,
      ContactsEntityDataMapper contactsEntityDataMapper) {

    this.dataStoreFactory = contactsDataStoreFactory;
    this.dataMapper = contactsEntityDataMapper;
  }

  @Override public Observable<List<ContactGroup>> contactGroups() {
    return dataStoreFactory.create().contactGroupEntityList().map(dataMapper::transform);
  }
}
