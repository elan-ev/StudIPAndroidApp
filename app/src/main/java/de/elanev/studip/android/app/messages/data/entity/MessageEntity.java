/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.messages.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.elanev.studip.android.app.user.data.entity.UserEntity;

/**
 * @author joern
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class MessageEntity {
  private String messageId;
  private String senderId;
  private String receiverId;
  private String subject;
  private String message;
  private Long date;
  private String priority;
  private int unread;
  private UserEntity sender;
  private UserEntity receiver;

  @JsonIgnore public UserEntity getSender() {
    return sender;
  }

  @JsonIgnore public void setSender(UserEntity sender) {
    this.sender = sender;
  }

  @JsonIgnore public UserEntity getReceiver() {
    return receiver;
  }

  @JsonIgnore public void setReceiver(UserEntity receiver) {
    this.receiver = receiver;
  }

  @JsonProperty("message_id") public String getMessageId() {
    return messageId;
  }

  @JsonProperty("message_id") public void setMessageId(String messageId) {
    this.messageId = messageId;
  }

  @JsonProperty("sender_id") public String getSenderId() {
    return senderId;
  }

  @JsonProperty("sender_id") public void setSenderId(String senderId) {
    this.senderId = senderId;
  }

  @JsonProperty("receiver_id") public String getReceiverId() {
    return receiverId;
  }

  @JsonProperty("receiver_id") public void setReceiverId(String receiverId) {
    this.receiverId = receiverId;
  }

  @JsonProperty("subject") public String getSubject() {
    return subject;
  }

  @JsonProperty("subject") public void setSubject(String subject) {
    this.subject = subject;
  }

  @JsonProperty("message") public String getMessage() {
    return message;
  }

  @JsonProperty("message") public void setMessage(String message) {
    this.message = message;
  }

  @JsonProperty("mkdate") public Long getDate() {
    return date;
  }

  @JsonProperty("mkdate") public void setDate(Long date) {
    this.date = date;
  }

  @JsonProperty("priority") public String getPriority() {
    return priority;
  }

  @JsonProperty("priority") public void setPriority(String priority) {
    this.priority = priority;
  }

  @JsonProperty("unread") public int getUnread() {
    return unread;
  }

  @JsonProperty("unread") public void setUnread(int unread) {
    this.unread = unread;
  }

  //  public int getDeleted() {
  //    return deleted;
  //  }
  //
  //  public void setDeleted(int deleted) {
  //    this.deleted = deleted;
  //  }
  //
  //  public String getMessageOriginal() {
  //    return messageOriginal;
  //  }
  //
  //  public void setMessageOriginal(String messageOriginal) {
  //    this.messageOriginal = messageOriginal;
  //  }
  //
  //  public String[] getAttachments() {
  //    return attachments;
  //  }
  //
  //  public void setAttachments(String[] attachments) {
  //    this.attachments = attachments;
  //  }
}
