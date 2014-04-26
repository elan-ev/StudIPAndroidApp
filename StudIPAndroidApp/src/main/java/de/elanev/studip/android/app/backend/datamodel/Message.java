/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.datamodel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * Represents a message as java object
 * 
 * @author joern
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName(value = "message")
public class Message {
	/**
	 * the studip internal id of the message
	 */
	public String message_id;
	/**
	 * the studip internal id of the message sender
	 */
	public String sender_id;
	/**
	 * the studip internal id of the message receiver
	 */
	public String receiver_id;
	/**
	 * the message subject
	 */
	public String subject;
	/**
	 * the message
	 */
	public String message;
	/**
	 * the date timestamp of the message
	 */
	public Long mkdate;
	/**
	 * the priority of the message
	 */
	public String priority;
	/**
	 * signals if the message is read or not
	 */
	public int unread;

	/**
	 * default constructor, needed for Jackson
	 */
	public Message() {
	}

	/**
	 * constructor for creating a object with all attributes
	 * 
	 * @param message_id
	 *            the message id
	 * @param sender_id
	 *            the sender id
	 * @param receiver_id
	 *            the receiver id
	 * @param subject
	 *            the message subject
	 * @param message
	 *            the message
	 * @param mkdate
	 *            the message date timestamp
	 * @param priority
	 *            the message priority
	 * @param unread
	 *            message read or nod
	 */
	public Message(String message_id, String sender_id, String receiver_id,
			String subject, String message, Long mkdate, String priority,
			int unread) {
		this.message_id = message_id;
		this.sender_id = sender_id;
		this.receiver_id = receiver_id;
		this.subject = subject;
		this.message = message;
		this.mkdate = mkdate;
		this.priority = priority;
		this.unread = unread;
	}

}
