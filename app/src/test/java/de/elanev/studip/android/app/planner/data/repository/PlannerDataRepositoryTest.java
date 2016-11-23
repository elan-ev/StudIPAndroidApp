/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.planner.data.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import de.elanev.studip.android.app.planner.data.entity.EventEntity;
import de.elanev.studip.android.app.planner.data.repository.datastore.PlannerDataStore;
import de.elanev.studip.android.app.planner.data.repository.datastore.PlannerDataStoreFactory;
import rx.Observable;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * @author joern
 */
public class PlannerDataRepositoryTest {
  private PlannerDataRepository plannerDataRepository;
  @Mock private EventsEntityDataMapper mockEntityDataMapper;
  @Mock private PlannerDataStoreFactory mockPlannerDataFactory;
  @Mock private PlannerDataStore mockPlannerDataStore;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    given(mockPlannerDataFactory.create()).willReturn(mockPlannerDataStore);

    plannerDataRepository = new PlannerDataRepository(mockEntityDataMapper, mockPlannerDataFactory);
  }

  @Test public void eventsList() throws Exception {
    List<EventEntity> eventEntities = new ArrayList<>(5);
    eventEntities.add(new EventEntity());
    eventEntities.add(new EventEntity());

    given(mockPlannerDataStore.eventEntityList()).willReturn(Observable.just(eventEntities));

    plannerDataRepository.eventsList();

    verify(mockPlannerDataFactory).create();
    verify(mockPlannerDataStore).eventEntityList();
  }

}