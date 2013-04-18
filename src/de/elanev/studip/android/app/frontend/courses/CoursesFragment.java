/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.frontend.courses;

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
import de.elanev.studip.android.app.backend.db.SemestersContract;
import de.elanev.studip.android.app.backend.net.services.syncservice.activitys.CoursesResponderFragment;
import de.elanev.studip.android.app.frontend.news.CourseNewsFragment;
import de.elanev.studip.android.app.frontend.util.SimpleSectionedListAdapter;

/**
 * @author joern
 * 
 */
public class CoursesFragment extends SherlockListFragment implements
		LoaderCallbacks<Cursor> {
	public static final String TAG = CourseNewsFragment.class.getSimpleName();
	private Context mContext = null;
	private CourseAdapter mCourseAdapter;
	private SimpleSectionedListAdapter mAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getSherlockActivity();
		getSherlockActivity().setTitle(getString(R.string.Courses));
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();

		CoursesResponderFragment responderFragment = (CoursesResponderFragment) fm
				.findFragmentByTag("coursesResponder");
		if (responderFragment == null) {
			responderFragment = new CoursesResponderFragment();
			responderFragment.setFragment(this);
			ft.add(responderFragment, "coursesResponder");
		}
		ft.commit();

		mCourseAdapter = new CourseAdapter(mContext);
		mAdapter = new SimpleSectionedListAdapter(mContext,
				R.layout.list_item_header, mCourseAdapter);
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
				CoursesContract.CONTENT_URI, true, mObserver);
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
		if (selectedObject instanceof CursorWrapper) {
			Intent intent = new Intent();
			intent.setClass(getActivity(), CourseViewActivity.class);
			intent.putExtra(
					CoursesContract.Columns.COURSE_ID,
					((CursorWrapper) selectedObject).getString(((CursorWrapper) selectedObject)
							.getColumnIndex(CoursesContract.Columns.COURSE_ID)));
			intent.putExtra(
					CoursesContract.Columns._ID,
					((CursorWrapper) selectedObject).getString(((CursorWrapper) selectedObject)
							.getColumnIndex(CoursesContract.Columns._ID)));

			mContext.startActivity(intent);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.LoaderManager.LoaderCallbacks#onCreateLoader(int,
	 * android.os.Bundle)
	 */
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new CursorLoader(getActivity(), CoursesContract.CONTENT_URI,
				CourseQuery.PROJECTION, null, null,
				CoursesContract.Qualified.COURSES_COURSE_SEMESERT_ID + " ASC");
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

		List<SimpleSectionedListAdapter.Section> sections = new ArrayList<SimpleSectionedListAdapter.Section>();
		cursor.moveToFirst();
		String prevSemesterId = null;
		String currSemesterId = null;
		while (!cursor.isAfterLast()) {
			currSemesterId = cursor
					.getString(cursor
							.getColumnIndex(CoursesContract.Columns.COURSE_SEMESERT_ID));
			if (!TextUtils.equals(prevSemesterId, currSemesterId)) {
				sections.add(new SimpleSectionedListAdapter.Section(
						cursor.getPosition(),
						cursor.getString(cursor
								.getColumnIndex(SemestersContract.Columns.SEMESTER_TITLE))));
			}
			prevSemesterId = currSemesterId;
			cursor.moveToNext();
		}

		mCourseAdapter.changeCursor(cursor);

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

	public interface CourseQuery {

		String[] PROJECTION = { CoursesContract.Qualified.COURSES_ID,
				CoursesContract.Qualified.COURSES_COURSE_TITLE,
				CoursesContract.Qualified.COURSES_COURSE_ID,
				SemestersContract.Qualified.SEMESTERS_SEMESTER_ID,
				SemestersContract.Qualified.SEMESTERS_SEMESTER_TITLE };

	}

	class CourseAdapter extends CursorAdapter {

		public CourseAdapter(Context context) {
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
			final String courseTitle = cursor.getString(1);

			final TextView courseTitleTextVew = (TextView) view
					.findViewById(R.id.title);

			courseTitleTextVew.setText(courseTitle);

			final ImageView icon = (ImageView) view
					.findViewById(R.id.course_icon);
			icon.setImageResource(R.drawable.seminar);
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
					R.layout.courses_item, parent, false);
		}

	}

}
