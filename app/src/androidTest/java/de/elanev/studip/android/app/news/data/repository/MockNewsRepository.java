/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.data.repository;

import android.text.TextUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.elanev.studip.android.app.StudIPConstants;
import de.elanev.studip.android.app.course.data.repository.MockCourseRepository;
import de.elanev.studip.android.app.courses.data.entity.Course;
import de.elanev.studip.android.app.news.domain.NewsItem;
import de.elanev.studip.android.app.news.domain.NewsRepository;
import de.elanev.studip.android.app.user.data.repository.MockUserRepository;
import rx.Observable;

/**
 * @author joern
 */

public class MockNewsRepository implements NewsRepository {
  private static final String GLOBAL_NEWS_ID = "globalNewsId1";
  public static final NewsItem GLOBAL_NEWS = new NewsItem(GLOBAL_NEWS_ID, "Global news Title",
      946684800L, "Global news body", MockUserRepository.TEACHER,
      StudIPConstants.STUDIP_NEWS_GLOBAL_RANGE, null);
  private static final String COURSE_NEWS_ID = "courseNewsId1";

  //FIXME: Use the correct course form the MockRepo after the NewsRepo was fixed
  private static final Course COURSE = createCourse();
  public static final NewsItem COURSE_NEWS = new NewsItem(COURSE_NEWS_ID, "Course news Title",
      946684800L, "Course news body", MockUserRepository.TEACHER, COURSE.getCourseId(), COURSE);
  private static final String INSTITUTE_NEWS_ID = "instituteNewsId1";
  //FIXME
  public static final NewsItem INSTITUTE_NEWS = new NewsItem(INSTITUTE_NEWS_ID,
      "Institute news Title", 946684800L, "Course news body", MockUserRepository.TEACHER,
      "institute", null);

  private static Course createCourse() {
    Course course = new Course();
    course.setCourseId(MockCourseRepository.COURSE.getCourseId());
    return course;
  }

  @Override public Observable<NewsItem> newsItem(String id, boolean forceUpdate) {
    return Observable.just(createNewsItem(id));
  }

  private NewsItem createNewsItem(String id) {
    switch (id) {
      case COURSE_NEWS_ID:
        return COURSE_NEWS;
      case GLOBAL_NEWS_ID:
        return GLOBAL_NEWS;
      case INSTITUTE_NEWS_ID:
        return INSTITUTE_NEWS;
      default:
        return null;
    }
  }

  @Override public Observable<List<NewsItem>> newsList(boolean forceUpdate) {
    return Observable.just(Arrays.asList(COURSE_NEWS, GLOBAL_NEWS, INSTITUTE_NEWS));
  }

  @Override public Observable<List<NewsItem>> newsForRange(String range, boolean forceUpdate) {
    if (TextUtils.equals(range, GLOBAL_NEWS.getRange())) {
      return Observable.just(Collections.singletonList(GLOBAL_NEWS));
    } else if (TextUtils.equals(range, COURSE_NEWS.getRange())) {
      return Observable.just(Collections.singletonList(COURSE_NEWS));
    } else {
      return Observable.just(Collections.singletonList(INSTITUTE_NEWS));
    }
  }
}
