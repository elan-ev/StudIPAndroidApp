/*
 * Copyright (c) 2017 ELAN e.V.
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
  private List<String> teachers;
  private List<String> tutors;
  private List<String> students;
  private String color;
  private int type;
  private CourseAdditionalData courseAdditionalData;
  private SemesterEntity semesterEntity;

  @JsonIgnore public static String getID() {
    return ID;
  }

  @JsonProperty("teachers") public List<String> getTeachers() {
    return teachers;
  }

  @JsonProperty("teachers") public void setTeachers(List<String> teachers) {
    this.teachers = teachers;
  }

  @JsonProperty("tutors") public List<String> getTutors() {
    return tutors;
  }

  @JsonProperty("tutors") public void setTutors(List<String> tutors) {
    this.tutors = tutors;
  }

  @JsonProperty("students") public List<String> getStudents() {
    return students;
  }

  @JsonProperty("students") public void setStudents(List<String> students) {
    this.students = students;
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

  @JsonIgnore public SemesterEntity getSemesterEntity() {
    return this.semesterEntity;
  }

  @JsonIgnore public void setSemesterEntity(SemesterEntity semesterEntity) {
    this.semesterEntity = semesterEntity;
  }

  @Override public int hashCode() {
    int result = courseId.hashCode();
    result = 31 * result + startTime.hashCode();
    result = 31 * result + durationTime.hashCode();
    result = 31 * result + title.hashCode();
    result = 31 * result + subtitle.hashCode();
    result = 31 * result + modules.hashCode();
    result = 31 * result + description.hashCode();
    result = 31 * result + location.hashCode();
    result = 31 * result + semesterId.hashCode();
    result = 31 * result + teachers.hashCode();
    result = 31 * result + tutors.hashCode();
    result = 31 * result + students.hashCode();
    result = 31 * result + color.hashCode();
    result = 31 * result + type;
    result = 31 * result + (courseAdditionalData != null ? courseAdditionalData.hashCode() : 0);
    result = 31 * result + (semesterEntity != null ? semesterEntity.hashCode() : 0);
    return result;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Course)) return false;

    Course course = (Course) o;

    if (type != course.type) return false;
    if (!courseId.equals(course.courseId)) return false;
    if (!startTime.equals(course.startTime)) return false;
    if (!durationTime.equals(course.durationTime)) return false;
    if (!title.equals(course.title)) return false;
    if (!subtitle.equals(course.subtitle)) return false;
    if (!modules.equals(course.modules)) return false;
    if (!description.equals(course.description)) return false;
    if (!location.equals(course.location)) return false;
    if (!semesterId.equals(course.semesterId)) return false;
    if (!teachers.equals(course.teachers)) return false;
    if (!tutors.equals(course.tutors)) return false;
    if (!students.equals(course.students)) return false;
    if (!color.equals(course.color)) return false;
    if (courseAdditionalData != null
        ? !courseAdditionalData.equals(course.courseAdditionalData)
        : course.courseAdditionalData != null) return false;
    return semesterEntity != null
        ? semesterEntity.equals(course.semesterEntity)
        : course.semesterEntity == null;

  }
}
