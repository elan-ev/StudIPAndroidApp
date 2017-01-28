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

import de.elanev.studip.android.app.base.data.net.StudIpLegacyApiService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNull.notNullValue;

/**
 * @author joern
 */
public class ContactsDataStoreFactoryTest {
  @Mock private StudIpLegacyApiService mockApiService;
  private ContactsDataStoreFactory contactsDataStoreFactory;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    contactsDataStoreFactory = new ContactsDataStoreFactory(mockApiService);
  }

  @Test public void shouldCreateContactsCloudDataStore() throws Exception {
    ContactsDataStore dataStore = contactsDataStoreFactory.create();

    assertThat(dataStore, is(notNullValue()));
    assertThat(dataStore, is(instanceOf(ContactsCloudDataStore.class)));
  }

}