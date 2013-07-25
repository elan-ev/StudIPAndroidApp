/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.db;

import de.elanev.studip.android.app.backend.datamodel.Event;
import de.elanev.studip.android.app.backend.datamodel.Events;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class EventsRepository {
	private static EventsRepository instance;
	private Context mContext;

	public static synchronized EventsRepository getInstance(Context context) {
		if (instance == null)
			instance = new EventsRepository(context);

		return instance;
	}

	private EventsRepository(Context context) {
		this.mContext = context;
	}

	public void addEvents(Events e) {
		SQLiteDatabase db = null;
		try {
			for (Event event : e.events) {

				ContentValues values = new ContentValues();
				values.put(EventsContract.Columns.EVENT_ID, event.event_id);
				values.put(EventsContract.Columns.EVENT_COURSE_ID,
						event.course_id);
				values.put(EventsContract.Columns.EVENT_START,
						event.start * 1000L);
				values.put(EventsContract.Columns.EVENT_END, event.end * 1000L);
				values.put(EventsContract.Columns.EVENT_TITLE, event.title);
				values.put(EventsContract.Columns.EVENT_DESCRIPTION,
						event.description);
				values.put(EventsContract.Columns.EVENT_CATEGORIES,
						event.categories);
				values.put(EventsContract.Columns.EVENT_ROOM, event.room);
				db = DatabaseHandler.getInstance(mContext)
						.getWritableDatabase();
				db.beginTransaction();
				try {
					db.insertWithOnConflict(EventsContract.TABLE, null, values,
							SQLiteDatabase.CONFLICT_IGNORE);
					db.setTransactionSuccessful();
				} catch (Exception ex) {
					ex.printStackTrace();
				} finally {
					db.endTransaction();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Events getCurrentEventsForCourse(String cid) {
		Cursor cursor = null;
		Events events = new Events();
		cursor = getCurrentEventsCursorForCourse(cid);
		if (cursor == null) {
			return events;
		}
		try {

			if (cursor.moveToFirst()) {
				do {
					events.events
							.add(new Event(
									cursor.getString(cursor
											.getColumnIndex(EventsContract.Columns.EVENT_ID)),
									cursor.getString(cursor
											.getColumnIndex(EventsContract.Columns.EVENT_COURSE_ID)),
									cursor.getLong(cursor
											.getColumnIndex(EventsContract.Columns.EVENT_START)),
									cursor.getLong(cursor
											.getColumnIndex(EventsContract.Columns.EVENT_END)),
									cursor.getString(cursor
											.getColumnIndex(EventsContract.Columns.EVENT_TITLE)),
									cursor.getString(cursor
											.getColumnIndex(EventsContract.Columns.EVENT_DESCRIPTION)),
									cursor.getString(cursor
											.getColumnIndex(EventsContract.Columns.EVENT_CATEGORIES)),
									cursor.getString(cursor
											.getColumnIndex(EventsContract.Columns.EVENT_ROOM))));
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return events;
	}

	public Cursor getCurrentEventsCursorForCourse(String cid) {
		SQLiteDatabase db = DatabaseHandler.getInstance(mContext)
				.getReadableDatabase();
		Cursor cursor = null;
		cursor = db.query(EventsContract.TABLE, null,
				EventsContract.Columns.EVENT_COURSE_ID + "=? AND "
						+ EventsContract.Columns.EVENT_START
						+ " >= strftime('%s','now')", new String[] { cid },
				null, null, EventsContract.Columns.EVENT_START + " ASC");
		return cursor;
	}
}
