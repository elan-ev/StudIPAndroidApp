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

public abstract class AbstractContract {
	public String TABLE;
	public static String CREATE_STRING;
	public static final String CONTENT_AUTHORITY = "de.elanev.studip.android.app";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

	protected AbstractContract() {
	}

	public static abstract class Columns implements BaseColumns {
	}
}
