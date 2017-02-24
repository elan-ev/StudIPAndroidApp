/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.data.repository;

import java.util.ArrayList;
import java.util.List;

import de.elanev.studip.android.app.course.data.repository.MockCourseRepository;
import de.elanev.studip.android.app.news.domain.NewsItem;
import de.elanev.studip.android.app.news.domain.NewsRepository;
import de.elanev.studip.android.app.user.data.repository.MockUserRepository;
import de.elanev.studip.android.app.user.domain.User;
import de.elanev.studip.android.app.util.TextTools;
import rx.Observable;

/**
 * @author joern
 */

public class MockNewsRepository implements NewsRepository {
  public static final String NEWS_TITLE = "Test News";
  public static final String NEWS_TITLE_RANGE = "Range Test News Title";
  public static final String NEWS_BODY = "Test News Body";
  public static final String USER_NAME = "News Test User";
  private static final String NEWS_ID = "123";
  private static final Long NEWS_DATE = 946684800L;
  public static final User NEWS_AUTHOR = MockUserRepository.TEACHER;

  @Override public Observable<NewsItem> newsItem(String id, boolean forceUpdate) {
    NewsItem newsItem = createNewsItem(id);
    return Observable.just(newsItem);
  }

  private NewsItem createNewsItem(String id) {
    NewsItem newsItem = new NewsItem();
    newsItem.setTitle(NEWS_TITLE);
    newsItem.setBody(NEWS_BODY);
    newsItem.setDate(NEWS_DATE);
    newsItem.setRange(MockCourseRepository.COURSE.getCourseId());
    newsItem.setAuthor(NEWS_AUTHOR);

    if (TextTools.isEmpty(id)) {
      newsItem.setNewsId(id);
    }

    return newsItem;
  }

  @Override public Observable<List<NewsItem>> newsList(boolean forceUpdate) {
    List<NewsItem> items = new ArrayList<>(1);
    items.add(createNewsItem(NEWS_ID));

    return Observable.just(items);
  }

  @Override public Observable<List<NewsItem>> newsForRange(String id, boolean forceUpdate) {
    List<NewsItem> items = new ArrayList<>(1);
    NewsItem item = createNewsItem(NEWS_ID);
    item.setTitle(NEWS_TITLE_RANGE);
    item.setRange(id);
    items.add(item);

    return Observable.just(items);
  }
}
