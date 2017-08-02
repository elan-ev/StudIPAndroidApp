/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.data.entity;

import de.elanev.studip.android.app.base.data.db.realm.RealmString;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author joern
 */
public class RealmCourseEntity extends RealmObject {
  @PrimaryKey private String courseId;
  private Long startTime;
  private Long durationTime;
  private String title;
  private String subtitle;
  private RealmCourseModulesEntity modules;
  private RealmSemesterEntity semester;
  private String color;
  private String description;
  private String location;
  private int type;
  private RealmList<RealmString> teachers;
  private RealmList<RealmString> tutors;
  private RealmList<RealmString> students;
  private RealmCourseAdditionalDataEntity courseAdditionalData;

  public RealmCourseAdditionalDataEntity getCourseAdditionalData() {
    return courseAdditionalData;
  }

  public void setCourseAdditionalData(RealmCourseAdditionalDataEntity courseAdditionalData) {
    this.courseAdditionalData = courseAdditionalData;
  }

  public RealmList<RealmString> getTeachers() {
    return teachers;
  }

  public void setTeachers(RealmList<RealmString> teachers) {
    this.teachers = teachers;
  }

  public RealmList<RealmString> getTutors() {
    return tutors;
  }

  public void setTutors(RealmList<RealmString> tutors) {
    this.tutors = tutors;
  }

  public RealmList<RealmString> getStudents() {
    return students;
  }

  public void setStudents(RealmList<RealmString> students) {
    this.students = students;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
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

  public RealmCourseModulesEntity getModules() {
    return modules;
  }

  public void setModules(RealmCourseModulesEntity modules) {
    this.modules = modules;
  }

  public RealmSemesterEntity getSemester() {
    return semester;
  }

  public void setSemester(RealmSemesterEntity semester) {
    this.semester = semester;
  }

  public String getCourseId() {
    return courseId;
  }

  public void setCourseId(String courseId) {
    this.courseId = courseId;
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

}
