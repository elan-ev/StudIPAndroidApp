/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import de.elanev.studip.android.app.internal.di.mvp.BasePresenter;
import de.elanev.studip.android.app.internal.di.mvp.PresenterBundle;
import de.elanev.studip.android.app.news.repository.NewsModel;
import de.elanev.studip.android.app.news.repository.NewsRepository;

/**
 * @author joern
 */
public class NewsViewPresenter extends BasePresenter<NewsView, NewsModel> {
  private NewsRepository mNewsRepository;

  public NewsViewPresenter(NewsRepository newsRepository) {
    mNewsRepository = newsRepository;
  }

  @Override public void onCreate(@Nullable PresenterBundle bundle) {
    loadNews(bundle.getString(NewsViewFragment.NEWS_ID));
  }

  private void loadNews(String id) {
    if (TextUtils.isEmpty(id)) {
      return;
    }

    subscribe(this.mNewsRepository.newsItem(id));
  }

  @Override public void onSaveInstanceState(@NonNull PresenterBundle bundle) {

  }

  @Override public void onDestroy() {

  }
}