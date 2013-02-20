/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package studip.app.backend.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

@SuppressLint("SimpleDateFormat")
public class DatabaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "studip.db";

    private static DatabaseHandler instance;

    private DatabaseHandler(Context context) {
	super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DatabaseHandler getInstance(Context context) {
	if (instance == null)
	    instance = new DatabaseHandler(context.getApplicationContext());

	return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

	db.execSQL(SemestersContract.CREATE_STRING);
	db.execSQL(ActivitiesContract.CREATE_STRING);
	db.execSQL(NewsContract.CREATE_STRING);
	db.execSQL(CoursesContract.CREATE_STRING);
	db.execSQL(UsersContract.CREATE_STRING);
	db.execSQL(DocumentsContract.CREATE_STRING);
	db.execSQL(MessagesContract.CREATE_STRING);
	db.execSQL(EventsConstract.CREATE_STRING);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	// TODO Im Produktiv Umfeld, migration der Daten
	db.execSQL("drop table if exists " + SemestersContract.TABLE);
	db.execSQL("drop table if exists " + ActivitiesContract.TABLE);
	db.execSQL("drop table if exists " + NewsContract.TABLE);
	db.execSQL("drop table if exists " + CoursesContract.TABLE);
	db.execSQL("drop table if exists " + UsersContract.TABLE);
	db.execSQL("drop table if exists " + DocumentsContract.TABLE);
	db.execSQL("drop table if exists " + MessagesContract.TABLE);
	db.execSQL("drop table if exists " + EventsConstract.TABLE);

	onCreate(db);
    }

    public void close() {
	SQLiteDatabase db = this.getWritableDatabase();
	db.close();
    }

    public void addObject(Object object, String table) {
	// SQLiteDatabase db = this.getWritableDatabase();
	//
	// ContentValues values = new ContentValues();
	// values.put(KEY_VALUE, Serializer.serializeObject(object));
	//
	// db.insertWithOnConflict(table, null, values,
	// SQLiteDatabase.CONFLICT_REPLACE);
	// db.close();
    }

    public Object getObject(int id, String table) {
	// SQLiteDatabase db = this.getReadableDatabase();
	//
	// Cursor cursor = db.query(table, new String[] { KEY_ID, KEY_VALUE },
	// KEY_ID + "=?", new String[] { String.valueOf(id) }, null, null,
	// null, null);
	// if (cursor != null)
	// cursor.moveToFirst();
	//
	// byte[] data = cursor.getBlob(1);
	// cursor.close();
	// db.close();
	// // TODO Jackson Parsing
	// return Serializer.deserializeObject(data);
	return null;
    }

    // public ArrayList<Object> getAllObjects(String table) {
    //
    // // TODO getObject benutzen, doppelten Code verhindern
    // ArrayList<Object> list = new ArrayList<Object>();
    //
    // String selectQuery = "SELECT  * FROM " + table;
    //
    // SQLiteDatabase db = this.getWritableDatabase();
    // Cursor cursor = db.rawQuery(selectQuery, null);
    //
    // if (cursor.moveToFirst()) {
    // do {
    // list.add(Serializer.deserializeObject(cursor.getBlob(1)));
    // } while (cursor.moveToNext());
    // }
    //
    // cursor.close();
    // db.close();
    //
    // return list;
    // }

}
