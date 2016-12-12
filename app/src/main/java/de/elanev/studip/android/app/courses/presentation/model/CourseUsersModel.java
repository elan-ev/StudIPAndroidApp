/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.presentation.model;

import java.util.List;

/**
 * @author joern
 */
public class CourseUsersModel {
  private List<CourseUserModel> teachers;
  private List<CourseUserModel> tutors;
  private List<CourseUserModel> students;

  public List<CourseUserModel> getStudents() {
    return students;
  }

  public void setStudents(List<CourseUserModel> students) {
    this.students = students;
  }

  public List<CourseUserModel> getTeachers() {
    return teachers;
  }

  public void setTeachers(List<CourseUserModel> teachers) {
    this.teachers = teachers;
  }

  public List<CourseUserModel> getTutors() {
    return tutors;
  }

  public void setTutors(List<CourseUserModel> tutors) {
    this.tutors = tutors;
  }
}
