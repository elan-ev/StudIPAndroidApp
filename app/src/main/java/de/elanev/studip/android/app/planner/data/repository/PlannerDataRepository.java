/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.planner.data.repository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.planner.data.repository.datastore.PlannerDataStoreFactory;
import de.elanev.studip.android.app.planner.domain.Event;
import de.elanev.studip.android.app.planner.domain.PlannerRepository;
import rx.Observable;

/**
 * @author joern
 */
@Singleton
public class PlannerDataRepository implements PlannerRepository {
  private final EventsEntityDataMapper entityDataMapper;
  private final PlannerDataStoreFactory dataStoreFactory;

  @Inject PlannerDataRepository(EventsEntityDataMapper eventsEntityDataMapper,
      PlannerDataStoreFactory dataStoreFactory) {
    this.entityDataMapper = eventsEntityDataMapper;
    this.dataStoreFactory = dataStoreFactory;
  }

  @Override public Observable<List<Event>> eventsList() {
    return dataStoreFactory.create()
        .eventEntityList()
        .map(entityDataMapper::transform);
  }
}
