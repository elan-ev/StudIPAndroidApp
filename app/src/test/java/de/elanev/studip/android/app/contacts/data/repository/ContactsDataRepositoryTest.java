/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.contacts.data.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import de.elanev.studip.android.app.contacts.data.entity.ContactGroupEntity;
import de.elanev.studip.android.app.contacts.data.repository.datastore.ContactsDataStore;
import de.elanev.studip.android.app.contacts.data.repository.datastore.ContactsDataStoreFactory;
import rx.Observable;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * @author joern
 */
public class ContactsDataRepositoryTest {
  @Mock ContactsDataStore mockContactsDataStore;
  @Mock ContactsEntityDataMapper mockContactsEntityDataMapper;
  @Mock ContactsDataStoreFactory mockContactsDataStoreFactory;
  private ContactsDataRepository contactsDataRepository;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    given(mockContactsDataStoreFactory.create()).willReturn(mockContactsDataStore);

    contactsDataRepository = new ContactsDataRepository(mockContactsDataStoreFactory,
        mockContactsEntityDataMapper);
  }

  @Test public void contactGroups() throws Exception {
    List<ContactGroupEntity> contactGroups = new ArrayList<>(5);
    contactGroups.add(new ContactGroupEntity());
    contactGroups.add(new ContactGroupEntity());

    given(mockContactsDataStore.contactGroupEntityList()).willReturn(
        Observable.just(contactGroups));

    contactsDataRepository.contactGroups();

    verify(mockContactsDataStoreFactory).create();
    verify(mockContactsDataStore).contactGroupEntityList();
  }

}