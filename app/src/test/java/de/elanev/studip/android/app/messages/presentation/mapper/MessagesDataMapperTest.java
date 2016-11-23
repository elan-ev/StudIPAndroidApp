/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.messages.presentation.mapper;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import de.elanev.studip.android.app.messages.domain.Message;
import de.elanev.studip.android.app.messages.presentation.model.MessageModel;
import de.elanev.studip.android.app.user.domain.User;
import de.elanev.studip.android.app.user.presentation.mapper.UserModelDataMapper;
import de.elanev.studip.android.app.user.presentation.model.UserModel;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

/**
 * @author joern
 */
public class MessagesDataMapperTest {

  private static final String FAKE_ID = "123";
  private static final String FAKE_SUBJECT = "fake-subject";
  private static final String FAKE_MESSAGE = "fake-message";
  private static final long FAKE_DATE = 123L;
  @Mock UserModelDataMapper mockUserModelDataMapper;
  @Mock User mockDomainSender;
  @Mock User mockDomainReceiver;
  @Mock UserModel mockPresentationSender;
  @Mock UserModel mockPresentationReceiver;

  private MessagesDataMapper messagesDataMapper;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    given(mockUserModelDataMapper.transform(mockDomainSender)).willReturn(mockPresentationSender);
    given(mockUserModelDataMapper.transform(mockDomainReceiver)).willReturn(mockPresentationReceiver);
    given(mockUserModelDataMapper.transform(mockPresentationSender)).willReturn(mockDomainSender);
    given(mockUserModelDataMapper.transform(mockPresentationReceiver)).willReturn(mockDomainReceiver);

    messagesDataMapper = new MessagesDataMapper(mockUserModelDataMapper);
  }

  @Test public void transformMessagesListToMessageModelList() throws Exception {
    Message mockMessage1= mock(Message.class);
    Message mockMessage2 = mock(Message.class);

    List<Message> list = new ArrayList<>(5);
    list.add(mockMessage1);
    list.add(mockMessage2);

    List<MessageModel> messageModelList = messagesDataMapper.transform(list);

    assertThat(messageModelList.toArray()[0], is(instanceOf(MessageModel.class)));
    assertThat(messageModelList.toArray()[1], is(instanceOf(MessageModel.class)));
    assertThat(messageModelList.size(), is(2));
  }

  @Test public void shouldTransformMessageToMessageModel() throws Exception {
      Message message = new Message();
    message.setMessageId(FAKE_ID);
    message.setSender(mockDomainSender);
    message.setReceiver(mockDomainReceiver);
    message.setSubject(FAKE_SUBJECT);
    message.setMessage(FAKE_MESSAGE);
    message.setDate(FAKE_DATE);
    message.setUnread(true);

    MessageModel messageModel = messagesDataMapper.transform(message);

    assertThat(messageModel, is(instanceOf(MessageModel.class)));
    assertThat(messageModel.getMessageId(), is(FAKE_ID));
    assertThat(messageModel.getSender(),is(mockPresentationSender));
    assertThat(messageModel.getReceiver(), is(mockPresentationReceiver));
    assertThat(messageModel.getSubject(), is(FAKE_SUBJECT));
    assertThat(messageModel.getMessage(), is(FAKE_MESSAGE));
    assertThat(messageModel.getDate(), is(FAKE_DATE));
    assertThat(messageModel.isUnread(), is(Boolean.TRUE));
  }

  @Test public void shouldTransformMessageModelToMessage() throws Exception {
    MessageModel messageModel = new MessageModel();
    messageModel.setMessageId(FAKE_ID);
    messageModel.setSender(mockPresentationSender);
    messageModel.setReceiver(mockPresentationReceiver);
    messageModel.setSubject(FAKE_SUBJECT);
    messageModel.setMessage(FAKE_MESSAGE);
    messageModel.setDate(FAKE_DATE);
    messageModel.setUnread(true);

    Message message = messagesDataMapper.transform(messageModel);

    assertThat(message, is(instanceOf(Message.class)));
    assertThat(message.getMessageId(), is(FAKE_ID));
    assertThat(message.getSender(),is(mockDomainSender));
    assertThat(message.getReceiver(), is(mockDomainReceiver));
    assertThat(message.getSubject(), is(FAKE_SUBJECT));
    assertThat(message.getMessage(), is(FAKE_MESSAGE));
    assertThat(message.getDate(), is(FAKE_DATE));
    assertThat(message.isUnread(), is(Boolean.TRUE));
  }

}