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
public class UsersContract extends AbstractContract {
	public static final String TABLE = "users";
	public static final String CREATE_STRING = String
			.format("create table if not exists %s (%s integer primary key, %s text unique,"
					+ " %s text, %s text, %s text, %s text, %s text, %s text, %s text,"
					+ " %s text, %s text, %s text, %s text, %s text, %s text)",
					TABLE, BaseColumns._ID, Columns.USER_ID,
					Columns.USER_USERNAME, Columns.USER_PERMS,
					Columns.USER_TITLE_PRE, Columns.USER_FORENAME,
					Columns.USER_LASTNAME, Columns.USER_TITLE_POST,
					Columns.USER_EMAIL, Columns.USER_AVATAR_SMALL,
					Columns.USER_AVATAR_MEDIUM, Columns.USER_AVATAR_NORMAL,
					Columns.USER_PHONE, Columns.USER_HOMEPAGE,
					Columns.USER_PRIVADR);

	public static final String USERS_JOIN_COURSES = String
			.format("JOIN %s  ON %s = %s JOIN %s ON %s = %s",
					CoursesContract.TABLE_COURSE_USER,
					CoursesContract.Qualified.CourseUsers.COURSES_USERS_TABLE_COURSE_USER_USER_ID,
					Qualified.USERS_USER_ID,
					CoursesContract.TABLE_COURSES,
					CoursesContract.Qualified.Courses.COURSES_COURSE_ID,
					CoursesContract.Qualified.CourseUsers.COURSES_USERS_TABLE_COURSE_USER_COURSE_ID);

	// Content Provider
	public static final String PATH = "users";
	public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
			.appendPath(PATH).build();
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.studip.users";
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.studip.users";
	public static final String DEFAULT_SORT_ORDER = Qualified.USERS_USER_LASTNAME
			+ " ASC";

	public UsersContract() {
	}

	public static final class Columns implements BaseColumns {
		private Columns() {
		}

		public static final String USER_ID = "user_id";
		public static final String USER_USERNAME = "username";
		public static final String USER_PERMS = "perms";
		public static final String USER_TITLE_PRE = "title_pre";
		public static final String USER_FORENAME = "forename";
		public static final String USER_LASTNAME = "lastname";
		public static final String USER_TITLE_POST = "title_post";
		public static final String USER_EMAIL = "email";
		public static final String USER_AVATAR_SMALL = "avatar_small";
		public static final String USER_AVATAR_MEDIUM = "avatar_medium";
		public static final String USER_AVATAR_NORMAL = "avatar_normal";
		public static final String USER_PHONE = "phone";
		public static final String USER_HOMEPAGE = "homepage";
		public static final String USER_PRIVADR = "privadr";
	}

	public interface Qualified {
		public static final String USERS_ID = TABLE + "." + BaseColumns._ID;
		public static final String USERS_COUNT = TABLE + "."
				+ BaseColumns._COUNT;
		public static final String USERS_USER_ID = TABLE + "."
				+ Columns.USER_ID;
		public static final String USERS_USER_USERNAME = TABLE + "."
				+ Columns.USER_USERNAME;
		public static final String USERS_USER_PERMS = TABLE + "."
				+ Columns.USER_PERMS;
		public static final String USERS_USER_TITLE_PRE = TABLE + "."
				+ Columns.USER_TITLE_PRE;
		public static final String USERS_USER_FORENAME = TABLE + "."
				+ Columns.USER_FORENAME;
		public static final String USERS_USER_LASTNAME = TABLE + "."
				+ Columns.USER_LASTNAME;
		public static final String USERS_USER_TITLE_POST = TABLE + "."
				+ Columns.USER_TITLE_POST;
		public static final String USERS_USER_EMAIL = TABLE + "."
				+ Columns.USER_EMAIL;
		public static final String USERS_USER_AVATAR_SMALL = TABLE + "."
				+ Columns.USER_AVATAR_SMALL;
		public static final String USERS_USER_AVATAR_MEDIUM = TABLE + "."
				+ Columns.USER_AVATAR_MEDIUM;
		public static final String USERS_USER_AVATAR_NORMAL = TABLE + "."
				+ Columns.USER_AVATAR_NORMAL;
		public static final String USERS_USER_PHONE = TABLE + "."
				+ Columns.USER_PHONE;
		public static final String USERS_USER_HOMEPAGE = TABLE + "."
				+ Columns.USER_HOMEPAGE;
		public static final String USERS_USER_PRIVADR = TABLE + "."
				+ Columns.USER_PRIVADR;
	}
}
