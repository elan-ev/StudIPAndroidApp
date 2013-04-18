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
package de.elanev.studip.android.app.frontend.news;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.db.NewsContract;
import de.elanev.studip.android.app.backend.db.UsersContract;
import de.elanev.studip.android.app.backend.net.services.syncservice.activitys.NewsResponderFragment;
import de.elanev.studip.android.app.frontend.util.SimpleSectionedListAdapter;
import de.elanev.studip.android.app.util.TextTools;

/**
 * @author joern
 * 
 */
public class GlobalNewsFragment extends SherlockListFragment implements
		LoaderCallbacks<Cursor> {

	public static String TAG = GlobalNewsFragment.class.getSimpleName();
	private Context mContext = null;
	private NewsAdapter mNewsAdapter;
	private SimpleSectionedListAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getSherlockActivity();
		getActivity().setTitle(getString(R.string.News));
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		NewsResponderFragment responderFragment = (NewsResponderFragment) fm
				.findFragmentByTag(getString(R.string.News));
		if (responderFragment == null) {
			responderFragment = new NewsResponderFragment();
			responderFragment.setFragment(this);
			ft.add(responderFragment, getString(R.string.News));
		}
		ft.commit();
		mNewsAdapter = new NewsAdapter(mContext);
		mAdapter = new SimpleSectionedListAdapter(mContext,
				R.layout.list_item_header, mNewsAdapter);
		setListAdapter(mAdapter);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list, null);
	}

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

	@Override
	public void onDetach() {
		super.onDetach();
		getActivity().getContentResolver().unregisterContentObserver(mObserver);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.ListFragment#onListItemClick(android.widget.ListView
	 * , android.view.View, int, long)
	 */
	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);

		Object selectedObject = l.getItemAtPosition(position);
		Log.d(TAG, selectedObject.getClass().getName());
		if (selectedObject instanceof CursorWrapper) {

			Bundle args = new Bundle();
			args.putString(
					NewsContract.Columns.NEWS_TOPIC,
					((CursorWrapper) selectedObject).getString(((CursorWrapper) selectedObject)
							.getColumnIndex(NewsContract.Columns.NEWS_TOPIC)));
			args.putString(
					NewsContract.Columns.NEWS_BODY,
					((CursorWrapper) selectedObject).getString(((CursorWrapper) selectedObject)
							.getColumnIndex(NewsContract.Columns.NEWS_BODY)));
			args.putLong(
					NewsContract.Columns.NEWS_DATE,
					((CursorWrapper) selectedObject).getLong((((CursorWrapper) selectedObject)
							.getColumnIndex(NewsContract.Columns.NEWS_DATE))));
			args.putString(
					UsersContract.Columns.USER_FORENAME,
					((CursorWrapper) selectedObject).getString(((CursorWrapper) selectedObject)
							.getColumnIndex(UsersContract.Columns.USER_FORENAME)));

			Intent intent = new Intent();
			intent.setClass(getActivity(), NewsItemView.class);
			intent.putExtras(args);
			startActivity(intent);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.elanev.studip.android.app.frontend.news.GeneralNewsFragment#onCreateLoader
	 * (int, android.os.Bundle)
	 */
	public Loader<Cursor> onCreateLoader(int id, Bundle data) {
		return new CursorLoader(getActivity(), NewsContract.GLOBAL_CONTENT_URI,
				NewsQuery.PROJECTION, null, null,
				NewsContract.DEFAULT_SORT_ORDER);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.elanev.studip.android.app.frontend.news.GeneralNewsFragment#onLoadFinished
	 * (android.support.v4.content.Loader, android.database.Cursor)
	 */
	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		if (getActivity() == null) {
			return;
		}

		List<SimpleSectionedListAdapter.Section> sections = new ArrayList<SimpleSectionedListAdapter.Section>();
		cursor.moveToFirst();
		long previousDay = -1;
		long currentDay = -1;
		while (!cursor.isAfterLast()) {
			currentDay = cursor.getLong(cursor
					.getColumnIndex(NewsContract.Columns.NEWS_DATE));
			if (!TextTools.isSameDay(previousDay, currentDay)) {
				sections.add(new SimpleSectionedListAdapter.Section(cursor
						.getPosition(), TextTools.getLocalizedTime(currentDay,
						mContext)));
			}

			previousDay = currentDay;

			cursor.moveToNext();
		}

		mNewsAdapter.changeCursor(cursor);

		SimpleSectionedListAdapter.Section[] dummy = new SimpleSectionedListAdapter.Section[sections
				.size()];
		mAdapter.setSections(sections.toArray(dummy));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.LoaderManager.LoaderCallbacks#onLoaderReset(android
	 * .support.v4.content.Loader)
	 */
	public void onLoaderReset(Loader<Cursor> arg0) {
	}

	public interface NewsQuery {

		String[] PROJECTION = { NewsContract.Qualified.NEWS_ID,
				NewsContract.Qualified.NEWS_NEWS_ID,
				NewsContract.Qualified.NEWS_NEWS_TOPIC,
				NewsContract.Qualified.NEWS_NEWS_BODY,
				NewsContract.Qualified.NEWS_NEWS_DATE,
				UsersContract.Qualified.USERS_USER_ID,
				UsersContract.Qualified.USERS_USER_FORENAME,
				UsersContract.Qualified.USERS_USER_LASTNAME };

	}

	class NewsAdapter extends CursorAdapter {

		public NewsAdapter(Context context) {
			super(context, null, false);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.support.v4.widget.CursorAdapter#bindView(android.view.View,
		 * android.content.Context, android.database.Cursor)
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

			final TextView newsTopicView = (TextView) view
					.findViewById(R.id.title);
			final TextView newsAuthorView = (TextView) view
					.findViewById(R.id.author);

			newsTopicView.setText(newsTopic);
			newsAuthorView.setText(TextTools.getLocalizedAuthorAndDateString(
					String.format("%s %s", userForename, userLastname),
					newsDate, getActivity()));
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * android.support.v4.widget.CursorAdapter#newView(android.content.Context
		 * , android.database.Cursor, android.view.ViewGroup)
		 */
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			return getActivity().getLayoutInflater().inflate(
					R.layout.news_item, parent, false);
		}

	}

}
