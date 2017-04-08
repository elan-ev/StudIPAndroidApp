/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import io.realm.RealmObject;

/**
 * @author joern
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseModules extends RealmObject {

    /* Unused modules are commented out to save parse time. Enable if implementing the feature. */
  //      public boolean admin = false;
  //      public boolean personal = false;
  //      public boolean literature = false;
  //      public boolean wiki = false;
  //      public boolean scm = false;
  //      public boolean elearning_interface = false;
  //      public boolean documents_folder_permissions = false;
  //      public boolean calendar = false;
  //      public boolean resources = false;

  private boolean overview = false;
  private boolean documents = false;
  private boolean schedule = false;
  private boolean participants = false;
  private boolean recordings = false;
  private boolean unizensus = false;
  private boolean forum = false;

  @JsonIgnore public static CourseModules fromJson(String m) {
    ObjectMapper mapper = new ObjectMapper();
    CourseModules modules = null;
    try {
      modules = mapper.readValue(m, CourseModules.class);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return modules;
  }

  @JsonProperty("overview") public boolean isOverview() {
    return overview;
  }

  @JsonProperty("overview") public void setOverview(boolean overview) {
    this.overview = overview;
  }

  @JsonProperty("documents") public boolean isDocuments() {
    return documents;
  }

  @JsonProperty("documents") public void setDocuments(boolean documents) {
    this.documents = documents;
  }

  @JsonProperty("schedule") public boolean isSchedule() {
    return schedule;
  }

  @JsonProperty("schedule") public void setSchedule(boolean schedule) {
    this.schedule = schedule;
  }

  @JsonProperty("participants") public boolean isParticipants() {
    return participants;
  }

  @JsonProperty("participants") public void setParticipants(boolean participants) {
    this.participants = participants;
  }

  @JsonProperty("oc_matterhorn") public boolean isRecordings() {
    return recordings;
  }

  @JsonProperty("oc_matterhorn") public void setRecordings(boolean recordings) {
    this.recordings = recordings;
  }

  @JsonProperty("unizensus") public boolean isUnizensus() {
    return unizensus;
  }

  @JsonProperty("unizensus") public void setUnizensus(boolean unizensus) {
    this.unizensus = unizensus;
  }

  @JsonProperty("forum") public boolean isForum() {
    return forum;
  }

  @JsonProperty("forum") public void setForum(boolean forum) {
    this.forum = forum;
  }

  @JsonIgnore public String getAsJson() {
    ObjectMapper mapper = new ObjectMapper();
    String json = "";
    try {
      json = mapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return json;
  }

}
