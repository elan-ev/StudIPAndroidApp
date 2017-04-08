/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.domain;

/**
 * @author joern
 */
public class Semester {
  private String semesterId;
  private String title;
  private String description;
  private long begin;
  private long end;
  private long seminarsBegin;
  private long seminarsEnd;

  public Semester() {}

  public Semester(String semesterId, String title, String description, long begin, long end,
      long seminarsBegin, long seminarsEnd) {
    this.semesterId = semesterId;
    this.title = title;
    this.description = description;
    this.begin = begin;
    this.end = end;
    this.seminarsBegin = seminarsBegin;
    this.seminarsEnd = seminarsEnd;
  }

  public long getSeminarsEnd() {
    return seminarsEnd;
  }

  public void setSeminarsEnd(long seminarsEnd) {
    this.seminarsEnd = seminarsEnd;
  }

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

  public long getBegin() {
    return begin;
  }

  public void setBegin(long begin) {
    this.begin = begin;
  }

  public long getEnd() {
    return end;
  }

  public void setEnd(long end) {
    this.end = end;
  }

  public long getSeminarsBegin() {
    return seminarsBegin;
  }

  public void setSeminarsBegin(long seminarsBegin) {
    this.seminarsBegin = seminarsBegin;
  }
}
