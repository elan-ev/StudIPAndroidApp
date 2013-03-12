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

import com.fasterxml.jackson.databind.ObjectMapper;

public class CoursesRepository {
    private static CoursesRepository instance;
    private Context mContext;
    ObjectMapper mMapper;

    public static synchronized CoursesRepository getInstance(Context context) {
	if (instance == null)
	    instance = new CoursesRepository(context);

	return instance;
    }

    private CoursesRepository(Context context) {
	this.mContext = context;
	this.mMapper = new ObjectMapper();
    }

    public void addCourses(Courses c) {
	SQLiteDatabase db = null;
	// .getWritableDatabase();
	// // remove non existing entries
	// db.execSQL("DELETE FROM " + CoursesContract.TABLE);
	// db.close();

	for (studip.app.backend.datamodel.Course course : c.courses) {
	    try {
		ContentValues values = new ContentValues();

		values.put(CoursesContract.Columns.COURSE_ID, course.course_id);
		values.put(CoursesContract.Columns.COURSE_START_TIME,
			course.start_time);
		values.put(CoursesContract.Columns.COURSE_DURATION_TIME,
			course.duration_time);
		values.put(CoursesContract.Columns.COURSE_NUMBER, course.number);
		values.put(CoursesContract.Columns.COURSE_TITLE, course.title);
		values.put(CoursesContract.Columns.COURSE_SUBTITLE,
			course.subtitle);
		values.put(CoursesContract.Columns.COURSE_TYPE, course.type);

		values.put(CoursesContract.Columns.COURSE_MODULES,
			mMapper.writeValueAsString(course.modules));

		values.put(CoursesContract.Columns.COURSE_DESCIPTION,
			course.description);
		values.put(CoursesContract.Columns.COURSE_LOCATION,
			course.location);
		values.put(CoursesContract.Columns.COURSE_SEMESERT_ID,
			course.semester_id);
		values.put(CoursesContract.Columns.COURSE_TEACHERS,
			mMapper.writeValueAsString(course.teachers));
		values.put(CoursesContract.Columns.COURSE_TUTORS,
			mMapper.writeValueAsString(course.tutors));
		values.put(CoursesContract.Columns.COURSE_STUDENTS,
			mMapper.writeValueAsString(course.students));
		values.put(CoursesContract.Columns.COURSE_COLORS, course.colors);
		db = DatabaseHandler.getInstance(mContext)
			.getWritableDatabase();
		db.beginTransaction();
		try {
		    db.insertWithOnConflict(CoursesContract.TABLE, null,
			    values, SQLiteDatabase.CONFLICT_IGNORE);
		    db.setTransactionSuccessful();
		} catch (Exception e) {
		    e.printStackTrace();
		} finally {
		    db.endTransaction();
		}
	    } catch (Exception e) {
		e.printStackTrace();
	    }

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
		    courses.courses.add(new Course(cursor));
		} while (cursor.moveToNext());
	    }
	} catch (Exception e) {
	    e.printStackTrace();
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
	    course = new Course(cursor);
	} catch (Exception e) {
	    e.printStackTrace();
	}

	return course;
    }

}
