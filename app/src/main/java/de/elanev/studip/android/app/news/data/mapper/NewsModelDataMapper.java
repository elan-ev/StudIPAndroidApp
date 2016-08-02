/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.data.mapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.elanev.studip.android.app.data.datamodel.NewsItem;
import de.elanev.studip.android.app.data.datamodel.User;
import de.elanev.studip.android.app.base.internal.di.PerActivity;
import de.elanev.studip.android.app.news.data.model.NewsModel;
import de.elanev.studip.android.app.news.data.model.UserModel;
import rx.functions.Func1;

/**
 * @author joern
 */
@PerActivity
public class NewsModelDataMapper {

  public Func1<NewsItem, NewsModel> transformNewsItem = new Func1<NewsItem, NewsModel>() {
    @Override public NewsModel call(NewsItem newsItem) {
      return transformNewsItem(newsItem);
    }
  };

  public Func1<List<NewsItem>, List<NewsModel>> transformNewsItemList = new Func1<List<NewsItem>, List<NewsModel>>() {
    @Override public List<NewsModel> call(List<NewsItem> newsItems) {
      return transformNewsList(newsItems);
    }
  };

  @Inject public NewsModelDataMapper() {}

  public List<NewsModel> transformNewsList(List<NewsItem> news) {
    if (news == null) {
      throw new IllegalArgumentException("News List must not be null");
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
    newsModel.author = transformUser(newsItem.author);
    newsModel.title = newsItem.topic;
    newsModel.body = newsItem.body;
    newsModel.date = newsItem.date;
    newsModel.id = newsItem.news_id;
    newsModel.range = newsItem.range;

    return newsModel;
  }

  public UserModel transformUser(User user) {
    if (user == null) {
      throw new IllegalArgumentException("User must not be null");
    }

    UserModel userModel = new UserModel();
    userModel.setTitlePre(user.titlePre);
    userModel.setFirstName(user.forename);
    userModel.setLastName(user.lastname);
    userModel.setTitlePost(user.titlePost);

    return userModel;
  }
}
