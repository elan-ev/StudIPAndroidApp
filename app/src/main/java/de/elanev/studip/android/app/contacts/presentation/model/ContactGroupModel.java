/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.contacts.presentation.model;

import java.util.List;

import de.elanev.studip.android.app.user.presentation.model.UserModel;

/**
 * @author joern
 */

public class ContactGroupModel {
  private String groupId;
  private String name;
  private List<UserModel> members;

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

  public List<UserModel> getMembers() {
    return members;
  }

  public void setMembers(List<UserModel> members) {
    this.members = members;
  }
}
