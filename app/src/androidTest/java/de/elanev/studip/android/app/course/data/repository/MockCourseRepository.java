/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.course.data.repository;

import java.util.ArrayList;
import java.util.List;

import de.elanev.studip.android.app.courses.domain.CoursesRepository;
import de.elanev.studip.android.app.courses.domain.DomainCourse;
import de.elanev.studip.android.app.planner.domain.Event;
import rx.Observable;

/**
 * @author joern
 */

public class MockCourseRepository implements CoursesRepository {
  private static final String COURSE_ID = "courseId1";

  @Override public Observable<List<DomainCourse>> courses(boolean forceUpdate) {
    List<DomainCourse> courses = new ArrayList<>(1);
    DomainCourse course = new DomainCourse();
    course.setCourseId(COURSE_ID);
    courses.add(course);
    return Observable.just(courses);
  }

  @Override public Observable<DomainCourse> course(String courseId, boolean forceUpdate) {
    return null;
  }

  @Override public Observable<List<Event>> courseEvents(String id, boolean forceUpdate) {
    return null;
  }
}
