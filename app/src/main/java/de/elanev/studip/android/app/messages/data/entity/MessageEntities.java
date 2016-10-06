/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

/**
 *
 */
package de.elanev.studip.android.app.messages.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

/**
 * @author joern
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageEntities {
  private ArrayList<MessageEntity> messages;

  @JsonProperty("messages") public ArrayList<MessageEntity> getMessages() {
    return messages;
  }

  @JsonProperty("messages") public void setMessages(ArrayList<MessageEntity> messages) {
    this.messages = messages;
  }
}
