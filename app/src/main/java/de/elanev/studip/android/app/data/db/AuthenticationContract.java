/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
/**
 *
 */
package de.elanev.studip.android.app.data.db;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author joern
 */
public class AuthenticationContract extends AbstractContract {

    /*
     * table names
     */
    public static final String TABLE_AUTHENTICATION = "authentication";

    /*
     * table creation strings
     */
    // courses table
    public static final String CREATE_TABLE_AUTHENTICATION = String
            .format("CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY, %s TEXT UNIQUE, "
                    + "%s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT);",
                    TABLE_AUTHENTICATION,
                    Columns.Authentication._ID,
                    Columns.Authentication.SERVER_NAME,
                    Columns.Authentication.SERVER_URL,
                    Columns.Authentication.SERVER_KEY,
                    Columns.Authentication.SERVER_SECRET,
                    Columns.Authentication.SERVER_CONTACT_EMAIL,
                    Columns.Authentication.ACCESS_TOKEN,
                    Columns.Authentication.ACCESS_TOKEN_SECRET);


    /*
     * content sProvider
     */
    // pathes
    public static final String PATH_AUTHENTICATION = "authentication";

    // content uris
    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
            .appendPath(PATH_AUTHENTICATION).build();

    // content mime types
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.studip.authentication";


    // no constructor
    private AuthenticationContract() {
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
        public static interface Authentication extends BaseColumns {
            public static final String SERVER_NAME = "server_name";
            public static final String ACCESS_TOKEN = "access_token";
            public static final String ACCESS_TOKEN_SECRET = "access_token_secret";
            public static final String SERVER_URL = "server_url";
            public static final String SERVER_KEY = "server_key";
            public static final String SERVER_SECRET = "server_secret";
            public static final String SERVER_CONTACT_EMAIL = "server_contact_email";

        }
    }

}
