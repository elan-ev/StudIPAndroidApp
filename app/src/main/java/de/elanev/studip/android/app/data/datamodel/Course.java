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
package de.elanev.studip.android.app.data.datamodel;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.util.ArrayList;


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
  private ArrayList<String> teachers;
  private ArrayList<String> tutors;
  private ArrayList<String> students;
  private String color;
  private int type;
  private CourseAdditionalData courseAdditionalData;

  @JsonIgnore public static String getID() {
    return ID;
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

  @JsonProperty("teachers") public ArrayList<String> getTeachers() {
    return teachers;
  }

  @JsonProperty("teachers") public void setTeachers(ArrayList<String> teachers) {
    this.teachers = teachers;
  }

  @JsonProperty("tutors") public ArrayList<String> getTutors() {
    return tutors;
  }

  @JsonProperty("tutors") public void setTutors(ArrayList<String> tutors) {
    this.tutors = tutors;
  }

  @JsonProperty("students") public ArrayList<String> getStudents() {
    return students;
  }

  @JsonProperty("students") public void setStudents(ArrayList<String> students) {
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


}
