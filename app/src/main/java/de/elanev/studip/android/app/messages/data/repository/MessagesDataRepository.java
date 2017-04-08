/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.messages.data.repository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.messages.data.repository.datastore.MessagesDataStoreFactory;
import de.elanev.studip.android.app.messages.domain.Message;
import de.elanev.studip.android.app.messages.domain.MessagesRepository;
import rx.Observable;

/**
 * @author joern
 */
@Singleton
public class MessagesDataRepository implements MessagesRepository {

  private final MessagesDataStoreFactory messagesDataStoreFactory;
  private final MessagesEntityDataMapper messageEntityDataMapper;

  @Inject MessagesDataRepository(MessagesDataStoreFactory messagesDataStoreFactory,
      MessagesEntityDataMapper messagesEntityDataMapper) {
    this.messagesDataStoreFactory = messagesDataStoreFactory;
    this.messageEntityDataMapper = messagesEntityDataMapper;
  }

  @Override public Observable<List<Message>> inboxMessages() {
    return messagesDataStoreFactory.create()
        .messageInbox()
        .map(messageEntityDataMapper::transform);
  }

  @Override public Observable<List<Message>> outboxMessages() {
    return messagesDataStoreFactory.create()
        .messageOutbox()
        .map(messageEntityDataMapper::transform);
  }

  @Override public Observable<Message> message(String messageId) {
    return messagesDataStoreFactory.create()
        .message(messageId)
        .map(messageEntityDataMapper::transform);
  }

  @Override public Observable<Void> delete(String messageId) {
    return messagesDataStoreFactory.create()
        .delete(messageId);
  }

  @Override public Observable<Message> send(Message message) {
    return messagesDataStoreFactory.create()
        .send(messageEntityDataMapper.transform(message))
        .map(messageEntityDataMapper::transform);
  }
}
