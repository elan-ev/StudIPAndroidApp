/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.messages.data.repository.datastore;

import java.util.List;

import de.elanev.studip.android.app.base.data.net.StudIpLegacyApiService;
import de.elanev.studip.android.app.messages.data.entity.MessageEntity;
import rx.Observable;

/**
 * @author joern
 */

public class MessagesCloudDataStore implements MessagesDataStore {
  private final StudIpLegacyApiService apiService;

  public MessagesCloudDataStore(StudIpLegacyApiService apiService) {
    this.apiService = apiService;
  }

  @Override public Observable<List<MessageEntity>> messageInbox() {
    return apiService.getInboxMessages(0, 100); //FIXME: Proper pagination
  }

  @Override public Observable<List<MessageEntity>> messageOutbox() {
    return apiService.getOutboxMessages(0, 100); //FIXME: Proper pagination
  }

  @Override public Observable<MessageEntity> message(String messageId) {
    return apiService.getMessage(messageId);
  }

  @Override public Observable<Void> delete(String messageId) {
    return apiService.deleteMessage(messageId);
  }

  @Override public Observable<MessageEntity> send(MessageEntity message) {
    return apiService.sendMessage(message.getReceiver()
        .getUserId(), message.getSubject(), message.getMessage());
  }

}
