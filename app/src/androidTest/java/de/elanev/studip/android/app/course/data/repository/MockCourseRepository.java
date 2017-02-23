/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.course.data.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.elanev.studip.android.app.courses.domain.CoursesRepository;
import de.elanev.studip.android.app.courses.domain.DomainCourse;
import de.elanev.studip.android.app.courses.domain.DomainCourseAdditionalData;
import de.elanev.studip.android.app.courses.domain.DomainCourseModules;
import de.elanev.studip.android.app.courses.domain.DomainRecording;
import de.elanev.studip.android.app.courses.domain.DomainUnizensus;
import de.elanev.studip.android.app.courses.domain.Semester;
import de.elanev.studip.android.app.planner.domain.Event;
import de.elanev.studip.android.app.user.data.repository.MockUserRepository;
import rx.Observable;

/**
 * @author joern
 */

public class MockCourseRepository implements CoursesRepository {
  public static final String COURSE_TITLE = "Course Title 1";
  public static final int COURSE_TYPE_INT = 2;
  public static final String COURSE_ID = "courseId1";
  public static final List<String> COURSE_TEACHERS = Collections.singletonList(
      MockUserRepository.USER_TEACHER_ID);
  public static final List<String> COURSE_TUTORS = Collections.singletonList(
      MockUserRepository.USER_TUTORS_ID);
  public static final List<String> COURSE_STUDENTS = Collections.singletonList(
      MockUserRepository.USER_STUDENTS_ID);
  public static final DomainCourseModules COURSE_MODULES = new DomainCourseModules();
  public static final String COURSE_SEMESTER_ID = "semesterId1";
  public static final String COURSE_SEMESTER_NAME = "Semester 1";
  public static final Long COURSE_SEMESTER_BEGIN = 946684800L;
  public static final Long COURSE_SEMESTER_END = 946684801L;
  public static final Long COURSE_EVENT_START = 2272147200L;
  public static final Long COURSE_EVENT_END = 2272239000L;
  public static final String COURSE_EVENT_TITLE = "Course event title";
  public static final String COURSE_DESCRIPTION = "Course description";
  private static final DomainCourseAdditionalData COURSE_ADD_DATA = new DomainCourseAdditionalData();
  private static final List<DomainRecording> COURSE_RECORDINGS = new ArrayList<>(1);
  private static final Long COURSE_DURATION_TIME = 5400L;
  private static final String COURSE_RECORDING_ID = "courseRecId1";
  private static final String COURSE_RECORDING_AUTHOR = "Prof. Dr. Course Rec Author";
  private static final String COURSE_RECORDING_DESC = "Course recording description";
  private static final String COURSE_RECORDING_TITLE = "Course recording title";
  private static final String COURSE_UNIZENSUS_TYPE = "Unizensus1";
  private static final String COURSE_UNIZENSUS_URL = "http://localhost";
  private static final Semester COURSE_SEMESTER = new Semester();

  static {

    COURSE_MODULES.setOverview(true);
    COURSE_MODULES.setParticipants(true);
    COURSE_MODULES.setSchedule(true);

    //FIXME: Reenable after refactoring to new arch
    COURSE_MODULES.setDocuments(false);
    COURSE_MODULES.setForum(false);
    COURSE_MODULES.setRecordings(false);
    COURSE_MODULES.setUnizensus(false);

    DomainRecording recording = new DomainRecording();
    recording.setId(COURSE_RECORDING_ID);
    recording.setAuthor(COURSE_RECORDING_AUTHOR);
    recording.setDescription(COURSE_RECORDING_DESC);
    recording.setTitle(COURSE_RECORDING_TITLE);
    COURSE_RECORDINGS.add(recording);
    COURSE_ADD_DATA.setRecordings(COURSE_RECORDINGS);
    COURSE_ADD_DATA.setUnizensusItem(
        new DomainUnizensus(COURSE_UNIZENSUS_TYPE, COURSE_UNIZENSUS_URL));

    COURSE_SEMESTER.setSemesterId(COURSE_SEMESTER_ID);
    COURSE_SEMESTER.setTitle(COURSE_SEMESTER_NAME);
    COURSE_SEMESTER.setBegin(COURSE_SEMESTER_BEGIN);
    COURSE_SEMESTER.setEnd(COURSE_SEMESTER_END);
  }

  @Override public Observable<List<DomainCourse>> courses(boolean forceUpdate) {
    List<DomainCourse> courses = new ArrayList<>(1);
    courses.add(createCourse(COURSE_ID));

    return Observable.just(courses);
  }

  private DomainCourse createCourse(String courseId) {
    DomainCourse course = new DomainCourse();
    course.setCourseId(courseId);
    course.setTitle(COURSE_TITLE);
    course.setType(COURSE_TYPE_INT);
    course.setModules(COURSE_MODULES);
    course.setCourseAdditionalData(COURSE_ADD_DATA);
    course.setSemester(COURSE_SEMESTER);
    course.setDurationTime(COURSE_DURATION_TIME);
    course.setTeachers(COURSE_TEACHERS);
    course.setTutors(COURSE_TUTORS);
    course.setStudents(COURSE_STUDENTS);
    course.setDescription(COURSE_DESCRIPTION);

    return course;
  }

  @Override public Observable<DomainCourse> course(String courseId, boolean forceUpdate) {
    return Observable.just(createCourse(courseId));
  }

  @Override public Observable<List<Event>> courseEvents(String id, boolean forceUpdate) {
    Event event = new Event();
    event.setTitle(COURSE_EVENT_TITLE);
    event.setStart(COURSE_EVENT_START);
    event.setEnd(COURSE_EVENT_END);
    ArrayList<Event> events = new ArrayList<>(1);
    events.add(event);

    return Observable.just(events);
  }
}
