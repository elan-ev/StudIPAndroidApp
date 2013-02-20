/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package studip.app.backend.datamodel;

import java.util.Date;

public class Message {
    public String message_id;
    public String sender_id;
    public String receiver_id;
    public String subject;
    public String message;
    public Date mkdate;
    public String priority;
    public Boolean unread;

    /**
     * @param message_id
     * @param sender_id
     * @param receiver_id
     * @param subject
     * @param message
     * @param mkdate
     * @param priority
     * @param unread
     */
    public Message(String message_id, String sender_id, String receiver_id,
	    String subject, String message, Date mkdate, String priority,
	    Boolean unread) {
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
