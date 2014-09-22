/*
 * Copyright (c) 2014 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package de.elanev.studip.android.app.backend.db;

import android.content.Context;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.io.File;

import de.elanev.studip.android.app.BuildConfig;
import de.elanev.studip.android.app.R;

public class DatabaseHandler extends SQLiteOpenHelper {
  private static final Patch[] PATCHES = new Patch[]{
      // First db migration, create table for recordings.
      new Patch() {
        @Override public void apply(SQLiteDatabase db) {
          db.execSQL(RecordingsContract.CREATE_TABLE_RECORDINGS);
        }
      }
  };

  // Add patch count to the existing db version for backward compatibility
  private static final int LEGACY_DATABASE_VERSION = 9;
  private static final int DATABASE_VERSION = LEGACY_DATABASE_VERSION + PATCHES.length;

  private static final String DATABASE_NAME = BuildConfig.DATABASE;
  private static final String LEGACY_DATABASE_NAME = "studip.db";

  private static DatabaseHandler sInstance;
  private Context mContext;

  private DatabaseHandler(Context context) {
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
    this.mContext = context;
  }

  /**
   * Returns an instance of the DatabaseHandler
   *
   * @param context a context to get the application context for the database to live in
   * @return an DatabaseHandler instance
   */
  public static DatabaseHandler getInstance(Context context) {
    if (sInstance == null) {
      sInstance = new DatabaseHandler(context.getApplicationContext());
    }

    return sInstance;
  }

  /**
   * Deletes the old unencrypted database files to prevent leaking of unencrypted values
   *
   * @param context the context to execute the operation in
   */
  public static void deleteLegacyDatabase(Context context) {
    if (context.getApplicationContext().deleteDatabase(LEGACY_DATABASE_NAME)) return;
    else {
      File legacyDb = context.getApplicationContext().getDatabasePath(LEGACY_DATABASE_NAME);
      if (legacyDb.exists()) legacyDb.delete();
    }

  }

  /**
   * Deletes the whole database
   */
  public void deleteDatabase() {
    mContext.deleteDatabase(DATABASE_NAME);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    // Install the previous Database.
    installDb(db);

    // Run the full migration path since introduction.
    for (Patch patch : PATCHES) {
      patch.apply(db);
    }
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    if (oldVersion < LEGACY_DATABASE_VERSION) {
      deleteDatabase();
      onCreate(db);
    } else {
      int normalizedOld = oldVersion - LEGACY_DATABASE_VERSION;
      int normalizedNew = newVersion - LEGACY_DATABASE_VERSION;
      for (int i = normalizedOld; i < normalizedNew; i++) {
        PATCHES[i].apply(db);
      }
    }
  }

  @Override
  public void onOpen(SQLiteDatabase db) {
    db.execSQL("PRAGMA foreign_keys = ON;");
    super.onOpen(db);
  }

  /*
   * Runs the installation process for previous db schema.
   */
  private void installDb(SQLiteDatabase db) {
    // Semesters
    db.execSQL(SemestersContract.CREATE_STRING);
    db.execSQL("INSERT INTO semesters (semester_id, title) VALUES ('" +
        SemestersContract.UNLIMITED_COURSES_SEMESTER_ID +
        "', " + "'" + mContext.getString(R.string.course_without_duration_limit) + "')");

    // News
    db.execSQL(NewsContract.CREATE_STRING);

    // Courses
    db.execSQL(CoursesContract.CREATE_TABLE_COURSES_STRING);
    db.execSQL("INSERT INTO courses (course_id, title) VALUES ('studip', 'Global')");
    db.execSQL(CoursesContract.CREATE_TABLE_COURSE_USERS_STRING);

    // Users
    db.execSQL(UsersContract.CREATE_STRING);
    db.execSQL("INSERT INTO users (user_id, title_pre, forename, lastname, title_post) "
        + "VALUES ('____%system%____', '', 'Stud.IP', '', '')");

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

    // Authentication
    db.execSQL(AuthenticationContract.CREATE_TABLE_AUTHENTICATION);

    // Institutes
    db.execSQL(InstitutesContract.CREATE_STRING);
  }

  /*
   * Abstract Patch class to encapsulate each database migration
   */
  private abstract static class Patch {
    public abstract void apply(SQLiteDatabase db);
  }
}
