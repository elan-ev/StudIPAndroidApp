/*
 * Copyright (c) 2014 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
/**
 *
 */
package de.elanev.studip.android.app.frontend.courses;

import android.app.Activity;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.db.CoursesContract;
import de.elanev.studip.android.app.backend.db.EventsContract;

/**
 * @author joern
 */
public class CourseScheduleFragment extends SherlockListFragment implements LoaderCallbacks<Cursor> {
  public static final String TAG = CourseScheduleFragment.class.getSimpleName();
  private static final int COURSOR_EVENTS_LIST_LOADER = 102;
  protected final ContentObserver mEventsObserver = new ContentObserver(new Handler()) {
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
  protected Context mContext;
  private Bundle mArgs;
  private SimpleCursorAdapter mAdapter;
  private View mListContainerView, mProgressView;
  private TextView mEmptyMessageTextView;

  public CourseScheduleFragment() {
  }

  /*
       * (non-Javadoc)
       *
       * @see android.support.v4.app.ListFragment#onCreateView(android.view.
       * LayoutInflater, android.view.ViewGroup, android.os.Bundle)
       */
  @Override
  public View onCreateView(LayoutInflater inflater,
      ViewGroup container,
      Bundle savedInstanceState) {

    View v = inflater.inflate(R.layout.simple_list, null);
    mEmptyMessageTextView = (TextView) v.findViewById(R.id.empty_message);
    mListContainerView = v.findViewById(R.id.list_container);
    mProgressView = v.findViewById(R.id.progressbar);

    return v;
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mArgs = getArguments();
    mContext = getActivity();

    String[] from = new String[]{
        EventsContract.Columns.EVENT_TITLE,
        EventsContract.Columns.EVENT_ROOM,
        EventsContract.Columns.EVENT_DESCRIPTION
    };
    int[] to = new int[]{R.id.event_title, R.id.event_room, R.id.event_description};

    mAdapter = new SimpleCursorAdapter(mContext, R.layout.list_item_event, null, from, to, 0);

    setListAdapter(mAdapter);

  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    setEmptyMessage(R.string.no_appointments);
    // initialize CursorLoaders with IDs
    getLoaderManager().initLoader(COURSOR_EVENTS_LIST_LOADER, mArgs, this);
  }

  /**
   * Sets the message resource to be displayed when the ListView is empty
   *
   * @param messageRes string resource for the empty message
   */
  protected void setEmptyMessage(int messageRes) {
    mEmptyMessageTextView.setText(messageRes);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    activity.getContentResolver()
        .registerContentObserver(EventsContract.CONTENT_URI, true, mEventsObserver);
  }

  @Override
  public void onDetach() {
    super.onDetach();
    getActivity().getContentResolver().unregisterContentObserver(mEventsObserver);
  }

  public Loader<Cursor> onCreateLoader(int id, Bundle data) {
    setLoadingViewVisible(true);
    CursorLoader loader = new CursorLoader(mContext,
        CoursesContract.CONTENT_URI.buildUpon()
            .appendPath("events")
            .appendPath(data.getString(CoursesContract.Columns.Courses.COURSE_ID))
            .build(),
        CourseEventsListQuery.projection,
        EventsContract.Columns.EVENT_START + " >= strftime('%s','now')",
        null,
        EventsContract.DEFAULT_SORT_ORDER
    );

    return loader;
  }

  /**
   * Toggles the visibility of the list container and progress bar
   *
   * @param visible progress bar visibility
   */
  protected void setLoadingViewVisible(boolean visible) {
    if (mProgressView != null && mListContainerView != null) {
      mListContainerView.setVisibility(visible ? View.GONE : View.VISIBLE);
      mProgressView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
  }

  public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    if (getActivity() == null) {
      return;
    }
    mAdapter.swapCursor(cursor);
    setLoadingViewVisible(false);
  }

  public void onLoaderReset(Loader<Cursor> loader) {
    mAdapter.swapCursor(null);
  }

  private interface CourseEventsListQuery {
    String[] projection = {
        EventsContract.Columns._ID,
        EventsContract.Columns.EVENT_TITLE,
        EventsContract.Columns.EVENT_ROOM,
        EventsContract.Columns.EVENT_DESCRIPTION
    };
  }

}
