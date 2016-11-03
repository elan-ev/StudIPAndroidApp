/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.messages.data.repository.datastore;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.elanev.studip.android.app.data.net.services.StudIpLegacyApiService;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

/**
 * @author joern
 */
public class MessagesDataStoreFactoryTest {

  @Mock StudIpLegacyApiService mockApiService;
  private MessagesDataStoreFactory messagesDataStoreFactory;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    messagesDataStoreFactory = new MessagesDataStoreFactory(mockApiService);
  }

  @Test public void shouldCreateCloudDataStore() throws Exception {
    MessagesDataStore store = messagesDataStoreFactory.create();

    assertThat(store, is(notNullValue()));
    assertThat(store, is(instanceOf(MessagesCloudDataStore.class)));
  }
}