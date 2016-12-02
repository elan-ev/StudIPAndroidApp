/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.data.repository.datastore;

import android.annotation.SuppressLint;
import android.support.annotation.WorkerThread;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.courses.data.entity.Course;
import de.elanev.studip.android.app.courses.data.entity.RealmCourseEntity;
import de.elanev.studip.android.app.courses.data.repository.CourseEntityDataMapper;
import de.elanev.studip.android.app.planner.data.entity.EventEntity;
import de.elanev.studip.android.app.planner.data.entity.RealmEventEntity;
import de.elanev.studip.android.app.planner.data.repository.EventsEntityDataMapper;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmList;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Observable;

/**
 * @author joern
 */

@Singleton
@SuppressLint("NewApi")
public class CoursesRealmDataStore implements CoursesDataStore {
  private final RealmConfiguration realConfig;
  private final CourseEntityDataMapper coursesEntityDataMapper;
  private final EventsEntityDataMapper eventsEntityDataMapper;

  @Inject public CoursesRealmDataStore(RealmConfiguration realConfig,
      CourseEntityDataMapper courseEntityDataMapper,
      EventsEntityDataMapper eventsEntityDataMapper) {
    this.realConfig = realConfig;
    this.coursesEntityDataMapper = courseEntityDataMapper;
    this.eventsEntityDataMapper = eventsEntityDataMapper;
  }

  @WorkerThread @Override public Observable<List<Course>> courses() {
    try (Realm realm = Realm.getInstance(realConfig)) {
      RealmResults<RealmCourseEntity> realmResults = realm.where(RealmCourseEntity.class)
          .findAllSorted("startTime", Sort.DESCENDING);
      if (realmResults.isEmpty()) return Observable.empty();


      return Observable.just(realm.copyFromRealm(realmResults))
          .map(coursesEntityDataMapper::transformFromRealm);
    }
  }

  @WorkerThread @Override public Observable<Course> course(String courseId) {
    try (Realm realm = Realm.getInstance(realConfig)) {
      RealmCourseEntity realmCourseEntity = realm.where(RealmCourseEntity.class)
          .equalTo("courseId", courseId)
          .findFirst();

      return Observable.just(realm.copyFromRealm(realmCourseEntity))
          .map(coursesEntityDataMapper::transformFromRealm);
    }

  }

  @Override public Observable<List<EventEntity>> courseEvents(String courseId) {
    return Observable.empty();
  }

  @WorkerThread public void save(List<Course> courses) {
    List<RealmCourseEntity> realmCourseEntities = coursesEntityDataMapper.transformToRealm(courses);
    try (Realm realm = Realm.getInstance(realConfig)) {
      realm.executeTransaction(tsRealm -> tsRealm.copyToRealmOrUpdate(realmCourseEntities));
    }
  }

  @WorkerThread public void save(Course course) {
    RealmCourseEntity realmCourseEntity = coursesEntityDataMapper.transformToRealm(course);
    try (Realm realm = Realm.getInstance(realConfig)) {
      realm.executeTransaction(tsRealm -> tsRealm.copyToRealmOrUpdate(realmCourseEntity));
    }
  }

  @WorkerThread public void saveEvents(List<EventEntity> events) {
    RealmList<RealmEventEntity> realmEventEntity = eventsEntityDataMapper.transformToReal(events);
    try (Realm realm = Realm.getInstance(realConfig)) {
      realm.executeTransaction(realm1 -> realm1.copyToRealmOrUpdate(realmEventEntity));
    }
  }
}
