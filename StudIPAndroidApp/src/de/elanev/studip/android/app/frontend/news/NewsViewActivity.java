/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.frontend.news;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.db.CoursesContract;
import de.elanev.studip.android.app.backend.db.NewsContract;
import de.elanev.studip.android.app.backend.db.UsersContract;
import de.elanev.studip.android.app.frontend.util.BaseSlidingFragmentActivity;
import de.elanev.studip.android.app.frontend.util.SimpleSectionedListAdapter;
import de.elanev.studip.android.app.util.TextTools;

/**
 * @author joern
 * 
 */
public class NewsViewActivity extends BaseSlidingFragmentActivity {

	/**
	 * @param titleRes
	 */
	public NewsViewActivity() {
		super(R.string.News);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.elanev.studip.android.app.frontend.util.BaseSlidingFragmentActivity
	 * #onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.content_frame);

		if (savedInstanceState != null)
			return;

		FragmentManager fm = getSupportFragmentManager();
		NewsListFragment frag = null;
		frag = (NewsListFragment) fm.findFragmentByTag(NewsListFragment.class
				.getName());
		if (frag == null)
			frag = (NewsListFragment) NewsListFragment.instantiate(this,
					NewsListFragment.class.getName());

		fm.beginTransaction()
				.add(R.id.content_frame, frag, NewsListFragment.class.getName())
				.commit();
	}

	public static class NewsListFragment extends SherlockListFragment implements
			LoaderCallbacks<Cursor> {

		public static final String TAG = NewsListFragment.class.getSimpleName();
		private Context mContext = null;
		private NewsAdapter mNewsAdapter;
		private SimpleSectionedListAdapter mAdapter;

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
		 */
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			mContext = getSherlockActivity();

			mNewsAdapter = new NewsAdapter(mContext);
			mAdapter = new SimpleSectionedListAdapter(mContext,
					R.layout.list_item_header, mNewsAdapter);
			setListAdapter(mAdapter);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.support.v4.app.ListFragment#onCreateView(android.view.
		 * LayoutInflater, android.view.ViewGroup, android.os.Bundle)
		 */
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View v = inflater.inflate(R.layout.list, null);
			((TextView) v.findViewById(R.id.empty_message))
					.setText(R.string.no_news);
			return v;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
		 */
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			super.onActivityCreated(savedInstanceState);
			// initialize CursorLoader
			getLoaderManager().initLoader(0, null, this);
		}

		protected final ContentObserver mObserver = new ContentObserver(
				new Handler()) {
			@Override
			public void onChange(boolean selfChange) {
				if (getActivity() == null) {
					return;
				}

				Loader<Cursor> loader = getLoaderManager().getLoader(0);
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
			activity.getContentResolver().registerContentObserver(
					NewsContract.CONTENT_URI, true, mObserver);

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see com.actionbarsherlock.app.SherlockListFragment#onDetach()
		 */
		@Override
		public void onDetach() {
			super.onDetach();
			getActivity().getContentResolver().unregisterContentObserver(
					mObserver);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.support.v4.app.ListFragment#onListItemClick(android.widget
		 * .ListView , android.view.View, int, long)
		 */
		@Override
		public void onListItemClick(ListView l, View v, int position, long id) {
			super.onListItemClick(l, v, position, id);

			Cursor c = (Cursor) l.getItemAtPosition(position);
			String topic = c.getString(c
					.getColumnIndex(NewsContract.Columns.NEWS_TOPIC));
			String body = c.getString(c
					.getColumnIndex(NewsContract.Columns.NEWS_BODY));
			String name = String
					.format("%s %s %s %s",
							c.getString(c
									.getColumnIndex(UsersContract.Columns.USER_TITLE_PRE)),
							c.getString(c
									.getColumnIndex(UsersContract.Columns.USER_FORENAME)),
							c.getString(c
									.getColumnIndex(UsersContract.Columns.USER_LASTNAME)),
							c.getString(c
									.getColumnIndex(UsersContract.Columns.USER_TITLE_POST)));
			long date = c.getLong(c
					.getColumnIndex(NewsContract.Columns.NEWS_DATE));

			Bundle args = new Bundle();
			args.putString(NewsContract.Columns.NEWS_TOPIC, topic);
			args.putString(NewsContract.Columns.NEWS_BODY, body);
			args.putLong(NewsContract.Columns.NEWS_DATE, date);
			args.putString(UsersContract.Columns.USER_FORENAME, name);

			Intent intent = new Intent();
			intent.setClass(getActivity(), NewsItemView.class);
			intent.putExtras(args);
			startActivity(intent);

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.elanev.studip.android.app.frontend.news.GeneralNewsFragment#
		 * onCreateLoader (int, android.os.Bundle)
		 */
		public Loader<Cursor> onCreateLoader(int id, Bundle data) {
			return new CursorLoader(getActivity(), NewsContract.CONTENT_URI,
					NewsQuery.PROJECTION, null, null,
					NewsContract.DEFAULT_SORT_ORDER);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see de.elanev.studip.android.app.frontend.news.GeneralNewsFragment#
		 * onLoadFinished (android.support.v4.content.Loader,
		 * android.database.Cursor)
		 */
		public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
			if (getActivity() == null) {
				return;
			}

			List<SimpleSectionedListAdapter.Section> sections = new ArrayList<SimpleSectionedListAdapter.Section>();
			cursor.moveToFirst();
			String prevCourseId = null;
			String currentCourseId = null;
			while (!cursor.isAfterLast()) {
				currentCourseId = cursor
						.getString(cursor
								.getColumnIndex(CoursesContract.Columns.Courses.COURSE_TITLE));
				if (!TextUtils.equals(prevCourseId, currentCourseId)) {
					sections.add(new SimpleSectionedListAdapter.Section(
							cursor.getPosition(),
							cursor.getString(cursor
									.getColumnIndex(CoursesContract.Columns.Courses.COURSE_TITLE))));
				}
				prevCourseId = currentCourseId;
				cursor.moveToNext();
			}

			mNewsAdapter.swapCursor(cursor);

			SimpleSectionedListAdapter.Section[] dummy = new SimpleSectionedListAdapter.Section[sections
					.size()];
			mAdapter.setSections(sections.toArray(dummy));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.support.v4.app.LoaderManager.LoaderCallbacks#onLoaderReset
		 * (android .support.v4.content.Loader)
		 */
		public void onLoaderReset(Loader<Cursor> loader) {
			mNewsAdapter.swapCursor(null);
		}

		private interface NewsQuery {

			String[] PROJECTION = { NewsContract.Qualified.NEWS_ID,
					NewsContract.Qualified.NEWS_NEWS_TOPIC,
					NewsContract.Qualified.NEWS_NEWS_BODY,
					NewsContract.Qualified.NEWS_NEWS_DATE,
					NewsContract.Qualified.NEWS_NEWS_COURSE_ID,
					UsersContract.Qualified.USERS_USER_TITLE_PRE,
					UsersContract.Qualified.USERS_USER_TITLE_POST,
					UsersContract.Qualified.USERS_USER_FORENAME,
					UsersContract.Qualified.USERS_USER_LASTNAME,
					CoursesContract.Qualified.Courses.COURSES_COURSE_TITLE };

		}

		private class NewsAdapter extends CursorAdapter {

			public NewsAdapter(Context context) {
				super(context, null, false);
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * android.support.v4.widget.CursorAdapter#bindView(android.view
			 * .View, android.content.Context, android.database.Cursor)
			 */
			@Override
			public void bindView(View view, Context context, final Cursor cursor) {
				final String newsTopic = cursor.getString(cursor
						.getColumnIndex(NewsContract.Columns.NEWS_TOPIC));
				final Long newsDate = cursor.getLong(cursor
						.getColumnIndex(NewsContract.Columns.NEWS_DATE));
				final String userForename = cursor.getString(cursor
						.getColumnIndex(UsersContract.Columns.USER_FORENAME));
				final String userLastname = cursor.getString(cursor
						.getColumnIndex(UsersContract.Columns.USER_LASTNAME));
				final String courseId = cursor.getString(cursor
						.getColumnIndex(NewsContract.Columns.NEWS_COURSE_ID));

				final TextView newsTopicView = (TextView) view
						.findViewById(R.id.text1);
				final TextView newsAuthorView = (TextView) view
						.findViewById(R.id.text2);
				final ImageView icon = (ImageView) view.findViewById(R.id.icon);

				if (TextUtils.equals(courseId,
						getString(R.string.restip_news_global_identifier))) {
					icon.setImageResource(R.drawable.ic_action_global);
				} else {
					icon.setImageResource(R.drawable.ic_seminar);
				}

				newsTopicView.setText(newsTopic);
				newsAuthorView.setText(TextTools
						.getLocalizedAuthorAndDateString(String.format("%s %s",
								userForename, userLastname), newsDate,
								getActivity()));
			}

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * android.support.v4.widget.CursorAdapter#newView(android.content
			 * .Context , android.database.Cursor, android.view.ViewGroup)
			 */
			@Override
			public View newView(Context context, Cursor cursor, ViewGroup parent) {
				return getActivity().getLayoutInflater().inflate(
						R.layout.list_item_two_text_icon, parent, false);
			}

		}

	}

}
