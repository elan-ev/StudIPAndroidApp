/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.domain;

import java.util.List;

import javax.inject.Inject;

import de.elanev.studip.android.app.base.UseCase;
import de.elanev.studip.android.app.base.domain.executor.PostExecutionThread;
import de.elanev.studip.android.app.base.domain.executor.ThreadExecutor;
import de.elanev.studip.android.app.planner.domain.Event;
import rx.Observable;

/**
 * @author joern
 */
public class GetCourseSchedule extends UseCase<List<Event>> {
  private final String id;
  private final CoursesRepository repository;

  @Inject public GetCourseSchedule(String id, CoursesRepository coursesRepository,
      ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
    super(threadExecutor, postExecutionThread);

    this.id = id;
    this.repository = coursesRepository;
  }

  @Override protected Observable<List<Event>> buildUseCaseObservable(boolean forceUpdate) {
    return this.repository.courseEvents(id, forceUpdate)
        .flatMap(courseEvents -> Observable.defer(() -> Observable.from(courseEvents))
            .filter(courseEvent -> courseEvent.getStart() * 1000L > System.currentTimeMillis()))
        .toSortedList((event, event2) -> event.getStart()
            .compareTo(event2.getStart()));
  }
}
