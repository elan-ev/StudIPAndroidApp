/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package studip.app.backend.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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
	// Activites
	db.execSQL(ActivitiesContract.CREATE_STRING);
	// News
	db.execSQL(NewsContract.CREATE_STRING);
	// Courses
	db.execSQL(CoursesContract.CREATE_STRING);
	// db.execSQL(CoursesContract.CREATE_STUDENT_USER_STRING);
	// db.execSQL(CoursesContract.CREATE_TEACHER_USER_STRING);
	// db.execSQL(CoursesContract.CREATE_TUTOR_USER_STRING);
	// Users
	db.execSQL(UsersContract.CREATE_STRING);
	// Documents
	db.execSQL(DocumentsContract.CREATE_STRING);
	// Messages
	db.execSQL(MessagesContract.CREATE_STRING);
	// Events
	db.execSQL(EventsConstract.CREATE_STRING);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	// Semesters
	db.execSQL("drop table if exists " + SemestersContract.TABLE);
	// Activites
	db.execSQL("drop table if exists " + ActivitiesContract.TABLE);
	// News
	db.execSQL("drop table if exists " + NewsContract.TABLE);
	// Courses
	db.execSQL("drop table if exists " + CoursesContract.TABLE);
//	db.execSQL("drop table if exists "
//		+ CoursesContract.STUDENT_COURSE_USER_TABLE);
//	db.execSQL("drop table if exists "
//		+ CoursesContract.TEACHER_COURSE_USER_TABLE);
//	db.execSQL("drop table if exists "
//		+ CoursesContract.TUTOR_COURSE_USER_TABLE);
	// Users
	db.execSQL("drop table if exists " + UsersContract.TABLE);
	// Documents
	db.execSQL("drop table if exists " + DocumentsContract.TABLE);
	// Messages
	db.execSQL("drop table if exists " + MessagesContract.TABLE);
	// Events
	db.execSQL("drop table if exists " + EventsConstract.TABLE);

	onCreate(db);
    }

}
