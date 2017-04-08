/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.planner.domain;

import de.elanev.studip.android.app.courses.domain.DomainCourse;

/**
 * @author joern
 */

public class Event {
  private String eventId;
  private DomainCourse course;
  private Long start;
  private Long end;
  private String title;
  private String description;
  private String room;
  private String color;
  private String category;
  private String courseId;

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public String getColor() {
    return color;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public String getEventId() {
    return eventId;
  }

  public void setEventId(String eventId) {
    this.eventId = eventId;
  }

  public DomainCourse getCourse() {
    return course;
  }

  public void setCourse(DomainCourse course) {
    this.course = course;
  }

  public Long getStart() {
    return start;
  }

  public void setStart(Long start) {
    this.start = start;
  }

  public Long getEnd() {
    return end;
  }

  public void setEnd(Long end) {
    this.end = end;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getRoom() {
    return room;
  }

  public void setRoom(String room) {
    this.room = room;
  }

  public void setCourseId(String courseId) {
    this.courseId = courseId;
  }

  public String getCourseId() {
    return courseId;
  }
}
