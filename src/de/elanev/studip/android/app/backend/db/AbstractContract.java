/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.db;

import android.provider.BaseColumns;

public abstract class AbstractContract {
	public String TABLE;
	public static String CREATE_STRING;

	protected AbstractContract() {
	}

	public static abstract class Columns implements BaseColumns {
	}
}
