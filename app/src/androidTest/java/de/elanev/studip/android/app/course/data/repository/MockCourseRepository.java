/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.course.data.repository;

import java.util.Collections;
import java.util.List;

import de.elanev.studip.android.app.courses.domain.CoursesRepository;
import de.elanev.studip.android.app.courses.domain.DomainCourse;
import de.elanev.studip.android.app.courses.domain.DomainCourseAdditionalData;
import de.elanev.studip.android.app.courses.domain.DomainCourseModules;
import de.elanev.studip.android.app.courses.domain.Semester;
import de.elanev.studip.android.app.planner.domain.Event;
import de.elanev.studip.android.app.user.data.repository.MockUserRepository;
import rx.Observable;

/**
 * @author joern
 */
public class MockCourseRepository implements CoursesRepository {
  public static final DomainCourse COURSE = createCourse();
  public static final Event COURSE_EVENT = new Event("courseEventId1", COURSE, 2272147200L,
      2272239000L, "Course event title", "A course event", "Event Room1", "#efefef", "Event",
      COURSE.getCourseId(), false);

  private static DomainCourse createCourse() {
    DomainCourse course = new DomainCourse();
    course.setCourseId("courseId1");
    course.setTitle("Course Title 1");
    course.setType(2);

    //FIXME: Enable Documents, Forums, Recordings and Unizensus after refactoring
    course.setModules(new DomainCourseModules(true, true, true, false, false, false, false));
    course.setCourseAdditionalData(new DomainCourseAdditionalData());
    //

    course.setSemester(
        new Semester("semesterId1", "Semester 1", "The first semester ever", 946684800L, 946684801L,
            946684800L, 946684801L));
    course.setDurationTime(5400L);
    course.setTeachers(Collections.singletonList(MockUserRepository.USER_TEACHER_ID));
    course.setTutors(Collections.singletonList(MockUserRepository.USER_TUTORS_ID));
    course.setStudents(Collections.singletonList(MockUserRepository.USER_STUDENTS_ID));
    course.setDescription("Course description");

    return course;
  }

  @Override public Observable<List<DomainCourse>> courses(boolean forceUpdate) {
    return Observable.just(Collections.singletonList(COURSE));
  }

  @Override public Observable<DomainCourse> course(String courseId, boolean forceUpdate) {
    return Observable.just(COURSE);
  }

  @Override public Observable<List<Event>> courseEvents(String id, boolean forceUpdate) {
    return Observable.just(Collections.singletonList(COURSE_EVENT));
  }
}
