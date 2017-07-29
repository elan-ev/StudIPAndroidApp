/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.data.repository.datastore;

import android.annotation.SuppressLint;
import android.support.annotation.WorkerThread;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.news.data.entity.NewsEntity;
import de.elanev.studip.android.app.news.data.entity.RealmNewsEntity;
import de.elanev.studip.android.app.news.data.repository.NewsEntityDataMapper;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import rx.Observable;

/**
 * @author joern
 */
@Singleton
@SuppressLint("NewApi")
public class RealmNewsDataStore implements NewsDataStore {
  private final NewsEntityDataMapper newsEntityDataMapper;
  private final RealmConfiguration realmConfiguration;

  @Inject RealmNewsDataStore(RealmConfiguration realmConfig,
      NewsEntityDataMapper newsEntityDataMapper) {
    this.realmConfiguration = realmConfig;
    this.newsEntityDataMapper = newsEntityDataMapper;
  }

  @WorkerThread @Override public Observable<List<NewsEntity>> newsEntityList() {
    try (Realm realm = Realm.getInstance(realmConfiguration)) {
      RealmResults<RealmNewsEntity> realmResults = realm.where(RealmNewsEntity.class)
          .findAll();

      if (realmResults.isEmpty()) return Observable.empty();

      return Observable.just(realm.copyFromRealm(realmResults))
          .map(newsEntityDataMapper::transformFromRealm);
    }

  }

  @WorkerThread @Override public Observable<NewsEntity> newsEntity(String newsId) {
    try (Realm realm = Realm.getInstance(realmConfiguration)) {
      RealmNewsEntity newsEntity = realm.where(RealmNewsEntity.class)
          .equalTo("newsId", newsId)
          .findFirst();

      if (newsEntity == null) return Observable.empty();

      return Observable.just(realm.copyFromRealm(newsEntity))
          .map(newsEntityDataMapper::transform);
    }

  }

  @WorkerThread @Override public Observable<List<NewsEntity>> newsEntityListForRange(String id) {
    try (Realm realm = Realm.getInstance(realmConfiguration)) {
      RealmResults<RealmNewsEntity> newsEntities = realm.where(RealmNewsEntity.class)
          .equalTo("course.courseId", id)
          .findAll();

      if (newsEntities.isEmpty()) return Observable.empty();

      return Observable.just(realm.copyFromRealm(newsEntities))
          .map(newsEntityDataMapper::transformFromRealm);
    }
  }

  @WorkerThread public void save(List<NewsEntity> newsEntities, boolean forceUpdate) {
    List<RealmNewsEntity> realmNewsEntities = newsEntityDataMapper.transformToRealm(newsEntities);
    try (Realm realm = Realm.getInstance(realmConfiguration)) {
      realm.executeTransaction(tsRealm -> {
        if (forceUpdate) tsRealm.delete(RealmNewsEntity.class);

        tsRealm.copyToRealmOrUpdate(realmNewsEntities);
      });
    }
  }

  @WorkerThread public void save(NewsEntity entity) {
    RealmNewsEntity realmNewsEntity = newsEntityDataMapper.transformToRealm(entity);
    try (Realm realm = Realm.getInstance(realmConfiguration)) {
      realm.executeTransaction(tsRealm -> tsRealm.copyToRealmOrUpdate(realmNewsEntity));
    }
  }
}
