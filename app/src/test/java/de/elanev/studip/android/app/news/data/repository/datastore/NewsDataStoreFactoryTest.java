/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.data.repository.datastore;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.elanev.studip.android.app.data.net.services.StudIpLegacyApiService;
import de.elanev.studip.android.app.util.Prefs;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * @author joern
 */
public class NewsDataStoreFactoryTest {
  @Mock StudIpLegacyApiService mockApiService;
  @Mock Prefs mockPrefs;
  private NewsDataStoreFactory newsDataStoreFactory;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    newsDataStoreFactory = new NewsDataStoreFactory(mockApiService, mockPrefs);
  }

  @Test public void shouldCreateCloudDataStore() throws Exception {
    NewsDataStore store = newsDataStoreFactory.create();

    assertNotNull(store);
    assertThat(store, is(instanceOf(CloudNewsDataStore.class)));
  }

}