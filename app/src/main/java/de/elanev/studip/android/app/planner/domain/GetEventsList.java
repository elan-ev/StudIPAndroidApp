/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.planner.domain;

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
public class GetEventsList extends UseCase<List<Event>> {
  private final PlannerRepository repository;
  private final CoursesRepository coursesRepository;

  @Inject GetEventsList(PlannerRepository plannerRepository, ThreadExecutor threadExecutor,
      PostExecutionThread postExecutionThread, CoursesRepository coursesRepository) {
    super(threadExecutor, postExecutionThread);

    this.repository = plannerRepository;
    this.coursesRepository = coursesRepository;
  }

  @Override protected Observable<List<Event>> buildUseCaseObservable(boolean forceUpdate) {
    return repository.eventsList(forceUpdate)
        .flatMap(events -> Observable.defer(() -> Observable.from(events)
            //FIXME: Find better way to filter the events for the next two weeks
            .filter(event -> event.getStart() * 1000L >= System.currentTimeMillis()
                && event.getStart() * 1000L <= System.currentTimeMillis() + 1209600000)
            .flatMap(event -> coursesRepository.course(event.getCourseId(), forceUpdate)
                .flatMap(course -> {
                  event.setCourse(course);

                  return Observable.just(event);
                }))))
        .toList();
  }
}
