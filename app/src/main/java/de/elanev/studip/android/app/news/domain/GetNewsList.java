/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.domain;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.elanev.studip.android.app.base.UseCase;
import de.elanev.studip.android.app.base.domain.executor.PostExecutionThread;
import de.elanev.studip.android.app.base.domain.executor.ThreadExecutor;
import de.elanev.studip.android.app.courses.domain.CoursesRepository;
import rx.Observable;

/**
 * @author joern
 */
public class GetNewsList extends UseCase<List<NewsItem>> {
  private final NewsRepository mRepository;
  private final CoursesRepository coursesRepository;

  @Inject public GetNewsList(NewsRepository repository, ThreadExecutor threadExecutor,
      PostExecutionThread postExecutionThread, CoursesRepository coursesRepository) {
    super(threadExecutor, postExecutionThread);

    this.mRepository = repository;
    this.coursesRepository = coursesRepository;
  }

  @Override protected Observable<List<NewsItem>> buildUseCaseObservable(boolean forceUpdate) {
    Observable<List<NewsItem>> coursesObs = coursesRepository.courses(forceUpdate)
        .flatMap(domainCourses -> Observable.defer(() -> Observable.from(domainCourses)
            .flatMap(domainCourse -> mRepository.newsForRange(domainCourse.getCourseId(),
                forceUpdate))));

    return Observable.zip(mRepository.newsList(forceUpdate), coursesObs, (news, courses) -> {

      ArrayList<NewsItem> newsItems = new ArrayList<>(news.size() + courses.size());
      newsItems.addAll(news);
      newsItems.addAll(courses);

      return newsItems;
    });
  }
}