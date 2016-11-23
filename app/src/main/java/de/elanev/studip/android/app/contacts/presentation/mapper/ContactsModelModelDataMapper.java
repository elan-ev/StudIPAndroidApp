/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.contacts.presentation.mapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.elanev.studip.android.app.base.internal.di.PerActivity;
import de.elanev.studip.android.app.contacts.domain.ContactGroup;
import de.elanev.studip.android.app.contacts.presentation.model.ContactGroupModel;
import de.elanev.studip.android.app.user.presentation.mapper.UserModelDataMapper;

/**
 * @author joern
 */
@PerActivity
public class ContactsModelModelDataMapper {

  private final UserModelDataMapper userModelDataMapper;

  @Inject ContactsModelModelDataMapper(UserModelDataMapper userModelDataMapper) {
    this.userModelDataMapper = userModelDataMapper;
  }

  public List<ContactGroupModel> transform(List<ContactGroup> contactGroups) {
    List<ContactGroupModel> contactsList = new ArrayList<>(contactGroups.size());

    for (ContactGroup contactGroup : contactGroups) {
      contactsList.add(transform(contactGroup));
    }

    return contactsList;
  }

  private ContactGroupModel transform(ContactGroup contactGroup) {
    ContactGroupModel contactGroupModel = new ContactGroupModel();

    contactGroupModel.setGroupId(contactGroup.getGroupId());
    contactGroupModel.setName(contactGroup.getName());
    contactGroupModel.setMembers(userModelDataMapper.transform(contactGroup.getMembers()));

    return contactGroupModel;
  }
}
