/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * @author joern
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseAdditionalData {
  //Opencast Course Recordings
  @JsonProperty("oc_recordings") private List<Recording> recordings = new ArrayList<Recording>();
  //Unizensus plugin
  @JsonProperty("unizensus") private UnizensusItem unizensusItem;

  @JsonProperty("oc_recordings") public List<Recording> getRecordings() {
    return recordings;
  }

  @JsonProperty("oc_recordings") public void setRecordings(List<Recording> recordings) {
    this.recordings = recordings;
  }

  @JsonProperty("unizensus") public UnizensusItem getUnizensusItem() {
    return unizensusItem;
  }

  @JsonProperty("unizensus") public void setUnizensusItem(UnizensusItem unizensusItem) {
    this.unizensusItem = unizensusItem;
  }
}
