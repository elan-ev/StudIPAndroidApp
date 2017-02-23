/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.messages.domain;

import de.elanev.studip.android.app.user.domain.User;

/**
 * @author joern
 */
public class Message {
  private String messageId;
  private String subject;
  private long date;
  private String message;
  private User sender;
  private User receiver;
  private boolean unread;

  public Message() {}

  public Message(String messageId, String subject, long date, String message, User sender,
      User receiver, boolean unread) {
    this.messageId = messageId;
    this.subject = subject;
    this.date = date;
    this.message = message;
    this.sender = sender;
    this.receiver = receiver;
    this.unread = unread;
  }

  public boolean isUnread() {
    return unread;
  }

  public void setUnread(boolean unread) {
    this.unread = unread;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public long getDate() {
    return date;
  }

  public void setDate(long date) {
    this.date = date;
  }

  public String getSubject() {
    return subject;
  }

  public void setSubject(String subject) {
    this.subject = subject;
  }

  public String getMessageId() {
    return messageId;
  }

  public void setMessageId(String messageId) {
    this.messageId = messageId;
  }

  public User getSender() {
    return sender;
  }

  public void setSender(User sender) {
    this.sender = sender;
  }

  public User getReceiver() {
    return receiver;
  }

  public void setReceiver(User receiver) {
    this.receiver = receiver;
  }
}
