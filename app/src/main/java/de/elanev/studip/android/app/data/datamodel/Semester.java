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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author joern
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Semester {
  private String semesterId;
  private String title;
  private String description;
  private Long begin;
  private Long end;
  private Long seminarsBegin;
  private Long seminarsEnd;

  @JsonProperty("semester_id") public String getSemesterId() {
    return semesterId;
  }

  @JsonProperty("semester_id") public void setSemesterId(String semesterId) {
    this.semesterId = semesterId;
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

  @JsonProperty("begin") public Long getBegin() {
    return begin;
  }

  @JsonProperty("begin") public void setBegin(Long begin) {
    this.begin = begin;
  }

  @JsonProperty("end") public Long getEnd() {
    return end;
  }

  @JsonProperty("end") public void setEnd(Long end) {
    this.end = end;
  }


  @JsonProperty("seminars_begin") public Long getSeminarsBegin() {
    return seminarsBegin;
  }

  @JsonProperty("seminars_begin") public void setSeminarsBegin(Long seminarsBegin) {
    this.seminarsBegin = seminarsBegin;
  }

  @JsonProperty("seminars_end") public Long getSeminarsEnd() {
    return seminarsEnd;
  }

  @JsonProperty("seminars_end") public void setSeminarsEnd(Long seminarsEnd) {
    this.seminarsEnd = seminarsEnd;
  }
}
