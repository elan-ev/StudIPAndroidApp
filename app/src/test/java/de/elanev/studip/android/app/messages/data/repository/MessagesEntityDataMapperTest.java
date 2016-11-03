/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.messages.data.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import de.elanev.studip.android.app.messages.data.entity.MessageEntity;
import de.elanev.studip.android.app.messages.domain.Message;
import de.elanev.studip.android.app.user.data.entity.UserEntity;
import de.elanev.studip.android.app.user.data.entity.UserEntityDataMapper;
import de.elanev.studip.android.app.user.domain.User;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

/**
 * @author joern
 */
public class MessagesEntityDataMapperTest {
  private static final String FAKE_MESSAGE_ID = "123";
  private static final String FAKE_SUBJCT = "Fake title";
  private static final String FAKE_MESSAGE = "Fake body";
  private static final long FAKE_DATE = 123L;


  @Mock UserEntityDataMapper mockUserEntityDataMapper;
  @Mock UserEntity mockUser;
  @Mock User mockDomainUser;
  private MessagesEntityDataMapper messagesEntityDataMapper;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    messagesEntityDataMapper = new MessagesEntityDataMapper(mockUserEntityDataMapper);
  }

  @Test public void shouldTransformMessageEntityListToMessagesList() throws Exception {
    MessageEntity messageEntity1 = new MessageEntity();
    MessageEntity messageEntity2 = new MessageEntity();

    List<MessageEntity> messageEntityList = new ArrayList<>(5);
    messageEntityList.add(messageEntity1);
    messageEntityList.add(messageEntity2);

    List<Message> messageList = messagesEntityDataMapper.transform(messageEntityList);

    assertThat(messageList.toArray()[0], is(instanceOf(Message.class)));
    assertThat(messageList.toArray()[1], is(instanceOf(Message.class)));
    assertThat(messageList.size(), is(2));
  }

  @Test public void shouldTransformMessageEntityToMessage() throws Exception {

  }

  @Test public void shouldTransformMessageToMessageEntity() throws Exception {

  }

}