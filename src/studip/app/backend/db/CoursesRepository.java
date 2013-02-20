/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package studip.app.backend.db;

import studip.app.backend.datamodel.Course;
import studip.app.backend.datamodel.Courses;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class CoursesRepository {
    private static CoursesRepository instance;
    private Context mContext;

    public static CoursesRepository getInstance(Context context) {
	if (instance == null)
	    instance = new CoursesRepository(context);

	return instance;
    }

    private CoursesRepository(Context context) {
	this.mContext = context;
    }

    public void addCourses(Courses c) {
	SQLiteDatabase db = DatabaseHandler.getInstance(mContext)
		.getWritableDatabase();
	// remove non existing entrys
	db.execSQL("DELETE FROM " + CoursesContract.TABLE);
	try {
	    for (studip.app.backend.datamodel.Course course : c.courses) {

		ContentValues values = new ContentValues();

		values.put(CoursesContract.Columns.COURSE_ID, course.course_id);
		values.put(CoursesContract.Columns.COURSE_TITLE, course.title);
		values.put(CoursesContract.Columns.COURSE_SUBTITLE,
			course.subtitle);
		values.put(CoursesContract.Columns.COURSE_DESCIPTION,
			course.description);
		values.put(CoursesContract.Columns.COURSE_LOCATION,
			course.location);
		values.put(CoursesContract.Columns.COURSE_DURATION_TIME,
			course.duration_time);
		values.put(CoursesContract.Columns.COURSE_START_TIME,
			course.start_time);
		values.put(CoursesContract.Columns.COURSE_SEMESERT_ID,
			course.semester_id);
		values.put(CoursesContract.Columns.COURSE_TYPE, course.type);

		db.insertWithOnConflict(CoursesContract.TABLE, null, values,
			SQLiteDatabase.CONFLICT_IGNORE);

	    }
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    db.close();
	}
    }

    public Courses getAllCourses() {
	String selectQuery = "SELECT  * FROM " + CoursesContract.TABLE;

	SQLiteDatabase db = DatabaseHandler.getInstance(mContext)
		.getReadableDatabase();
	Cursor cursor = db.rawQuery(selectQuery, null);
	Courses courses = new Courses();
	try {
	    if (cursor.moveToFirst()) {
		do {
		    courses.courses
			    .add(new Course(
				    cursor.getString(cursor
					    .getColumnIndex(CoursesContract.Columns.COURSE_ID)),
				    cursor.getString(cursor
					    .getColumnIndex(CoursesContract.Columns.COURSE_START_TIME)),
				    cursor.getString(cursor
					    .getColumnIndex(CoursesContract.Columns.COURSE_DURATION_TIME)),
				    cursor.getString(cursor
					    .getColumnIndex(CoursesContract.Columns.COURSE_TITLE)),
				    cursor.getString(cursor
					    .getColumnIndex(CoursesContract.Columns.COURSE_SUBTITLE)),
				    cursor.getString(cursor
					    .getColumnIndex(CoursesContract.Columns.COURSE_DESCIPTION)),
				    cursor.getString(cursor
					    .getColumnIndex(CoursesContract.Columns.COURSE_LOCATION)),
				    cursor.getString(cursor
					    .getColumnIndex(CoursesContract.Columns.COURSE_TYPE)),
				    cursor.getString(cursor
					    .getColumnIndex(CoursesContract.Columns.COURSE_SEMESERT_ID))));
		} while (cursor.moveToNext());
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    cursor.close();
	    db.close();
	}
	return courses;
    }

    public Course getCourse(String cid) {
	SQLiteDatabase db = DatabaseHandler.getInstance(mContext)
		.getReadableDatabase();
	Cursor cursor = null;
	Course course = null;
	try {
	    cursor = db.query(CoursesContract.TABLE, null,
		    CoursesContract.Columns.COURSE_ID + "=?",
		    new String[] { cid }, null, null, null);
	    cursor.moveToFirst();
	    course = new Course(
		    cursor.getString(cursor
			    .getColumnIndex(CoursesContract.Columns.COURSE_ID)),
		    cursor.getString(cursor
			    .getColumnIndex(CoursesContract.Columns.COURSE_START_TIME)),
		    cursor.getString(cursor
			    .getColumnIndex(CoursesContract.Columns.COURSE_DURATION_TIME)),
		    cursor.getString(cursor
			    .getColumnIndex(CoursesContract.Columns.COURSE_TITLE)),
		    cursor.getString(cursor
			    .getColumnIndex(CoursesContract.Columns.COURSE_SUBTITLE)),
		    cursor.getString(cursor
			    .getColumnIndex(CoursesContract.Columns.COURSE_DESCIPTION)),
		    cursor.getString(cursor
			    .getColumnIndex(CoursesContract.Columns.COURSE_LOCATION)),
		    cursor.getString(cursor
			    .getColumnIndex(CoursesContract.Columns.COURSE_TYPE)),
		    cursor.getString(cursor
			    .getColumnIndex(CoursesContract.Columns.COURSE_SEMESERT_ID)));
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    cursor.close();
	    db.close();
	}

	return course;
    }

}
