/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.domain;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.elanev.studip.android.app.data.datamodel.Course;
import de.elanev.studip.android.app.user.domain.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author joern
 */
public class NewsItemTest {
  private static final String FAKE_NEWS_ID = "123";
  @Mock User mockUser;
  @Mock Course mockCourse;
  private NewsItem newsItem;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    newsItem = new NewsItem(FAKE_NEWS_ID);
  }

  @Test public void shouldReturnCorrectString() throws Exception {
    insertFakeNewsData();

    String expected = "**************** News **************\n";
    expected += "id=123\n";
    expected += "topic=Test topic\n";
    expected += "date=123\n";
    expected += "body=Test body\n";
    expected += "author=null\n";
    expected += "course=null\n";
    expected += "**************************************";

    assertThat(newsItem.toString(), is(expected));
  }

  private void insertFakeNewsData() {
    newsItem.setTitle("Test topic");
    newsItem.setAuthor(null);
    newsItem.setDate(123L);
    newsItem.setBody("Test body");
    newsItem.setCourse(null);
    newsItem.setRange("123");
  }

  @Test public void getNewsId() throws Exception {
    String newsId = newsItem.getNewsId();

    assertThat(newsId, is(FAKE_NEWS_ID));
  }

  @Test public void getTitle() throws Exception {
    newsItem.setTitle("Test topic");

    assertThat(newsItem.getTitle(), is("Test topic"));
  }

  @Test public void getDate() throws Exception {
    newsItem.setDate(123L);

    assertThat(newsItem.getDate(), is(123L));
  }

  @Test public void getBody() throws Exception {
    newsItem.setBody("Test body");

    assertThat(newsItem.getBody(), is("Test body"));
  }

  @Test public void getAuthor() throws Exception {
    newsItem.setAuthor(mockUser);

    assertThat(newsItem.getAuthor(), is(mockUser));
  }

  @Test public void getCourse() throws Exception {
    newsItem.setCourse(mockCourse);

    assertThat(newsItem.getCourse(), is(mockCourse));
  }

  @Test public void getRange() throws Exception {
    newsItem.setRange("123");

    assertThat(newsItem.getRange(), is("123"));
  }
}