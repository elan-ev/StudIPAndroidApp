/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.presentation.model;

import java.util.List;

import de.elanev.studip.android.app.user.presentation.model.UserModel;

/**
 * @author joern
 */

public class CourseModel {
  private String courseId;
  private Long startTime;
  private Long durationTime;
  private String title;
  private String subtitle;
  private CourseModulesModel modules;
  private String description;
  private String location;
  private List<UserModel> teachers;
  private List<UserModel> tutors;
  private List<UserModel> students;
  private String color;
  private int type;
  private String typeString;
  private CourseAdditionalDataModel courseAdditionalData;
  private SemesterModel semester;

  public int getType() {
    return type;
  }

  public void setType(int type) {
    this.type = type;
  }

  public String getTypeString() {
    return typeString;
  }

  public void setTypeString(String typeString) {
    this.typeString = typeString;
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

  public CourseModulesModel getModules() {
    return modules;
  }

  public void setModules(CourseModulesModel modules) {
    this.modules = modules;
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

  public List<UserModel> getTeachers() {
    return teachers;
  }

  public void setTeachers(List<UserModel> teachers) {
    this.teachers = teachers;
  }

  public List<UserModel> getTutors() {
    return tutors;
  }

  public void setTutors(List<UserModel> tutors) {
    this.tutors = tutors;
  }

  public List<UserModel> getStudents() {
    return students;
  }

  public void setStudents(List<UserModel> students) {
    this.students = students;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public CourseAdditionalDataModel getCourseAdditionalData() {
    return courseAdditionalData;
  }

  public void setCourseAdditionalData(CourseAdditionalDataModel courseAdditionalData) {
    this.courseAdditionalData = courseAdditionalData;
  }

  public SemesterModel getSemester() {
    return semester;
  }

  public void setSemester(SemesterModel semester) {
    this.semester = semester;
  }
}