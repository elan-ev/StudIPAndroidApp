/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.messages.domain;

import java.util.List;

import de.elanev.studip.android.app.messages.data.entity.MessageEntity;
import rx.Observable;

/**
 * @author joern
 */
public interface MessagesRepository {
  Observable<List<Message>> inboxMessages();

  Observable<List<Message>> outboxMessages();

  Observable<Message> message(String messageId);

  Observable<Void> delete(String messageId);

  Observable<Message> send(Message message);
}
