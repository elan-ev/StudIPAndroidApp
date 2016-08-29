/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.contacts.data.repository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.contacts.data.entity.ContactGroupEntity;
import de.elanev.studip.android.app.contacts.domain.ContactGroup;
import de.elanev.studip.android.app.user.data.entity.UserEntityDataMapper;

/**
 * @author joern
 */
@Singleton
public class ContactsEntityDataMapper {

  private final UserEntityDataMapper userEntityDataMapper;

  @Inject ContactsEntityDataMapper(UserEntityDataMapper userEntityDataMapper) {
    this.userEntityDataMapper = userEntityDataMapper;
  }

  public List<ContactGroup> transform(List<ContactGroupEntity> contactGroupEntities) {
    ArrayList<ContactGroup> contactGroups = new ArrayList<>();

    for (ContactGroupEntity contactGroupEntity : contactGroupEntities) {
      if (contactGroupEntity != null) {
        contactGroups.add(transform(contactGroupEntity));
      }
    }

    return contactGroups;
  }

  private ContactGroup transform(ContactGroupEntity contactGroupEntity) {
    ContactGroup contactGroup = new ContactGroup();

    contactGroup.setGroupId(contactGroupEntity.getGroupId());
    contactGroup.setName(contactGroupEntity.getName());
    contactGroup.setMembers(userEntityDataMapper.transform(contactGroupEntity.getMembers()));

    return contactGroup;
  }
}
