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

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import de.elanev.studip.android.app.backend.datamodel.Message;
import de.elanev.studip.android.app.backend.datamodel.Messages;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author joern
 * 
 */
public class MessagesRepository {

	private Context mContext;
	private static MessagesRepository mInstance;

	public static synchronized MessagesRepository getInstance(Context context) {
		if (mInstance == null)
			mInstance = new MessagesRepository(context);

		return mInstance;
	}

	private MessagesRepository(Context context) {
		this.mContext = context;
	}

	@SuppressLint("SimpleDateFormat")
	public void addMessages(ArrayList<Message> msgList) {
		// Debug
		// db.execSQL("DELETE FROM " + TABLE_MESSAGES);
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		try {
			for (Message message : msgList) {
				ContentValues values = new ContentValues();
				values.put(MessagesContract.Columns.MESSAGE_ID,
						message.message_id);
				values.put(MessagesContract.Columns.MESSAGE_SENDER_ID,
						message.sender_id);
				values.put(MessagesContract.Columns.MESSAGE_RECEIVER_ID,
						message.receiver_id);
				values.put(MessagesContract.Columns.MESSAGE_SUBJECT,
						message.subject);
				values.put(MessagesContract.Columns.MESSAGE, message.message);
				values.put(MessagesContract.Columns.MESSAGE_MKDATE,
						dateFormat.format(message.mkdate));
				values.put(MessagesContract.Columns.MESSAGE_PRIORITY,
						message.priority);
				values.put(MessagesContract.Columns.MESSAGE_UNREAD,
						message.unread);
				SQLiteDatabase db = DatabaseHandler.getInstance(mContext)
						.getWritableDatabase();
				db.beginTransaction();
				db.insertWithOnConflict(MessagesContract.TABLE, null, values,
						SQLiteDatabase.CONFLICT_IGNORE);
				db.endTransaction();
				db.close();

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressLint("SimpleDateFormat")
	public Messages getAllMessages() {
		// TODO getObject benutzen, doppelten Code verhindern
		String selectQuery = "SELECT  * FROM " + MessagesContract.TABLE;
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		SQLiteDatabase db = DatabaseHandler.getInstance(mContext)
				.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		Messages messages = new Messages();
		try {
			if (cursor.moveToFirst()) {
				do {

					messages.messages
							.add(new Message(
									cursor.getString(cursor
											.getColumnIndex(MessagesContract.Columns.MESSAGE_ID)),
									cursor.getString(cursor
											.getColumnIndex(MessagesContract.Columns.MESSAGE_SENDER_ID)),
									cursor.getString(cursor
											.getColumnIndex(MessagesContract.Columns.MESSAGE_RECEIVER_ID)),
									cursor.getString(cursor
											.getColumnIndex(MessagesContract.Columns.MESSAGE_SUBJECT)),
									cursor.getString(cursor
											.getColumnIndex(MessagesContract.Columns.MESSAGE)),
									dateFormat.parse(cursor.getString(cursor
											.getColumnIndex(MessagesContract.Columns.MESSAGE_MKDATE))),
									cursor.getString(cursor
											.getColumnIndex(MessagesContract.Columns.MESSAGE_PRIORITY)),
									Boolean.valueOf(cursor.getString(cursor
											.getColumnIndex(MessagesContract.Columns.MESSAGE_UNREAD)))));

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return messages;
	}
}
