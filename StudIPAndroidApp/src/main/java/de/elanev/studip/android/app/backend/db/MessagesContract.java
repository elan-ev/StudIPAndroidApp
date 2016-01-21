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
package de.elanev.studip.android.app.backend.db;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author joern
 * 
 */
public final class MessagesContract extends AbstractContract {
	/*
	 * table names
	 */
	public static final String TABLE_MESSAGES = "messages";
	public static final String TABLE_MESSAGE_FOLDERS = "message_folders";

	/*
	 * table creation strings
	 */
	// messages table
	public static final String CREATE_TABLE_MESSAGES_STRING = String
			.format("CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY, %s TEXT NOT NULL,"
					+ " %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s INTEGER, %s TEXT, %s INTEGER, %s INTEGER NOT NULL, "
					+ "FOREIGN KEY(%s) REFERENCES %s(%s), UNIQUE(%s, %s))",
					TABLE_MESSAGES, Columns.Messages._ID,
					Columns.Messages.MESSAGE_ID,
					Columns.Messages.MESSAGE_SENDER_ID,
					Columns.Messages.MESSAGE_RECEIVER_ID,
					Columns.Messages.MESSAGE_SUBJECT, Columns.Messages.MESSAGE,
					Columns.Messages.MESSAGE_MKDATE,
					Columns.Messages.MESSAGE_PRIORITY,
					Columns.Messages.MESSAGE_UNREAD,
					Columns.Messages.MESSAGE_FOLDER_ID,
					Columns.Messages.MESSAGE_FOLDER_ID, TABLE_MESSAGE_FOLDERS,
					Columns.MessageFolders._ID, Columns.Messages.MESSAGE_ID,
					Columns.Messages.MESSAGE_FOLDER_ID);

	// inbox message_folders tables
	public static final String CREATE_TABLE_MESSAGE_FOLDERS_STRING = String
			.format("CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY, %s TEXT NOT NULL, %s TEXT NOT NULL, "
					+ "UNIQUE(%s, %s))", TABLE_MESSAGE_FOLDERS,
					Columns.MessageFolders._ID,
					Columns.MessageFolders.MESSAGE_FOLDER_NAME,
					Columns.MessageFolders.MESSAGE_FOLDER_BOX,
					Columns.MessageFolders.MESSAGE_FOLDER_NAME,
					Columns.MessageFolders.MESSAGE_FOLDER_BOX);

	/*
	 * joins
	 */
	// messages and message folders joined
	public static final String MESSAGES_JOIN_MESSAGE_FOLDERS = String.format(
			"%s INNER JOIN %s on %s = %s ", TABLE_MESSAGES,
			TABLE_MESSAGE_FOLDERS,
			Qualified.MessageFolders.MESSAGES_FOLDERS_ID,
			Qualified.Messages.MESSAGES_MESSAGE_FOLDER_ID);

	// messages and users joined
	public static final String MESSAGES_JOIN_USERS = String.format(
			"%s INNER JOIN %s on %s = %s ", TABLE_MESSAGES,
			UsersContract.TABLE, Qualified.Messages.MESSAGES_MESSAGE_SENDER_ID,
			UsersContract.Qualified.USERS_USER_ID);

	// messages, message folders and users joined
	public static final String MESSAGES_JOIN_MESSAGE_FOLDERS_JOIN_USERS = MESSAGES_JOIN_MESSAGE_FOLDERS
			+ String.format("INNER JOIN %s on %s = %s ", UsersContract.TABLE,
					Qualified.Messages.MESSAGES_MESSAGE_SENDER_ID,
					UsersContract.Qualified.USERS_USER_ID);

	/*
	 * content provider
	 */
	// paths
	public static final String PATH_MESSAGES = "messages";
	public static final String PATH_MESSAGE_FOLDERS = "folders";
	// content uris
	public static final Uri CONTENT_URI_MESSAGES = BASE_CONTENT_URI.buildUpon()
			.appendPath(PATH_MESSAGES).build();
	public static final Uri CONTENT_URI_MESSAGE_FOLDERS = BASE_CONTENT_URI
			.buildUpon().appendPath(PATH_MESSAGES)
			.appendPath(PATH_MESSAGE_FOLDERS).build();
	// content types
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.studip.messages";
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.studip.messages";
	public static final String FOLDER_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.studip.messageFolders";
	public static final String FOLDER_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.studip.messageFolders";
	// sort orders
	public static final String DEFAULT_SORT_ORDER_MESSAGES = Qualified.Messages.MESSAGES_MESSAGE_MKDATE
			+ " DESC";
	public static final String DEFAULT_SORT_ORDER_FOLDERS = Qualified.MessageFolders.MESSAGES_FOLDERS_MESSAGE_FOLDER_BOX
			+ " ASC, "
			+ Qualified.MessageFolders.MESSAGES_FOLDERS_MESSAGE_FOLDER_NAME
			+ " ASC";

	// no constructor
	private MessagesContract() {
	}

	/*
	 * table columns
	 */
	public static final class Columns {
		private Columns() {
		}

		/*
		 * messages
		 */
		public interface Messages extends BaseColumns {
			public static final String MESSAGE_ID = "message_id";
			public static final String MESSAGE_SENDER_ID = "sender_id";
			public static final String MESSAGE_RECEIVER_ID = "receiver_id";
			public static final String MESSAGE_SUBJECT = "subject";
			public static final String MESSAGE = "message";
			public static final String MESSAGE_MKDATE = "mkdate";
			public static final String MESSAGE_PRIORITY = "priority";
			public static final String MESSAGE_UNREAD = "unread";
			public static final String MESSAGE_FOLDER_ID = "folder_id";
		}

		/*
		 * message folders
		 */
		public interface MessageFolders extends BaseColumns {
			public static final String MESSAGE_FOLDER_BOX = "folder_box";
			public static final String MESSAGE_FOLDER_NAME = "folder_name";
		}

	}

	/*
	 * qualified column names
	 */
	public static final class Qualified {
		/*
		 * messages
		 */
		public interface Messages {
			public static final String MESSAGES_ID = TABLE_MESSAGES + "."
					+ Columns.Messages._ID;
			public static final String MESSAGES_MESSAGE_ID = TABLE_MESSAGES
					+ "." + Columns.Messages.MESSAGE_ID;
			public static final String MESSAGES_MESSAGE_SENDER_ID = TABLE_MESSAGES
					+ "." + Columns.Messages.MESSAGE_SENDER_ID;
			public static final String MESSAGES_MESSAGE_RECEIVER_ID = TABLE_MESSAGES
					+ "." + Columns.Messages.MESSAGE_SENDER_ID;
			public static final String MESSAGES_MESSAGE_SUBJECT = TABLE_MESSAGES
					+ "." + Columns.Messages.MESSAGE_SUBJECT;
			public static final String MESSAGES_MESSAGE = TABLE_MESSAGES + "."
					+ Columns.Messages.MESSAGE;
			public static final String MESSAGES_MESSAGE_MKDATE = TABLE_MESSAGES
					+ "." + Columns.Messages.MESSAGE_MKDATE;
			public static final String MESSAGES_MESSAGE_PRIORITY = TABLE_MESSAGES
					+ "." + Columns.Messages.MESSAGE_PRIORITY;
			public static final String MESSAGES_MESSAGE_UNREAD = TABLE_MESSAGES
					+ "." + Columns.Messages.MESSAGE_UNREAD;
			public static final String MESSAGES_MESSAGE_FOLDER_ID = TABLE_MESSAGES
					+ "." + Columns.Messages.MESSAGE_FOLDER_ID;
		}

		/*
		 * message folders
		 */
		public interface MessageFolders {
			public static final String MESSAGES_FOLDERS_ID = TABLE_MESSAGE_FOLDERS
					+ "." + Columns.MessageFolders._ID;
			public static final String MESSAGES_FOLDERS_MESSAGE_FOLDER_NAME = TABLE_MESSAGE_FOLDERS
					+ "." + Columns.MessageFolders.MESSAGE_FOLDER_NAME;
			public static final String MESSAGES_FOLDERS_MESSAGE_FOLDER_BOX = TABLE_MESSAGE_FOLDERS
					+ "." + Columns.MessageFolders.MESSAGE_FOLDER_BOX;
		}
	}
}
