/*
 * Copyright (c) 2014 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package de.elanev.studip.android.app.backend.db;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author joern
 */
public final class NewsContract extends AbstractContract {
    // Table
    public static final String TABLE = "news";
    public static final String CREATE_STRING = String.format(
            "create table if not exists %s (%s integer primary key, %s text unique,"
                    + " %s text, %s text, %s integer, %s text, %s integer, %s integer, "
                    + "%s integer, %s integer, %s text, %s text, %s text)",
            TABLE,
            BaseColumns._ID,
            Columns.NEWS_ID,
            Columns.NEWS_TOPIC,
            Columns.NEWS_BODY,
            Columns.NEWS_DATE,
            Columns.NEWS_USER_ID,
            Columns.NEWS_CHDATE,
            Columns.NEWS_MKDATE,
            Columns.NEWS_EXPIRE,
            Columns.NEWS_ALLOW_COMMENTS,
            Columns.NEWS_CHDATE_UID,
            Columns.NEWS_BODY_ORIGINAL,
            Columns.NEWS_RANGE_ID
    );

    public static final String NEWS_JOIN_USER = String.format(
            "%s INNER JOIN %s ON %s = %s ",
            TABLE,
            UsersContract.TABLE,
            Qualified.NEWS_NEWS_USER_ID,
            UsersContract.Qualified.USERS_USER_ID
    );

    public static final String NEWS_JOIN_USER_INSTITUTES = NEWS_JOIN_USER +
            String.format(
                    "INNER JOIN %s ON %s = %s",
                    InstitutesContract.TABLE,
                    Qualified.NEWS_NEWS_RANGE_ID,
                    InstitutesContract.Qualified.INSTITUTES_INSTITUTE_ID
            );

    public static final String NEWS_JOIN_USER_COURSES = NEWS_JOIN_USER +
            String.format(
                    "INNER JOIN %s ON %s = %s",
                    CoursesContract.TABLE_COURSES,
                    Qualified.NEWS_NEWS_RANGE_ID,
                    CoursesContract.Qualified.Courses.COURSES_COURSE_ID
            );

    // ContentProvider
    public static final String PATH = "news";

    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
            .appendPath(PATH)
            .build();

    public static final Uri GLOBAL_CONTENT_URI = BASE_CONTENT_URI.buildUpon()
            .appendPath(PATH)
            .appendPath("global")
            .build();

    public static final Uri COURSES_CONTENT_URI = BASE_CONTENT_URI.buildUpon()
            .appendPath(PATH)
            .appendPath("courses")
            .build();

    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.studip.news";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.studip.news";
    public static final String DEFAULT_SORT_ORDER = Qualified.NEWS_NEWS_MKDATE
            + " DESC";

    // Table Columns
    public NewsContract() {
    }

    public interface Qualified {
        public static final String NEWS_ID = TABLE + "." + BaseColumns._ID;
        public static final String NEWS_COUNT = TABLE + "."
                + BaseColumns._COUNT;
        public static final String NEWS_NEWS_ID = TABLE + "." + Columns.NEWS_ID;
        public static final String NEWS_NEWS_TOPIC = TABLE + "."
                + Columns.NEWS_TOPIC;
        public static final String NEWS_NEWS_BODY = TABLE + "."
                + Columns.NEWS_BODY;
        public static final String NEWS_NEWS_DATE = TABLE + "."
                + Columns.NEWS_DATE;
        public static final String NEWS_NEWS_USER_ID = TABLE + "."
                + Columns.NEWS_USER_ID;
        public static final String NEWS_NEWS_CHDATE = TABLE + "."
                + Columns.NEWS_CHDATE;
        public static final String NEWS_NEWS_MKDATE = TABLE + "."
                + Columns.NEWS_MKDATE;
        public static final String NEWS_NEWS_EXPIRE = TABLE + "."
                + Columns.NEWS_EXPIRE;
        public static final String NEWS_NEWS_ALLOW_COMMENTS = TABLE + "."
                + Columns.NEWS_ALLOW_COMMENTS;
        public static final String NEWS_NEWS_CHDATE_UID = TABLE + "."
                + Columns.NEWS_CHDATE_UID;
        public static final String NEWS_NEWS_BODY_ORIGINAL = TABLE + "."
                + Columns.NEWS_BODY_ORIGINAL;
        public static final String NEWS_NEWS_RANGE_ID = TABLE + "."
                + Columns.NEWS_RANGE_ID;
    }

    public static final class Columns implements BaseColumns {
        public static final String NEWS_ID = "news_id";
        public static final String NEWS_TOPIC = "topic";
        public static final String NEWS_BODY = "body";
        public static final String NEWS_DATE = "date";
        public static final String NEWS_USER_ID = "user_id";
        public static final String NEWS_CHDATE = "chdate";
        public static final String NEWS_MKDATE = "mkdate";
        public static final String NEWS_EXPIRE = "expire";
        public static final String NEWS_ALLOW_COMMENTS = "allow_comments";
        public static final String NEWS_CHDATE_UID = "chdate_uid";
        public static final String NEWS_BODY_ORIGINAL = "body_original";
        public static final String NEWS_RANGE_ID = "range_id";

        private Columns() {
        }

    }

}
