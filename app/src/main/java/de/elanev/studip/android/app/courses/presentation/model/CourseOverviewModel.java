/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.presentation.model;

import de.elanev.studip.android.app.news.presentation.model.NewsModel;
import de.elanev.studip.android.app.planner.presentation.model.EventModel;

/**
 * @author joern
 */

public class CourseOverviewModel {
  private CourseModel course;
  private NewsModel courseNews;
  private EventModel courseEvents;

  public NewsModel getCourseNews() {
    return courseNews;
  }

  public void setCourseNews(NewsModel courseNews) {
    this.courseNews = courseNews;
  }

  public EventModel getCourseEvents() {
    return courseEvents;
  }

  public void setCourseEvents(EventModel courseEvents) {
    this.courseEvents = courseEvents;
  }

  public CourseModel getCourse() {
    return course;
  }

  public void setCourse(CourseModel course) {
    this.course = course;
  }

}
