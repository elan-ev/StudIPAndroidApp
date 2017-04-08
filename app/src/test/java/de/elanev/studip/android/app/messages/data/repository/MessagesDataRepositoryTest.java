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
import de.elanev.studip.android.app.messages.data.repository.datastore.MessagesDataStore;
import de.elanev.studip.android.app.messages.data.repository.datastore.MessagesDataStoreFactory;
import de.elanev.studip.android.app.messages.domain.Message;
import de.elanev.studip.android.app.user.data.entity.UserEntity;
import de.elanev.studip.android.app.user.domain.User;
import rx.Observable;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * @author joern
 */
public class MessagesDataRepositoryTest {
  private static final String FAKE_ID = "123";
  private static final String FAKE_SUBJECT = "fake-subject";
  private static final String FAKE_MESSAGE = "fake-message";
  private MessageEntity messageEntity;
  private Message message;
  @Mock private MessagesEntityDataMapper mockMessagesEntityMapper;
  @Mock private MessagesDataStoreFactory mockMessagesDataStoreFactory;
  @Mock private MessagesDataStore mockMessagesDataStore;
  private MessagesDataRepository messageDataRepository;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    given(mockMessagesDataStoreFactory.create()).willReturn(mockMessagesDataStore);

    messageEntity = new MessageEntity();
    UserEntity userEntity = new UserEntity();
    userEntity.setUserId(FAKE_ID);
    messageEntity.setReceiver(userEntity);
    messageEntity.setSubject(FAKE_SUBJECT);
    messageEntity.setMessage(FAKE_MESSAGE);

    message = new Message();
    User user = new User(FAKE_ID);
    message.setReceiver(user);
    message.setSubject(FAKE_SUBJECT);
    message.setMessage(FAKE_MESSAGE);

    given(mockMessagesEntityMapper.transform(messageEntity)).willReturn(message);
    given(mockMessagesEntityMapper.transform(message)).willReturn(messageEntity);


    messageDataRepository = new MessagesDataRepository(mockMessagesDataStoreFactory,
        mockMessagesEntityMapper);
  }

  @Test public void inboxMessages() throws Exception {
    List<MessageEntity> messageEntities = new ArrayList<>(5);
    messageEntities.add(new MessageEntity());
    messageEntities.add(new MessageEntity());

    given(mockMessagesDataStore.messageInbox()).willReturn(Observable.just(messageEntities));

    messageDataRepository.inboxMessages();

    verify(mockMessagesDataStoreFactory).create();
    verify(mockMessagesDataStore).messageInbox();
  }

  @Test public void outboxMessages() throws Exception {
    List<MessageEntity> messageEntities = new ArrayList<>(5);
    messageEntities.add(new MessageEntity());
    messageEntities.add(new MessageEntity());

    given(mockMessagesDataStore.messageOutbox()).willReturn(Observable.just(messageEntities));

    messageDataRepository.outboxMessages();

    verify(mockMessagesDataStoreFactory).create();
    verify(mockMessagesDataStore).messageOutbox();
  }

  @Test public void message() throws Exception {
    MessageEntity messageEntity = new MessageEntity();
    given(mockMessagesDataStore.message(FAKE_ID)).willReturn(Observable.just(messageEntity));

    messageDataRepository.message(FAKE_ID);

    verify(mockMessagesDataStoreFactory).create();
    verify(mockMessagesDataStore).message(FAKE_ID);
  }

  @Test public void delete() throws Exception {
    given(mockMessagesDataStore.delete(FAKE_ID)).willReturn(Observable.empty());

    messageDataRepository.delete(FAKE_ID);

    verify(mockMessagesDataStoreFactory).create();
    verify(mockMessagesDataStore).delete(FAKE_ID);
  }

  @Test public void send() throws Exception {
    given(mockMessagesDataStore.send(messageEntity)).willReturn(Observable.just(messageEntity));

    messageDataRepository.send(message);

    verify(mockMessagesDataStoreFactory).create();
    verify(mockMessagesDataStore).send(messageEntity);
  }

}