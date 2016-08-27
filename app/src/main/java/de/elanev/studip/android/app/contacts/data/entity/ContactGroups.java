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

import java.util.List;

/**
 * @author joern
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactGroups {
  private List<ContactGroup> groups;

  @JsonProperty("groups") public List<ContactGroup> getGroups() {
    return groups;
  }

  @JsonProperty("groups") public void setGroups(List<ContactGroup> groups) {
    this.groups = groups;
  }
}
