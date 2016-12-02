/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.domain;

import de.elanev.studip.android.app.news.domain.NewsItem;
import de.elanev.studip.android.app.planner.domain.Event;

/**
 * @author joern
 */
public class CourseOverview {
  private DomainCourse course;
  private NewsItem newsItem;
  private Event event;

  public DomainCourse getCourse() {
    return course;
  }

  public void setCourse(DomainCourse course) {
    this.course = course;
  }

  public NewsItem getNewsItem() {
    return newsItem;
  }

  public void setNewsItem(NewsItem newsItem) {
    this.newsItem = newsItem;
  }

  public Event getEvent() {
    return event;
  }

  public void setEvent(Event event) {
    this.event = event;
  }
}
