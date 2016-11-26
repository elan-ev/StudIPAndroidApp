/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.data.repository.datastore;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.data.net.services.StudIpLegacyApiService;
import de.elanev.studip.android.app.news.data.entity.NewsEntity;
import rx.Observable;

/**
 * @author joern
 */
@Singleton
public class CloudNewsDataStore implements NewsDataStore {
  private final StudIpLegacyApiService mApiService;

  @Inject CloudNewsDataStore(StudIpLegacyApiService apiService) {
    this.mApiService = apiService;
  }

  @Override public Observable<List<NewsEntity>> newsEntityList() {
    return mApiService.getNews();
  }

  @Override public Observable<NewsEntity> newsEntity(String newsId) {
    return mApiService.getNewsItem(newsId);
  }
}
