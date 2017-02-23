/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.user.data.repository;

import java.util.Collections;
import java.util.List;

import de.elanev.studip.android.app.user.domain.User;
import de.elanev.studip.android.app.user.domain.UserRepository;
import rx.Observable;

/**
 * @author joern
 */

public class MockUserRepository implements UserRepository {
  public static final String USER_TEACHER_ID = "userTeacherId1";
  public static final String USER_TUTORS_ID = "userTutorId1";
  public static final String USER_STUDENTS_ID = "userStudentId1";
  public static final User TEACHER = new User(USER_TEACHER_ID, "Test Teacher", "testTeacher",
      "http://teacher.test/avatar.jpg", "teacher@test.mail", "+123/123", "http://teacher.test",
      "Test Teacher address 123, 12345 Teacher city", "testTeacher", true);
  public static final User TUTOR = new User(USER_TUTORS_ID, "Test Tutor", "testTutor",
      "http://tutor.test/avatar.jpg", "tutor@test.mail", "+123/123", "http://tutor.test",
      "Test Tutor address 123, 12345 Tutor city", "testTutor", true);
  public static final User STUDENT = new User(USER_STUDENTS_ID, "Test Student", "testStudent",
      "http://student.test/avatar.jpg", "student@test.mail", "+123/123", "http://student.test",
      "Test Student address 123, 12345 Student city", "testStudent", true);
  public User defaultUser;

  @Override public Observable<User> user(String userId, boolean forceUpdate) {
    return Observable.just(createUser(userId));
  }

  @Override public Observable<List<User>> getUsers(List<String> userIds, boolean forceUpdate) {
    return Observable.just(Collections.singletonList(createUser(userIds.get(0))));
  }

  @Override public Observable<User> currentUser(boolean forceUpdate) {
    return Observable.just(createUser(USER_TEACHER_ID));
  }

  private User createUser(String userId) {
    switch (userId) {
      case USER_TEACHER_ID:
        return TEACHER;
      case USER_TUTORS_ID:
        return TUTOR;
      case USER_STUDENTS_ID:
        return STUDENT;
      default: {
        this.defaultUser = new User(userId, "Test user", "testUser", "http://user.test/avatar.jpg",
            "user@test.mail", "+123/123", "http://user.test",
            "Test User address 123, 12345 User city", "testUser", true);
        return this.defaultUser;
      }
    }
  }
}
