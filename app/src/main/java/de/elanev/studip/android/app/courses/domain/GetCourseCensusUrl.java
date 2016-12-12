/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.domain;

import javax.inject.Inject;

import de.elanev.studip.android.app.base.UseCase;
import de.elanev.studip.android.app.base.domain.executor.PostExecutionThread;
import de.elanev.studip.android.app.base.domain.executor.ThreadExecutor;
import rx.Observable;

/**
 * @author joern
 */
public class GetCourseCensusUrl extends UseCase<String> {
  private final String id;
  private final CoursesRepository repository;

  @Inject public GetCourseCensusUrl(String id, CoursesRepository coursesRepository,
      ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
    super(threadExecutor, postExecutionThread);

    this.id = id;
    this.repository = coursesRepository;
  }

  @Override protected Observable<String> buildUseCaseObservable(boolean forceUpdate) {
    return this.repository.course(id, forceUpdate)
        .flatMap(course -> {
          if (course.getCourseAdditionalData() != null && course.getCourseAdditionalData()
              .getUnizensusItem() != null) {
            return Observable.defer(() -> Observable.just(course.getCourseAdditionalData()
                .getUnizensusItem()
                .getUrl()));
          }

          return Observable.empty();
        });
  }
}
