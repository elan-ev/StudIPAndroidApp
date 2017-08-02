/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.user.data.repository.DataStore;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.elanev.studip.android.app.base.data.net.StudIpLegacyApiService;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @author joern
 */
public class UserDataStoreFactoryTest {
  @Mock StudIpLegacyApiService mockApiService;
  private UserDataStoreFactory userDataStoreFactory;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    userDataStoreFactory = new UserDataStoreFactory(mockApiService);
  }

  @Test public void shouldCreateUserDataStore() throws Exception {
    UserDataStore store = userDataStoreFactory.create();

    assertNotNull(store);
    assertThat(store, is(instanceOf(UserCloudDataStore.class)));
  }

}