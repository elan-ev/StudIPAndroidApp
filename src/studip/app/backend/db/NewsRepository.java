/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package studip.app.backend.db;

import java.text.SimpleDateFormat;

import studip.app.backend.datamodel.News;
import studip.app.backend.datamodel.NewsItem;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class NewsRepository {

    private static NewsRepository mInstance;
    private Context mContext;

    public static NewsRepository getInstance(Context context) {
	if (mInstance == null)
	    mInstance = new NewsRepository(context);

	return mInstance;
    }

    private NewsRepository(Context context) {
	this.mContext = context;
    }

    @SuppressLint("SimpleDateFormat")
    public void addNews(News n) {
	// Debug
	// db.execSQL("DELETE FROM " + TABLE_NEWS);
	SimpleDateFormat dateFormat = new SimpleDateFormat(
		"yyyy-MM-dd HH:mm:ss");
	SQLiteDatabase db = null;
	try {
	    for (NewsItem newsItem : n.news) {

		ContentValues values = new ContentValues();

		values.put(NewsContract.Columns.NEWS_ID, newsItem.news_id);
		values.put(NewsContract.Columns.NEWS_TOPIC, newsItem.topic);
		values.put(NewsContract.Columns.NEWS_BODY, newsItem.body);
		values.put(NewsContract.Columns.NEWS_DATE,
			dateFormat.format(newsItem.date));
		values.put(NewsContract.Columns.NEWS_USER_ID, newsItem.user_id);
		values.put(NewsContract.Columns.NEWS_CHDATE,
			dateFormat.format(newsItem.chdate));
		values.put(NewsContract.Columns.NEWS_MKDATE,
			dateFormat.format(newsItem.mkdate));

		db = DatabaseHandler.getInstance(mContext)
			.getWritableDatabase();
		db.insertWithOnConflict(NewsContract.TABLE, null, values,
			SQLiteDatabase.CONFLICT_IGNORE);

	    }
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    db.close();
	}
    }

    @SuppressLint("SimpleDateFormat")
    public News getAllNews() {
	// TODO getObject benutzen, doppelten Code verhindern
	String selectQuery = "SELECT  * FROM " + NewsContract.TABLE;
	SimpleDateFormat dateFormat = new SimpleDateFormat(
		"yyyy-MM-dd HH:mm:ss");
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
				    dateFormat.parse(cursor.getString(cursor
					    .getColumnIndex(NewsContract.Columns.NEWS_DATE))),
				    cursor.getString(cursor
					    .getColumnIndex(NewsContract.Columns.NEWS_USER_ID)),
				    dateFormat.parse(cursor.getString(cursor
					    .getColumnIndex(NewsContract.Columns.NEWS_CHDATE))),
				    dateFormat.parse(cursor.getString(cursor
					    .getColumnIndex(NewsContract.Columns.NEWS_MKDATE)))));

		} while (cursor.moveToNext());
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    cursor.close();
	    db.close();
	}

	return news;
    }
}
