/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.contacts.data.repository.datastore;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import de.elanev.studip.android.app.base.data.net.StudIpLegacyApiService;
import de.elanev.studip.android.app.contacts.data.entity.ContactGroupEntity;
import rx.Observable;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * @author joern
 */
public class ContactsCloudDataStoreTest {
  @Mock StudIpLegacyApiService mockApiService;
  private ContactsCloudDataStore contactsCloudDataStore;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    contactsCloudDataStore = new ContactsCloudDataStore(mockApiService);
  }

  @Test public void contactGroupEntityList() throws Exception {
    List<ContactGroupEntity> contactGroupEntities = new ArrayList<>();
    ContactGroupEntity contactGroupEntity1 = new ContactGroupEntity();
    ContactGroupEntity contactGroupEntity2 = new ContactGroupEntity();

    contactGroupEntities.add(contactGroupEntity2);
    contactGroupEntities.add(contactGroupEntity1);

    given(mockApiService.getContactGroups()).willReturn(Observable.just(contactGroupEntities));
    contactsCloudDataStore.contactGroupEntityList();

    verify(mockApiService).getContactGroups();
  }

}