/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.db;

import android.net.Uri;
import android.provider.BaseColumns;

public class DocumentsContract extends AbstractContract {

	/*
	 * documents table
	 */
	public static final String TABLE = "documents";
	public static final String CREATE_STRING = String
			.format("create table if not exists %s (%s integer primary key, %s text unique, "
					+ "%s text, %s text, %s text, %s text, %s integer,"
					+ " %s text, %s integer, %s integer, %s text, %s text, %s boolean, %s text)",
					TABLE, BaseColumns._ID, Columns.DOCUMENT_ID,
					Columns.DOCUMENT_USER_ID, Columns.DOCUMENT_NAME,
					Columns.DOCUMENT_DESCRIPTION, Columns.DOCUMENT_MKDATE,
					Columns.DOCUMENT_CHDATE, Columns.DOCUMENT_FILENAME,
					Columns.DOCUMENT_FILESIZE, Columns.DOCUMENT_DOWNLOADS,
					Columns.DOCUMENT_MIME_TYPE, Columns.DOCUMENT_ICON,
					Columns.DOCUMENT_PROTECTED, Columns.DOCUMENT_COURSE_ID);
	/*
	 * document folders table
	 */
	public static final String DOCUMENT_FOLDER_TABLE = "file_folders";
	public static final String CREATE_DOCUMENT_FOLDER_STRING = String
			.format("create table if not exists %s (%s integer primary key, %s text unique, "
					+ "%s text, %s text, %s text, %s integer, %s integer, %s text)",
					TABLE, BaseColumns._ID, Columns.FOLDER_ID,
					Columns.FOLDER_USER_ID, Columns.FOLDER_NAME,
					Columns.FOLDER_DESCRIPTION, Columns.FOLDER_MKDATE,
					Columns.FOLDER_CHDATE, Columns.FOLDER_PERMISSIONS);

	/*
	 * joins
	 */
	public static final String DOCUMENTS_JOIN_USERS_JOIN_COURSES = String
			.format("%s INNER JOIN %s on %s = %s INNER JOIN %s on %s = %s ",
					TABLE, UsersContract.TABLE,
					Qualified.DOCUMENTS_DOCUMENT_USER_ID,
					UsersContract.Qualified.USERS_USER_ID,
					CoursesContract.TABLE_COURSES,
					Qualified.DOCUMENTS_DOCUMENT_COURSE_ID,
					CoursesContract.Qualified.Courses.COURSES_COURSE_ID);

	/*
	 * content provider
	 */
	public static final String PATH = "documents";
	public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
			.appendPath(PATH).build();
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.studip.documents";
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.studip.documents";
	public static final String DEFAULT_SORT_ORDER = Qualified.DOCUMENTS_DOCUMENT_CHDATE
			+ " ASC";

	private DocumentsContract() {
	}

	public static final class Columns implements BaseColumns {
		private Columns() {
		}

		/*
		 * documents
		 */
		public static final String DOCUMENT_ID = "document_id";
		public static final String DOCUMENT_USER_ID = "user_id";
		public static final String DOCUMENT_NAME = "name";
		public static final String DOCUMENT_DESCRIPTION = "description";
		public static final String DOCUMENT_MKDATE = "mkdate";
		public static final String DOCUMENT_CHDATE = "chdate";
		public static final String DOCUMENT_FILENAME = "filename";
		public static final String DOCUMENT_FILESIZE = "filesize";
		public static final String DOCUMENT_DOWNLOADS = "downloads";
		public static final String DOCUMENT_MIME_TYPE = "mime_type";
		public static final String DOCUMENT_ICON = "icon";
		public static final String DOCUMENT_PROTECTED = "protected";
		public static final String DOCUMENT_COURSE_ID = "course_id";

		/*
		 * document folders
		 */
		public static final String FOLDER_ID = "folder_id";
		public static final String FOLDER_USER_ID = "user_id";
		public static final String FOLDER_NAME = "name";
		public static final String FOLDER_DESCRIPTION = "description";
		public static final String FOLDER_MKDATE = "mkdate";
		public static final String FOLDER_CHDATE = "chdate";
		public static final String FOLDER_PERMISSIONS = "permissions"; // json
																		// string
	}

	public interface Qualified {
		/*
		 * documents
		 */
		public static final String DOCUMENTS_ID = TABLE + "." + Columns._ID;
		public static final String DOCUMENTS_COUNT = TABLE + "."
				+ Columns._COUNT;
		public static final String DOCUMENTS_DOCUMENT_ID = TABLE + "."
				+ Columns.DOCUMENT_ID;
		public static final String DOCUMENTS_DOCUMENT_USER_ID = TABLE + "."
				+ Columns.DOCUMENT_USER_ID;
		public static final String DOCUMENTS_DOCUMENT_NAME = TABLE + "."
				+ Columns.DOCUMENT_NAME;
		public static final String DOCUMENTS_DOCUMENT_DESCRIPTION = TABLE + "."
				+ Columns.DOCUMENT_DESCRIPTION;
		public static final String DOCUMENTS_DOCUMENT_MKDATE = TABLE + "."
				+ Columns.DOCUMENT_MKDATE;
		public static final String DOCUMENTS_DOCUMENT_CHDATE = TABLE + "."
				+ Columns.DOCUMENT_CHDATE;
		public static final String DOCUMENTS_DOCUMENT_FILENAME = TABLE + "."
				+ Columns.DOCUMENT_FILENAME;
		public static final String DOCUMENTS_DOCUMENT_FILESIZE = TABLE + "."
				+ Columns.DOCUMENT_FILESIZE;
		public static final String DOCUMENTS_DOCUMENT_DOWNLOADS = TABLE + "."
				+ Columns.DOCUMENT_DOWNLOADS;
		public static final String DOCUMENTS_DOCUMENT_MIME_TYPE = TABLE + "."
				+ Columns.DOCUMENT_MIME_TYPE;
		public static final String DOCUMENTS_DOCUMENT_ICON = TABLE + "."
				+ Columns.DOCUMENT_ICON;
		public static final String DOCUMENTS_DOCUMENT_PROTECTED = TABLE + "."
				+ Columns.DOCUMENT_PROTECTED;
		public static final String DOCUMENTS_DOCUMENT_COURSE_ID = TABLE + "."
				+ Columns.DOCUMENT_COURSE_ID;

		/*
		 * document folders
		 */
		public static final String DOCUMENTS_FOLDERS_ID = DOCUMENT_FOLDER_TABLE
				+ "." + BaseColumns._ID;
		public static final String DOCUMENTS_FOLDERS_COUNT = DOCUMENT_FOLDER_TABLE
				+ "." + BaseColumns._COUNT;
		public static final String DOCUMENTS_FOLDERS_FOLDER_ID = DOCUMENT_FOLDER_TABLE
				+ "." + Columns.FOLDER_ID;
		public static final String DOCUMENTS_FOLDERS_FOLDER_USER_ID = DOCUMENT_FOLDER_TABLE
				+ "." + Columns.FOLDER_USER_ID;
		public static final String DOCUMENTS_FOLDERS_FOLDER_NAME = DOCUMENT_FOLDER_TABLE
				+ "." + Columns.FOLDER_NAME;
		public static final String DOCUMENTS_FOLDERS_FOLDER_DESCRIPTION = DOCUMENT_FOLDER_TABLE
				+ "." + Columns.FOLDER_DESCRIPTION;
		public static final String DOCUMENTS_FOLDERS_FOLDER_MKDATE = DOCUMENT_FOLDER_TABLE
				+ "." + Columns.FOLDER_MKDATE;
		public static final String DOCUMENTS_FOLDERS_FOLDER_CHDATE = DOCUMENT_FOLDER_TABLE
				+ "." + Columns.FOLDER_CHDATE;
		public static final String DOCUMENTS_FOLDERS_FOLDER_PERMISSIONS = DOCUMENT_FOLDER_TABLE
				+ "." + Columns.FOLDER_PERMISSIONS;
	}

}
