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
import de.elanev.studip.android.app.user.domain.User;
import de.elanev.studip.android.app.user.domain.UserRepository;
import rx.Observable;

/**
 * @author joern
 */
public class GetCourseUsers extends UseCase<CourseUsers> {
  private final String id;
  private final CoursesRepository coursesRepository;
  private final UserRepository userRepository;

  @Inject public GetCourseUsers(String id, UserRepository userRepository,
      CoursesRepository coursesRepository, ThreadExecutor threadExecutor,
      PostExecutionThread postExecutionThread) {
    super(threadExecutor, postExecutionThread);

    this.id = id;
    this.userRepository = userRepository;
    this.coursesRepository = coursesRepository;
  }

  @Override protected Observable<CourseUsers> buildUseCaseObservable(boolean forceUpdate) {
    return this.coursesRepository.course(id, forceUpdate)
        .flatMap(course -> {
          Observable<List<User>> teachersObs = userRepository.getUsers(course.getTeachers(), forceUpdate);
          Observable<List<User>> tutorsObs = userRepository.getUsers(course.getTutors(), forceUpdate);
          Observable<List<User>> studentsObs = userRepository.getUsers(course.getStudents(), forceUpdate);

          return Observable.zip(teachersObs, tutorsObs, studentsObs,
              (teachers, tutors, students) -> {

                CourseUsers courseUsers = new CourseUsers();
                courseUsers.setTeachers(teachers);
                courseUsers.setTutors(tutors);
                courseUsers.setStudents(students);

                return courseUsers;
              });
        });
  }
}
