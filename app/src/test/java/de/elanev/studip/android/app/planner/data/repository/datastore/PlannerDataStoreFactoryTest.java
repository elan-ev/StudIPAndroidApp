/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.planner.data.repository.datastore;

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
public class PlannerDataStoreFactoryTest {
  private PlannerDataStoreFactory plannerDataStoreFactory;

  @Mock private StudIpLegacyApiService mockApiService;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    plannerDataStoreFactory = new PlannerDataStoreFactory(mockApiService);
  }

  @Test public void create() throws Exception {
    PlannerDataStore store = plannerDataStoreFactory.create();

    assertThat(store, is(notNullValue()));
    assertThat(store, is(instanceOf(PlannerCloudDataStore.class)));
  }

}