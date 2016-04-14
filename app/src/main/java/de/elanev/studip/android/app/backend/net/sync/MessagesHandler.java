/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package de.elanev.studip.android.app.backend.net.sync;

import android.content.ContentProviderOperation;

import de.elanev.studip.android.app.backend.datamodel.Message;
import de.elanev.studip.android.app.backend.datamodel.Messages;
import de.elanev.studip.android.app.backend.db.MessagesContract;

import java.util.ArrayList;

/**
 * @author joern
 */
public class MessagesHandler implements ResultHandler {

	private Messages mMessages;
	private String mFolder;
	private String mBox;

	public MessagesHandler(Messages messages, String folder, String box) {
		this.mMessages = messages;
		this.mFolder = folder;
		this.mBox = box;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.elanev.studip.android.app.backend.net.sync.ResultHandler#parse()
	 */
	public ArrayList<ContentProviderOperation> parse() {
		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

		operations.add(parseMessageFolder(mFolder, mBox));
		for (Message message : mMessages.messages) {
			operations.add(parseMessage(message, 0));
		}

		return operations;
	}

	/*
	 * Parses the folder and builds a ContentProviderOperation for inserting it.
	 */
	private ContentProviderOperation parseMessageFolder(String folder,
			String box) {
		ContentProviderOperation.Builder builder = ContentProviderOperation
				.newInsert(MessagesContract.CONTENT_URI_MESSAGE_FOLDERS)
				.withValue(
						MessagesContract.Columns.MessageFolders.MESSAGE_FOLDER_NAME,
						folder)
				.withValue(
						MessagesContract.Columns.MessageFolders.MESSAGE_FOLDER_BOX,
						box);
		return builder.build();
	}

	/*
	 * Parses the message object and builds a ContentProviderOperation with the
	 * folder back reference for inserting it.
	 */
	private ContentProviderOperation parseMessage(Message m, int folderBackRef) {
		ContentProviderOperation.Builder builder = ContentProviderOperation
				.newInsert(MessagesContract.CONTENT_URI_MESSAGES)
				.withValue(MessagesContract.Columns.Messages.MESSAGE_ID,
						m.messageId)
				.withValue(MessagesContract.Columns.Messages.MESSAGE, m.message)
				.withValue(MessagesContract.Columns.Messages.MESSAGE_MKDATE,
						m.mkdate)
				.withValue(MessagesContract.Columns.Messages.MESSAGE_PRIORITY,
						m.priority)
				.withValue(
						MessagesContract.Columns.Messages.MESSAGE_RECEIVER_ID,
						m.receiverId)
				.withValue(MessagesContract.Columns.Messages.MESSAGE_SENDER_ID,
						m.senderId)
				.withValue(MessagesContract.Columns.Messages.MESSAGE_SUBJECT,
						m.subject)
				.withValue(MessagesContract.Columns.Messages.MESSAGE_UNREAD,
						m.unread)
				.withValueBackReference(
						MessagesContract.Columns.Messages.MESSAGE_FOLDER_ID,
						folderBackRef);
		return builder.build();
	}
}
