/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package de.elanev.studip.android.app.backend.datamodel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import java.io.Serializable;

/**
 * Represents a message as java object
 *
 * @author joern
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName(value = "message")
public class Message implements Serializable{

  /**
   * the studip internal id of the message
   */
  @JsonProperty("message_id") public String messageId;
  /**
   * the studip internal id of the message sender
   */
  @JsonProperty("sender_id") public String senderId;
  /**
   * the studip internal id of the message receiver
   */
  @JsonProperty("receiver_id") public String receiverId;
  /**
   * the message subject
   */
  @JsonProperty("subject") public String subject;
  /**
   * the message
   */
  @JsonProperty("message") public String message;
  /**
   * the date timestamp of the message
   */
  @JsonProperty("mkdate") public Long mkdate;
  /**
   * the priority of the message
   */
  @JsonProperty("priority") public String priority;
  /**
   * signals if the message is read or not
   */
  @JsonProperty("unread") public int unread;
  /**
   * Has this message been deleted
   */
  @JsonProperty("deleted") public int deleted;
  /**
   * Original format of the message. E.g. with HTML etc.
   */
  @JsonProperty("message_original") public String messageOriginal;
  /**
   * String array of message attachments
   */
  @JsonProperty("attachments") public String[] attachments;

  /**
   * default constructor, needed for Jackson
   */
  public Message() {
  }

  /**
   * constructor for creating a object with all attributes
   *
   * @param messageId       the message id
   * @param senderId        the sender id
   * @param receiverId      the receiver id
   * @param subject         the message subject
   * @param message         the message
   * @param mkdate          the message date timestamp
   * @param priority        the message priority
   * @param unread          message read or not
   * @param deleted         Has message been deleted
   * @param messageOriginal Original format of message
   * @param attachments     Message attachments array
   */
  public Message(String messageId, String senderId, String receiverId, String subject,
      String message, Long mkdate, String priority, int unread, int deleted, String messageOriginal,
      String[] attachments) {
    this.messageId = messageId;
    this.senderId = senderId;
    this.receiverId = receiverId;
    this.subject = subject;
    this.message = message;
    this.mkdate = mkdate;
    this.priority = priority;
    this.unread = unread;
    this.deleted = deleted;
    this.messageOriginal = messageOriginal;
    this.attachments = attachments;
  }

}
