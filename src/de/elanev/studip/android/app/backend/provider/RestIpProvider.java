/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.provider;

import java.util.ArrayList;
import java.util.Map;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDoneException;
import android.database.sqlite.SQLiteStatement;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.db.AbstractContract;
import de.elanev.studip.android.app.backend.db.CoursesContract;
import de.elanev.studip.android.app.backend.db.DatabaseHandler;
import de.elanev.studip.android.app.backend.db.DocumentsContract;
import de.elanev.studip.android.app.backend.db.EventsContract;
import de.elanev.studip.android.app.backend.db.MessagesContract;
import de.elanev.studip.android.app.backend.db.NewsContract;
import de.elanev.studip.android.app.backend.db.SemestersContract;
import de.elanev.studip.android.app.backend.db.UsersContract;
import de.elanev.studip.android.app.backend.net.api.ApiEndpoints;

/**
 * @author joern
 * 
 */
public class RestIpProvider extends ContentProvider {
	public static final String TAG = RestIpProvider.class.getSimpleName();

	private static final UriMatcher sUriMatcher = buildUriMatcher();

	private static final int NEWS = 100;
	private static final int NEWS_ID = 101;
	private static final int NEWS_GLOBAL = 102;
	private static final int NEWS_COURSES = 103;

	private static final int COURSES = 200;
	private static final int COURSES_ID = 201;
	private static final int COURSES_ID_EVENTS = 202;
	private static final int COURSES_USERS = 203;

	private static final int USERS = 300;
	private static final int USERS_ID = 301;
	private static final int USERS_COURSE_ID = 302;

	private static final int EVENTS = 400;
	private static final int EVENTS_ID = 401;

	private static final int SEMESTERS = 500;
	private static final int SEMESTERS_ID = 501;

	private static final int DOCUMENTS = 600;
	private static final int DOCUMENTS_ID = 601;

	private static final int MESSAGES_IN = 700;
	private static final int MESSAGES_OUT = 701;
	private static final int MESSAGES_FOLDERS = 702;
	private static final int MESSAGES_IN_FOLDERS = 703;
	private static final int MESSAGES_OUT_FOLDERS = 704;
	private static final int MESSAGES_FOLDER_MESSAGES = 705;
	private static final int MESSAGES_IN_FOLDER_MESSAGES = 706;
	private static final int MESSAGES_OUT_FOLDER_MESSAGES = 707;
	private static final int MESSAGES_IN_ID = 708;
	private static final int MESSAGES_OUT_ID = 709;
	private static final int MESSAGES_ID = 710;
	private static final int MESSAGES_STRING_ID = 711;

	private static UriMatcher buildUriMatcher() {
		final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
		final String authority = AbstractContract.CONTENT_AUTHORITY;

		matcher.addURI(authority, "news", NEWS);
		matcher.addURI(authority, "news/global", NEWS_GLOBAL);
		matcher.addURI(authority, "news/courses", NEWS_COURSES);
		matcher.addURI(authority, "news/#", NEWS_ID);

		matcher.addURI(authority, "courses", COURSES);
		matcher.addURI(authority, "courses/users", COURSES_USERS);
		matcher.addURI(authority, "courses/events/*", COURSES_ID_EVENTS);
		matcher.addURI(authority, "courses/#", COURSES_ID);

		matcher.addURI(authority, "users", USERS);
		matcher.addURI(authority, "users/course/*", USERS_COURSE_ID);
		matcher.addURI(authority, "users/#", USERS_ID);

		matcher.addURI(authority, "events", EVENTS);
		matcher.addURI(authority, "events/#", EVENTS_ID);

		matcher.addURI(authority, "semesters", SEMESTERS);
		matcher.addURI(authority, "semesters/#", SEMESTERS_ID);

		matcher.addURI(authority, "documents", DOCUMENTS);
		matcher.addURI(authority, "documents/#", DOCUMENTS_ID);

		matcher.addURI(authority, "messages/in", MESSAGES_IN);
		matcher.addURI(authority, "messages/out", MESSAGES_OUT);
		matcher.addURI(authority, "messages/folders", MESSAGES_FOLDERS);
		matcher.addURI(authority, "messages/folders/#",
				MESSAGES_FOLDER_MESSAGES);
		matcher.addURI(authority, "messages/in/folders", MESSAGES_IN_FOLDERS);
		matcher.addURI(authority, "messages/out/folders", MESSAGES_OUT_FOLDERS);
		matcher.addURI(authority, "messages/in/folders/#",
				MESSAGES_IN_FOLDER_MESSAGES);
		matcher.addURI(authority, "messages/out/folders/#",
				MESSAGES_OUT_FOLDER_MESSAGES);
		matcher.addURI(authority, "messages/in/#", MESSAGES_IN_ID);
		matcher.addURI(authority, "messages/out/#", MESSAGES_OUT_ID);
		matcher.addURI(authority, "messages/#", MESSAGES_ID);
		matcher.addURI(authority, "messages/*", MESSAGES_STRING_ID);
		return matcher;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType(Uri uri) {
		final int match = sUriMatcher.match(uri);
		switch (match) {
		case NEWS:
			return NewsContract.CONTENT_TYPE;
		case NEWS_GLOBAL:
			return NewsContract.CONTENT_TYPE;
		case NEWS_COURSES:
			return NewsContract.CONTENT_TYPE;
		case NEWS_ID:
			return NewsContract.CONTENT_ITEM_TYPE;
		case COURSES:
			return CoursesContract.CONTENT_TYPE;
		case COURSES_ID:
			return CoursesContract.CONTENT_ITEM_TYPE;
		case USERS:
			return UsersContract.CONTENT_TYPE;
		case USERS_COURSE_ID:
			return UsersContract.CONTENT_TYPE;
		case USERS_ID:
			return UsersContract.CONTENT_ITEM_TYPE;
		case EVENTS:
			return EventsContract.CONTENT_TYPE;
		case EVENTS_ID:
			return EventsContract.CONTENT_ITEM_TYPE;
		case SEMESTERS:
			return SemestersContract.CONTENT_TYPE;
		case SEMESTERS_ID:
			return SemestersContract.CONTENT_ITEM_TYPE;
		case DOCUMENTS:
			return DocumentsContract.CONTENT_TYPE;
		case DOCUMENTS_ID:
			return DocumentsContract.CONTENT_ITEM_TYPE;
		case MESSAGES_IN:
			return MessagesContract.CONTENT_TYPE;
		case MESSAGES_OUT:
			return MessagesContract.CONTENT_TYPE;
		case MESSAGES_FOLDERS:
			return MessagesContract.FOLDER_CONTENT_TYPE;
		case MESSAGES_IN_FOLDERS:
			return MessagesContract.FOLDER_CONTENT_TYPE;
		case MESSAGES_OUT_FOLDERS:
			return MessagesContract.FOLDER_CONTENT_TYPE;
		case MESSAGES_IN_FOLDER_MESSAGES:
			return MessagesContract.CONTENT_TYPE;
		case MESSAGES_OUT_FOLDER_MESSAGES:
			return MessagesContract.CONTENT_TYPE;
		case MESSAGES_IN_ID:
			return MessagesContract.CONTENT_ITEM_TYPE;
		case MESSAGES_OUT_ID:
			return MessagesContract.CONTENT_ITEM_TYPE;
		case MESSAGES_ID:
			return MessagesContract.CONTENT_ITEM_TYPE;
		case MESSAGES_STRING_ID:
			return MessagesContract.CONTENT_ITEM_TYPE;
		default:
			throw new UnsupportedOperationException("Unknown mime type: " + uri);
		}
	}

	private void deleteDatabase() {
		Context context = getContext();
		DatabaseHandler.getInstance(context).close();
		DatabaseHandler.getInstance(context).deleteDatabase(context);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#delete(android.net.Uri,
	 * java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = DatabaseHandler.getInstance(getContext())
				.getWritableDatabase();
		int retVal = -1;
		// Deleting the whole db
		if (uri == AbstractContract.BASE_CONTENT_URI) {
			deleteDatabase();
			getContext().getContentResolver().notifyChange(uri, null, false);
			return 1;
		}
		final int match = sUriMatcher.match(uri);
		switch (match) {
		case NEWS: {
			break;
		}
		case USERS: {
			break;
		}
		case EVENTS: {
			break;
		}
		case COURSES: {
			break;
		}
		case SEMESTERS: {
			break;
		}
		case DOCUMENTS: {
			break;
		}
		case MESSAGES_ID: {
			long messageId = ContentUris.parseId(uri);
			retVal = db.delete(MessagesContract.TABLE_MESSAGES,
					MessagesContract.Columns.Messages._ID
							+ " = "
							+ messageId
							+ (!TextUtils.isEmpty(selection) ? " AND ("
									+ selection + ")" : ""), selectionArgs);
			break;
		}
		case MESSAGES_STRING_ID: {
			String messageId = uri.getLastPathSegment();
			retVal = db.delete(MessagesContract.TABLE_MESSAGES,
					MessagesContract.Columns.Messages.MESSAGE_ID
							+ " = "
							+ '"'
							+ messageId
							+ '"'
							+ (!TextUtils.isEmpty(selection) ? " AND ("
									+ selection + ")" : ""), selectionArgs);
			break;
		}
		case MESSAGES_IN_FOLDERS: {
			break;
		}

		default: {
			throw new UnsupportedOperationException("Unsupported delete uri: "
					+ uri);
		}
		}

		getContext().getContentResolver().notifyChange(uri, null);
		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#insert(android.net.Uri,
	 * android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = DatabaseHandler.getInstance(getContext())
				.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		switch (match) {
		case NEWS: {
			long rowId = db.insertWithOnConflict(NewsContract.TABLE, null,
					values, SQLiteDatabase.CONFLICT_IGNORE);
			getContext().getContentResolver().notifyChange(uri, null);
			return ContentUris.withAppendedId(NewsContract.CONTENT_URI, rowId);
		}
		case USERS: {
			long rowId = db.insertWithOnConflict(UsersContract.TABLE, null,
					values, SQLiteDatabase.CONFLICT_IGNORE);
			getContext().getContentResolver().notifyChange(uri, null);
			return ContentUris.withAppendedId(UsersContract.CONTENT_URI, rowId);
		}
		case COURSES_USERS: {
			long rowId = db.insertWithOnConflict(
					CoursesContract.TABLE_COURSE_USER, null, values,
					SQLiteDatabase.CONFLICT_IGNORE);
			getContext().getContentResolver().notifyChange(uri, null);
			return ContentUris.withAppendedId(
					CoursesContract.COURSES_USERS_CONTENT_URI, rowId);
		}
		case EVENTS: {
			long rowId = db.insertWithOnConflict(EventsContract.TABLE, null,
					values, SQLiteDatabase.CONFLICT_IGNORE);
			getContext().getContentResolver().notifyChange(uri, null);
			return ContentUris
					.withAppendedId(EventsContract.CONTENT_URI, rowId);
		}
		case COURSES: {
			long rowId = db.insertWithOnConflict(CoursesContract.TABLE_COURSES,
					null, values, SQLiteDatabase.CONFLICT_IGNORE);
			getContext().getContentResolver().notifyChange(uri, null);
			return ContentUris.withAppendedId(CoursesContract.CONTENT_URI,
					rowId);
		}
		case SEMESTERS: {
			long rowId = db.insertWithOnConflict(SemestersContract.TABLE, null,
					values, SQLiteDatabase.CONFLICT_IGNORE);
			getContext().getContentResolver().notifyChange(uri, null);
			return ContentUris.withAppendedId(SemestersContract.CONTENT_URI,
					rowId);
		}
		case DOCUMENTS: {
			long rowId = db.insertWithOnConflict(DocumentsContract.TABLE, null,
					values, SQLiteDatabase.CONFLICT_IGNORE);
			getContext().getContentResolver().notifyChange(uri, null);
			return ContentUris.withAppendedId(DocumentsContract.CONTENT_URI,
					rowId);
		}
		case MESSAGES_IN: {
			long rowId = insertIgnoringConflict(db,
					MessagesContract.TABLE_MESSAGES,
					MessagesContract.Columns.Messages._ID, values, false);
			getContext().getContentResolver().notifyChange(uri, null);
			return ContentUris.withAppendedId(
					MessagesContract.CONTENT_URI_MESSAGES, rowId);
		}
		case MESSAGES_FOLDERS: {
			long rowId = insertIgnoringConflict(db,
					MessagesContract.TABLE_MESSAGE_FOLDERS,
					MessagesContract.Columns.MessageFolders._ID, values, false);
			getContext().getContentResolver().notifyChange(uri, null);
			return ContentUris.withAppendedId(
					MessagesContract.CONTENT_URI_MESSAGE_FOLDERS, rowId);
		}
		default: {
			throw new UnsupportedOperationException("Unsupported insert uri: "
					+ uri);
		}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#query(android.net.Uri,
	 * java.lang.String[], java.lang.String, java.lang.String[],
	 * java.lang.String)
	 */

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {

		SQLiteDatabase db = DatabaseHandler.getInstance(getContext())
				.getReadableDatabase();
		String orderBy;
		final int match = sUriMatcher.match(uri);
		Cursor c = null;

		switch (match) {
		case NEWS:
			if (TextUtils.isEmpty(sortOrder)) {
				orderBy = NewsContract.DEFAULT_SORT_ORDER;
			} else {
				orderBy = sortOrder;
			}
			c = db.query(NewsContract.NEWS_JOIN_USER, projection, selection,
					selectionArgs, null, null, orderBy);
			c.setNotificationUri(getContext().getContentResolver(),
					NewsContract.CONTENT_URI);

			break;
		case NEWS_GLOBAL:
			if (TextUtils.isEmpty(sortOrder)) {
				orderBy = NewsContract.DEFAULT_SORT_ORDER;
			} else {
				orderBy = sortOrder;
			}
			c = db.query(NewsContract.NEWS_JOIN_USER, projection,
					NewsContract.Qualified.NEWS_NEWS_COURSE_ID + " = ?",
					new String[] { ApiEndpoints.NEWS_GLOBAL_RANGE_IDENITFIER },
					null, null, orderBy);
			c.setNotificationUri(getContext().getContentResolver(),
					NewsContract.CONTENT_URI);
			break;
		case NEWS_COURSES:
			if (TextUtils.isEmpty(sortOrder)) {
				orderBy = NewsContract.DEFAULT_SORT_ORDER;
			} else {
				orderBy = sortOrder;
			}
			c = db.query(NewsContract.NEWS_JOIN_USER_COURSES, projection, null,
					null, null, null, orderBy);
			c.setNotificationUri(getContext().getContentResolver(),
					NewsContract.CONTENT_URI);
			break;
		case NEWS_ID:
			if (TextUtils.isEmpty(sortOrder)) {
				orderBy = NewsContract.DEFAULT_SORT_ORDER;
			} else {
				orderBy = sortOrder;
			}
			long newsId = ContentUris.parseId(uri);
			c = db.query(
					NewsContract.NEWS_JOIN_USER,
					projection,
					NewsContract.Columns._ID
							+ " = "
							+ newsId
							+ (!TextUtils.isEmpty(selection) ? " AND ("
									+ selection + ")" : ""), selectionArgs,
					null, null, orderBy);
		case COURSES:
			if (TextUtils.isEmpty(sortOrder)) {
				orderBy = CoursesContract.DEFAULT_SORT_ORDER;
			} else {
				orderBy = sortOrder;
			}
			c = db.query(CoursesContract.COURSES_JOIN_SEMESTERS, projection,
					selection, selectionArgs, null, null, orderBy);
			c.setNotificationUri(getContext().getContentResolver(),
					CoursesContract.CONTENT_URI);
			break;

		case COURSES_ID:
			if (TextUtils.isEmpty(sortOrder)) {
				orderBy = CoursesContract.DEFAULT_SORT_ORDER;
			} else {
				orderBy = sortOrder;
			}
			long courseId = ContentUris.parseId(uri);
			c = db.query(CoursesContract.COURSES_JOIN_USERS_SEMESTERS,
					projection, CoursesContract.Qualified.Courses.COURSES_ID
							+ " = "
							+ courseId
							+ (!TextUtils.isEmpty(selection) ? " AND ("
									+ selection + ")" : ""), selectionArgs,
					null, null, orderBy);
			c.setNotificationUri(getContext().getContentResolver(),
					CoursesContract.CONTENT_URI);
			break;
		case COURSES_ID_EVENTS:
			if (TextUtils.isEmpty(sortOrder)) {
				orderBy = EventsContract.DEFAULT_SORT_ORDER;
			} else {
				orderBy = sortOrder;
			}
			String cid = uri.getLastPathSegment();
			String whereStr = "("
					+ EventsContract.Columns.EVENT_COURSE_ID
					+ " = "
					+ "'"
					+ cid
					+ "')"
					+ (!TextUtils.isEmpty(selection) ? " AND (" + selection
							+ ")" : "");
			c = db.query(EventsContract.TABLE, projection, whereStr,
					selectionArgs, null, null, orderBy);

			c.setNotificationUri(getContext().getContentResolver(),
					EventsContract.CONTENT_URI);
			break;
		case USERS:
			if (TextUtils.isEmpty(sortOrder)) {
				orderBy = UsersContract.DEFAULT_SORT_ORDER;
			} else {
				orderBy = sortOrder;
			}
			c = db.query(UsersContract.TABLE, projection, selection,
					selectionArgs, null, null, orderBy);
			c.setNotificationUri(getContext().getContentResolver(),
					UsersContract.CONTENT_URI);
			break;
		case USERS_COURSE_ID:
			String usersCourseId = uri.getLastPathSegment();

			if (TextUtils.isEmpty(sortOrder)) {
				orderBy = UsersContract.DEFAULT_SORT_ORDER;
			} else {
				orderBy = sortOrder;
			}

			c = db.query(
					CoursesContract.COURSES_JOIN_USERS,
					projection,
					CoursesContract.Qualified.CourseUsers.COURSES_USERS_TABLE_COURSE_USER_COURSE_ID
							+ " = "
							+ '"'
							+ usersCourseId
							+ '"'
							+ (!TextUtils.isEmpty(selection) ? " AND ("
									+ selection + ")" : ""), selectionArgs,
					CoursesContract.Qualified.CourseUsers.COURSES_USERS_TABLE_COURSE_USER_USER_ID,
					null, orderBy);

			c.setNotificationUri(getContext().getContentResolver(),
					UsersContract.CONTENT_URI);
			break;
		case DOCUMENTS:
			if (TextUtils.isEmpty(sortOrder)) {
				orderBy = DocumentsContract.DEFAULT_SORT_ORDER;
			} else {
				orderBy = sortOrder;
			}
			c = db.query(DocumentsContract.TABLE, projection, selection,
					selectionArgs, null, null, orderBy);
			c.setNotificationUri(getContext().getContentResolver(),
					DocumentsContract.CONTENT_URI);
			break;
		case MESSAGES_ID:
			long messageId = ContentUris.parseId(uri);
			if (TextUtils.isEmpty(sortOrder)) {
				orderBy = MessagesContract.DEFAULT_SORT_ORDER_MESSAGES;
			} else {
				orderBy = sortOrder;
			}
			c = db.query(MessagesContract.MESSAGES_JOIN_USERS, projection,
					MessagesContract.Qualified.Messages.MESSAGES_ID
							+ " = "
							+ messageId
							+ (!TextUtils.isEmpty(selection) ? " AND ("
									+ selection + ")" : ""), selectionArgs,
					null, null, orderBy);
			c.setNotificationUri(getContext().getContentResolver(),
					MessagesContract.CONTENT_URI_MESSAGES);
			break;
		case MESSAGES_FOLDERS:
			if (TextUtils.isEmpty(sortOrder)) {
				orderBy = MessagesContract.DEFAULT_SORT_ORDER_FOLDERS;
			} else {
				orderBy = sortOrder;
			}

			c = db.query(MessagesContract.TABLE_MESSAGE_FOLDERS, projection,
					selection, selectionArgs, null, null, orderBy);

			c.setNotificationUri(getContext().getContentResolver(),
					MessagesContract.CONTENT_URI_MESSAGE_FOLDERS);
			break;
		case MESSAGES_FOLDER_MESSAGES:
			if (TextUtils.isEmpty(sortOrder)) {
				orderBy = MessagesContract.DEFAULT_SORT_ORDER_MESSAGES;
			} else {
				orderBy = sortOrder;
			}

			long folderId = ContentUris.parseId(uri);

			c = db.query(
					MessagesContract.MESSAGES_JOIN_MESSAGE_FOLDERS_JOIN_USERS,
					projection,
					MessagesContract.Qualified.Messages.MESSAGES_MESSAGE_FOLDER_ID
							+ " = "
							+ folderId
							+ (!TextUtils.isEmpty(selection) ? " AND ("
									+ selection + ")" : ""), selectionArgs,
					null, null, orderBy);
			c.setNotificationUri(getContext().getContentResolver(),
					MessagesContract.CONTENT_URI_MESSAGES);
			break;
		default:
			throw new IllegalArgumentException("Unsupported uri: " + uri);
		}

		return c;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#update(android.net.Uri,
	 * android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ContentProviderResult[] applyBatch(
			ArrayList<ContentProviderOperation> operations)
			throws OperationApplicationException {
		final SQLiteDatabase db = DatabaseHandler.getInstance(getContext())
				.getWritableDatabase();
		db.beginTransaction();
		try {
			final int numOperations = operations.size();
			final ContentProviderResult[] results = new ContentProviderResult[numOperations];
			for (int i = 0; i < numOperations; i++) {
				results[i] = operations.get(i).apply(this, results, i);
			}
			db.setTransactionSuccessful();
			return results;
		} finally {
			db.endTransaction();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#bulkInsert(android.net.Uri,
	 * android.content.ContentValues[])
	 */
	@Override
	public int bulkInsert(Uri uri, ContentValues[] values) {
		SQLiteDatabase db = DatabaseHandler.getInstance(getContext())
				.getWritableDatabase();
		final int match = sUriMatcher.match(uri);
		switch (match) {
		case MESSAGES_FOLDER_MESSAGES:
			long folderId = ContentUris.parseId(uri);
			int numInserted = 0;
			db.beginTransaction();
			try {
				// first clear the messages table
				StringBuilder deleteSql = new StringBuilder();
				deleteSql
						.append("DELETE FROM ")
						.append(MessagesContract.TABLE_MESSAGES)
						.append(" WHERE ")
						.append(MessagesContract.Columns.Messages.MESSAGE_FOLDER_ID)
						.append(" = ?");

				// build standard insert string
				StringBuilder sqlMessage = new StringBuilder();
				sqlMessage
						.append("INSERT OR IGNORE INTO ")
						.append(MessagesContract.TABLE_MESSAGES)
						.append(" ( ")
						.append(MessagesContract.Columns.Messages.MESSAGE_SENDER_ID)
						.append(", ")
						.append(MessagesContract.Columns.Messages.MESSAGE_RECEIVER_ID)
						.append(", ")
						.append(MessagesContract.Columns.Messages.MESSAGE_SUBJECT)
						.append(", ")
						.append(MessagesContract.Columns.Messages.MESSAGE)
						.append(", ")
						.append(MessagesContract.Columns.Messages.MESSAGE_MKDATE)
						.append(", ")
						.append(MessagesContract.Columns.Messages.MESSAGE_PRIORITY)
						.append(", ")
						.append(MessagesContract.Columns.Messages.MESSAGE_UNREAD)
						.append(", ")
						.append(MessagesContract.Columns.Messages.MESSAGE_ID)
						.append(", ")
						.append(MessagesContract.Columns.Messages.MESSAGE_FOLDER_ID)
						.append(") VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? ) ");

				// compile to reusable sql statement
				SQLiteStatement deleteMessages = db.compileStatement(deleteSql
						.toString());
				deleteMessages.bindLong(1, folderId);
				deleteMessages.execute();

				SQLiteStatement insertMessage = db.compileStatement(sqlMessage
						.toString());

				// set values and execute
				for (ContentValues value : values) {
					insertMessage
							.bindString(
									1,
									value.getAsString(MessagesContract.Columns.Messages.MESSAGE_SENDER_ID));
					insertMessage
							.bindString(
									2,
									value.getAsString(MessagesContract.Columns.Messages.MESSAGE_RECEIVER_ID));
					insertMessage
							.bindString(
									3,
									value.getAsString(MessagesContract.Columns.Messages.MESSAGE_SUBJECT));
					insertMessage
							.bindString(
									4,
									value.getAsString(MessagesContract.Columns.Messages.MESSAGE));
					insertMessage
							.bindLong(
									5,
									value.getAsLong(MessagesContract.Columns.Messages.MESSAGE_MKDATE));
					insertMessage
							.bindString(
									6,
									value.getAsString(MessagesContract.Columns.Messages.MESSAGE_PRIORITY));
					insertMessage
							.bindLong(
									7,
									value.getAsInteger(MessagesContract.Columns.Messages.MESSAGE_UNREAD));
					insertMessage
							.bindString(
									8,
									value.getAsString(MessagesContract.Columns.Messages.MESSAGE_ID));
					insertMessage.bindLong(9, folderId);
					insertMessage.execute();
				}

				db.setTransactionSuccessful();
				numInserted = values.length;

			} finally {
				db.endTransaction();
			}

			getContext().getContentResolver().notifyChange(uri, null);
			return numInserted;

		default: {
			throw new UnsupportedOperationException(
					"Unsupported bulk insert uri: " + uri);
		}
		}

	}

	public static long insertIgnoringConflict(SQLiteDatabase db, String table,
			String idColumn, ContentValues values, boolean updateFlag) {
		try {
			return db.insertOrThrow(table, null, values);
		} catch (SQLException e) {
			StringBuilder sql = new StringBuilder();
			sql.append("SELECT ").append(idColumn).append(" FROM ")
					.append(table).append(" WHERE ");

			Object[] bindArgs = new Object[values.size()];
			int i = 0;
			for (Map.Entry<String, Object> entry : values.valueSet()) {
				sql.append((i > 0) ? " AND " : "").append(entry.getKey())
						.append(" = ?");
				bindArgs[i++] = entry.getValue();
			}

			SQLiteStatement stmt = db.compileStatement(sql.toString());
			for (i = 0; i < bindArgs.length; i++) {
				DatabaseUtils.bindObjectToProgram(stmt, i + 1, bindArgs[i]);
			}

			long id = -1;
			try {
				id = stmt.simpleQueryForLong();
			} catch (SQLiteDoneException e2) {
			} finally {
				stmt.close();
			}

			if (updateFlag) {
				try {
					int affected = db.update(table, values, idColumn + " = ?",
							new String[] { String.format("%d", id) });
					Log.d(TAG, affected + " rows updated!");
				} catch (SQLException ex) {
					ex.printStackTrace();
				}

			}

			return id;
		}
	}

}
