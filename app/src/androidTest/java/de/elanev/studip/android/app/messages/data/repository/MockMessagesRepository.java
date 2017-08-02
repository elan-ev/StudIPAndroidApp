/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.messages.data.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.elanev.studip.android.app.messages.domain.Message;
import de.elanev.studip.android.app.messages.domain.MessagesRepository;
import de.elanev.studip.android.app.user.data.repository.MockUserRepository;
import rx.Observable;

/**
 * @author joern
 */
public class MockMessagesRepository implements MessagesRepository {
  public static final Message INBOX_MESSAGE = new Message("inboxMessageId1", "Inbox test message",
      946684800L, "Inbox test message body", MockUserRepository.TEACHER, MockUserRepository.STUDENT,
      true);
  public static final Message OUTBOX_MESSAGE = new Message("outboxMessageId1",
      "Outbox test message", 946684800L, "Outbox test message body", MockUserRepository.TEACHER,
      MockUserRepository.STUDENT, false);
  private final Map<String, Message> inbox = new HashMap<String, Message>() {{
    put(INBOX_MESSAGE.getMessageId(), INBOX_MESSAGE);
  }};
  private final Map<String, Message> outbox = new HashMap<String, Message>() {{
    put(OUTBOX_MESSAGE.getMessageId(), OUTBOX_MESSAGE);
  }};

  @Override public Observable<List<Message>> inboxMessages() {
    return Observable.just(new ArrayList<>(inbox.values()));
  }

  @Override public Observable<List<Message>> outboxMessages() {
    return Observable.just(new ArrayList<>(outbox.values()));
  }

  @Override public Observable<Message> message(String messageId) {
    if (inbox.containsKey(messageId)) {
      return Observable.just(inbox.get(messageId));
    } else if (outbox.containsKey(messageId)) {
      return Observable.just(outbox.get(messageId));
    } else {
      return Observable.just(inbox.get(INBOX_MESSAGE.getMessage()));
    }
  }

  @Override public Observable<Void> delete(String messageId) {
    if (inbox.containsKey(messageId)) {
      inbox.remove(messageId);
    } else if (outbox.containsKey(messageId)) {
      outbox.remove(messageId);
    } else {
      return Observable.error(new Exception("Item not found"));
    }

    return Observable.empty();
  }

  @Override public Observable<Message> send(Message message) {
    message.setMessageId("sendMessageId1");
    outbox.put(message.getMessageId(), message);

    return Observable.just(outbox.get(message.getMessageId()));
  }
}
