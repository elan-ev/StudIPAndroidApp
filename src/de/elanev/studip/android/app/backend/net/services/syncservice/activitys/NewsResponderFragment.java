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
package de.elanev.studip.android.app.backend.net.services.syncservice.activitys;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;

import com.actionbarsherlock.app.SherlockListFragment;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.datamodel.Course;
import de.elanev.studip.android.app.backend.datamodel.Courses;
import de.elanev.studip.android.app.backend.datamodel.News;
import de.elanev.studip.android.app.backend.datamodel.NewsItem;
import de.elanev.studip.android.app.backend.datamodel.User;
import de.elanev.studip.android.app.backend.db.CoursesRepository;
import de.elanev.studip.android.app.backend.db.NewsContract;
import de.elanev.studip.android.app.backend.db.UsersRepository;
import de.elanev.studip.android.app.backend.net.api.ApiEndpoints;
import de.elanev.studip.android.app.backend.net.services.syncservice.AbstractParserTask;
import de.elanev.studip.android.app.backend.net.services.syncservice.RestApiRequest;
import de.elanev.studip.android.app.backend.net.services.syncservice.RestIPSyncService;

/**
 * @author joern
 * 
 */
public class NewsResponderFragment extends
		AbstractRestIPResultReceiver<News, SherlockListFragment> {

	// private static final String TAG =
	// NewsResponderFragment.class.getSimpleName();
	private boolean newData = false;

	public void loadData() {
		if (getActivity() != null) {
			if (mContext != null && !newData) {
				loadNews();
			}
		}
	}

	private void loadNews() {
		Intent intent = new Intent(mContext, RestIPSyncService.class);
		intent.setData(Uri.parse(mServerApiUrl + "/"
				+ ApiEndpoints.NEWS_GLOBAL_ENDPOINT));

		intent.putExtra(RestIPSyncService.RESTIP_RESULT_RECEIVER,
				getResultReceiver());

		mContext.startService(intent);

		// TODO change to contentprovider
		Courses c = CoursesRepository.getInstance(mContext).getAllCourses();

		if (c != null) {
			for (Course course : c.courses) {
				String cid = course.course_id;
				if (cid != null) {
					Intent i = new Intent(mContext, RestIPSyncService.class);
					i.setData(Uri.parse(mServerApiUrl + "/"
							+ String.format(ApiEndpoints.NEWS_ENDPOINT, cid)));

					i.putExtra(RestIPSyncService.RESTIP_RESULT_RECEIVER,
							getResultReceiver());

					mContext.startService(i);
				}
			}

		}

	}

	class NewsParserTask extends AbstractParserTask<News> {
		ContentResolver resolver = mContext.getContentResolver();

		@Override
		protected News doInBackground(String... params) {
			News items = new News();
			JsonParser jp;
			try {
				jp = jsonFactory.createJsonParser(params[0]);
				items = objectMapper.readValue(jp, News.class);
			} catch (JsonParseException e) {
				e.printStackTrace();
				cancel(true);

			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (items != null && items.news.size() != 0) {
				RestApiRequest userLoader = new RestApiRequest();
				ArrayList<User> userList = new ArrayList<User>();
				UsersRepository userDB = UsersRepository.getInstance(mContext);
				for (NewsItem newsItem : items.news) {
					if (!userDB.userExists(newsItem.user_id)) {
						String userResponse = userLoader.get(
								ApiEndpoints.USER_ENDPOINT, newsItem.user_id);
						User user = null;
						try {
							// unwrap element
							JSONObject jsono = (new JSONObject(userResponse))
									.getJSONObject("user");
							jp = jsonFactory.createJsonParser(jsono.toString());
							user = objectMapper.readValue(jp, User.class);
						} catch (JsonParseException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (JSONException e) {
							e.printStackTrace();
						}
						if (user != null) {
							userList.add(user);
						}

					}
				}
				if (!userList.isEmpty()) {
					userDB.addUsers(userList);
				}
			}
			if (!items.news.isEmpty()) {
				// NewsRepository.getInstance(mContext).addNews(items);
				for (NewsItem newsItem : items.news) {

					ContentValues values = new ContentValues();

					values.put(NewsContract.Columns.NEWS_ID, newsItem.news_id);
					values.put(NewsContract.Columns.NEWS_TOPIC, newsItem.topic);
					values.put(NewsContract.Columns.NEWS_BODY, newsItem.body);
					values.put(NewsContract.Columns.NEWS_DATE,
							newsItem.date * 1000L);
					values.put(NewsContract.Columns.NEWS_USER_ID,
							newsItem.user_id);
					values.put(NewsContract.Columns.NEWS_CHDATE,
							newsItem.chdate * 1000L);
					values.put(NewsContract.Columns.NEWS_MKDATE,
							newsItem.mkdate * 1000L);
					values.put(NewsContract.Columns.NEWS_EXPIRE,
							newsItem.expire * 1000L);
					values.put(NewsContract.Columns.NEWS_ALLOW_COMMENTS,
							newsItem.allow_comments);
					values.put(NewsContract.Columns.NEWS_CHDATE_UID,
							newsItem.chdate_uid);
					values.put(NewsContract.Columns.NEWS_BODY_ORIGINAL,
							newsItem.body_original);
					values.put(NewsContract.Columns.NEWS_COURSE_ID,
							mResponseUri.getLastPathSegment());

					resolver.insert(NewsContract.CONTENT_URI, values);
				}
			}
			return items;
		}

		@Override
		protected void onPostExecute(News result) {
			super.onPostExecute(result);
			newData = true;
			loadData();
		}

	}

	@Override
	protected void parse(String result) {
		NewsParserTask pTask = new NewsParserTask();
		pTask.execute(result);
	}

}
