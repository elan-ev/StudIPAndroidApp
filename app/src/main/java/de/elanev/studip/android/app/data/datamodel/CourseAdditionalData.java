/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.data.datamodel;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;

/**
 * @author joern
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseAdditionalData {
  //Opencast Course Recordings
  @Ignore @JsonProperty("oc_recordings") private ArrayList<Recording> recordings = new ArrayList<Recording>();
  //Unizensus plugin
  @Ignore @JsonProperty("unizensus") private UnizensusItem unizensusItem;
  // Other not recognized data
  @Ignore @JsonIgnore private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  @JsonProperty("oc_recordings") public ArrayList<Recording> getRecordings() {
    return recordings;
  }

  @JsonProperty("oc_recordings") public void setRecordings(ArrayList<Recording> recordings) {
    this.recordings = recordings;
  }

  @JsonProperty("unizensus") public UnizensusItem getUnizensusItem() {
    return unizensusItem;
  }

  @JsonProperty("unizensus") public void setUnizensusItem(UnizensusItem unizensusItem) {
    this.unizensusItem = unizensusItem;
  }

  @JsonAnyGetter public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  @JsonAnySetter public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }
}
