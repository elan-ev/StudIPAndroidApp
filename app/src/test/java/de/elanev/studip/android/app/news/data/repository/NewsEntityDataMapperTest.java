/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.data.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import de.elanev.studip.android.app.courses.data.repository.CourseEntityDataMapper;
import de.elanev.studip.android.app.courses.data.entity.Course;
import de.elanev.studip.android.app.news.data.entity.NewsEntity;
import de.elanev.studip.android.app.news.domain.NewsItem;
import de.elanev.studip.android.app.user.data.entity.UserEntity;
import de.elanev.studip.android.app.user.data.entity.UserEntityDataMapper;
import de.elanev.studip.android.app.user.domain.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * @author joern
 */
public class NewsEntityDataMapperTest {
  private static final String FAKE_NEWS_ID = "123";
  private static final String FAKE_TITLE = "Fake title";
  private static final String FAKE_BODY = "Fake body";
  private static final long FAKE_DATE = 123L;
  private static final String FAKE_RANGE = "123";


  @Mock UserEntityDataMapper mockUserEntityDataMapper;
  @Mock CourseEntityDataMapper mockCourseEntityDataMapper;
  @Mock UserEntity mockUser;
  @Mock Course mockCourse;
  @Mock User mockDomainUser;
  private NewsEntityDataMapper newsEntityDataMapper;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    newsEntityDataMapper = new NewsEntityDataMapper(mockUserEntityDataMapper,
        mockCourseEntityDataMapper);
  }

  @Test public void shouldTransformNewsEntityListToNewsList() throws Exception {
    NewsEntity mockNewsEntity1 = mock(NewsEntity.class);
    NewsEntity mockNewsEntity2 = mock(NewsEntity.class);

    List<NewsEntity> list = new ArrayList<>(5);
    list.add(mockNewsEntity1);
    list.add(mockNewsEntity2);

    List<NewsItem> newsModels = newsEntityDataMapper.transform(list);

    assertThat(newsModels.toArray()[0], is(instanceOf(NewsItem.class)));
    assertThat(newsModels.toArray()[1], is(instanceOf(NewsItem.class)));
    assertThat(newsModels.size(), is(2));
  }

  @Test public void shouldTransformNewsEntityToNews() throws Exception {
    given(mockUserEntityDataMapper.transform(mockUser)).willReturn(mockDomainUser);
    NewsEntity newsEntity = createFakeNewsEntity();
    NewsItem newsItem = newsEntityDataMapper.transform(newsEntity);

    assertThat(newsItem, is(instanceOf(NewsItem.class)));
    assertThat(newsItem.getAuthor(), is(mockDomainUser));
    assertThat(newsItem.getTitle(), is(FAKE_TITLE));
    assertThat(newsItem.getBody(), is(FAKE_BODY));
    assertThat(newsItem.getNewsId(), is(FAKE_NEWS_ID));
    assertThat(newsItem.getCourse(), is(mockCourse));
    assertThat(newsItem.getRange(), is(FAKE_RANGE));
    assertThat(newsItem.getDate(), is(FAKE_DATE));
  }

  private NewsEntity createFakeNewsEntity() {
    NewsEntity newsEntity = new NewsEntity();

    newsEntity.setNewsId(FAKE_NEWS_ID);
    newsEntity.setTopic(FAKE_TITLE);
    newsEntity.setBody(FAKE_BODY);
    newsEntity.setDate(FAKE_DATE);
    newsEntity.setCourse(mockCourse);
    newsEntity.setRange(FAKE_RANGE);
    newsEntity.setAuthor(mockUser);

    return newsEntity;
  }

}