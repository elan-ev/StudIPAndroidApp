/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.elanev.studip.android.app.backend.datamodel.News;
import de.elanev.studip.android.app.backend.datamodel.NewsItem;

public class NewsRepository {

	public static final String TAG = NewsRepository.class.getSimpleName();
	private static NewsRepository mInstance;
	private Context mContext;

	public static synchronized NewsRepository getInstance(Context context) {
		if (mInstance == null)
			mInstance = new NewsRepository(context);

		return mInstance;
	}

	private NewsRepository(Context context) {
		this.mContext = context;
	}

	public void addNews(News n) {
		SQLiteDatabase db = null;
		// Debug
		// db.execSQL("DELETE FROM " + TABLE_NEWS);

		try {
			for (NewsItem newsItem : n.news) {

				ContentValues values = new ContentValues();

				values.put(NewsContract.Columns.NEWS_ID, newsItem.news_id);
				values.put(NewsContract.Columns.NEWS_TOPIC, newsItem.topic);
				values.put(NewsContract.Columns.NEWS_BODY, newsItem.body);
				values.put(NewsContract.Columns.NEWS_DATE, newsItem.date);
				values.put(NewsContract.Columns.NEWS_USER_ID, newsItem.user_id);
				values.put(NewsContract.Columns.NEWS_CHDATE, newsItem.chdate);
				values.put(NewsContract.Columns.NEWS_MKDATE, newsItem.mkdate);
				values.put(NewsContract.Columns.NEWS_EXPIRE, newsItem.expire);
				values.put(NewsContract.Columns.NEWS_ALLOW_COMMENTS,
						newsItem.allow_comments);
				values.put(NewsContract.Columns.NEWS_CHDATE_UID,
						newsItem.chdate_uid);
				values.put(NewsContract.Columns.NEWS_BODY_ORIGINAL,
						newsItem.body_original);

				db = DatabaseHandler.getInstance(mContext)
						.getWritableDatabase();
				db.beginTransaction();
				try {
					db.insertWithOnConflict(NewsContract.TABLE, null, values,
							SQLiteDatabase.CONFLICT_IGNORE);
					db.setTransactionSuccessful();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					db.endTransaction();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public News getAllNews() {

		String selectQuery = "SELECT  * FROM " + NewsContract.TABLE;
		SQLiteDatabase db = DatabaseHandler.getInstance(mContext)
				.getReadableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		News news = new News();
		try {
			if (cursor.moveToFirst()) {
				do {

					news.news
							.add(new NewsItem(
									cursor.getString(cursor
											.getColumnIndex(NewsContract.Columns.NEWS_ID)),
									cursor.getString(cursor
											.getColumnIndex(NewsContract.Columns.NEWS_TOPIC)),
									cursor.getString(cursor
											.getColumnIndex(NewsContract.Columns.NEWS_BODY)),
									cursor.getLong(cursor
											.getColumnIndex(NewsContract.Columns.NEWS_DATE)),
									cursor.getString(cursor
											.getColumnIndex(NewsContract.Columns.NEWS_USER_ID)),
									cursor.getLong(cursor
											.getColumnIndex(NewsContract.Columns.NEWS_CHDATE)),
									cursor.getLong(cursor
											.getColumnIndex(NewsContract.Columns.NEWS_MKDATE)),
									cursor.getLong(cursor
											.getColumnIndex(NewsContract.Columns.NEWS_EXPIRE)),
									cursor.getInt(cursor
											.getColumnIndex(NewsContract.Columns.NEWS_ALLOW_COMMENTS)),
									cursor.getString(cursor
											.getColumnIndex(NewsContract.Columns.NEWS_CHDATE_UID)),
									cursor.getString(cursor
											.getColumnIndex(NewsContract.Columns.NEWS_BODY_ORIGINAL))));

				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return news;
	}

	/**
	 * @return
	 */
	public Cursor getCurrentNewsCursor() {
		SQLiteDatabase db = DatabaseHandler.getInstance(mContext)
				.getReadableDatabase();
		Cursor cursor = null;
		// cursor = db.query(NewsContract.TABLE, null,
		// NewsContract.Columns.NEWS_DATE + ">= ?",
		// new String[] { "strftime('%s','now')" }, null, null,
		// NewsContract.Columns.NEWS_DATE + " ASC");
		// String query = String.format(
		// "select * from %s, %s where %s.%s = %s.%s AND %s.%s >= %s",
		// NewsContract.TABLE, UsersContract.TABLE, NewsContract.TABLE,
		// NewsContract.Columns.NEWS_USER_ID, UsersContract.TABLE,
		// UsersContract.Columns.USER_ID, NewsContract.TABLE,
		// NewsContract.Columns.NEWS_EXPIRE, "strftime('%s','now')");
		String query = String.format(
				"select * from %s, %s where %s.%s = %s.%s", NewsContract.TABLE,
				UsersContract.TABLE, NewsContract.TABLE,
				NewsContract.Columns.NEWS_USER_ID, UsersContract.TABLE,
				UsersContract.Columns.USER_ID, NewsContract.TABLE);
		Log.d(TAG, query);
		cursor = db.rawQuery(query, null);
		return cursor;
	}
}
