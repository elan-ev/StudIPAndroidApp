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

import de.elanev.studip.android.app.planner.data.entity.EventEntity;
import de.elanev.studip.android.app.planner.data.repository.datastore.PlannerRealmDataStore;
import de.elanev.studip.android.app.planner.data.repository.datastore.PlannerCloudDataStore;
import de.elanev.studip.android.app.planner.domain.Event;
import de.elanev.studip.android.app.planner.domain.PlannerRepository;
import rx.Observable;

/**
 * @author joern
 */
@Singleton
public class PlannerDataRepository implements PlannerRepository {
  private final EventsEntityDataMapper entityDataMapper;
  private final PlannerCloudDataStore cloudDataStore;
  private final PlannerRealmDataStore localDataStore;

  @Inject PlannerDataRepository(EventsEntityDataMapper eventsEntityDataMapper,
      PlannerCloudDataStore cloudDataStore, PlannerRealmDataStore localDataStore) {
    this.entityDataMapper = eventsEntityDataMapper;
    this.cloudDataStore = cloudDataStore;
    this.localDataStore = localDataStore;
  }

  @Override public Observable<List<Event>> eventsList(boolean forceUpdate) {
    Observable<List<EventEntity>> cloudDataObs = cloudDataStore.eventEntityList()
        .doOnNext(entities -> localDataStore.save(entities, forceUpdate));
    Observable<List<EventEntity>> localDataObs = localDataStore.eventEntityList();

    return localDataObs.exists(newsEntities -> !newsEntities.isEmpty())
        .flatMap(isInDb -> (isInDb && !forceUpdate) ? localDataObs : cloudDataObs)
        .map(entityDataMapper::transform);
  }
}
