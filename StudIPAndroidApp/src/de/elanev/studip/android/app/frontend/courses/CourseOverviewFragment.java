/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.frontend.courses;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.android.volley.toolbox.NetworkImageView;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.db.CoursesContract;
import de.elanev.studip.android.app.backend.db.EventsContract;
import de.elanev.studip.android.app.backend.db.NewsContract;
import de.elanev.studip.android.app.backend.db.UsersContract;
import de.elanev.studip.android.app.util.TextTools;
import de.elanev.studip.android.app.util.VolleyHttp;

/**
 * @author joern
 * 
 */
public class CourseOverviewFragment extends SherlockFragment implements
		LoaderCallbacks<Cursor> {
	public static final String TAG = CourseOverviewFragment.class
			.getSimpleName();
	private static final int COURSE_LOADER = 101;
	private static final int COURSE_EVENTS_LOADER = 102;
	private static final int COURSE_NEWS_LOADER = 103;

	private TextView mTitleTextView;
	private TextView mTeacherNameTextView;
	private TextView mDescriptionTextView;
	private TextView mNewsTitleTextView;
	private TextView mNewsAuthorTextView;
	private TextView mNewsTextTextView;
	private TextView mNewsShowMoreTextView;

	private Context mContext;
	private Bundle mArgs;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mArgs = getArguments();
		mContext = getActivity();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_course_details, null);

		mTitleTextView = (TextView) view.findViewById(R.id.course_title);
		mDescriptionTextView = (TextView) view
				.findViewById(R.id.course_description);
		mTeacherNameTextView = (TextView) view
				.findViewById(R.id.course_teacher_name);
		mNewsTitleTextView = (TextView) view.findViewById(R.id.news_title);
		mNewsAuthorTextView = (TextView) view.findViewById(R.id.news_author);
		mNewsTextTextView = (TextView) view.findViewById(R.id.news_text);
		mNewsShowMoreTextView = (TextView) view
				.findViewById(R.id.show_news_body);
		return view;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// initialize CursorLoaders with IDs
		LoaderManager lm = getLoaderManager();
		lm.initLoader(COURSE_LOADER, mArgs, this);
		lm.initLoader(COURSE_EVENTS_LOADER, mArgs, this);
		lm.initLoader(COURSE_NEWS_LOADER, mArgs, this);

		// mListView.setAdapter(mAdapter);

		mNewsShowMoreTextView.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				v.setVisibility(View.GONE);
				mNewsTextTextView.setVisibility(View.VISIBLE);

			}
		});
	}

	protected final ContentObserver mObserverCourse = new ContentObserver(
			new Handler()) {

		@Override
		public void onChange(boolean selfChange) {
			if (getActivity() == null) {
				return;
			}

			Loader<Cursor> loader = getLoaderManager().getLoader(COURSE_LOADER);
			if (loader != null) {
				loader.forceLoad();
			}
		}
	};

	protected final ContentObserver mObserverEvents = new ContentObserver(
			new Handler()) {

		@Override
		public void onChange(boolean selfChange) {
			if (getActivity() == null) {
				return;
			}

			Loader<Cursor> loader = getLoaderManager().getLoader(
					COURSE_EVENTS_LOADER);
			if (loader != null) {
				loader.forceLoad();
			}
		}
	};

	protected final ContentObserver mObserverNews = new ContentObserver(
			new Handler()) {

		@Override
		public void onChange(boolean selfChange) {
			if (getActivity() == null) {
				return;
			}

			Loader<Cursor> loader = getLoaderManager().getLoader(
					COURSE_NEWS_LOADER);
			if (loader != null) {
				loader.forceLoad();
			}
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.elanev.studip.android.app.frontend.news.GeneralNewsFragment#onAttach
	 * (android.app.Activity)
	 */
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		final ContentResolver contentResolver = activity.getContentResolver();

		contentResolver.registerContentObserver(CoursesContract.CONTENT_URI,
				true, mObserverCourse);

		contentResolver.registerContentObserver(EventsContract.CONTENT_URI,
				true, mObserverEvents);

		contentResolver.registerContentObserver(CoursesContract.CONTENT_URI,
				true, mObserverNews);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.actionbarsherlock.app.SherlockFragment#onDetach()
	 */
	@Override
	public void onDetach() {
		super.onDetach();
		final ContentResolver contentResolver = getActivity()
				.getContentResolver();
		contentResolver.unregisterContentObserver(mObserverCourse);
		contentResolver.unregisterContentObserver(mObserverEvents);
		contentResolver.unregisterContentObserver(mObserverNews);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int,
	 * android.os.Bundle)
	 */
	public Loader<Cursor> onCreateLoader(int id, Bundle data) {

		// Create loaders based on id
		switch (id) {
		case COURSE_LOADER:
			return new CursorLoader(
					mContext,
					CoursesContract.CONTENT_URI
							.buildUpon()
							.appendPath(
									data.getString(CoursesContract.Columns.Courses._ID))
							.build(), CourseItemQuery.projection, null, null,
					CoursesContract.DEFAULT_SORT_ORDER);

		case COURSE_EVENTS_LOADER:
			return new CursorLoader(
					mContext,
					CoursesContract.CONTENT_URI
							.buildUpon()
							.appendPath("events")
							.appendPath(
									data.getString(CoursesContract.Columns.Courses.COURSE_ID))
							.build(), CourseEventQuery.projection,
					EventsContract.Columns.EVENT_START
							+ " >= strftime('%s','now')", null,
					EventsContract.DEFAULT_SORT_ORDER + " LIMIT 1");

		case COURSE_NEWS_LOADER:
			return new CursorLoader(
					mContext,
					NewsContract.CONTENT_URI
							.buildUpon()
							.appendPath(
									data.getString(CoursesContract.Columns.Courses.COURSE_ID))
							.build(), CourseNewsQuery.PROJECTION, null, null,
					NewsContract.DEFAULT_SORT_ORDER + " LIMIT 1");
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.LoaderManager.LoaderCallbacks#onLoadFinished(android
	 * .support.v4.content.Loader, java.lang.Object)
	 */
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

		if (getActivity() == null) {
			return;
		}
		cursor.moveToFirst();

		int loaderId = loader.getId();
		switch (loaderId) {
		case COURSE_LOADER:

			if (!cursor.isAfterLast()) {
				String courseTitle = cursor
						.getString(cursor
								.getColumnIndex(CoursesContract.Columns.Courses.COURSE_TITLE));
				String courseDescription = cursor
						.getString(cursor
								.getColumnIndex(CoursesContract.Columns.Courses.COURSE_DESCIPTION));
				String teacherAvatarUrl = cursor
						.getString(cursor
								.getColumnIndex(UsersContract.Columns.USER_AVATAR_NORMAL));
				getSherlockActivity().setTitle(courseTitle);
				mTitleTextView.setText(courseTitle);
				mTeacherNameTextView
						.setText(cursor.getString(cursor
								.getColumnIndex(UsersContract.Columns.USER_TITLE_PRE))
								+ " "
								+ cursor.getString(cursor
										.getColumnIndex(UsersContract.Columns.USER_FORENAME))
								+ " "
								+ cursor.getString(cursor
										.getColumnIndex(UsersContract.Columns.USER_LASTNAME))
								+ " "
								+ cursor.getString(cursor
										.getColumnIndex(UsersContract.Columns.USER_TITLE_POST)));

				if (!teacherAvatarUrl.contains("nobody")) {
					final NetworkImageView teacherImage = (NetworkImageView) getView()
							.findViewById(R.id.user_image);
					teacherImage.setImageUrl(teacherAvatarUrl,
							VolleyHttp.getImageLoader());
					teacherImage.setVisibility(View.VISIBLE);
					((ImageView) getView().findViewById(
							R.id.user_image_placeholder))
							.setVisibility(View.GONE);
				}

				if (!TextUtils.isEmpty(courseDescription)) {
					mDescriptionTextView.setText(courseDescription);
					mDescriptionTextView
							.setMovementMethod(new ScrollingMovementMethod());
				}

			}
			break;
		case COURSE_EVENTS_LOADER:
			final TextView nextAppointmentTextView = (TextView) getView()
					.findViewById(R.id.course_next_appointment);
			if (cursor.getCount() >= 1) {

				String room = cursor.getString(cursor
						.getColumnIndex(EventsContract.Columns.EVENT_ROOM));
				String title = cursor.getString(cursor
						.getColumnIndex(EventsContract.Columns.EVENT_TITLE));
				nextAppointmentTextView.setText(String.format("%s\n%s", title,
						room));
			}

			break;
		case COURSE_NEWS_LOADER:
			if (cursor.getCount() >= 1) {

				final String newsTopic = cursor.getString(cursor
						.getColumnIndex(NewsContract.Columns.NEWS_TOPIC));
				final Long newsDate = cursor.getLong(cursor
						.getColumnIndex(NewsContract.Columns.NEWS_DATE));
				final String newsBody = cursor.getString(cursor
						.getColumnIndex(NewsContract.Columns.NEWS_BODY));
				final String userForename = cursor.getString(cursor
						.getColumnIndex(UsersContract.Columns.USER_FORENAME));
				final String userLastname = cursor.getString(cursor
						.getColumnIndex(UsersContract.Columns.USER_LASTNAME));

				mNewsTitleTextView.setText(newsTopic);
				mNewsAuthorTextView.setText(TextTools
						.getLocalizedAuthorAndDateString(String.format("%s %s",
								userForename, userLastname), newsDate,
								getActivity()));
				mNewsAuthorTextView.setVisibility(View.VISIBLE);
				mNewsShowMoreTextView.setVisibility(View.VISIBLE);
				mNewsTextTextView.setText(Html.fromHtml(newsBody));

			}
			break;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.LoaderManager.LoaderCallbacks#onLoaderReset(android
	 * .support.v4.content.Loader)
	 */
	public void onLoaderReset(Loader<Cursor> loader) {
	}

	private interface CourseItemQuery {
		String[] projection = {
				CoursesContract.Qualified.Courses.COURSES_COURSE_TITLE,
				CoursesContract.Qualified.Courses.COURSES_COURSE_DESCIPTION,
				UsersContract.Qualified.USERS_USER_TITLE_PRE,
				UsersContract.Qualified.USERS_USER_FORENAME,
				UsersContract.Qualified.USERS_USER_LASTNAME,
				UsersContract.Qualified.USERS_USER_TITLE_POST,
				UsersContract.Qualified.USERS_USER_AVATAR_NORMAL };
	}

	private interface CourseEventQuery {
		String[] projection = { EventsContract.Columns.EVENT_TITLE,
				EventsContract.Columns.EVENT_START,
				EventsContract.Columns.EVENT_END,
				EventsContract.Columns.EVENT_ROOM };
	}

	public interface CourseNewsQuery {

		String[] PROJECTION = { NewsContract.Qualified.NEWS_NEWS_TOPIC,
				NewsContract.Qualified.NEWS_NEWS_BODY,
				NewsContract.Qualified.NEWS_NEWS_DATE,
				UsersContract.Qualified.USERS_USER_FORENAME,
				UsersContract.Qualified.USERS_USER_LASTNAME };

	}

}