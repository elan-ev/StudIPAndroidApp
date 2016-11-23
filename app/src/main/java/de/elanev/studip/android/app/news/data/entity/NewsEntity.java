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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import de.elanev.studip.android.app.data.datamodel.Course;
import de.elanev.studip.android.app.user.data.entity.UserEntity;

/**
 * @author joern
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewsEntity {

  public String news_id;
  public String topic;
  public String body;
  public Long date;
  public String user_id;
  public Long chdate;
  public Long mkdate;
  public Long expire;
  public int allow_comments;
  public String chdate_uid;
  public String body_original;
  public UserEntity author;
  @JsonIgnoreProperties public String range;
  @JsonIgnoreProperties public Course course;

  public NewsEntity() {
  }
}
