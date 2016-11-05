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
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * @author joern
 */
public class MessagesEntityDataMapperTest {
  private static final String FAKE_MESSAGE_ID = "123";
  private static final String FAKE_SUBJCT = "Fake title";
  private static final String FAKE_MESSAGE = "Fake body";
  private static final long FAKE_DATE = 123L;


  @Mock UserEntityDataMapper mockUserEntityDataMapper;
  @Mock UserEntity mockReceiver;
  @Mock UserEntity mockSender;
  @Mock User mockDomainReceiver;
  @Mock User mockDomainSender;

  private MessagesEntityDataMapper messagesEntityDataMapper;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    given(mockUserEntityDataMapper.transform(mockSender)).willReturn(mockDomainSender);
    given(mockUserEntityDataMapper.transform(mockReceiver)).willReturn(mockDomainReceiver);
    given(mockUserEntityDataMapper.transform(mockDomainSender)).willReturn(mockSender);
    given(mockUserEntityDataMapper.transform(mockDomainReceiver)).willReturn(mockReceiver);

    messagesEntityDataMapper = new MessagesEntityDataMapper(mockUserEntityDataMapper);
  }

  @Test public void shouldTransformMessageEntityListToMessagesList() throws Exception {
    MessageEntity messageEntity1 = new MessageEntity();
    MessageEntity messageEntity2 = new MessageEntity();
    messageEntity1.setDate(FAKE_DATE);
    messageEntity2.setDate(FAKE_DATE);

    List<MessageEntity> messageEntityList = new ArrayList<>(5);
    messageEntityList.add(messageEntity1);
    messageEntityList.add(messageEntity2);

    List<Message> messageList = messagesEntityDataMapper.transform(messageEntityList);

    assertThat(messageList.toArray()[0], is(instanceOf(Message.class)));
    assertThat(messageList.toArray()[1], is(instanceOf(Message.class)));
    assertThat(messageList.size(), is(2));
  }

  @Test public void shouldTransformMessageEntityToMessage() throws Exception {

    MessageEntity messageEntity = new MessageEntity();
    messageEntity.setMessageId(FAKE_MESSAGE_ID);
    messageEntity.setSubject(FAKE_SUBJCT);
    messageEntity.setMessage(FAKE_MESSAGE);
    messageEntity.setDate(FAKE_DATE);
    messageEntity.setReceiver(mockReceiver);
    messageEntity.setSender(mockSender);
    messageEntity.setUnread(1);

    Message message = messagesEntityDataMapper.transform(messageEntity);

    assertThat(message, is(instanceOf(Message.class)));
    assertThat(message.getMessageId(), is(FAKE_MESSAGE_ID));
    assertThat(message.getSender(), is(mockDomainSender));
    assertThat(message.getReceiver(), is(mockDomainReceiver));
    assertThat(message.getMessage(), is(FAKE_MESSAGE));
    assertThat(message.getSubject(), is(FAKE_SUBJCT));
    assertThat(message.getDate(), is(FAKE_DATE));
    assertThat(message.isUnread(), is(true));
  }

  @Test public void shouldTransformMessageToMessageEntity() throws Exception {

    Message m = new Message();
    m.setMessageId(FAKE_MESSAGE_ID);
    m.setSubject(FAKE_SUBJCT);
    m.setMessage(FAKE_MESSAGE);
    m.setDate(FAKE_DATE);
    m.setReceiver(mockDomainReceiver);
    m.setSender(mockDomainSender);
    m.setUnread(true);

    MessageEntity messageEntity = messagesEntityDataMapper.transform(m);

    assertThat(messageEntity, is(instanceOf(MessageEntity.class)));
    assertThat(messageEntity.getMessageId(), is(FAKE_MESSAGE_ID));
    assertThat(messageEntity.getSender(), is(mockSender));
    assertThat(messageEntity.getReceiver(), is(mockReceiver));
    assertThat(messageEntity.getMessage(), is(FAKE_MESSAGE));
    assertThat(messageEntity.getSubject(), is(FAKE_SUBJCT));
    assertThat(messageEntity.getDate(), is(FAKE_DATE));
    assertThat(messageEntity.getUnread(), is(1));
  }

}