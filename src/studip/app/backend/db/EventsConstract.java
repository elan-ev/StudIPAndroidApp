/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package studip.app.backend.db;

import android.provider.BaseColumns;

/**
 * @author joern
 * 
 */
public class EventsConstract extends AbstractContract {
	public static final String TABLE = "events";
	public static final String CREATE_STRING = String
			.format("create table if not exists %s (%s text primary key, %s text, %s text, %s text, %s text, %s text, %s text, %s text)",
					TABLE, Columns.EVENT_ID, Columns.EVENT_COURSE_ID,
					Columns.EVENT_START, Columns.EVENT_END,
					Columns.EVENT_TITLE, Columns.EVENT_DESCRIPTION,
					Columns.EVENT_CATEGORIES, Columns.EVENT_ROOM);

	private EventsConstract() {
	}

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
}
