/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

/**
 *
 */
package de.elanev.studip.android.app.courses.data.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.List;

import de.elanev.studip.android.app.data.datamodel.Semester;
import de.elanev.studip.android.app.user.data.entity.UserEntity;


/**
 * POJO that represents the response of the /courses/:course_id route.
 *
 * @author joern
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName(value = "course")
public class Course {
  public static final String ID = Course.class.getName() + ".id";
  private String courseId;
  private Long startTime;
  private Long durationTime;
  private String title;
  private String subtitle;
  private CourseModules modules;
  private String description;
  private String location;
  private String semesterId;
  private List<String> teachersIds;
  private List<String> tutorsIds;
  private List<String> studentsIds;
  private List<UserEntity> teachers;
  private List<UserEntity> tutors;
  private List<UserEntity> students;
  private String color;
  private int type;
  private CourseAdditionalData courseAdditionalData;
  private Semester semester;

  @JsonIgnore public static String getID() {
    return ID;
  }

  @JsonProperty("teachers") public List<String> getTeachersIds() {
    return teachersIds;
  }

  @JsonProperty("teachers") public void setTeachersIds(List<String> teachersIds) {
    this.teachersIds = teachersIds;
  }

  @JsonProperty("tutors") public List<String> getTutorsIds() {
    return tutorsIds;
  }

  @JsonProperty("tutors") public void setTutorsIds(List<String> tutorsIds) {
    this.tutorsIds = tutorsIds;
  }

  @JsonProperty("students") public List<String> getStudentsIds() {
    return studentsIds;
  }

  @JsonProperty("students") public void setStudentsIds(List<String> studentsIds) {
    this.studentsIds = studentsIds;
  }

  @JsonProperty("course_id") public String getCourseId() {
    return courseId;
  }

  @JsonProperty("course_id") public void setCourseId(String courseId) {
    this.courseId = courseId;
  }

  @JsonProperty("start_time") public Long getStartTime() {
    return startTime;
  }

  @JsonProperty("start_time") public void setStartTime(Long startTime) {
    this.startTime = startTime;
  }

  @JsonProperty("duration_time") public Long getDurationTime() {
    return durationTime;
  }

  @JsonProperty("duration_time") public void setDurationTime(Long durationTime) {
    this.durationTime = durationTime;
  }

  @JsonProperty("title") public String getTitle() {
    return title;
  }

  @JsonProperty("title") public void setTitle(String title) {
    this.title = title;
  }

  @JsonProperty("subtitle") public String getSubtitle() {
    return subtitle;
  }

  @JsonProperty("subtitle") public void setSubtitle(String subtitle) {
    this.subtitle = subtitle;
  }

  @JsonProperty("modules") public CourseModules getModules() {
    return modules;
  }

  @JsonProperty("modules") public void setModules(CourseModules modules) {
    this.modules = modules;
  }

  @JsonProperty("description") public String getDescription() {
    return description;
  }

  @JsonProperty("description") public void setDescription(String description) {
    this.description = description;
  }

  @JsonProperty("location") public String getLocation() {
    return location;
  }

  @JsonProperty("location") public void setLocation(String location) {
    this.location = location;
  }

  @JsonProperty("semester_id") public String getSemesterId() {
    return semesterId;
  }

  @JsonProperty("semester_id") public void setSemesterId(String semesterId) {
    this.semesterId = semesterId;
  }

  @JsonIgnore public List<UserEntity> getTeachers() {
    return teachers;
  }

  @JsonIgnore public void setTeachers(List<UserEntity> teachers) {
    this.teachers = teachers;
  }

  @JsonIgnore public List<UserEntity> getTutors() {
    return tutors;
  }

  @JsonIgnore public void setTutors(List<UserEntity> tutors) {
    this.tutors = tutors;
  }

  @JsonIgnore public List<UserEntity> getStudents() {
    return students;
  }

  @JsonIgnore public void setStudents(List<UserEntity> students) {
    this.students = students;
  }

  @JsonProperty("color") public String getColor() {
    return color;
  }

  @JsonProperty("color") public void setColor(String color) {
    this.color = color;
  }

  @JsonProperty("type") public int getType() {
    return type;
  }

  @JsonProperty("type") public void setType(int type) {
    this.type = type;
  }

  @JsonProperty("additional_data") public CourseAdditionalData getCourseAdditionalData() {
    return courseAdditionalData;
  }

  @JsonProperty("additional_data") public void setCourseAdditionalData(
      CourseAdditionalData courseAdditionalData) {
    this.courseAdditionalData = courseAdditionalData;
  }

  @JsonIgnore public Semester getSemester() {
    return this.semester;
  }

  @JsonIgnore public void setSemester(Semester semester) {
    this.semester = semester;
  }
}
