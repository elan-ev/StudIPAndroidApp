/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package studip.app.backend.db;

import studip.app.backend.datamodel.Event;
import studip.app.backend.datamodel.Events;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class EventsRepository {
    private static EventsRepository instance;
    private Context mContext;

    public static EventsRepository getInstance(Context context) {
	if (instance == null)
	    instance = new EventsRepository(context);

	return instance;
    }

    private EventsRepository(Context context) {
	this.mContext = context;
    }

    public void addEvents(Events e) {
	SQLiteDatabase db = DatabaseHandler.getInstance(mContext)
		.getWritableDatabase();
	// remove non existing entrys
	db.execSQL("DELETE FROM " + EventsConstract.TABLE);
	try {
	    for (studip.app.backend.datamodel.Event event : e.events) {

		ContentValues values = new ContentValues();
		values.put(EventsConstract.Columns.EVENT_ID, event.event_id);
		values.put(EventsConstract.Columns.EVENT_COURSE_ID,
			event.course_id);
		values.put(EventsConstract.Columns.EVENT_START, event.start);
		values.put(EventsConstract.Columns.EVENT_END, event.end);
		values.put(EventsConstract.Columns.EVENT_TITLE, event.title);
		values.put(EventsConstract.Columns.EVENT_DESCRIPTION,
			event.description);
		values.put(EventsConstract.Columns.EVENT_CATEGORIES,
			event.categories);
		values.put(EventsConstract.Columns.EVENT_ROOM, event.room);

		db.insertWithOnConflict(EventsConstract.TABLE, null, values,
			SQLiteDatabase.CONFLICT_IGNORE);

	    }
	} catch (Exception ex) {
	    ex.printStackTrace();
	} finally {
	    db.close();
	}
    }

    public Events getEventsForCourse(String cid) {
	SQLiteDatabase db = DatabaseHandler.getInstance(mContext)
		.getReadableDatabase();
	Cursor cursor = null;
	Events events = new Events();
	cursor = db.query(EventsConstract.TABLE, null,
		EventsConstract.Columns.EVENT_COURSE_ID + "=?",
		new String[] { cid }, null, null, null);
	if (cursor == null) {
	    return events;
	}
	try {

	    if (cursor.moveToFirst()) {
		do {
		    events.events
			    .add(new Event(
				    cursor.getString(cursor
					    .getColumnIndex(EventsConstract.Columns.EVENT_ID)),
				    cursor.getString(cursor
					    .getColumnIndex(EventsConstract.Columns.EVENT_COURSE_ID)),
				    cursor.getString(cursor
					    .getColumnIndex(EventsConstract.Columns.EVENT_START)),
				    cursor.getString(cursor
					    .getColumnIndex(EventsConstract.Columns.EVENT_END)),
				    cursor.getString(cursor
					    .getColumnIndex(EventsConstract.Columns.EVENT_TITLE)),
				    cursor.getString(cursor
					    .getColumnIndex(EventsConstract.Columns.EVENT_DESCRIPTION)),
				    cursor.getString(cursor
					    .getColumnIndex(EventsConstract.Columns.EVENT_CATEGORIES)),
				    cursor.getString(cursor
					    .getColumnIndex(EventsConstract.Columns.EVENT_ROOM))));
		} while (cursor.moveToNext());
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    cursor.close();
	    db.close();
	}

	return events;
    }
}
