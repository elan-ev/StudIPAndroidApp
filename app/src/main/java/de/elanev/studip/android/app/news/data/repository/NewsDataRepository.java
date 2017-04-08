/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.data.repository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.news.data.entity.NewsEntity;
import de.elanev.studip.android.app.news.data.repository.datastore.CloudNewsDataStore;
import de.elanev.studip.android.app.news.data.repository.datastore.RealmNewsDataStore;
import de.elanev.studip.android.app.news.domain.NewsItem;
import de.elanev.studip.android.app.news.domain.NewsRepository;
import rx.Observable;

/**
 * @author joern
 */
@Singleton
public class NewsDataRepository implements NewsRepository {

  private final NewsEntityDataMapper mEntityDataMapper;
  private final RealmNewsDataStore realmNewsDataStore;
  private final CloudNewsDataStore cloudNewsDataStore;

  @Inject NewsDataRepository(NewsEntityDataMapper entityDataMapper,
      RealmNewsDataStore realmNewsDataStore, CloudNewsDataStore cloudNewsDataStore) {
    this.mEntityDataMapper = entityDataMapper;
    this.realmNewsDataStore = realmNewsDataStore;
    this.cloudNewsDataStore = cloudNewsDataStore;
  }

  @Override public Observable<NewsItem> newsItem(String id, boolean forceUpdate) {
    Observable<NewsEntity> cloudDataObs = cloudNewsDataStore.newsEntity(id)
        .doOnNext(realmNewsDataStore::save);
    Observable<NewsEntity> localDataObs = realmNewsDataStore.newsEntity(id);

    return localDataObs.exists(newsEntity -> newsEntity != null)
        .flatMap(isInDb -> (isInDb && !forceUpdate) ? localDataObs : cloudDataObs)
        .map(mEntityDataMapper::transform);
  }

  @Override public Observable<List<NewsItem>> newsList(boolean forceUpdate) {
    Observable<List<NewsEntity>> cloudDataObs = cloudNewsDataStore.newsEntityList()
        .doOnNext(newsEntities -> realmNewsDataStore.save(newsEntities, forceUpdate));
    Observable<List<NewsEntity>> localDataObs = realmNewsDataStore.newsEntityList();

    return localDataObs.exists(newsEntities -> !newsEntities.isEmpty())
        .flatMap(isInDb -> (isInDb && !forceUpdate) ? localDataObs : cloudDataObs)
        .map(mEntityDataMapper::transform);
  }

  @Override public Observable<List<NewsItem>> newsForRange(String id, boolean forceUpdate) {
    Observable<List<NewsEntity>> cloudDataObs = cloudNewsDataStore.newsEntityListForRange(id)
        .doOnNext(newsEntities -> realmNewsDataStore.save(newsEntities, false));
    Observable<List<NewsEntity>> localDataObs = realmNewsDataStore.newsEntityListForRange(id);

    return localDataObs.exists(newsEntities -> !newsEntities.isEmpty())
        .flatMap(isInDb -> (isInDb && !forceUpdate) ? localDataObs : cloudDataObs)
        .map(mEntityDataMapper::transform);
  }
}
