/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.repository;

import android.content.ContentResolver;

import java.util.Collection;

import de.elanev.studip.android.app.data.datamodel.News;
import de.elanev.studip.android.app.data.datamodel.NewsItem;
import de.elanev.studip.android.app.data.net.services.StudIpLegacyApiService;
import rx.Observable;
import rx.functions.Func1;

/**
 * @author joern
 */
public class ApiNewsRepositoryImpl implements NewsRepository {

  private StudIpLegacyApiService mApiService;


  public ApiNewsRepositoryImpl(StudIpLegacyApiService apiService) {
    mApiService = apiService;
  }

  @Override public Observable<NewsModel> newsItem(String id) {
    return mApiService.getNewsItem(id)
        .flatMap(new Func1<NewsItem, Observable<NewsModel>>() {
          @Override public Observable<NewsModel> call(NewsItem newsItem) {
            return Observable.just(NewsModelDataMapper.transform(newsItem));
          }
        });
  }

  @Override public Observable<Collection<NewsModel>> newsList(String range) {
    return mApiService.getNews(range)
        .flatMap(new Func1<News, Observable<Collection<NewsModel>>>() {
          @Override public Observable<Collection<NewsModel>> call(News news) {
            return Observable.just(NewsModelDataMapper.transform(news.news));
          }
        });
  }
}
