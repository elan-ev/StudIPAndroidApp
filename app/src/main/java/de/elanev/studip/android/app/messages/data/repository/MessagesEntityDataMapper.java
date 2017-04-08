/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.messages.data.repository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.messages.data.entity.MessageEntity;
import de.elanev.studip.android.app.messages.domain.Message;
import de.elanev.studip.android.app.user.data.entity.UserEntityDataMapper;

/**
 * @author joern
 */
@Singleton
public class MessagesEntityDataMapper {

  private final UserEntityDataMapper userEntityMapper;

  @Inject public MessagesEntityDataMapper(UserEntityDataMapper userEntityDataMapper) {
    this.userEntityMapper = userEntityDataMapper;
  }

  public List<Message> transform(List<MessageEntity> messageEntities) {
    List<Message> messages = new ArrayList<>(messageEntities.size());

    for (MessageEntity messageEntity : messageEntities) {
      if (messageEntity != null) {
        messages.add(transform(messageEntity));
      }
    }

    return messages;
  }

  public Message transform(MessageEntity messageEntity) {
    Message message = new Message();
    message.setMessageId(messageEntity.getMessageId());
    message.setSubject(messageEntity.getSubject());
    message.setMessage(messageEntity.getMessage());
    message.setDate(messageEntity.getDate());
    message.setUnread(messageEntity.getUnread() == 1);
    message.setSender(userEntityMapper.transform(messageEntity.getSender()));
    message.setReceiver(userEntityMapper.transform(messageEntity.getReceiver()));

    return message;
  }

  public MessageEntity transform(Message message) {
    MessageEntity messageEntity = new MessageEntity();
    messageEntity.setMessageId(message.getMessageId());
    messageEntity.setSubject(message.getSubject());
    messageEntity.setMessage(message.getMessage());
    messageEntity.setDate(message.getDate());
    messageEntity.setUnread(message.isUnread() ? 1 : 0);
    messageEntity.setSender(userEntityMapper.transform(message.getSender()));
    messageEntity.setReceiver(userEntityMapper.transform(message.getReceiver()));

    return messageEntity;
  }
}
