/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
/**
 * 
 */
package de.elanev.studip.android.app.backend.db;

import android.provider.BaseColumns;

/**
 * @author joern
 * 
 */
public class MessagesContract extends AbstractContract {
	public static final String TABLE = "messages";
	public static final String CREATE_STRING = String
			.format("create table if not exists %s (%s integer primary key, %s text unique,"
					+ " %s text, %s text, %s text, %s text, %s date, %s text, %s boolean)",
					TABLE, BaseColumns._ID, Columns.MESSAGE_ID,
					Columns.MESSAGE_SENDER_ID, Columns.MESSAGE_RECEIVER_ID,
					Columns.MESSAGE_SUBJECT, Columns.MESSAGE,
					Columns.MESSAGE_MKDATE, Columns.MESSAGE_PRIORITY,
					Columns.MESSAGE_UNREAD);

	public MessagesContract() {
	}

	public static final class Columns implements BaseColumns {
		private Columns() {
		}

		public static final String MESSAGE_ID = "message_id";
		public static final String MESSAGE_SENDER_ID = "sender_id";
		public static final String MESSAGE_RECEIVER_ID = "receiver_id";
		public static final String MESSAGE_SUBJECT = "subject";
		public static final String MESSAGE = "message";
		public static final String MESSAGE_MKDATE = "mkdate";
		public static final String MESSAGE_PRIORITY = "priority";
		public static final String MESSAGE_UNREAD = "unread";
	}
}
