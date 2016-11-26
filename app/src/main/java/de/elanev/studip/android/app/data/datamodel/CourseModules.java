/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.data.datamodel;

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
  // Course Overview
  @JsonProperty("overview") public boolean overview = false;
  // Course documents
  @JsonProperty("documents") public boolean documents = false;
  // Course schedule
  @JsonProperty("schedule") public boolean schedule = false;
  // Course participants
  @JsonProperty("participants") public boolean participants = false;
  // Course matterhorn recordings
  @JsonProperty("oc_matterhorn") public boolean recordings = false;
  // Course unizensus
  @JsonProperty("unizensus") public boolean unizensus = false;
  // Course forum
  @JsonProperty("forum") public boolean forum = false;

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
