/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.planner.data.repository.datastore;

import android.annotation.SuppressLint;
import android.support.annotation.WorkerThread;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.planner.data.entity.EventEntity;
import de.elanev.studip.android.app.planner.data.entity.RealmEventEntity;
import de.elanev.studip.android.app.planner.data.repository.EventsEntityDataMapper;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import rx.Observable;

/**
 * @author joern
 */

@Singleton
@SuppressLint("NewApi")
public class PlanerRealmDataStore implements PlannerDataStore {
  private final RealmConfiguration realmConfig;
  private final EventsEntityDataMapper mapper;

  @Inject public PlanerRealmDataStore(RealmConfiguration realmConfig,
      EventsEntityDataMapper mapper) {
    this.realmConfig = realmConfig;
    this.mapper = mapper;
  }

  @WorkerThread @Override public Observable<List<EventEntity>> eventEntityList() {
    try (Realm realm = Realm.getInstance(realmConfig)) {
      RealmResults<RealmEventEntity> results = realm.where(RealmEventEntity.class)
          .findAll();

      if (results.isEmpty()) return Observable.empty();

      return Observable.just(realm.copyFromRealm(results))
          .map(mapper::transformFromRealm);
    }

  }

  public void save(List<EventEntity> entities, boolean forceUpdate) {
    RealmList<RealmEventEntity> events = mapper.transformToRealm(entities);
    try (Realm realm = Realm.getInstance(realmConfig)) {
      realm.executeTransaction(tsRealm -> {
        if (forceUpdate) tsRealm.delete(RealmEventEntity.class);

        tsRealm.copyToRealmOrUpdate(events);
      });
    }
  }
}
