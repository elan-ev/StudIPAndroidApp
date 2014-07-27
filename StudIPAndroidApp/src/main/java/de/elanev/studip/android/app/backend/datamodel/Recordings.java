/*
 * Copyright (c) 2014 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.backend.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;


/**
 * Simple POJO for parsing the API response for oc_updates.
 *
 * @author Jörn
 */
public class Recordings {
  private ArrayList<Recording> recordings = new ArrayList<Recording>();

  @JsonProperty("oc_recordings")
  public ArrayList<Recording> getRecordings() {
    return recordings;
  }

  @JsonProperty("oc_recordings")
  public void setRecordings(ArrayList<Recording> recordings) {
    this.recordings = recordings;
  }

}
