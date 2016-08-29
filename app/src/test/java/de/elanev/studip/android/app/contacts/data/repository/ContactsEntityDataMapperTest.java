/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.contacts.data.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import de.elanev.studip.android.app.contacts.data.entity.ContactGroupEntity;
import de.elanev.studip.android.app.contacts.domain.ContactGroup;
import de.elanev.studip.android.app.user.data.entity.UserEntityDataMapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.mockito.Mockito.mock;

/**
 * @author joern
 */
public class ContactsEntityDataMapperTest {
  @Mock UserEntityDataMapper userEntityDataMapper;
  private ContactsEntityDataMapper contactsEntityDataMapper;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    contactsEntityDataMapper = new ContactsEntityDataMapper(userEntityDataMapper);
  }

  @Test public void transform() throws Exception {
    ContactGroupEntity mockContactGroupsEntity1 = mock(ContactGroupEntity.class);
    ContactGroupEntity mockContactGroupsEntity2 = mock(ContactGroupEntity.class);

    List<ContactGroupEntity> list = new ArrayList<>(5);
    list.add(mockContactGroupsEntity1);
    list.add(mockContactGroupsEntity2);

    List<ContactGroup> domainContactGroups = contactsEntityDataMapper.transform(list);

    assertThat(domainContactGroups.toArray()[0], is(instanceOf(ContactGroup.class)));
    assertThat(domainContactGroups.toArray()[1], is(instanceOf(ContactGroup.class)));
    assertThat(domainContactGroups.size(), is(2));
  }

}