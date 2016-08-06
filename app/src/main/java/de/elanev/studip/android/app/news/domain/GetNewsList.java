/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.domain;

import java.util.List;

import javax.inject.Inject;

import de.elanev.studip.android.app.base.UseCase;
import rx.Observable;

/**
 * @author joern
 */
public class GetNewsList extends UseCase<List<NewsItem>> {
  private final NewsRepository mRepository;

  @Inject public GetNewsList(NewsRepository repository) {
    this.mRepository = repository;
  }

  @Override protected Observable<List<NewsItem>> buildUseCaseObservable() {
    return mRepository.newsList();
  }
}