/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.messages.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

import de.elanev.studip.android.app.data.datamodel.Pagination;

/**
 * @author joern
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageFolders {
  private ArrayList<String> folders;
  private Pagination pagination;

  @JsonProperty("folders") public ArrayList<String> getFolders() {
    return folders;
  }

  @JsonProperty("folders") public void setFolders(ArrayList<String> folders) {
    this.folders = folders;
  }

  @JsonProperty("pagination") public Pagination getPagination() {
    return pagination;
  }

  @JsonProperty("pagination") public void setPagination(Pagination pagination) {
    this.pagination = pagination;
  }
}
