/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.presentation.presenter;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import de.elanev.studip.android.app.base.BaseRxLcePresenter;
import de.elanev.studip.android.app.base.DefaultSubscriber;
import de.elanev.studip.android.app.base.UseCase;
import de.elanev.studip.android.app.base.internal.di.PerActivity;
import de.elanev.studip.android.app.news.domain.NewsItem;
import de.elanev.studip.android.app.news.presentation.mapper.NewsModelDataMapper;
import de.elanev.studip.android.app.news.presentation.model.NewsModel;
import de.elanev.studip.android.app.news.presentation.view.NewsListView;
import timber.log.Timber;

/**
 * @author joern
 */
@PerActivity
public class NewsListPresenter extends BaseRxLcePresenter<NewsListView, List<NewsModel>> {

  private final UseCase getNewsList;
  private final NewsModelDataMapper mNewsModelDataMapper;

  @Inject NewsListPresenter(@Named("newsList") UseCase getNewsListUseCase,
      NewsModelDataMapper dataMapper) {
    this.getNewsList = getNewsListUseCase;
    this.mNewsModelDataMapper = dataMapper;
  }

  public void loadNews(boolean pullToRefresh) {
    getNewsList.execute(new NewsListSubscriber(pullToRefresh));
  }

  @SuppressWarnings("ConstantConditions") public void onNewsClicked(NewsModel news) {
    if (isViewAttached()) {
      getView().viewNews(news);
    }
  }

  @Override protected void unsubscribe() {
    getNewsList.unsubscribe();
  }

  private final class NewsListSubscriber extends DefaultSubscriber<List<NewsItem>> {

    NewsListSubscriber(boolean ptr) {
      super(ptr);
    }

    @Override public void onCompleted() {
      NewsListPresenter.this.onCompleted();
    }

    @Override public void onError(Throwable e) {
      Timber.e(e, e.getLocalizedMessage());
      NewsListPresenter.this.onError(e, this.isPullToRefresh());
    }

    @Override public void onNext(List<NewsItem> newsItems) {
      NewsListPresenter.this.onNext(
          NewsListPresenter.this.mNewsModelDataMapper.transform(newsItems));
    }


  }
}
