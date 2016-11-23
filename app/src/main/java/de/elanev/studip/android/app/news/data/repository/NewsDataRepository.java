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
import de.elanev.studip.android.app.news.data.repository.datastore.NewsDataStoreFactory;
import de.elanev.studip.android.app.news.domain.NewsItem;
import de.elanev.studip.android.app.news.domain.NewsRepository;
import rx.Observable;
import rx.functions.Func1;

/**
 * @author joern
 */
@Singleton
public class NewsDataRepository implements NewsRepository {

  private final NewsEntityDataMapper mEntityDataMapper;
  private final NewsDataStoreFactory mNewsDataStoreFactory;

  @Inject NewsDataRepository(NewsEntityDataMapper entityDataMapper,
      NewsDataStoreFactory newsDataStoreFactory) {
    this.mEntityDataMapper = entityDataMapper;
    this.mNewsDataStoreFactory = newsDataStoreFactory;
  }


  @Override public Observable<NewsItem> newsItem(String id) {
    return mNewsDataStoreFactory.create()
        .newsEntity(id)
        .map(mEntityDataMapper::transform);
  }

  @Override public Observable<List<NewsItem>> newsList() {
    return mNewsDataStoreFactory.create()
        .newsEntityList()
        .map(mEntityDataMapper::transform);
  }
}
