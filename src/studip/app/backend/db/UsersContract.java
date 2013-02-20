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
public class UsersContract {
    public static final String TABLE = "users";
    public static final String CREATE_STRING = String
	    .format("create table if not exists %s (%s text primary key, %s text, %s text, %s text, %s text, %s text, %s text, %s text, %s text, %s text, %s text, %s text, %s text, %s text)",
		    TABLE, Columns.USER_ID, Columns.USER_USERNAME,
		    Columns.USER_PERMS, Columns.USER_TITLE_PRE,
		    Columns.USER_FORENAME, Columns.USER_LASTNAME,
		    Columns.USER_TITLE_POST, Columns.USER_EMAIL,
		    Columns.USER_AVATAR_SMALL, Columns.USER_AVATAR_MEDIUM,
		    Columns.USER_AVATAR_NORMAL, Columns.USER_PHONE,
		    Columns.USER_HOMEPAGE, Columns.USER_PRIVADR);

    public UsersContract() {
    }

    public static final class Columns implements BaseColumns {
	private Columns() {
	}

	public static final String USER_ID = "user_id";
	public static final String USER_USERNAME = "username";
	public static final String USER_PERMS = "perms";
	public static final String USER_TITLE_PRE = "title_pre";
	public static final String USER_FORENAME = "forename";
	public static final String USER_LASTNAME = "lastname";
	public static final String USER_TITLE_POST = "title_post";
	public static final String USER_EMAIL = "email";
	public static final String USER_AVATAR_SMALL = "avatar_small";
	public static final String USER_AVATAR_MEDIUM = "avatar_medium";
	public static final String USER_AVATAR_NORMAL = "avatar_normal";
	public static final String USER_PHONE = "phone";
	public static final String USER_HOMEPAGE = "homepage";
	public static final String USER_PRIVADR = "privadr";
    }
}
