/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.domain;

/**
 * @author joern
 */
public class DomainCourseModules {
  private boolean overview = false;
  private boolean documents = false;
  private boolean schedule = false;
  private boolean participants = false;
  private boolean recordings = false;
  private boolean unizensus = false;
  private boolean forum = false;

  public boolean isRecordings() {
    return recordings;
  }

  public void setRecordings(boolean recordings) {
    this.recordings = recordings;
  }

  public boolean isOverview() {
    return overview;
  }

  public void setOverview(boolean overview) {
    this.overview = overview;
  }

  public boolean isDocuments() {
    return documents;
  }

  public void setDocuments(boolean documents) {
    this.documents = documents;
  }

  public boolean isSchedule() {
    return schedule;
  }

  public void setSchedule(boolean schedule) {
    this.schedule = schedule;
  }

  public boolean isParticipants() {
    return participants;
  }

  public void setParticipants(boolean participants) {
    this.participants = participants;
  }

  public boolean isUnizensus() {
    return unizensus;
  }

  public void setUnizensus(boolean unizensus) {
    this.unizensus = unizensus;
  }

  public boolean isForum() {
    return forum;
  }

  public void setForum(boolean forum) {
    this.forum = forum;
  }
}
