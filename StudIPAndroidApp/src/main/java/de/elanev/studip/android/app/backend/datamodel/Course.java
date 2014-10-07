/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
/**
 *
 */
package de.elanev.studip.android.app.backend.datamodel;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * POJO that represents the response of the /courses/:course_id route.
 *
 * @author joern
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName(value = "course")
public class Course {
  @JsonProperty("course_id")
  public String courseId;
  @JsonProperty("start_time")
  public Long startTime;
  @JsonProperty("duration_time")
  public Long durationTime;
  @JsonProperty("title")
  public String title;
  @JsonProperty("subtitle")
  public String subtitle;
  @JsonProperty("modules")
  public Modules modules;
  @JsonProperty("description")
  public String description;
  @JsonProperty("location")
  public String location;
  @JsonProperty("semester_id")
  public String semesterId;
  @JsonProperty("teachers")
  public ArrayList<String> teachers;
  @JsonProperty("tutors")
  public ArrayList<String> tutors;
  @JsonProperty("students")
  public ArrayList<String> students;
  @JsonProperty("color")
  public String color;
  @JsonProperty("type")
  public int type;
  @JsonProperty("additional_data")
  private AdditionalData additionalData;
  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  public Course() {
  }

  @JsonProperty("additional_data")
  public AdditionalData getAdditionalData() {
    return additionalData;
  }

  @JsonProperty("additional_data")
  public void setAdditionalData(AdditionalData additionalData) {
    this.additionalData = additionalData;
  }

  @JsonAnyGetter
  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class AdditionalData {

    //Opencast Course Recordings
    @JsonProperty("oc_recordings")
    private List<Recording> recordings = new ArrayList<Recording>();

    @JsonProperty("oc_recordings")
    public List<Recording> getRecordings() {
      return recordings;
    }

    @JsonProperty("oc_recordings")
    public void setRecordings(List<Recording> recordings) {
      this.recordings = recordings;
    }

    //Unizensus plugin
    @JsonProperty("unizensus")
    private UnizensusItem unizensusItem;

    @JsonProperty("unizensus")
    public UnizensusItem getUnizensusItem() {
      return unizensusItem;
    }

    @JsonProperty("unizensus")
    public void setUnizensusItem(UnizensusItem unizensusItem) {
      this.unizensusItem = unizensusItem;
    }

    // Other not recognized data
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
      return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
      this.additionalProperties.put(name, value);
    }

  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Modules {
    // Course Overview
    @JsonProperty("overview")
    public boolean overview = false;
    // Course documents
    @JsonProperty("documents")
    public boolean documents = false;
    // Course schedule
    @JsonProperty("schedule")
    public boolean schedule = false;
    // Course participants
    @JsonProperty("participants")
    public boolean participants = false;
    // Course matterhorn recordings
    @JsonProperty("oc_matterhorn")
    public boolean recordings = false;
    // Course unizensus
    @JsonProperty("unizensus")
    public boolean unizensus = false;

    /* Unused modules are commented out to save parse time. Enable if implementing the feature. */
    //      public boolean admin = false;
    //      public boolean forum = false;
    //      public boolean personal = false;
    //      public boolean literature = false;
    //      public boolean wiki = false;
    //      public boolean scm = false;
    //      public boolean elearning_interface = false;
    //      public boolean documents_folder_permissions = false;
    //      public boolean calendar = false;
    //      public boolean resources = false;

    @JsonIgnore
    public String getAsJson() {
      ObjectMapper mapper = new ObjectMapper();
      String json = "";
      try {
        json = mapper.writeValueAsString(this);
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      }
      return json;
    }

    @JsonIgnore
    public static Modules fromJson(String m) {
      ObjectMapper mapper = new ObjectMapper();
      Modules modules = null;
      try {
        modules = mapper.readValue(m, Modules.class);
      } catch (JsonProcessingException e) {
        e.printStackTrace();
      } catch (IOException e) {
        e.printStackTrace();
      }
      return modules;
    }
  }

}
