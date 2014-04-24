/*
 * Copyright (c) 2014 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.backend.db;

/**
 * Created by joern on 05.04.14.
 */

import android.net.Uri;
import android.provider.BaseColumns;

public class InstitutesContract extends AbstractContract {

    public static final String TABLE = "institutes";

    public static final String CREATE_STRING = String.format(
            "create table if not exists %s (%s integer primary key, %s text unique,"
                    + " %s text, %s text, %s text, %s text, %s text, %s text," +
                    " %s text, %s text, %s text, %s text, %s text)",
            TABLE,
            BaseColumns._ID,
            Columns.INSTITUTE_ID,
            Columns.INSTITUTE_NAME,
            Columns.INSTITUTE_PERMS,
            Columns.INSTITUTE_CONSULTATION,
            Columns.INSTITUTE_ROOM,
            Columns.INSTITUTE_PHONE,
            Columns.INSTITUTE_FAX,
            Columns.INSTITUTE_STREET,
            Columns.INSTITUTE_CITY,
            Columns.INSTITUTE_FACULTY_NAME,
            Columns.INSTITUTE_FACULTY_STREET,
            Columns.INSTITUTE_FACULTY_CITY
    );

    // Content Provider
    public static final String PATH = "institutes";
    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
            .appendPath(PATH).build();
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd" +
            ".studip.institutes";
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor" +
            ".item/vnd.studip.instititues";
    public static final String DEFAULT_SORT_ORDER = Qualified.INSTITUTES_INSTITUTE_NAME
            + " ASC";

    private InstitutesContract() {
    }

    public interface Qualified {
        public static final String INSTITUTES_ID = TABLE + "." + BaseColumns._ID;
        public static final String INSTITUTES_COUNT = TABLE + "."
                + BaseColumns._COUNT;
        public static final String INSTITUTES_INSTITUTE_ID = TABLE + "."
                + Columns.INSTITUTE_ID;
        public static final String INSTITUTES_INSTITUTE_NAME = TABLE + "."
                + Columns.INSTITUTE_NAME;
        public static final String INSTITUTES_INSTITUTE_PERMS = TABLE + "."
                + Columns.INSTITUTE_PERMS;
        public static final String INSTITUTES_INSTITUTE_CONSULTATION = TABLE +
                "."
                + Columns.INSTITUTE_CONSULTATION;
        public static final String INSTITUTES_INSTITUTE_ROOM = TABLE + "."
                + Columns.INSTITUTE_ROOM;
        public static final String INSTITUTES_INSTITUTE_PHONE = TABLE
                + "." + Columns.INSTITUTE_PHONE;
        public static final String INSTITUTES_INSTITUTE_FAX = TABLE
                + "." + Columns.INSTITUTE_FAX;
        public static final String INSTITUTES_INSTITUTE_STREET = TABLE
                + "." + Columns.INSTITUTE_STREET;
        public static final String INSTITUTES_INSTITUTE_CITY = TABLE
                + "." + Columns.INSTITUTE_CITY;
        public static final String INSTITUTES_INSTITUTE_FACULTY_NAME = TABLE
                + "." + Columns.INSTITUTE_FACULTY_NAME;
        public static final String INSTITUTES_INSTITUTE_FACULTY_STREET = TABLE
                + "." + Columns.INSTITUTE_FACULTY_STREET;
        public static final String INSTITUTES_INSTITUTE_FACULTY_CITY = TABLE
                + "." + Columns.INSTITUTE_FACULTY_CITY;
    }

    public static final class Columns implements BaseColumns {
        public static final String INSTITUTE_ID = "institute_id";
        public static final String INSTITUTE_NAME = "name";
        public static final String INSTITUTE_PERMS = "perms";
        public static final String INSTITUTE_CONSULTATION = "consultation";
        public static final String INSTITUTE_ROOM = "room";
        public static final String INSTITUTE_PHONE = "phone";
        public static final String INSTITUTE_FAX = "fax";
        public static final String INSTITUTE_STREET = "street";
        public static final String INSTITUTE_CITY = "city";
        public static final String INSTITUTE_FACULTY_NAME = "faculty_name";
        public static final String INSTITUTE_FACULTY_STREET = "faculty_street";
        public static final String INSTITUTE_FACULTY_CITY = "faculty_city";

        private Columns() {
        }
    }

}

