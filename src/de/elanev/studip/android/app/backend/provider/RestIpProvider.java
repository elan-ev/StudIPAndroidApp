/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.provider;

import java.util.ArrayList;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import de.elanev.studip.android.app.backend.db.AbstractContract;
import de.elanev.studip.android.app.backend.db.CoursesContract;
import de.elanev.studip.android.app.backend.db.DatabaseHandler;
import de.elanev.studip.android.app.backend.db.DocumentsContract;
import de.elanev.studip.android.app.backend.db.EventsContract;
import de.elanev.studip.android.app.backend.db.NewsContract;
import de.elanev.studip.android.app.backend.db.SemestersContract;
import de.elanev.studip.android.app.backend.db.UsersContract;
import de.elanev.studip.android.app.backend.net.api.ApiEndpoints;

/**
 * @author joern
 * 
 */
public class RestIpProvider extends ContentProvider {
	public final String TAG = RestIpProvider.class.getSimpleName();

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
	private static final int EVENTS = 400;
	private static final int EVENTS_ID = 401;
	private static final int SEMESTERS = 500;
	private static final int SEMESTERS_ID = 501;
	private static final int DOCUMENTS = 600;
	private static final int DOCUMENTS_ID = 601;

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
		matcher.addURI(authority, "users/#", USERS_ID);

		matcher.addURI(authority, "events", EVENTS);
		matcher.addURI(authority, "events/#", EVENTS_ID);

		matcher.addURI(authority, "semesters", SEMESTERS);
		matcher.addURI(authority, "semesters/#", SEMESTERS_ID);

		matcher.addURI(authority, "documents", DOCUMENTS);
		matcher.addURI(authority, "documents/#", DOCUMENTS_ID);
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
		default:
			throw new UnsupportedOperationException("Unknown mime type: " + uri);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.content.ContentProvider#delete(android.net.Uri,
	 * java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		// TODO Auto-generated method stub
		return 0;
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
					CoursesContract.COURSE_USER_TABLE, null, values,
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
			long rowId = db.insertWithOnConflict(CoursesContract.TABLE, null,
					values, SQLiteDatabase.CONFLICT_IGNORE);
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
			return ContentUris.withAppendedId(SemestersContract.CONTENT_URI,
					rowId);
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
					projection, CoursesContract.Qualified.COURSES_ID
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
			c = db.query(
					UsersContract.USERS_JOIN_COURSES,
					projection,
					selection,
					selectionArgs,
					CoursesContract.Qualified.COURSES_USERS_TABLE_COURSE_USER_USER_ID,
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

}
