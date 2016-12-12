/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.domain;

import java.util.List;

import de.elanev.studip.android.app.user.domain.User;

/**
 * @author joern
 */
public class CourseUsers {
  private List<User> teachers;
  private List<User> tutors;
  private List<User> students;

  public List<User> getStudents() {
    return students;
  }

  public void setStudents(List<User> students) {
    this.students = students;
  }

  public List<User> getTeachers() {
    return teachers;
  }

  public void setTeachers(List<User> teachers) {
    this.teachers = teachers;
  }

  public List<User> getTutors() {
    return tutors;
  }

  public void setTutors(List<User> tutors) {
    this.tutors = tutors;
  }
}
