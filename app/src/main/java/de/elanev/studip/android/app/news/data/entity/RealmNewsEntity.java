/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.data.entity;

import de.elanev.studip.android.app.courses.data.entity.RealmCourseEntity;
import de.elanev.studip.android.app.user.data.entity.RealmUserEntity;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author joern
 */

public class RealmNewsEntity extends RealmObject {
  @PrimaryKey private String newsId;
  private String topic;
  private String body;
  private Long date;
  private Long expire;
  private String range;
  private RealmUserEntity author;
  private RealmCourseEntity course;

  public String getNewsId() {
    return newsId;
  }

  public void setNewsId(String newsId) {
    this.newsId = newsId;
  }

  public String getTopic() {
    return topic;
  }

  public void setTopic(String topic) {
    this.topic = topic;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public Long getDate() {
    return date;
  }

  public void setDate(Long date) {
    this.date = date;
  }

  public Long getExpire() {
    return expire;
  }

  public void setExpire(Long expire) {
    this.expire = expire;
  }

  public String getRange() {
    return range;
  }

  public void setRange(String range) {
    this.range = range;
  }

  public RealmUserEntity getAuthor() {
    return author;
  }

  public void setAuthor(RealmUserEntity author) {
    this.author = author;
  }

  public RealmCourseEntity getCourse() {
    return course;
  }

  public void setCourse(RealmCourseEntity course) {
    this.course = course;
  }
}
