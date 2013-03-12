/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.db;

import android.provider.BaseColumns;

public class FoldersContract {
	public static final String TABLE = "folders";
	public static final String CREATE_STRING = String.format(
			"create table if not exists %s (%s integer primary key, %s text unique, "
					+ "%s text, %s text, %s text, %s date, %s date, %s text)",
			TABLE, BaseColumns._ID, Columns.FOLDER_ID, Columns.FOLDER_USER_ID,
			Columns.FOLDER_NAME, Columns.FOLDER_DESCRIPTION,
			Columns.FOLDER_MKDATE, Columns.FOLDER_CHDATE,
			Columns.FOLDER_PERMISSIONS);

	public FoldersContract() {
	}

	public static final class Columns implements BaseColumns {
		private Columns() {
		}

		public static final String FOLDER_ID = "folder_id";
		public static final String FOLDER_USER_ID = "user_id";
		public static final String FOLDER_NAME = "name";
		public static final String FOLDER_DESCRIPTION = "description";
		public static final String FOLDER_MKDATE = "mkdate";
		public static final String FOLDER_CHDATE = "chdate";
		public static final String FOLDER_PERMISSIONS = "permissions"; // Saved
		// as
		// JSON-String

	}

}
