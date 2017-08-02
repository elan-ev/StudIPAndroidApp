/*
 * Copyright (c) 2017 ELAN e.V.
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
  private List<String> teachers;
  private List<String> tutors;
  private List<String> students;
  private List<User> teacherEntities;
  private List<User> tutorEntities;
  private List<User> studentEntities;
  private String color;
  private int type;
  private String typeString;
  private DomainCourseAdditionalData courseAdditionalData;
  private Semester semester;

  public String getTypeString() {
    return typeString;
  }

  public void setTypeString(String typeString) {
    this.typeString = typeString;
  }

  public List<User> getStudentEntities() {
    return studentEntities;
  }

  public void setStudentEntities(List<User> studentEntities) {
    this.studentEntities = studentEntities;
  }

  public List<User> getTeacherEntities() {
    return teacherEntities;
  }

  public void setTeacherEntities(List<User> teacherEntities) {
    this.teacherEntities = teacherEntities;
  }

  public List<User> getTutorEntities() {
    return tutorEntities;
  }

  public void setTutorEntities(List<User> tutorEntities) {
    this.tutorEntities = tutorEntities;
  }

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

  public List<String> getTeachers() {
    return teachers;
  }

  public void setTeachers(List<String> teachers) {
    this.teachers = teachers;
  }

  public List<String> getTutors() {
    return tutors;
  }

  public void setTutors(List<String> tutors) {
    this.tutors = tutors;
  }

  public List<String> getStudents() {
    return students;
  }

  public void setStudents(List<String> students) {
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

  public Semester getSemester() {
    return semester;
  }

  public void setSemester(Semester semester) {
    this.semester = semester;
  }

}
