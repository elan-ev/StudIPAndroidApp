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
package de.elanev.studip.android.app.planner.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author joern
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventEntity {
  private String eventId;
  private String courseId;
  private Long start;
  private Long end;
  private String title;
  private String description;
  private String categories;
  private String room;
  private String color;

  @JsonProperty("event_id") public String getEventId() {
    return eventId;
  }

  @JsonProperty("event_id") public void setEventId(String eventId) {
    this.eventId = eventId;
  }

  @JsonProperty("course_id") public String getCourseId() {
    return courseId;
  }

  @JsonProperty("course_id") public void setCourseId(String courseId) {
    this.courseId = courseId;
  }

  @JsonProperty("start") public Long getStart() {
    return start;
  }

  @JsonProperty("start") public void setStart(Long start) {
    this.start = start;
  }

  @JsonProperty("end") public Long getEnd() {
    return end;
  }

  @JsonProperty("end") public void setEnd(Long end) {
    this.end = end;
  }

  @JsonProperty("title") public String getTitle() {
    return title;
  }

  @JsonProperty("title") public void setTitle(String title) {
    this.title = title;
  }

  @JsonProperty("description") public String getDescription() {
    return description;
  }

  @JsonProperty("description") public void setDescription(String description) {
    this.description = description;
  }

  @JsonProperty("categories") public String getCategories() {
    return categories;
  }

  @JsonProperty("categories") public void setCategories(String categories) {
    this.categories = categories;
  }

  @JsonProperty("room") public String getRoom() {
    return room;
  }

  @JsonProperty("room") public void setRoom(String room) {
    this.room = room;
  }

  @JsonProperty("color") public String getColor() {
    return color;
  }

  @JsonProperty("color") public void setColor(String color) {
    this.color = color;
  }
}
