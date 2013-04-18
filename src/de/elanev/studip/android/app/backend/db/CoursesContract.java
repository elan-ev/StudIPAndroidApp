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

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author joern
 * 
 */
public class CoursesContract extends AbstractContract {
	public static final String TABLE = "courses";
	public static final String COURSE_USER_TABLE = "course_users";

	public static final String CREATE_STRING = String
			.format("create table if not exists %s (%s integer primary key, %s text unique, "
					+ "%s date, %s date, %s double, %s text, %s text, %s text, %s text, "
					+ "%s text, %s text, %s text, %s text);", TABLE,
					BaseColumns._ID, Columns.COURSE_ID,
					Columns.COURSE_START_TIME, Columns.COURSE_DURATION_TIME,
					Columns.COURSE_NUMBER, Columns.COURSE_TITLE,
					Columns.COURSE_SUBTITLE, Columns.COURSE_DESCIPTION,
					Columns.COURSE_LOCATION, Columns.COURSE_TYPE,
					Columns.COURSE_SEMESERT_ID, Columns.COURSE_MODULES,
					Columns.COURSE_COLORS);

	public static final String CREATE_COURSE_USER_STRING = String
			.format("create table if not exists %s (_id integer primary key, %s text, %s text, %s integer,"
					+ "foreign key(%s) references %s(%s), foreign key(%s) references %s(%s));",
					COURSE_USER_TABLE, Columns.COURSE_USER_USER_ID,
					Columns.COURSE_USER_COURSE_ID,
					Columns.COURSE_USER_USER_ROLE, Columns.COURSE_USER_USER_ID,
					UsersContract.TABLE, UsersContract.Columns.USER_ID,
					Columns.COURSE_USER_COURSE_ID, TABLE, Columns.COURSE_ID);

	public static final String COURSES_JOIN_USERS_SEMESTERS = String.format(
			"%s INNER JOIN %s on %s = %s " + "INNER JOIN %s on %s = %s "
					+ "INNER JOIN %s on %s = %s ", TABLE, COURSE_USER_TABLE,
			Qualified.COURSES_USERS_TABLE_COURSE_USER_COURSE_ID,
			Qualified.COURSES_COURSE_ID, UsersContract.TABLE,
			Qualified.COURSES_USERS_TABLE_COURSE_USER_USER_ID,
			UsersContract.Qualified.USERS_USER_ID, SemestersContract.TABLE,
			Qualified.COURSES_COURSE_SEMESERT_ID,
			SemestersContract.Qualified.SEMESTERS_SEMESTER_ID);

	public static final String COURSES_JOIN_SEMESTERS = String.format(
			"%s INNER JOIN %s on %s = %s ", TABLE, SemestersContract.TABLE,
			Qualified.COURSES_COURSE_SEMESERT_ID,
			SemestersContract.Qualified.SEMESTERS_SEMESTER_ID);

	// Content Provider
	public static final String PATH = "courses";
	public static final String COURSES_USERS_PATH = "users";
	public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
			.appendPath(PATH).build();
	public static final Uri COURSES_USERS_CONTENT_URI = CONTENT_URI.buildUpon()
			.appendPath(COURSES_USERS_PATH).build();
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.studip.courses";
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.studip.courses";
	public static final String DEFAULT_SORT_ORDER = Qualified.COURSES_COURSE_TITLE
			+ " ASC";

	// Role fields
	public static final int USER_ROLE_TEACHER = 1000;
	public static final int USER_ROLE_TUTOR = 1001;
	public static final int USER_ROLE_STUDENT = 1002;

	private CoursesContract() {
	}

	public static final class Columns implements BaseColumns {
		private Columns() {
		}

		public static final String COURSE_ID = "course_id";
		public static final String COURSE_START_TIME = "start_time";
		public static final String COURSE_DURATION_TIME = "duration_time";
		public static final String COURSE_NUMBER = "number";
		public static final String COURSE_TITLE = "title";
		public static final String COURSE_SUBTITLE = "subtitle";
		public static final String COURSE_DESCIPTION = "description";
		public static final String COURSE_LOCATION = "location";
		public static final String COURSE_TYPE = "type";
		public static final String COURSE_SEMESERT_ID = "semester_id";
		public static final String COURSE_MODULES = "modules";
		public static final String COURSE_COLORS = "color";

		public static final String COURSE_USER_USER_ID = "user_id";
		public static final String COURSE_USER_COURSE_ID = "course_id";
		public static final String COURSE_USER_USER_ROLE = "user_role";
	}

	public interface Qualified {
		public static final String COURSES_ID = TABLE + "." + BaseColumns._ID;
		public static final String COURSES_COUNT = TABLE + "."
				+ BaseColumns._COUNT;
		public static final String COURSES_COURSE_ID = TABLE + "."
				+ Columns.COURSE_ID;
		public static final String COURSES_COURSE_START_TIME = TABLE + "."
				+ Columns.COURSE_START_TIME;
		public static final String COURSES_COURSE_DURATION_TIME = TABLE + "."
				+ Columns.COURSE_DURATION_TIME;
		public static final String COURSES_COURSE_NUMBER = TABLE + "."
				+ Columns.COURSE_NUMBER;
		public static final String COURSES_COURSE_TITLE = TABLE + "."
				+ Columns.COURSE_TITLE;
		public static final String COURSES_COURSE_SUBTITLE = TABLE + "."
				+ Columns.COURSE_SUBTITLE;
		public static final String COURSES_COURSE_DESCIPTION = TABLE + "."
				+ Columns.COURSE_DESCIPTION;
		public static final String COURSES_COURSE_LOCATION = TABLE + "."
				+ Columns.COURSE_LOCATION;
		public static final String COURSES_COURSE_TYPE = TABLE + "."
				+ Columns.COURSE_TYPE;
		public static final String COURSES_COURSE_SEMESERT_ID = TABLE + "."
				+ Columns.COURSE_SEMESERT_ID;
		public static final String COURSES_COURSE_MODULES = TABLE + "."
				+ Columns.COURSE_MODULES;
		public static final String COURSES_COURSE_COLORS = TABLE + "."
				+ Columns.COURSE_COLORS;

		public static final String COURSES_USERS_TABLE_COURSE_USER_USER_ID = COURSE_USER_TABLE
				+ "." + Columns.COURSE_USER_USER_ID;
		public static final String COURSES_USERS_TABLE_COURSE_USER_COURSE_ID = COURSE_USER_TABLE
				+ "." + Columns.COURSE_USER_COURSE_ID;
		public static final String COURSES_USERS_TABLE_COURSE_USER_USER_ROLE = COURSE_USER_TABLE
				+ "." + Columns.COURSE_USER_USER_ROLE;

	}

}
