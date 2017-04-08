/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.contacts.presentation.mapper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import de.elanev.studip.android.app.contacts.domain.ContactGroup;
import de.elanev.studip.android.app.contacts.presentation.model.ContactGroupModel;
import de.elanev.studip.android.app.user.presentation.mapper.UserModelDataMapper;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.mockito.Mockito.mock;

/**
 * @author joern
 */
public class ContactsModelModelDataMapperTest {
  @Mock UserModelDataMapper mockUserModelDataMapper;
  private ContactsModelModelDataMapper contactsModelModelDataMapper;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    contactsModelModelDataMapper = new ContactsModelModelDataMapper(mockUserModelDataMapper);
  }

  @Test public void transform() throws Exception {
    ContactGroup mockContactGroup1 = mock(ContactGroup.class);
    ContactGroup mockContactGroup2 = mock(ContactGroup.class);

    List<ContactGroup> list = new ArrayList<>(5);
    list.add(mockContactGroup1);
    list.add(mockContactGroup2);

    List<ContactGroupModel> newsModels = contactsModelModelDataMapper.transform(list);

    assertThat(newsModels.toArray()[0], is(instanceOf(ContactGroupModel.class)));
    assertThat(newsModels.toArray()[1], is(instanceOf(ContactGroupModel.class)));
    assertThat(newsModels.size(), is(2));
  }

}