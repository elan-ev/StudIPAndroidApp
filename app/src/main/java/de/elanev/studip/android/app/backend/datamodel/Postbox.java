/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.backend.datamodel;

import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * @author joern
 */
public class Postbox {
  @JsonProperty("message_inbox") public MessageFolders inbox;
  @JsonProperty("message_outbox") public MessageFolders outbox;

  public Postbox() {
    // Nothing to do
  }

  public Postbox(MessageFolders inbox, MessageFolders outbox) {
    this.inbox = inbox;
    this.outbox = outbox;
  }

  @JsonIgnore public static Postbox fromJson(String postboxJson) {
    if (TextUtils.isEmpty(postboxJson)) {
      return null;
    }

    ObjectMapper mapper = new ObjectMapper();
    Postbox postbox = null;

    try {
      postbox = mapper.readValue(postboxJson, Postbox.class);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return postbox;
  }

  @JsonIgnore public static String toJson(Postbox postbox) {

    if (postbox == null) {
      return null;
    }

    ObjectMapper mapper = new ObjectMapper();
    String json = "";

    try {
      json = mapper.writeValueAsString(postbox);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    return json;
  }

}
