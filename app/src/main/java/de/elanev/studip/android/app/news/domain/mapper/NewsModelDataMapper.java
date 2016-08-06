/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.domain.mapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.elanev.studip.android.app.base.internal.di.PerActivity;
import de.elanev.studip.android.app.news.domain.NewsItem;
import de.elanev.studip.android.app.news.domain.User;
import de.elanev.studip.android.app.news.presentation.model.NewsModel;
import de.elanev.studip.android.app.news.presentation.model.UserModel;

/**
 * @author joern
 */
@PerActivity
public class NewsModelDataMapper {


  @Inject public NewsModelDataMapper() {}

  public List<NewsModel> transformNewsList(List<NewsItem> news) {
    if (news == null) {
      throw new IllegalArgumentException("NewsItem List must not be null");
    }

    ArrayList<NewsModel> items = new ArrayList<>();

    for (NewsItem item : news) {
      items.add(transformNewsItem(item));
    }

    return items;
  }

  public NewsModel transformNewsItem(NewsItem newsItem) {
    if (newsItem == null) {
      throw new IllegalArgumentException("NewsItem must not be null");
    }

    NewsModel newsModel = new NewsModel();
    newsModel.author = transformUser(newsItem.getAuthor());
    newsModel.title = newsItem.getTitle();
    newsModel.body = newsItem.getBody();
    newsModel.date = newsItem.getDate();
    newsModel.id = newsItem.getNewsId();
    newsModel.range = newsItem.getRange();
    newsModel.course = newsItem.getCourse();

    return newsModel;
  }

  public UserModel transformUser(User user) {
    UserModel userModel = null;

    if (user != null) {
      userModel = new UserModel();
      userModel.setUserId(user.getUserId());
      userModel.setFullName(user.getFullname());
      userModel.setAvatarUrl(user.getAvatarUrl());
    }

    return userModel;
  }
}
