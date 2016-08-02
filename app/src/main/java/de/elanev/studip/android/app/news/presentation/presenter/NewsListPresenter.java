/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.presentation.presenter;

import com.fernandocejas.frodo.annotation.RxLogSubscriber;

import java.util.List;

import javax.inject.Inject;

import de.elanev.studip.android.app.base.BaseRxLcePresenter;
import de.elanev.studip.android.app.base.UseCase;
import de.elanev.studip.android.app.data.datamodel.NewsItem;
import de.elanev.studip.android.app.news.data.mapper.NewsModelDataMapper;
import de.elanev.studip.android.app.news.data.model.NewsModel;
import de.elanev.studip.android.app.news.domain.GetNewsList;
import de.elanev.studip.android.app.news.presentation.view.NewsListView;

/**
 * @author joern
 */
public class NewsListPresenter extends BaseRxLcePresenter<NewsListView, List<NewsModel>> {

  private final UseCase<List<NewsItem>> mGetNewsListUseCase;
  private final NewsModelDataMapper mNewsModelDataMapper;

  @Inject public NewsListPresenter(GetNewsList getNewsListUseCase, NewsModelDataMapper dataMapper) {
    this.mGetNewsListUseCase = getNewsListUseCase;
    this.mNewsModelDataMapper = dataMapper;
  }

  public void loadNews(boolean pullToRefresh) {
    this.mGetNewsListUseCase.get()
        .map(this.mNewsModelDataMapper.transformNewsItemList)
        .subscribe(new NewsListSubscriber(pullToRefresh));
  }

  @SuppressWarnings("ConstantConditions") public void onNewsClicked(NewsModel news) {
    if (isViewAttached()) {
      getView().viewNews(news);
    }
  }

  private final class NewsListSubscriber extends DefaultSubscriber {

    public NewsListSubscriber(boolean ptr) {
      super(ptr);
    }

    @Override public void onCompleted() {
      super.onCompleted();
    }

    @Override public void onError(Throwable e) {
      super.onError(e);
    }

    @Override public void onNext(List<NewsModel> newsModels) {
      super.onNext(newsModels);
    }
  }
}
