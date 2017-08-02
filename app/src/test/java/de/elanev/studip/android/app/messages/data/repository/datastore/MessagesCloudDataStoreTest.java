/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.messages.data.repository.datastore;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import de.elanev.studip.android.app.base.data.net.StudIpLegacyApiService;
import de.elanev.studip.android.app.messages.data.entity.MessageEntity;
import de.elanev.studip.android.app.user.data.entity.UserEntity;
import rx.Observable;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * @author joern
 */
public class MessagesCloudDataStoreTest {
  private static final String FAKE_ID = "123";
  private static final String FAKE_SUBJECT = "fake-subject";
  private static final String FAKE_MESSAGE = "fake-message";

  @Mock StudIpLegacyApiService mockApiService;
  private MessagesCloudDataStore messagesCloudDataStore;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    messagesCloudDataStore = new MessagesCloudDataStore(mockApiService);
  }

  @Test public void messageInbox() throws Exception {
    List<MessageEntity> messageEntities = new ArrayList<>();
    MessageEntity messageEntity1 = new MessageEntity();
    MessageEntity messageEntity2 = new MessageEntity();

    messageEntities.add(messageEntity2);
    messageEntities.add(messageEntity1);

    given(mockApiService.getInboxMessages(0, 100)).willReturn(Observable.just(messageEntities));
    messagesCloudDataStore.messageInbox();

    verify(mockApiService).getInboxMessages(0, 100);
  }

  @Test public void messageOutbox() throws Exception {
    List<MessageEntity> messageEntities = new ArrayList<>();
    MessageEntity messageEntity1 = new MessageEntity();
    MessageEntity messageEntity2 = new MessageEntity();

    messageEntities.add(messageEntity2);
    messageEntities.add(messageEntity1);

    given(mockApiService.getOutboxMessages(0, 100)).willReturn(Observable.just(messageEntities));
    messagesCloudDataStore.messageOutbox();

    verify(mockApiService).getOutboxMessages(0, 100);
  }

  @Test public void message() throws Exception {
    MessageEntity messageEntity = new MessageEntity();
    given(mockApiService.getMessage(FAKE_ID)).willReturn(Observable.just(messageEntity));
    messagesCloudDataStore.message(FAKE_ID);

    verify(mockApiService).getMessage(FAKE_ID);
  }

  @Test public void delete() throws Exception {
    given(mockApiService.getMessage(FAKE_ID)).willReturn(Observable.empty());
    messagesCloudDataStore.delete(FAKE_ID);

    verify(mockApiService).deleteMessage(FAKE_ID);
  }

  @Test public void send() throws Exception {
    MessageEntity messageEntity = new MessageEntity();
    UserEntity userEntity = new UserEntity();
    userEntity.setUserId(FAKE_ID);
    messageEntity.setReceiver(userEntity);
    messageEntity.setSubject(FAKE_SUBJECT);
    messageEntity.setMessage(FAKE_MESSAGE);
    given(mockApiService.sendMessage(FAKE_ID, FAKE_SUBJECT, FAKE_MESSAGE)).willReturn(
        Observable.just(messageEntity));
    messagesCloudDataStore.send(messageEntity);

    verify(mockApiService).sendMessage(FAKE_ID, FAKE_SUBJECT, FAKE_MESSAGE);
  }
}