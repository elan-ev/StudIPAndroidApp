/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.db;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.io.File;

import de.elanev.studip.android.app.util.Config;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 3;

    private static final String DATABASE_NAME = "studip_encrypted.db";
    private static final String LEGACY_DATABASE_NAME = "studip.db";

    private static DatabaseHandler instance;
    private Context mContext;

    private DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static synchronized DatabaseHandler getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHandler(context);
            SQLiteDatabase.loadLibs(context);
        }

        instance.mContext = context;
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Semesters
        db.execSQL(SemestersContract.CREATE_STRING);

        // News
        db.execSQL(NewsContract.CREATE_STRING);

        // Courses
        db.execSQL(CoursesContract.CREATE_TABLE_COURSES_STRING);
        // FIXME meh..
        db.execSQL("INSERT INTO courses (course_id, title) VALUES ('studip', 'Global')");
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

        // Contacts
        db.execSQL(ContactsContract.CREATE_TABLE_CONTACTS_STRING);
        db.execSQL(ContactsContract.CREATE_TABLE_CONTACT_GROUPS_STRING);
        db.execSQL(ContactsContract.CREATE_TABLE_CONTACT_GROUP_MEMBERS_STRING);

        //Authentication
        db.execSQL(AuthenticationContract.CREATE_TABLE_AUTHENTICATION);

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
        db.execSQL("drop table if exists " + DocumentsContract.TABLE_DOCUMENTS);
        db.execSQL("drop table if exists "
                + DocumentsContract.TABLE_DOCUMENT_FOLDERS);

        // Messages
        db.execSQL("drop table if exists " + MessagesContract.TABLE_MESSAGES);
        db.execSQL("drop table if exists "
                + MessagesContract.TABLE_MESSAGE_FOLDERS);

        // Events
        db.execSQL("drop table if exists " + EventsContract.TABLE);

        // Contacts
        db.execSQL("drop table if exists " + ContactsContract.TABLE_CONTACTS);
        db.execSQL("drop table if exists "
                + ContactsContract.TABLE_CONTACT_GROUPS);
        db.execSQL("drop table if exists "
                + ContactsContract.TABLE_CONTACT_GROUP_MEMBERS);

        // Authentication
        db.execSQL("drop table if exists " + AuthenticationContract.TABLE_AUTHENTICATION);

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

    public static void deleteLegacyDatabase(Context context) {
        if(context.deleteDatabase(LEGACY_DATABASE_NAME))
            return;
        else {
            File legacyDb = context.getDatabasePath(LEGACY_DATABASE_NAME);
            if(legacyDb.exists())
                legacyDb.delete();
        }

    }
}
