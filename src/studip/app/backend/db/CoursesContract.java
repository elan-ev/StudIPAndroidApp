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
package studip.app.backend.db;

import android.provider.BaseColumns;

/**
 * @author joern
 * 
 */
public class CoursesContract extends AbstractContract {
	public static final String TABLE = "courses";
	public static final String CREATE_STRING = String
			.format("create table if not exists %s (%s text primary key, %s text, %s text, %s text, %s text, %s text, %s text, %s text, %s text)",
					TABLE, Columns.COURSE_ID, Columns.COURSE_START_TIME,
					Columns.COURSE_DURATION_TIME, Columns.COURSE_TITLE,
					Columns.COURSE_SUBTITLE, Columns.COURSE_DESCIPTION,
					Columns.COURSE_LOCATION, Columns.COURSE_TYPE,
					Columns.COURSE_SEMESERT_ID);

	private CoursesContract() {
	}

	public static final class Columns implements BaseColumns {
		private Columns() {
		}

		public static final String COURSE_ID = "course_id";
		public static final String COURSE_START_TIME = "start_time";
		public static final String COURSE_DURATION_TIME = "duration_time";
		public static final String COURSE_TITLE = "title";
		public static final String COURSE_SUBTITLE = "subtitle";
		public static final String COURSE_DESCIPTION = "description";
		public static final String COURSE_LOCATION = "location";
		public static final String COURSE_TYPE = "type";
		public static final String COURSE_SEMESERT_ID = "semester_id";
	}
}
