/*
 * Copyright (c) 2017 ELAN e.V.
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

import java.util.ArrayList;
import java.util.List;

import de.elanev.studip.android.app.base.data.net.StudIpLegacyApiService;
import de.elanev.studip.android.app.planner.data.entity.EventEntity;
import rx.Observable;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * @author joern
 */
public class PlannerCloudDataStoreTest {
  private PlannerCloudDataStore plannerDataStore;

  @Mock private StudIpLegacyApiService mockApiService;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    plannerDataStore = new PlannerCloudDataStore(mockApiService);
  }

  @Test public void eventEntityList() throws Exception {
    List<EventEntity> eventEntities = new ArrayList<>();
    EventEntity eventEntity1 = new EventEntity();
    EventEntity eventEntity2 = new EventEntity();

    eventEntities.add(eventEntity1);
    eventEntities.add(eventEntity2);

    given(mockApiService.getEvents()).willReturn(Observable.just(eventEntities));
    plannerDataStore.eventEntityList();

    verify(mockApiService).getEvents();
  }

}