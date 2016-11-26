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
package de.elanev.studip.android.app.news.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.elanev.studip.android.app.data.datamodel.Course;
import de.elanev.studip.android.app.user.data.entity.UserEntity;

/**
 * @author joern
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewsEntity {

  private String newsId;
  private String range;
  private Course course;
  private String topic;
  private String body;
  private Long date;
  private String userId;
  private Long chdate;
  private Long expire;
  //  private Long mkdate;
  //  private int allow_comments;
  //  private String chdate_uid;
  //  private String body_original;
  private UserEntity author;

  @JsonIgnore public String getRange() {
    return range;
  }

  @JsonIgnore public void setRange(String range) {
    this.range = range;
  }

  @JsonIgnore public Course getCourse() {
    return course;
  }

  @JsonIgnore public void setCourse(Course course) {
    this.course = course;
  }

  @JsonProperty("news_id") public String getNewsId() {
    return newsId;
  }

  @JsonProperty("news_id") public void setNewsId(String newsId) {
    this.newsId = newsId;
  }

  @JsonProperty("topic") public String getTopic() {
    return topic;
  }

  @JsonProperty("topic") public void setTopic(String topic) {
    this.topic = topic;
  }

  @JsonProperty("body") public String getBody() {
    return body;
  }

  @JsonProperty("body") public void setBody(String body) {
    this.body = body;
  }

  @JsonProperty("date") public Long getDate() {
    return date;
  }

  @JsonProperty("date") public void setDate(Long date) {
    this.date = date;
  }

  @JsonProperty("user_id") public String getUserId() {
    return userId;
  }

  @JsonProperty("user_id") public void setUserId(String userId) {
    this.userId = userId;
  }

  @JsonProperty("chdate") public Long getChdate() {
    return chdate;
  }

  @JsonProperty("chdate") public void setChdate(Long chdate) {
    this.chdate = chdate;
  }

  @JsonProperty("expire") public Long getExpire() {
    return expire;
  }

  @JsonProperty("expire") public void setExpire(Long expire) {
    this.expire = expire;
  }

  @JsonIgnore public UserEntity getAuthor() {
    return author;
  }

  @JsonIgnore public void setAuthor(UserEntity author) {
    this.author = author;
  }
}
