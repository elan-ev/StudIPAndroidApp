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
package de.elanev.studip.android.app.frontend.courses;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.db.CoursesContract;
import de.elanev.studip.android.app.backend.db.EventsContract;
import de.elanev.studip.android.app.backend.db.UsersContract;
import de.elanev.studip.android.app.backend.net.services.syncservice.activitys.EventsResponderFragment;
import de.elanev.studip.android.app.frontend.util.SimpleSectionedListAdapter;
import de.elanev.studip.android.app.util.TextTools;

/**
 * @author joern
 * 
 */
public class CourseEventsFragment extends SherlockListFragment implements
		LoaderCallbacks<Cursor> {
	public static final String TAG = CourseEventsFragment.class.getSimpleName();
	private static final int COURSE_ITEM_LOADER = 101;
	private static final int COURSOR_EVENTS_LIST_LOADER = 102;

	private Bundle mArgs;
	private EventsAdapter mEventsAdapter;
	private SimpleSectionedListAdapter mAdapter;
	private Context mContext;

	private TextView mTitleTextView;
	private TextView mTeacherNameTextView;
	private TextView mDescriptionTextView;

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
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		EventsResponderFragment responderFragment = (EventsResponderFragment) fm
				.findFragmentByTag("eventsResponder");
		if (responderFragment == null) {
			responderFragment = new EventsResponderFragment();
			responderFragment.setFragment(this);
			Bundle args = new Bundle();
			args.putString("cid",
					mArgs.getString(CoursesContract.Columns.Courses.COURSE_ID));
			responderFragment.setArguments(args);
			ft.add(responderFragment, "eventsResponder");
		}
		ft.commit();

		// Creating the adapters for the listview
		mEventsAdapter = new EventsAdapter(mContext);
		mAdapter = new SimpleSectionedListAdapter(mContext,
				R.layout.list_item_header, mEventsAdapter);

		setListAdapter(mAdapter);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// initialize CursorLoaders with IDs
		getLoaderManager().initLoader(COURSE_ITEM_LOADER, mArgs, this);
		getLoaderManager().initLoader(COURSOR_EVENTS_LIST_LOADER, mArgs, this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_course_details, null);

		mTitleTextView = (TextView) view.findViewById(R.id.course_title);
		mDescriptionTextView = (TextView) view
				.findViewById(R.id.course_description);
		mTeacherNameTextView = (TextView) view
				.findViewById(R.id.course_teacher_name);

		return view;
	}

	protected final ContentObserver mCourseObserver = new ContentObserver(
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

	protected final ContentObserver mEventsObserver = new ContentObserver(
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
				CoursesContract.CONTENT_URI, true, mCourseObserver);
		activity.getContentResolver().registerContentObserver(
				EventsContract.CONTENT_URI, true, mEventsObserver);
	}

	@Override
	public void onDetach() {
		super.onDetach();
		getActivity().getContentResolver().unregisterContentObserver(
				mCourseObserver);
		getActivity().getContentResolver().unregisterContentObserver(
				mEventsObserver);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int,
	 * android.os.Bundle)
	 */
	public Loader<Cursor> onCreateLoader(int id, Bundle data) {
		CursorLoader loader;
		switch (id) {
		case COURSE_ITEM_LOADER:
			loader = new CursorLoader(
					mContext,
					CoursesContract.CONTENT_URI
							.buildUpon()
							.appendPath(
									data.getString(CoursesContract.Columns.Courses._ID))
							.build(), CourseItemQuery.projection, null, null,
					CoursesContract.DEFAULT_SORT_ORDER);
			break;
		default:
			loader = new CursorLoader(
					mContext,
					CoursesContract.CONTENT_URI
							.buildUpon()
							.appendPath("events")
							.appendPath(
									data.getString(CoursesContract.Columns.Courses.COURSE_ID))
							.build(), CourseEventsListQuery.projection,
					EventsContract.Columns.EVENT_START
							+ " >= strftime('%s','now')", null,
					EventsContract.DEFAULT_SORT_ORDER);
			break;
		}
		return loader;
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

		int loaderId = loader.getId();
		switch (loaderId) {
		case COURSE_ITEM_LOADER:
			cursor.moveToFirst();
			String courseTitle = cursor
					.getString(cursor
							.getColumnIndex(CoursesContract.Columns.Courses.COURSE_TITLE));
			String courseDescription = cursor
					.getString(cursor
							.getColumnIndex(CoursesContract.Columns.Courses.COURSE_DESCIPTION));
			getSherlockActivity().setTitle(courseTitle);
			mTitleTextView.setText(courseTitle);

			if (!TextUtils.isEmpty(courseDescription)) {
				mDescriptionTextView.setText(courseDescription);
				mDescriptionTextView
						.setMovementMethod(new ScrollingMovementMethod());
				mDescriptionTextView.setVisibility(View.VISIBLE);
			}

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
			break;

		default:

			List<SimpleSectionedListAdapter.Section> sections = new ArrayList<SimpleSectionedListAdapter.Section>();
			cursor.moveToFirst();
			long previousDay = -1;
			long currentDay = -1;
			while (!cursor.isAfterLast()) {
				currentDay = cursor.getLong(cursor
						.getColumnIndex(EventsContract.Columns.EVENT_START));
				if (!TextTools.isSameDay(previousDay, currentDay)) {
					sections.add(new SimpleSectionedListAdapter.Section(cursor
							.getPosition(), TextTools.getLocalizedTime(
							currentDay, mContext)));
				}

				previousDay = currentDay;

				cursor.moveToNext();
			}

			mEventsAdapter.changeCursor(cursor);

			SimpleSectionedListAdapter.Section[] dummy = new SimpleSectionedListAdapter.Section[sections
					.size()];
			mAdapter.setSections(sections.toArray(dummy));

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
		mEventsAdapter.swapCursor(null);
	}

	private interface CourseItemQuery {
		String[] projection = {
				CoursesContract.Qualified.Courses.COURSES_COURSE_TITLE,
				CoursesContract.Qualified.Courses.COURSES_COURSE_DESCIPTION,
				UsersContract.Qualified.USERS_USER_TITLE_PRE,
				UsersContract.Qualified.USERS_USER_FORENAME,
				UsersContract.Qualified.USERS_USER_LASTNAME,
				UsersContract.Qualified.USERS_USER_TITLE_POST };
	}

	private interface CourseEventsListQuery {
		String[] projection = { EventsContract.Columns._ID,
				EventsContract.Columns.EVENT_TITLE,
				EventsContract.Columns.EVENT_DESCRIPTION,
				EventsContract.Columns.EVENT_START,
				EventsContract.Columns.EVENT_END,
				EventsContract.Columns.EVENT_ROOM };
	}

	private class EventsAdapter extends CursorAdapter {

		public EventsAdapter(Context context) {
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
			final String eventTitle = cursor.getString(cursor
					.getColumnIndex(EventsContract.Columns.EVENT_TITLE));
			final String eventDescription = cursor.getString(cursor
					.getColumnIndex(EventsContract.Columns.EVENT_DESCRIPTION));
			final String eventLocation = cursor.getString(cursor
					.getColumnIndex(EventsContract.Columns.EVENT_ROOM));
			final Long eventStartTime = cursor.getLong(cursor
					.getColumnIndex(EventsContract.Columns.EVENT_START));
			final Long eventEndTime = cursor.getLong(cursor
					.getColumnIndex(EventsContract.Columns.EVENT_END));

			final TextView eventTitleTextView = (TextView) view
					.findViewById(R.id.event_title);
			final TextView eventDescriptionTextView = (TextView) view
					.findViewById(R.id.event_description);
			final TextView eventLocationTextView = (TextView) view
					.findViewById(R.id.event_room);
			final TextView eventTimeTextView = (TextView) view
					.findViewById(R.id.event_time);

			eventTitleTextView.setText(eventTitle);
			if (!TextUtils.isEmpty(eventDescription)) {
				eventDescriptionTextView.setText(eventDescription);
				eventDescriptionTextView.setVisibility(View.VISIBLE);
			}
			eventLocationTextView.setText(eventLocation);
			eventTimeTextView.setText(TextTools.buildLocalizedTimeString(
					eventStartTime, mContext)
					+ " - "
					+ TextTools
							.buildLocalizedTimeString(eventEndTime, mContext));
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
					R.layout.list_item_event, parent, false);
		}

	}
}
