/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.data.repository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.courses.data.entity.Course;
import de.elanev.studip.android.app.courses.data.repository.datastore.CoursesCloudDataStore;
import de.elanev.studip.android.app.courses.data.repository.datastore.CoursesRealmDataStore;
import de.elanev.studip.android.app.courses.domain.CoursesRepository;
import de.elanev.studip.android.app.courses.domain.DomainCourse;
import de.elanev.studip.android.app.planner.data.entity.EventEntity;
import de.elanev.studip.android.app.planner.data.repository.EventsEntityDataMapper;
import de.elanev.studip.android.app.planner.domain.Event;
import rx.Observable;

/**
 * @author joern
 */

@Singleton
public class CoursesDataRepository implements CoursesRepository {
  private final CoursesCloudDataStore coursesCloudDataStore;
  private final CourseEntityDataMapper courseEntityDataMapper;
  private final EventsEntityDataMapper eventsEntityDataMapper;
  private final CoursesRealmDataStore coursesRealmDataStore;

  @Inject public CoursesDataRepository(CourseEntityDataMapper courseEntityDataMapper,
      EventsEntityDataMapper eventsEntityDataMapper, CoursesRealmDataStore coursesRealmDataStore,
      CoursesCloudDataStore coursesCloudDataStore) {
    this.courseEntityDataMapper = courseEntityDataMapper;
    this.eventsEntityDataMapper = eventsEntityDataMapper;
    this.coursesRealmDataStore = coursesRealmDataStore;
    this.coursesCloudDataStore = coursesCloudDataStore;
  }

  @Override public Observable<List<DomainCourse>> courses(boolean forceUpdate) {
    Observable<List<Course>> localDataObs = coursesRealmDataStore.courses();
    Observable<List<Course>> cloudDataObs = coursesCloudDataStore.courses()
        .doOnNext(coursesRealmDataStore::save);

    return localDataObs.exists(courseEntities -> courseEntities != null)
        .flatMap(isInDb -> (isInDb && !forceUpdate) ? localDataObs : cloudDataObs)
        .flatMap(courseEntities -> {
          coursesRealmDataStore.save(courseEntities);
          return Observable.just(courseEntities);
        })
        .flatMap(courses -> Observable.from(courses)
            .toSortedList((course, course2) -> course2.getSemester()
                .getBegin()
                .compareTo(course.getSemester()
                    .getBegin())))
        .map(courseEntityDataMapper::transform);

  }

  @Override public Observable<DomainCourse> course(String id, boolean forceUpdate) {
    Observable<Course> cloudDataObs = coursesCloudDataStore.course(id)
        .doOnNext(coursesRealmDataStore::save);
    Observable<Course> localDataObs = coursesRealmDataStore.course(id);

    return localDataObs.exists(newsEntity -> newsEntity != null)
        .flatMap(isInDb -> (isInDb && !forceUpdate) ? localDataObs : cloudDataObs)
        .map(courseEntityDataMapper::transform);
  }

  @Override public Observable<List<Event>> courseEvents(String id, boolean forceUpdate) {
    Observable<List<EventEntity>> cloudDataObs = coursesCloudDataStore.courseEvents(id)
        .doOnNext(coursesRealmDataStore::saveEvents);
    Observable<List<EventEntity>> localDataObs = coursesRealmDataStore.courseEvents(id);

    return localDataObs.exists(eventEntities -> eventEntities != null)
        .flatMap(isInDb -> (isInDb && !forceUpdate) ? localDataObs : cloudDataObs)
        .map(eventsEntityDataMapper::transform);
  }
}
