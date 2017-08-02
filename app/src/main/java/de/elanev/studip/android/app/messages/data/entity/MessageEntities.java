/*
 * Copyright (c) 2017 ELAN e.V.
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

import de.elanev.studip.android.app.base.data.entity.Pagination;

/**
 * @author joern
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageEntities {
  private ArrayList<MessageEntity> messages;
  private Pagination pagination;

  @JsonProperty("messages") public ArrayList<MessageEntity> getMessages() {
    return messages;
  }

  @JsonProperty("messages") public void setMessages(ArrayList<MessageEntity> messages) {
    this.messages = messages;
  }

  @JsonProperty("pagination") public Pagination getPagination() {
    return pagination;
  }

  @JsonProperty("pagination") public void setPagination(Pagination pagination) {
    this.pagination = pagination;
  }
}
