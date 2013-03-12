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
package de.elanev.studip.android.app.backend.db;

import android.provider.BaseColumns;

/**
 * @author joern
 * 
 */
public class NewsContract {
	public static final String TABLE = "news";
	public static final String CREATE_STRING = String.format(
			"create table if not exists %s (%s integer primary key, %s text unique,"
					+ " %s text, %s text, %s date, %s text, %s date, %s date)",
			TABLE, BaseColumns._ID, Columns.NEWS_ID, Columns.NEWS_TOPIC,
			Columns.NEWS_BODY, Columns.NEWS_DATE, Columns.NEWS_USER_ID,
			Columns.NEWS_CHDATE, Columns.NEWS_MKDATE);

	public NewsContract() {
	}

	public static final class Columns implements BaseColumns {
		private Columns() {
		}

		public static final String NEWS_ID = "news_id";
		public static final String NEWS_TOPIC = "topic";
		public static final String NEWS_BODY = "body";
		public static final String NEWS_DATE = "date";
		public static final String NEWS_USER_ID = "user_id";
		public static final String NEWS_CHDATE = "chdate";
		public static final String NEWS_MKDATE = "mkdate";
	}
}
