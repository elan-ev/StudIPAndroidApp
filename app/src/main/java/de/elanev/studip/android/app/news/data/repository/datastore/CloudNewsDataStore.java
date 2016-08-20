/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.data.repository.datastore;

import java.util.List;

import de.elanev.studip.android.app.data.net.services.StudIpLegacyApiService;
import de.elanev.studip.android.app.news.data.entity.NewsEntity;
import de.elanev.studip.android.app.util.Prefs;
import rx.Observable;
import rx.functions.Func2;

/**
 * @author joern
 */
class CloudNewsDataStore implements NewsDataStore {
  private final StudIpLegacyApiService mApiService;
  private final Prefs mPrefs;

  CloudNewsDataStore(StudIpLegacyApiService apiService, Prefs prefs) {
    this.mApiService = apiService;
    this.mPrefs = prefs;
  }

  @Override public Observable<List<NewsEntity>> newsEntityList() {
    return mApiService.getNews();
  }

  @Override public Observable<NewsEntity> newsEntity(String newsId) {
    return mApiService.getNewsItem(newsId);
  }
}
