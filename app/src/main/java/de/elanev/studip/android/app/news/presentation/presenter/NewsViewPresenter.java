/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.presentation.presenter;

import javax.inject.Inject;
import javax.inject.Named;

import de.elanev.studip.android.app.base.BaseRxLcePresenter;
import de.elanev.studip.android.app.base.DefaultSubscriber;
import de.elanev.studip.android.app.base.UseCase;
import de.elanev.studip.android.app.base.internal.di.PerActivity;
import de.elanev.studip.android.app.news.domain.NewsItem;
import de.elanev.studip.android.app.news.presentation.mapper.NewsModelDataMapper;
import de.elanev.studip.android.app.news.presentation.model.NewsModel;
import de.elanev.studip.android.app.news.presentation.view.NewsView;
import timber.log.Timber;

/**
 * @author joern
 */
@PerActivity
public class NewsViewPresenter extends BaseRxLcePresenter<NewsView, NewsModel> {
  private final UseCase mGetNewsDetailsUseCase;
  private final NewsModelDataMapper newsModelMapper;

  @Inject NewsViewPresenter(@Named("newsDetails") UseCase getNewsDetails,
      NewsModelDataMapper newsModelDataMapper) {
    this.mGetNewsDetailsUseCase = getNewsDetails;
    this.newsModelMapper = newsModelDataMapper;
  }

  @SuppressWarnings("ConstantConditions") public void loadNews() {
    if (isViewAttached()) {
      getView().showLoading(false);
    }

    this.mGetNewsDetailsUseCase.execute(new NewsViewSubscriber(false));
  }

  @Override protected void unsubscribe() {
    mGetNewsDetailsUseCase.unsubscribe();
  }

  private final class NewsViewSubscriber extends DefaultSubscriber<NewsItem> {

    NewsViewSubscriber(boolean ptr) {
      super(ptr);
    }

    @Override public void onCompleted() {
      NewsViewPresenter.this.onCompleted();
    }

    @Override public void onError(Throwable e) {
      Timber.e(e, e.getLocalizedMessage());
      NewsViewPresenter.this.onError(e, this.isPullToRefresh());
    }

    @Override public void onNext(NewsItem newsItem) {
      NewsViewPresenter.this.onNext(newsModelMapper.transformNewsItem(newsItem));
    }
  }
}