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
	public static final String TABLE_DOCUMENTS = "documents";
	public static final String TABLE_DOCUMENT_FOLDERS = "document_folders";

	public static final String CREATE_STRING = String
			.format("create table if not exists %s (%s integer primary key, %s text unique, "
					+ "%s text, %s text, %s text, %s text, %s integer,"
					+ " %s text, %s integer, %s integer, %s text, %s text, %s boolean, %s text, %s integer,"
					+ "FOREIGN KEY(%s) REFERENCES %s(%s))", TABLE_DOCUMENTS,
					Columns.Documents._ID, Columns.Documents.DOCUMENT_ID,
					Columns.Documents.DOCUMENT_USER_ID,
					Columns.Documents.DOCUMENT_NAME,
					Columns.Documents.DOCUMENT_DESCRIPTION,
					Columns.Documents.DOCUMENT_MKDATE,
					Columns.Documents.DOCUMENT_CHDATE,
					Columns.Documents.DOCUMENT_FILENAME,
					Columns.Documents.DOCUMENT_FILESIZE,
					Columns.Documents.DOCUMENT_DOWNLOADS,
					Columns.Documents.DOCUMENT_MIME_TYPE,
					Columns.Documents.DOCUMENT_ICON,
					Columns.Documents.DOCUMENT_PROTECTED,
					Columns.Documents.DOCUMENT_COURSE_ID,
					Columns.Documents.DOCUMENT_FOLDER_ID,
					Columns.Documents.DOCUMENT_FOLDER_ID,
					TABLE_DOCUMENT_FOLDERS, Columns.DocumentFolders._ID);
	/*
	 * document folders table
	 */
	public static final String CREATE_DOCUMENT_FOLDER_STRING = String
			.format("create table if not exists %s (%s integer primary key, %s text unique, "
					+ "%s text, %s text, %s text, %s integer, %s integer, %s text)",
					TABLE_DOCUMENT_FOLDERS, Columns.DocumentFolders._ID,
					Columns.DocumentFolders.FOLDER_ID,
					Columns.DocumentFolders.FOLDER_USER_ID,
					Columns.DocumentFolders.FOLDER_NAME,
					Columns.DocumentFolders.FOLDER_DESCRIPTION,
					Columns.DocumentFolders.FOLDER_MKDATE,
					Columns.DocumentFolders.FOLDER_CHDATE,
					Columns.DocumentFolders.FOLDER_PERMISSIONS);

	/*
	 * joins
	 */
	public static final String DOCUMENTS_JOIN_USERS_JOIN_COURSES = String
			.format("%s INNER JOIN %s on %s = %s INNER JOIN %s on %s = %s ",
					TABLE_DOCUMENTS, UsersContract.TABLE,
					Qualified.Documents.DOCUMENTS_DOCUMENT_USER_ID,
					UsersContract.Qualified.USERS_USER_ID,
					CoursesContract.TABLE_COURSES,
					Qualified.Documents.DOCUMENTS_DOCUMENT_COURSE_ID,
					CoursesContract.Qualified.Courses.COURSES_COURSE_ID);

	public static final String DOCUMENTS_JOIN_USERS_JOIN_COURSES_JOIN_FOLDERS = String
			.format(DOCUMENTS_JOIN_USERS_JOIN_COURSES
					+ " INNER JOIN %s on %s = %s ", TABLE_DOCUMENT_FOLDERS,
					Qualified.DocumentFolders.DOCUMENTS_FOLDERS_ID,
					Qualified.Documents.DOCUMENTS_DOCUMENT_FOLDER_ID);

	/*
	 * content provider
	 */
	public static final String PATH = "documents";
	public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
			.appendPath(PATH).build();
	public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.studip.documents";
	public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.studip.documents";
	public static final String DEFAULT_SORT_ORDER = Qualified.Documents.DOCUMENTS_DOCUMENT_CHDATE
			+ " ASC";

	private DocumentsContract() {
	}

	public static final class Columns {
		private Columns() {
		}

		/*
		 * documents
		 */
		public interface Documents extends BaseColumns {
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
			public static final String DOCUMENT_FOLDER_ID = "folder_id";
		}

		/*
		 * document folders
		 */
		public interface DocumentFolders extends BaseColumns {
			public static final String FOLDER_ID = "folder_id";
			public static final String FOLDER_USER_ID = "folder_user_id";
			public static final String FOLDER_NAME = "folder_name";
			public static final String FOLDER_DESCRIPTION = "folder_description";
			public static final String FOLDER_MKDATE = "folder_mkdate";
			public static final String FOLDER_CHDATE = "folder_chdate";
			public static final String FOLDER_PERMISSIONS = "folder_permissions";
		}

	}

	public static class Qualified {
		/*
		 * documents
		 */
		public interface Documents {
			public static final String DOCUMENTS_ID = TABLE_DOCUMENTS + "."
					+ Columns.Documents._ID;
			public static final String DOCUMENTS_COUNT = TABLE_DOCUMENTS + "."
					+ Columns.Documents._COUNT;
			public static final String DOCUMENTS_DOCUMENT_ID = TABLE_DOCUMENTS
					+ "." + Columns.Documents.DOCUMENT_ID;
			public static final String DOCUMENTS_DOCUMENT_USER_ID = TABLE_DOCUMENTS
					+ "." + Columns.Documents.DOCUMENT_USER_ID;
			public static final String DOCUMENTS_DOCUMENT_NAME = TABLE_DOCUMENTS
					+ "." + Columns.Documents.DOCUMENT_NAME;
			public static final String DOCUMENTS_DOCUMENT_DESCRIPTION = TABLE_DOCUMENTS
					+ "." + Columns.Documents.DOCUMENT_DESCRIPTION;
			public static final String DOCUMENTS_DOCUMENT_MKDATE = TABLE_DOCUMENTS
					+ "." + Columns.Documents.DOCUMENT_MKDATE;
			public static final String DOCUMENTS_DOCUMENT_CHDATE = TABLE_DOCUMENTS
					+ "." + Columns.Documents.DOCUMENT_CHDATE;
			public static final String DOCUMENTS_DOCUMENT_FILENAME = TABLE_DOCUMENTS
					+ "." + Columns.Documents.DOCUMENT_FILENAME;
			public static final String DOCUMENTS_DOCUMENT_FILESIZE = TABLE_DOCUMENTS
					+ "." + Columns.Documents.DOCUMENT_FILESIZE;
			public static final String DOCUMENTS_DOCUMENT_DOWNLOADS = TABLE_DOCUMENTS
					+ "." + Columns.Documents.DOCUMENT_DOWNLOADS;
			public static final String DOCUMENTS_DOCUMENT_MIME_TYPE = TABLE_DOCUMENTS
					+ "." + Columns.Documents.DOCUMENT_MIME_TYPE;
			public static final String DOCUMENTS_DOCUMENT_ICON = TABLE_DOCUMENTS
					+ "." + Columns.Documents.DOCUMENT_ICON;
			public static final String DOCUMENTS_DOCUMENT_PROTECTED = TABLE_DOCUMENTS
					+ "." + Columns.Documents.DOCUMENT_PROTECTED;
			public static final String DOCUMENTS_DOCUMENT_COURSE_ID = TABLE_DOCUMENTS
					+ "." + Columns.Documents.DOCUMENT_COURSE_ID;
			public static final String DOCUMENTS_DOCUMENT_FOLDER_ID = TABLE_DOCUMENTS
					+ "." + Columns.Documents.DOCUMENT_FOLDER_ID;

		}

		/*
		 * document folders
		 */
		public interface DocumentFolders {
			public static final String DOCUMENTS_FOLDERS_ID = TABLE_DOCUMENT_FOLDERS
					+ "." + Columns.DocumentFolders._ID;
			public static final String DOCUMENTS_FOLDERS_COUNT = TABLE_DOCUMENT_FOLDERS
					+ "." + Columns.DocumentFolders._COUNT;
			public static final String DOCUMENTS_FOLDERS_FOLDER_ID = TABLE_DOCUMENT_FOLDERS
					+ "." + Columns.DocumentFolders.FOLDER_ID;
			public static final String DOCUMENTS_FOLDERS_FOLDER_USER_ID = TABLE_DOCUMENT_FOLDERS
					+ "." + Columns.DocumentFolders.FOLDER_USER_ID;
			public static final String DOCUMENTS_FOLDERS_FOLDER_NAME = TABLE_DOCUMENT_FOLDERS
					+ "." + Columns.DocumentFolders.FOLDER_NAME;
			public static final String DOCUMENTS_FOLDERS_FOLDER_DESCRIPTION = TABLE_DOCUMENT_FOLDERS
					+ "." + Columns.DocumentFolders.FOLDER_DESCRIPTION;
			public static final String DOCUMENTS_FOLDERS_FOLDER_MKDATE = TABLE_DOCUMENT_FOLDERS
					+ "." + Columns.DocumentFolders.FOLDER_MKDATE;
			public static final String DOCUMENTS_FOLDERS_FOLDER_CHDATE = TABLE_DOCUMENT_FOLDERS
					+ "." + Columns.DocumentFolders.FOLDER_CHDATE;
			public static final String DOCUMENTS_FOLDERS_FOLDER_PERMISSIONS = TABLE_DOCUMENT_FOLDERS
					+ "." + Columns.DocumentFolders.FOLDER_PERMISSIONS;
		}
	}

}
