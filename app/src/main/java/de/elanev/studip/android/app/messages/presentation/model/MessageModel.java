/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.messages.presentation.model;

import java.io.Serializable;

import de.elanev.studip.android.app.user.presentation.model.UserModel;

/**
 * @author joern
 */
public class MessageModel implements Serializable {
  private boolean unread;
  private String message;
  private long date;
  private String subject;
  private String messageId;
  private UserModel sender;
  private UserModel receiver;

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

  public UserModel getSender() {
    return sender;
  }

  public void setSender(UserModel sender) {
    this.sender = sender;
  }

  public UserModel getReceiver() {
    return receiver;
  }

  public void setReceiver(UserModel receiver) {
    this.receiver = receiver;
  }
}
