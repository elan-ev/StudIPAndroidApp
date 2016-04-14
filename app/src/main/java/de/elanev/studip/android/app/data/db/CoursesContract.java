/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package de.elanev.studip.android.app.data.db;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author joern
 */
public class CoursesContract extends AbstractContract {

  /*
   * table names
   */
  public static final String TABLE_COURSES = "courses";
  /*
   * table creation strings
   */
  // courses table
  public static final String CREATE_TABLE_COURSES_STRING = String.format(
      "CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY, %s TEXT UNIQUE, " +
          "%s INTEGER, %s INTEGER, %s INTEGER, %s TEXT, %s TEXT, %s TEXT, %s TEXT, " +
          "%s TEXT, %s TEXT, %s TEXT, %s TEXT);",
      TABLE_COURSES,
      Columns.Courses._ID,
      Columns.Courses.COURSE_ID,
      Columns.Courses.COURSE_START_TIME,
      Columns.Courses.COURSE_DURATION_TIME,
      Columns.Courses.COURSE_NUMBER,
      Columns.Courses.COURSE_TITLE,
      Columns.Courses.COURSE_SUBTITLE,
      Columns.Courses.COURSE_DESCIPTION,
      Columns.Courses.COURSE_LOCATION,
      Columns.Courses.COURSE_TYPE,
      Columns.Courses.COURSE_SEMESERT_ID,
      Columns.Courses.COURSE_MODULES,
      Columns.Courses.COURSE_COLOR
  );
  // courses and semesters joined
  public static final String COURSES_JOIN_SEMESTERS = String.format("%s INNER JOIN %s on %s = %s ",
      TABLE_COURSES,
      SemestersContract.TABLE,
      Qualified.Courses.COURSES_COURSE_SEMESERT_ID,
      SemestersContract.Qualified.SEMESTERS_SEMESTER_ID);
  public static final String TABLE_COURSE_USER = "course_users";
  // course users table
  public static final String CREATE_TABLE_COURSE_USERS_STRING = String.format(
      "CREATE TABLE IF NOT EXISTS %s " +
          "(%s INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s INTEGER," +
          " UNIQUE (%s, %s) ON CONFLICT IGNORE);",
      TABLE_COURSE_USER,
      Columns.CourseUsers._ID,
      Columns.CourseUsers.COURSE_USER_USER_ID,
      Columns.CourseUsers.COURSE_USER_COURSE_ID,
      Columns.CourseUsers.COURSE_USER_USER_ROLE,
      Columns.CourseUsers.COURSE_USER_USER_ID,
      Columns.CourseUsers.COURSE_USER_COURSE_ID
  );
  /*
   * joins
   */
  // courses and users joined
  public static final String COURSES_JOIN_USERS = String.format(
      "%s INNER JOIN %s on %s = %s INNER JOIN %s on %s = %s ",
      TABLE_COURSES,
      TABLE_COURSE_USER,
      Qualified.CourseUsers.COURSES_USERS_TABLE_COURSE_USER_COURSE_ID,
      Qualified.Courses.COURSES_COURSE_ID,
      UsersContract.TABLE,
      Qualified.CourseUsers.COURSES_USERS_TABLE_COURSE_USER_USER_ID,
      UsersContract.Qualified.USERS_USER_ID);
  // courses, users and events joined
  public static final String COURSES_JOIN_USERS_EVENTS = COURSES_JOIN_USERS + String.format(
      "INNER JOIN %s on %s = %s ",
      EventsContract.TABLE,
      EventsContract.Qualified.EVENTS_EVENT_COURSE_ID,
      Qualified.Courses.COURSES_COURSE_ID);
  // courses, users and semsters joined
  public static final String COURSES_JOIN_USERS_SEMESTERS = String.format(
      "%s INNER JOIN %s on %s = %s INNER JOIN %s on %s = %s INNER JOIN %s on %s = %s ",
      TABLE_COURSES,
      TABLE_COURSE_USER,
      Qualified.CourseUsers.COURSES_USERS_TABLE_COURSE_USER_COURSE_ID,
      Qualified.Courses.COURSES_COURSE_ID,
      UsersContract.TABLE,
      Qualified.CourseUsers.COURSES_USERS_TABLE_COURSE_USER_USER_ID,
      UsersContract.Qualified.USERS_USER_ID,
      SemestersContract.TABLE,
      Qualified.Courses.COURSES_COURSE_SEMESERT_ID,
      SemestersContract.Qualified.SEMESTERS_SEMESTER_ID);
  /*
   * content provider
   */
  // pathes
  public static final String PATH_COURSES = "courses";
  public static final String PATH_COURSES_USERS = "users";

  // content uris
  public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
      .appendPath(PATH_COURSES)
      .build();
  public static final Uri COURSES_USERS_CONTENT_URI = CONTENT_URI.buildUpon()
      .appendPath(PATH_COURSES_USERS)
      .build();

  // content mime types
  public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.studip.courses";
  public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.studip.courses";

  // default sort orders
  public static final String DEFAULT_SORT_ORDER = Qualified.Courses.COURSES_COURSE_TITLE + " ASC";

  public static final String COURSE_USERS_DEFAULT_SORT =
      CoursesContract.Qualified.CourseUsers.COURSES_USERS_TABLE_COURSE_USER_USER_ROLE + " ASC, " +
          CoursesContract.Qualified.CourseUsers.COURSES_USERS_TABLE_ID + " ASC";

  /*
   * user course roles
   */
  public static final int USER_ROLE_TEACHER = 1000;
  public static final int USER_ROLE_TUTOR = 1001;
  public static final int USER_ROLE_STUDENT = 1002;

  // no constructor
  private CoursesContract() {
  }

  /*
   * table columns
   */
  public static final class Columns {
    private Columns() {
    }

    /*
     * courses table columns
     */
    public static interface Courses extends BaseColumns {
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
      public static final String COURSE_COLOR = "color";
    }

    /*
     * course users table columns
     */
    public static interface CourseUsers extends BaseColumns {
      public static final String COURSE_USER_USER_ID = "user_id";
      public static final String COURSE_USER_COURSE_ID = "course_id";
      public static final String COURSE_USER_USER_ROLE = "user_role";
    }
  }

  /*
   * qualified column names
   */
  public static final class Qualified {

    /*
     * courses columns
     */
    public static interface Courses {
      public static final String COURSES_ID = TABLE_COURSES + "." + Columns.Courses._ID;
      public static final String COURSES_COURSE_ID =
          TABLE_COURSES + "." + Columns.Courses.COURSE_ID;
      public static final String COURSES_COURSE_START_TIME =
          TABLE_COURSES + "." + Columns.Courses.COURSE_START_TIME;
      public static final String COURSES_COURSE_DURATION_TIME =
          TABLE_COURSES + "." + Columns.Courses.COURSE_DURATION_TIME;
      public static final String COURSES_COURSE_NUMBER =
          TABLE_COURSES + "." + Columns.Courses.COURSE_NUMBER;
      public static final String COURSES_COURSE_TITLE =
          TABLE_COURSES + "." + Columns.Courses.COURSE_TITLE;
      public static final String COURSES_COURSE_SUBTITLE =
          TABLE_COURSES + "." + Columns.Courses.COURSE_SUBTITLE;
      public static final String COURSES_COURSE_DESCIPTION =
          TABLE_COURSES + "." + Columns.Courses.COURSE_DESCIPTION;
      public static final String COURSES_COURSE_LOCATION =
          TABLE_COURSES + "." + Columns.Courses.COURSE_LOCATION;
      public static final String COURSES_COURSE_TYPE =
          TABLE_COURSES + "." + Columns.Courses.COURSE_TYPE;
      public static final String COURSES_COURSE_SEMESERT_ID =
          TABLE_COURSES + "." + Columns.Courses.COURSE_SEMESERT_ID;
      public static final String COURSES_COURSE_MODULES =
          TABLE_COURSES + "." + Columns.Courses.COURSE_MODULES;
      public static final String COURSES_COURSE_COLOR =
          TABLE_COURSES + "." + Columns.Courses.COURSE_COLOR;
    }

    /*
     * course users column names
     */
    public static interface CourseUsers {
      public static final String COURSES_USERS_TABLE_ID =
          TABLE_COURSE_USER + "." + Columns.CourseUsers._ID;
      public static final String COURSES_USERS_TABLE_COURSE_USER_USER_ID =
          TABLE_COURSE_USER + "." + Columns.CourseUsers.COURSE_USER_USER_ID;
      public static final String COURSES_USERS_TABLE_COURSE_USER_COURSE_ID =
          TABLE_COURSE_USER + "." + Columns.CourseUsers.COURSE_USER_COURSE_ID;
      public static final String COURSES_USERS_TABLE_COURSE_USER_USER_ROLE =
          TABLE_COURSE_USER + "." + Columns.CourseUsers.COURSE_USER_USER_ROLE;
    }

  }

}
