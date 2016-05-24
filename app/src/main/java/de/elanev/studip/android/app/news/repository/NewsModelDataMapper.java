/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.elanev.studip.android.app.data.datamodel.NewsItem;
import de.elanev.studip.android.app.data.datamodel.User;

/**
 * @author joern
 */
public class NewsModelDataMapper {

  public static Collection<NewsModel> transform(List<NewsItem> news) {
    ArrayList<NewsModel> items = new ArrayList<>();

    for (NewsItem item : news) {
      items.add(transform(item));
    }

    return items;
  }

  public static NewsModel transform(NewsItem newsItem) {
    if (newsItem == null) {
      throw new IllegalArgumentException("NewsItem must not be null");
    }

    NewsModel newsModel = new NewsModel();
    newsModel.author = transform(newsItem.author);
    newsModel.title = newsItem.topic;
    newsModel.body = newsItem.body;
    newsModel.date = newsItem.date;

    return newsModel;
  }

  public static UserModel transform(User user) {
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
