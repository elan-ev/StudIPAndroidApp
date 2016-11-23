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
package de.elanev.studip.android.app.contacts.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * @author joern
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactGroup {
  private String groupId;
  private String name;
  private ArrayList<String> members;

  @JsonProperty("group_id") public String getGroupId() {
    return groupId;
  }

  @JsonProperty("group_id") public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  @JsonProperty("name") public String getName() {
    return name;
  }

  @JsonProperty("name") public void setName(String name) {
    this.name = name;
  }

  @JsonProperty("members") public ArrayList<String> getMembers() {
    return members;
  }

  @JsonProperty("members") public void setMembers(ArrayList<String> members) {
    this.members = members;
  }
}
