/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.presentation.mapper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import de.elanev.studip.android.app.courses.data.entity.Course;
import de.elanev.studip.android.app.news.domain.NewsItem;
import de.elanev.studip.android.app.news.presentation.model.NewsModel;
import de.elanev.studip.android.app.user.domain.User;
import de.elanev.studip.android.app.user.presentation.mapper.UserModelDataMapper;
import de.elanev.studip.android.app.user.presentation.model.UserModel;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * @author joern
 */
public class NewsModelDataMapperTest {
  private static final String FAKE_NEWS_ID = "123";
  private static final String FAKE_TITLE = "Fake title";
  private static final String FAKE_BODY = "Fake body";
  private static final long FAKE_DATE = 123L;
  private static final String FAKE_RANGE = "123";


  @Mock UserModelDataMapper mockUserModelDataMapper;
  @Mock User mockUser;
  @Mock Course mockCourse;
  @Mock UserModel mockPresentationUser;
  private NewsModelDataMapper newsModelDataMapper;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    newsModelDataMapper = new NewsModelDataMapper(mockUserModelDataMapper);
  }

  @Test public void transformNewsList() throws Exception {
    NewsItem mockNewsItem1 = mock(NewsItem.class);
    NewsItem mockNewsItem2 = mock(NewsItem.class);

    List<NewsItem> list = new ArrayList<>(5);
    list.add(mockNewsItem1);
    list.add(mockNewsItem2);

    List<NewsModel> newsModels = newsModelDataMapper.transformNewsList(list);

    assertThat(newsModels.toArray()[0], is(instanceOf(NewsModel.class)));
    assertThat(newsModels.toArray()[1], is(instanceOf(NewsModel.class)));
    assertThat(newsModels.size(), is(2));
  }

  @Test public void transformNewsItem() throws Exception {
    given(mockUserModelDataMapper.transform(mockUser)).willReturn(mockPresentationUser);
    NewsItem newsItem = createFakeNews();
    NewsModel newsModel = newsModelDataMapper.transformNewsItem(newsItem);

    assertThat(newsModel, is(instanceOf(NewsModel.class)));
    assertThat(newsModel.author, is(mockPresentationUser));
    assertThat(newsModel.title, is(FAKE_TITLE));
    assertThat(newsModel.body, is(FAKE_BODY));
    assertThat(newsModel.id, is(FAKE_NEWS_ID));
    assertThat(newsModel.course, is(mockCourse));
    assertThat(newsModel.range, is(FAKE_RANGE));
    assertThat(newsModel.date, is(FAKE_DATE));
  }

  private NewsItem createFakeNews() {
    NewsItem newsItem = new NewsItem(FAKE_NEWS_ID);
    newsItem.setTitle(FAKE_TITLE);
    newsItem.setBody(FAKE_BODY);
    newsItem.setDate(FAKE_DATE);
    newsItem.setCourse(mockCourse);
    newsItem.setRange(FAKE_RANGE);
    newsItem.setAuthor(mockUser);
    return newsItem;
  }
}

