/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.messages.presentation.mapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.elanev.studip.android.app.base.internal.di.PerActivity;
import de.elanev.studip.android.app.base.internal.di.PerFragment;
import de.elanev.studip.android.app.messages.domain.Message;
import de.elanev.studip.android.app.messages.presentation.model.MessageModel;
import de.elanev.studip.android.app.user.presentation.mapper.UserModelDataMapper;

/**
 * @author joern
 */
@PerFragment
public class MessagesDataMapper {

  private final UserModelDataMapper userDataMapper;

  @Inject MessagesDataMapper(UserModelDataMapper userModelDataMapper) {
    this.userDataMapper = userModelDataMapper;
  }

  public List<MessageModel> transform(List<Message> messages) {
    List<MessageModel> messageModels = new ArrayList<>();

    for (Message message : messages) {
      if (message != null) {
        messageModels.add(transform(message));
      }
    }

    return messageModels;
  }

  public MessageModel transform(Message message) {
    MessageModel messageModel = new MessageModel();
    messageModel.setMessageId(message.getMessageId());
    messageModel.setSubject(message.getSubject());
    messageModel.setMessage(message.getMessage());
    messageModel.setDate(message.getDate());
    messageModel.setUnread(message.isUnread());
    messageModel.setSender(userDataMapper.transform(message.getSender()));
    messageModel.setReceiver(userDataMapper.transform(message.getReceiver()));

    return messageModel;
  }
}
