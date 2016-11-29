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
public class DomainCourse {
  private String courseId;
  private Long startTime;
  private Long durationTime;
  private String title;
  private String subtitle;
  private DomainCourseModules modules;
  private String description;
  private String location;
  private List<User> teachers;
  private List<User> tutors;
  private List<User> students;
  private String color;
  private int type;
  private DomainCourseAdditionalData courseAdditionalData;
  private DomainSemester semester;

  public DomainCourseModules getModules() {
    return modules;
  }

  public void setModules(DomainCourseModules modules) {
    this.modules = modules;
  }

  public String getCourseId() {
    return courseId;
  }

  public void setCourseId(String courseId) {
    this.courseId = courseId;
  }

  public Long getStartTime() {
    return startTime;
  }

  public void setStartTime(Long startTime) {
    this.startTime = startTime;
  }

  public Long getDurationTime() {
    return durationTime;
  }

  public void setDurationTime(Long durationTime) {
    this.durationTime = durationTime;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getSubtitle() {
    return subtitle;
  }

  public void setSubtitle(String subtitle) {
    this.subtitle = subtitle;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
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

  public List<User> getStudents() {
    return students;
  }

  public void setStudents(List<User> students) {
    this.students = students;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public DomainCourseAdditionalData getCourseAdditionalData() {
    return courseAdditionalData;
  }

  public void setCourseAdditionalData(DomainCourseAdditionalData courseAdditionalData) {
    this.courseAdditionalData = courseAdditionalData;
  }

  public DomainSemester getSemester() {
    return semester;
  }

  public void setSemester(DomainSemester semester) {
    this.semester = semester;
  }

}
