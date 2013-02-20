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
package studip.app.backend.db;

import java.text.SimpleDateFormat;

import studip.app.backend.datamodel.Semester;
import studip.app.backend.datamodel.Semesters;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @author joern
 * 
 */
public class SemestersRepository {
    private static SemestersRepository mInstance;
    private Context mContext;

    public static SemestersRepository getInstance(Context context) {
	if (mInstance == null)
	    mInstance = new SemestersRepository(context);

	return mInstance;
    }

    private SemestersRepository(Context context) {
	this.mContext = context;
    }

    @SuppressLint("SimpleDateFormat")
    public void addSemester(Semester s) {
	SQLiteDatabase db = DatabaseHandler.getInstance(mContext)
		.getWritableDatabase();

	SimpleDateFormat dateFormat = new SimpleDateFormat(
		"yyyy-MM-dd HH:mm:ss");

	try {
	    ContentValues contentValues = new ContentValues();
	    contentValues.put(SemestersContract.Columns.SEMESTER_ID,
		    s.semester_id);
	    contentValues
		    .put(SemestersContract.Columns.SEMESTER_TITLE, s.title);
	    contentValues.put(SemestersContract.Columns.SEMESTER_BEGIN,
		    dateFormat.format(s.begin));
	    contentValues.put(SemestersContract.Columns.SEMESTER_END,
		    dateFormat.format(s.end));
	    contentValues.put(
		    SemestersContract.Columns.SEMESTER_SEMINARS_BEGIN,
		    dateFormat.format(s.seminars_begin));
	    contentValues.put(SemestersContract.Columns.SEMESTER_SEMINARS_END,
		    dateFormat.format(s.seminars_end));

	    db.insertWithOnConflict(SemestersContract.TABLE, null,
		    contentValues, SQLiteDatabase.CONFLICT_IGNORE);
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    db.close();
	}
    }

    @SuppressLint("SimpleDateFormat")
    public void addSemesters(Semesters sem) {
	SQLiteDatabase db = DatabaseHandler.getInstance(mContext)
		.getWritableDatabase();

	try {
	    for (Semester s : sem.semesters) {

		ContentValues contentValues = new ContentValues();
		contentValues.put(SemestersContract.Columns.SEMESTER_ID,
			s.semester_id);
		contentValues.put(SemestersContract.Columns.SEMESTER_TITLE,
			s.title);
		contentValues.put(SemestersContract.Columns.SEMESTER_BEGIN,
			s.begin);
		contentValues
			.put(SemestersContract.Columns.SEMESTER_END, s.end);
		contentValues.put(
			SemestersContract.Columns.SEMESTER_SEMINARS_BEGIN,
			s.seminars_begin);
		contentValues.put(
			SemestersContract.Columns.SEMESTER_SEMINARS_END,
			s.seminars_end);

		db.insertWithOnConflict(SemestersContract.TABLE, null,
			contentValues, SQLiteDatabase.CONFLICT_IGNORE);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    db.close();
	}
    }

    public Semesters getAllSemesters() {
	SQLiteDatabase db = DatabaseHandler.getInstance(mContext)
		.getReadableDatabase();
	Cursor cursor = db.query(SemestersContract.TABLE, null, null, null,
		null, null, SemestersContract.Columns.SEMESTER_BEGIN + " DESC");
	Semesters semesters = new Semesters();
	try {
	    if (cursor.moveToFirst()) {
		do {
		    semesters.semesters
			    .add(new Semester(
				    cursor.getString(cursor
					    .getColumnIndex(SemestersContract.Columns.SEMESTER_ID)),
				    cursor.getString(cursor
					    .getColumnIndex(SemestersContract.Columns.SEMESTER_TITLE)),
				    cursor.getString(cursor
					    .getColumnIndex(SemestersContract.Columns.SEMESTER_DESCRIPTION)),
				    cursor.getString(cursor
					    .getColumnIndex(SemestersContract.Columns.SEMESTER_BEGIN)),
				    cursor.getString(cursor
					    .getColumnIndex(SemestersContract.Columns.SEMESTER_END)),
				    cursor.getString(cursor
					    .getColumnIndex(SemestersContract.Columns.SEMESTER_SEMINARS_BEGIN)),
				    cursor.getString(cursor
					    .getColumnIndex(SemestersContract.Columns.SEMESTER_SEMINARS_END))));
		} while (cursor.moveToNext());
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    cursor.close();
	    db.close();
	}
	return semesters;
    }

    public Semester getSemester(String sem_id) {
	// TODO getObject benutzen, doppelten Code verhindern
	SQLiteDatabase db = DatabaseHandler.getInstance(mContext)
		.getReadableDatabase();
	Cursor cursor = null;
	Semester semester = null;
	try {
	    cursor = db.query(SemestersContract.TABLE, null,
		    SemestersContract.Columns.SEMESTER_ID + "=?",
		    new String[] { sem_id }, null, null, null);
	    cursor.moveToFirst();
	    semester = new Semester(
		    cursor.getString(cursor
			    .getColumnIndex(SemestersContract.Columns.SEMESTER_ID)),
		    cursor.getString(cursor
			    .getColumnIndex(SemestersContract.Columns.SEMESTER_TITLE)),
		    cursor.getString(cursor
			    .getColumnIndex(SemestersContract.Columns.SEMESTER_DESCRIPTION)),
		    cursor.getString(cursor
			    .getColumnIndex(SemestersContract.Columns.SEMESTER_BEGIN)),
		    cursor.getString(cursor
			    .getColumnIndex(SemestersContract.Columns.SEMESTER_END)),
		    cursor.getString(cursor
			    .getColumnIndex(SemestersContract.Columns.SEMESTER_SEMINARS_BEGIN)),
		    cursor.getString(cursor
			    .getColumnIndex(SemestersContract.Columns.SEMESTER_SEMINARS_END)));
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    cursor.close();
	    db.close();
	}

	return semester;
    }

    public boolean semesterExists(String sid) {
	SQLiteDatabase db = DatabaseHandler.getInstance(mContext)
		.getReadableDatabase();
	Cursor cursor = null;
	try {
	    cursor = db.query(SemestersContract.TABLE, null,
		    SemestersContract.Columns.SEMESTER_ID + "=?",
		    new String[] { sid }, null, null, null);
	    if (cursor != null && cursor.getCount() > 0) {
		return true;
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    cursor.close();
	    db.close();
	}

	return false;
    }
}
