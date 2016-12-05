/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.presentation.model;

import de.elanev.studip.android.app.courses.data.entity.Course;
import de.elanev.studip.android.app.user.presentation.model.UserModel;

/**
 * @author joern
 */
public class NewsModel {
  public String id;
  public String title;
  public UserModel author;
  public long date;
  public String body;
  public String range;
  public Course course;

  @Override public int hashCode() {
    int result = id.hashCode();
    result = 31 * result + title.hashCode();
    result = 31 * result + (author != null ? author.hashCode() : 0);
    result = 31 * result + (int) (date ^ (date >>> 32));
    result = 31 * result + body.hashCode();
    result = 31 * result + range.hashCode();
    result = 31 * result + (course != null ? course.hashCode() : 0);
    return result;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof NewsModel)) return false;

    NewsModel newsModel = (NewsModel) o;

    if (date != newsModel.date) return false;
    if (!id.equals(newsModel.id)) return false;
    if (!title.equals(newsModel.title)) return false;
    if (author != null ? !author.equals(newsModel.author) : newsModel.author != null) return false;
    if (!body.equals(newsModel.body)) return false;
    if (!range.equals(newsModel.range)) return false;
    return course != null ? course.equals(newsModel.course) : newsModel.course == null;

  }
}
