/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import de.elanev.studip.android.app.R;

public class DatabaseHandler extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;

	private static final String DATABASE_NAME = "studip.db";

	private static DatabaseHandler instance;

	private DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public static synchronized DatabaseHandler getInstance(Context context) {
		if (instance == null)
			instance = new DatabaseHandler(context);

		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// Semestes
		db.execSQL(SemestersContract.CREATE_STRING);
		// News
		db.execSQL(NewsContract.CREATE_STRING);
		// Courses
		db.execSQL(CoursesContract.CREATE_TABLE_COURSES_STRING);
		db.execSQL(CoursesContract.CREATE_TABLE_COURSE_USERS_STRING);

		// Users
		db.execSQL(UsersContract.CREATE_STRING);
		// Documents
		db.execSQL(DocumentsContract.CREATE_STRING);
		db.execSQL(DocumentsContract.CREATE_DOCUMENT_FOLDER_STRING);
		// Messages
		db.execSQL(MessagesContract.CREATE_TABLE_MESSAGES_STRING);
		db.execSQL(MessagesContract.CREATE_TABLE_MESSAGE_FOLDERS_STRING);
		// Events
		db.execSQL(EventsContract.CREATE_STRING);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// Semesters
		db.execSQL("drop table if exists " + SemestersContract.TABLE);
		// News
		db.execSQL("drop table if exists " + NewsContract.TABLE);
		// Courses
		db.execSQL("drop table if exists " + CoursesContract.TABLE_COURSES);
		db.execSQL("drop table if exists " + CoursesContract.TABLE_COURSE_USER);
		// Users
		db.execSQL("drop table if exists " + UsersContract.TABLE);
		// Documents
		db.execSQL("drop table if exists " + DocumentsContract.TABLE);
		db.execSQL("drop table if exists "
				+ DocumentsContract.DOCUMENT_FOLDER_TABLE);
		// Messages
		db.execSQL("drop table if exists " + MessagesContract.TABLE_MESSAGES);
		db.execSQL("drop table if exists "
				+ MessagesContract.TABLE_MESSAGE_FOLDERS);
		// Events
		db.execSQL("drop table if exists " + EventsContract.TABLE);

		onCreate(db);
	}

	/**
	 * 
	 */
	public void deleteDatabase(Context context) {
		context.deleteDatabase(DATABASE_NAME);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.database.sqlite.SQLiteOpenHelper#onOpen(android.database.sqlite
	 * .SQLiteDatabase)
	 */
	@Override
	public void onOpen(SQLiteDatabase db) {
		db.execSQL("PRAGMA foreign_keys = ON;");
		super.onOpen(db);
	}

}
