/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.net.sync;

import java.util.ArrayList;

import android.content.ContentProviderOperation;
import de.elanev.studip.android.app.backend.datamodel.News;
import de.elanev.studip.android.app.backend.datamodel.NewsItem;
import de.elanev.studip.android.app.backend.db.NewsContract;

/**
 * @author joern
 * 
 */
public class NewsHandler implements ResultHandler {

	private News mNews;
	private String mCourseId;

	public NewsHandler(News news, String courseId) {
		this.mNews = news;
		this.mCourseId = courseId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.elanev.studip.android.app.backend.net.sync.ResultHandler#parse()
	 */
	public ArrayList<ContentProviderOperation> parse() {
		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

		for (NewsItem newsItem : mNews.news) {
			operations.add(parseNewsItem(newsItem));
		}

		return operations;
	}

	private ContentProviderOperation parseNewsItem(NewsItem news) {
		ContentProviderOperation.Builder builder = ContentProviderOperation
				.newInsert(NewsContract.CONTENT_URI);
		builder.withValue(NewsContract.Columns.NEWS_ID, news.news_id);
		builder.withValue(NewsContract.Columns.NEWS_TOPIC, news.topic);
		builder.withValue(NewsContract.Columns.NEWS_BODY, news.body);
		builder.withValue(NewsContract.Columns.NEWS_DATE, news.date * 1000L);
		builder.withValue(NewsContract.Columns.NEWS_USER_ID, news.user_id);
		builder.withValue(NewsContract.Columns.NEWS_CHDATE, news.chdate * 1000L);
		builder.withValue(NewsContract.Columns.NEWS_MKDATE, news.mkdate * 1000L);
		builder.withValue(NewsContract.Columns.NEWS_EXPIRE, news.expire * 1000L);
		builder.withValue(NewsContract.Columns.NEWS_ALLOW_COMMENTS,
				news.allow_comments);
		builder.withValue(NewsContract.Columns.NEWS_CHDATE_UID, news.chdate_uid);
		builder.withValue(NewsContract.Columns.NEWS_BODY_ORIGINAL,
				news.body_original);
		builder.withValue(NewsContract.Columns.NEWS_RANGE_ID, mCourseId);

		return builder.build();
	}
}
