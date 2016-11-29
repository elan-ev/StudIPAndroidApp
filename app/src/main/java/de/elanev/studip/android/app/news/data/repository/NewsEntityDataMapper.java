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

import de.elanev.studip.android.app.courses.data.repository.CourseEntityDataMapper;
import de.elanev.studip.android.app.news.data.entity.NewsEntity;
import de.elanev.studip.android.app.news.data.entity.RealmNewsEntity;
import de.elanev.studip.android.app.news.domain.NewsItem;
import de.elanev.studip.android.app.user.data.entity.UserEntityDataMapper;

/**
 * @author joern
 */
@Singleton
public class NewsEntityDataMapper {

  final private UserEntityDataMapper userEntityDataMapper;
  final private CourseEntityDataMapper courseEntityDataMapper;

  @Inject NewsEntityDataMapper(UserEntityDataMapper userEntityDataMapper,
      CourseEntityDataMapper courseEntityDataMapper) {
    this.userEntityDataMapper = userEntityDataMapper;
    this.courseEntityDataMapper = courseEntityDataMapper;
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
      newsItem = new NewsItem(newsEntity.getNewsId());
      newsItem.setTitle(newsEntity.getTopic());
      newsItem.setDate(newsEntity.getDate());
      newsItem.setBody(newsEntity.getBody());
      newsItem.setRange(newsEntity.getRange());
      newsItem.setCourse(newsEntity.getCourse());
      newsItem.setAuthor(userEntityDataMapper.transform(newsEntity.getAuthor()));
    }

    return newsItem;
  }

  public List<NewsEntity> transformFromRealm(List<RealmNewsEntity> newsEntities) {

    ArrayList<NewsEntity> dataNewsEntities = new ArrayList<>(newsEntities.size());

    for (RealmNewsEntity realmNewsEntity : newsEntities) {
      NewsEntity newsEntity = transform(realmNewsEntity);
      if (newsEntity != null) {
        dataNewsEntities.add(newsEntity);
      }
    }

    return dataNewsEntities;
  }

  public NewsEntity transform(RealmNewsEntity realmNewsEntity) {
    NewsEntity newsEntity = new NewsEntity();
    newsEntity.setNewsId(realmNewsEntity.getNewsId());
    newsEntity.setTopic(realmNewsEntity.getTopic());
    newsEntity.setBody(realmNewsEntity.getBody());
    newsEntity.setDate(realmNewsEntity.getDate());
    newsEntity.setExpire(realmNewsEntity.getExpire());
    newsEntity.setRange(realmNewsEntity.getRange());
    newsEntity.setAuthor(userEntityDataMapper.transformFromRealm(realmNewsEntity.getAuthor()));
    newsEntity.setCourse(courseEntityDataMapper.transformFromRealm(realmNewsEntity.getCourse()));

    return newsEntity;
  }

  public List<NewsItem> transform(List<NewsEntity> newsEntities) {

    ArrayList<NewsItem> newsItems = new ArrayList<>(newsEntities.size());

    for (NewsEntity entity : newsEntities) {
      NewsItem newsItem = transform(entity);
      if (newsItem != null) {
        newsItems.add(newsItem);
      }
    }

    return newsItems;
  }

  public List<RealmNewsEntity> transformToRealm(List<NewsEntity> newsEntities) {

    ArrayList<RealmNewsEntity> realmNewsEntities = new ArrayList<>(newsEntities.size());

    for (NewsEntity entity : newsEntities) {
      RealmNewsEntity realmNewsEntity = transformToRealm(entity);
      if (realmNewsEntity != null) {
        realmNewsEntities.add(realmNewsEntity);
      }
    }

    return realmNewsEntities;
  }

  public RealmNewsEntity transformToRealm(NewsEntity newsEntity) {
    RealmNewsEntity realmNewsEntity = new RealmNewsEntity();
    realmNewsEntity.setNewsId(newsEntity.getNewsId());
    realmNewsEntity.setTopic(newsEntity.getTopic());
    realmNewsEntity.setBody(newsEntity.getBody());
    realmNewsEntity.setDate(newsEntity.getDate());
    realmNewsEntity.setExpire(newsEntity.getExpire());
    realmNewsEntity.setRange(newsEntity.getRange());
    realmNewsEntity.setAuthor(userEntityDataMapper.transformToRealm(newsEntity.getAuthor()));
    realmNewsEntity.setCourse(courseEntityDataMapper.transformToRealm(newsEntity.getCourse()));

    return realmNewsEntity;
  }

}
