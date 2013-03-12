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

import android.provider.BaseColumns;

/**
 * @author joern
 * 
 */
public class CoursesContract extends AbstractContract {
    public static final String TABLE = "courses";
    // public static final String STUDENT_COURSE_USER_TABLE =
    // "student_course_user";
    // public static final String TEACHER_COURSE_USER_TABLE =
    // "teacher_course_user";
    // public static final String TUTOR_COURSE_USER_TABLE = "tutor_course_user";

    public static final String CREATE_STRING = String
	    .format("create table if not exists %s (%s integer primary key, %s text unique, "
		    + "%s date, %s date, %s double, %s text, %s text, %s text, %s text, "
		    + "%s text, %s text, %s text, %s text, %s text, %s text, %s text);",
		    TABLE, BaseColumns._ID, Columns.COURSE_ID,
		    Columns.COURSE_START_TIME, Columns.COURSE_DURATION_TIME,
		    Columns.COURSE_NUMBER, Columns.COURSE_TITLE,
		    Columns.COURSE_SUBTITLE, Columns.COURSE_DESCIPTION,
		    Columns.COURSE_LOCATION, Columns.COURSE_TYPE,
		    Columns.COURSE_SEMESERT_ID, Columns.COURSE_MODULES,
		    Columns.COURSE_TEACHERS, Columns.COURSE_TUTORS,
		    Columns.COURSE_STUDENTS, Columns.COURSE_COLORS);

    // public static final String CREATE_STUDENT_USER_STRING = String
    // .format("create table if not exists %s (_id integer primary key, %s text, %s text, "
    // +
    // "foreign key(%s) references %s(%s), foreign key(%s) references %s(%s));",
    // STUDENT_COURSE_USER_TABLE, Columns.COURSE_STUDENT_USER_ID,
    // Columns.COURSE_STUDENT_COURSE_ID,
    // Columns.COURSE_STUDENT_USER_ID, UsersContract.TABLE,
    // UsersContract.Columns.USER_ID,
    // Columns.COURSE_STUDENT_COURSE_ID, TABLE, Columns.COURSE_ID);
    //
    // public static final String CREATE_TEACHER_USER_STRING = String
    // .format("create table if not exists %s (_id integer primary key, %s text, %s text, "
    // +
    // "foreign key(%s) references %s(%s), foreign key(%s) references %s(%s));",
    // TEACHER_COURSE_USER_TABLE, Columns.COURSE_TEACHER_USER_ID,
    // Columns.COURSE_TEACHER_COURSE_ID,
    // Columns.COURSE_TEACHER_USER_ID, UsersContract.TABLE,
    // UsersContract.Columns.USER_ID,
    // Columns.COURSE_TEACHER_COURSE_ID, TABLE, Columns.COURSE_ID);
    //
    // public static final String CREATE_TUTOR_USER_STRING = String
    // .format("create table if not exists %s (_id integer primary key, %s text, %s text, "
    // +
    // "foreign key(%s) references %s(%s), foreign key(%s) references %s(%s));",
    // TUTOR_COURSE_USER_TABLE, Columns.COURSE_TUTOR_USER_ID,
    // Columns.COURSE_TUTOR_COURSE_ID,
    // Columns.COURSE_TUTOR_USER_ID, UsersContract.TABLE,
    // UsersContract.Columns.USER_ID,
    // Columns.COURSE_TUTOR_COURSE_ID, TABLE, Columns.COURSE_ID);

    private CoursesContract() {
    }

    public static final class Columns implements BaseColumns {
	private Columns() {
	}

	public static final String COURSE_ID = "course_id";
	public static final String COURSE_START_TIME = "start_time";
	public static final String COURSE_DURATION_TIME = "duration_time";
	public static final String COURSE_NUMBER = "number";
	public static final String COURSE_TITLE = "title";
	public static final String COURSE_SUBTITLE = "subtitle";
	public static final String COURSE_DESCIPTION = "description";
	public static final String COURSE_LOCATION = "location";
	public static final String COURSE_TYPE = "type";
	public static final String COURSE_SEMESERT_ID = "semester_id";
	public static final String COURSE_MODULES = "modules";
	public static final String COURSE_TEACHERS = "teachers";
	public static final String COURSE_TUTORS = "tutors";
	public static final String COURSE_STUDENTS = "students";
	public static final String COURSE_COLORS = "color";

	// public static final String COURSE_STUDENT_USER_ID = "user_id";
	// public static final String COURSE_STUDENT_COURSE_ID = "course_id";
	//
	// public static final String COURSE_TEACHER_USER_ID = "user_id";
	// public static final String COURSE_TEACHER_COURSE_ID = "course_id";
	//
	// public static final String COURSE_TUTOR_USER_ID = "user_id";
	// public static final String COURSE_TUTOR_COURSE_ID = "course_id";

    }
}
