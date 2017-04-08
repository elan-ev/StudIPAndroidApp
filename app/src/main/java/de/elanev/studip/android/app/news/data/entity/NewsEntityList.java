/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import de.elanev.studip.android.app.data.datamodel.Pagination;

/**
 * @author joern
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewsEntityList {
   private List<NewsEntity> newsEntities;
   private Pagination pagination;

  @JsonProperty("news")
  public List<NewsEntity> getNewsEntities() {
    return newsEntities;
  }

  @JsonProperty("news")
  public void setNewsEntities(List<NewsEntity> newsEntities) {
    this.newsEntities = newsEntities;
  }

  @JsonProperty("pagination")
  public Pagination getPagination() {
    return pagination;
  }

  @JsonProperty("pagination")
  public void setPagination(Pagination pagination) {
    this.pagination = pagination;
  }
}
