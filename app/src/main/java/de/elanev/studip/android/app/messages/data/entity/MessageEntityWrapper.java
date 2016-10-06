/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.messages.data.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author joern
 */

public class MessageEntityWrapper {
  private MessageEntity messageEntity;

  @JsonProperty("message") public MessageEntity getMessageEntity() {
    return messageEntity;
  }

  @JsonProperty("message") public void setMessageEntity(MessageEntity messageEntity) {
    this.messageEntity = messageEntity;
  }
}
