/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.data.repository;

import java.util.List;

import de.elanev.studip.android.app.data.datamodel.NewsItem;
import de.elanev.studip.android.app.data.net.services.StudIpLegacyApiService;
import de.elanev.studip.android.app.util.Prefs;
import rx.Observable;
import rx.functions.Func2;

/**
 * @author joern
 */
public class ApiNewsRepositoryImpl implements NewsRepository {

  private StudIpLegacyApiService mApiService;
  private Prefs mPrefs;


  public ApiNewsRepositoryImpl(StudIpLegacyApiService apiService, Prefs prefs) {
    mApiService = apiService;
    mPrefs = prefs;
  }

  @Override public Observable<NewsItem> newsItem(String id) {
    return mApiService.getNewsItem(id);
  }

  @Override public Observable<List<NewsItem>> newsList() {
    return mApiService.getNews(mPrefs.getUserId())
        .toSortedList(new Func2<NewsItem, NewsItem, Integer>() {
          @Override public Integer call(NewsItem newsItem, NewsItem newsItem2) {
            return newsItem2.date.compareTo(newsItem.date);
          }
        });
  }
}
