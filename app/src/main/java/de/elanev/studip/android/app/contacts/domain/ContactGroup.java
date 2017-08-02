/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.contacts.domain;

import java.util.List;

import de.elanev.studip.android.app.user.domain.User;

/**
 * @author joern
 */

public class ContactGroup {
  private String groupId;
  private String name;
  private List<User> members;

  public ContactGroup() {}

  public ContactGroup(String groupId, String name, List<User> members) {
    this.groupId = groupId;
    this.name = name;
    this.members = members;
  }

  public String getGroupId() {
    return groupId;
  }

  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public List<User> getMembers() {
    return members;
  }

  public void setMembers(List<User> members) {
    this.members = members;
  }
}
