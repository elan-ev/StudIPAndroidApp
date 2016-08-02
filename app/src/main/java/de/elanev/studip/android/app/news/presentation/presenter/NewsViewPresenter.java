/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.presentation.presenter;

import javax.inject.Inject;

import de.elanev.studip.android.app.base.BaseRxLcePresenter;
import de.elanev.studip.android.app.news.data.mapper.NewsModelDataMapper;
import de.elanev.studip.android.app.news.data.model.NewsModel;
import de.elanev.studip.android.app.news.domain.GetNewsDetails;
import de.elanev.studip.android.app.news.presentation.view.NewsView;

/**
 * @author joern
 */
public class NewsViewPresenter extends BaseRxLcePresenter<NewsView, NewsModel> {
  GetNewsDetails mGetNewsDetailsUseCase;
  NewsModelDataMapper mNewsModelDataMapper;

  @Inject public NewsViewPresenter(GetNewsDetails getNewsDetails,
      NewsModelDataMapper newsModelDataMapper) {
    this.mGetNewsDetailsUseCase = getNewsDetails;
    this.mNewsModelDataMapper = newsModelDataMapper;
  }

  public void loadNews() {
    this.mGetNewsDetailsUseCase.get()
        .map(this.mNewsModelDataMapper.transformNewsItem)
        .subscribe(new DefaultSubscriber(false));
  }
}