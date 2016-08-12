/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.data.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.news.data.entity.NewsEntity;
import de.elanev.studip.android.app.user.data.entity.UserEntityDataMapper;
import de.elanev.studip.android.app.news.domain.NewsItem;
import rx.functions.Func1;

/**
 * @author joern
 */
@Singleton
public class NewsEntityDataMapper {

  private UserEntityDataMapper userEntityDataMapper;

  @Inject public NewsEntityDataMapper(UserEntityDataMapper userEntityDataMapper) {
    this.userEntityDataMapper = userEntityDataMapper;
  }

  public List<NewsItem> transform(Collection<NewsEntity> newsEntities) {
    List<NewsItem> newsItems = new ArrayList<>();

    NewsItem newsItem;
    for (NewsEntity newsEntity : newsEntities) {
      newsItem = transform(newsEntity);
      if (newsItem != null) {
        newsItems.add(newsItem);
      }
    }

    return newsItems;
  }

  public NewsItem transform(NewsEntity newsEntity) {
    NewsItem newsItem = null;

    if (newsEntity != null) {
      newsItem = new NewsItem(newsEntity.news_id);
      newsItem.setTitle(newsEntity.topic);
      newsItem.setDate(newsEntity.date);
      newsItem.setBody(newsEntity.body);
      newsItem.setRange(newsEntity.range);
      newsItem.setCourse(newsEntity.course);
      newsItem.setAuthor(userEntityDataMapper.transform(newsEntity.author));
    }

    return newsItem;
  }
}
