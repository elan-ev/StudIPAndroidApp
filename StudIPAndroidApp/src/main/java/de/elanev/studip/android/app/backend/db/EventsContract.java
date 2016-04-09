/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package de.elanev.studip.android.app.backend.db;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author joern
 * 
 */
public class EventsContract extends AbstractContract {
	public static final String TABLE = "events";
	public static final String CREATE_STRING = String.format(
			"create table if not exists %s (%s integer primary key, "
					+ "%s text unique, " + "%s text, " + "%s integer, "
					+ "%s integer, " + "%s text, " + "%s text, " + "%s text, "
					+ "%s text)", TABLE, BaseColumns._ID, Columns.EVENT_ID,
			Columns.EVENT_COURSE_ID, Columns.EVENT_START, Columns.EVENT_END,
			Columns.EVENT_TITLE, Columns.EVENT_DESCRIPTION,
			Columns.EVENT_CATEGORIES, Columns.EVENT_ROOM);

	private EventsContract() {
	}

	// ContentProvider
	public static final String PATH = "events";
	public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
			.appendPath(PATH).build();
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.studip.events";
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.studip.events";
	public static final String DEFAULT_SORT_ORDER = Qualified.EVENTS_EVENT_START
			+ " ASC";

	public static final class Columns implements BaseColumns {
		private Columns() {
		}

		public static final String EVENT_ID = "event_id";
		public static final String EVENT_COURSE_ID = "course_id";
		public static final String EVENT_START = "start";
		public static final String EVENT_END = "end";
		public static final String EVENT_TITLE = "title";
		public static final String EVENT_DESCRIPTION = "description";
		public static final String EVENT_CATEGORIES = "categories";
		public static final String EVENT_ROOM = "room";
	}

	public interface Qualified {
		public static final String EVENTS_ID = TABLE + "." + BaseColumns._ID;
		public static final String EVENTS_COUNT = TABLE + "."
				+ BaseColumns._COUNT;
		public static final String EVENTS_EVENT_ID = TABLE + "."
				+ Columns.EVENT_ID;
		public static final String EVENTS_EVENT_COURSE_ID = TABLE + "."
				+ Columns.EVENT_COURSE_ID;
		public static final String EVENTS_EVENT_START = TABLE + "."
				+ Columns.EVENT_START;
		public static final String EVENTS_EVENT_END = TABLE + "."
				+ Columns.EVENT_END;
		public static final String EVENTS_EVENT_TITLE = TABLE + "."
				+ Columns.EVENT_END;
		public static final String EVENTS_EVENT_DESCRIPTION = TABLE + "."
				+ Columns.EVENT_DESCRIPTION;
		public static final String EVENTS_EVENT_CATEGORIES = TABLE + "."
				+ Columns.EVENT_CATEGORIES;
		public static final String EVENTS_EVENT_ROOM = TABLE + "."
				+ Columns.EVENT_ROOM;
	}
}
