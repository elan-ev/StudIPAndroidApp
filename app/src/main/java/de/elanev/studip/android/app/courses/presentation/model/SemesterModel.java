/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.presentation.model;

/**
 * @author joern
 */
public class SemesterModel {
  private String semesterId;
  private String title;
  private String description;
  private Long begin;
  private Long end;
  private Long seminarsBegin;
  private Long seminarsEnd;

  public String getSemesterId() {
    return semesterId;
  }

  public void setSemesterId(String semesterId) {
    this.semesterId = semesterId;
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

  public Long getBegin() {
    return begin;
  }

  public void setBegin(Long begin) {
    this.begin = begin;
  }

  public Long getEnd() {
    return end;
  }

  public void setEnd(Long end) {
    this.end = end;
  }

  public Long getSeminarsBegin() {
    return seminarsBegin;
  }

  public void setSeminarsBegin(Long seminarsBegin) {
    this.seminarsBegin = seminarsBegin;
  }

  public Long getSeminarsEnd() {
    return seminarsEnd;
  }

  public void setSeminarsEnd(Long seminarsEnd) {
    this.seminarsEnd = seminarsEnd;
  }
}
