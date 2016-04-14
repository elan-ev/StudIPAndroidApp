/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package de.elanev.studip.android.app.courses;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.data.db.CoursesContract;
import de.elanev.studip.android.app.data.db.EventsContract;
import de.elanev.studip.android.app.util.DateTools;

/**
 * @author joern
 */
public class CourseScheduleFragment extends ListFragment implements LoaderCallbacks<Cursor> {
  public static final String TAG = CourseScheduleFragment.class.getSimpleName();
  private static final int COURSOR_EVENTS_LIST_LOADER = 102;
  protected final ContentObserver mEventsObserver = new ContentObserver(new Handler()) {
    @Override public void onChange(boolean selfChange) {
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
  private ScheduleAdapter mAdapter;
  private View mListContainerView, mProgressView;
  private TextView mEmptyMessageTextView;

  public CourseScheduleFragment() {}

  public static CourseScheduleFragment newInstance(Bundle arguments) {
    CourseScheduleFragment fragment = new CourseScheduleFragment();

    fragment.setArguments(arguments);

    return fragment;
  }

  @Override public View onCreateView(LayoutInflater inflater,
      ViewGroup container,
      Bundle savedInstanceState) {

    View v = inflater.inflate(R.layout.simple_list, container, false);
    mEmptyMessageTextView = (TextView) v.findViewById(R.id.empty_message);
    mListContainerView = v.findViewById(R.id.list_container);
    mProgressView = v.findViewById(R.id.progressbar);

    return v;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mArgs = getArguments();
    mContext = getActivity();

    mAdapter = new ScheduleAdapter(getActivity(), null, 0);
    setListAdapter(mAdapter);
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
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

  @Override public void onAttach(Context context) {
    super.onAttach(context);
    context.getContentResolver()
        .registerContentObserver(EventsContract.CONTENT_URI, true, mEventsObserver);
  }

  @Override public void onDetach() {
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
        EventsContract.DEFAULT_SORT_ORDER);

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
        EventsContract.Columns.EVENT_DESCRIPTION,
        EventsContract.Columns.EVENT_START,
        EventsContract.Columns.EVENT_END,
        EventsContract.Columns.EVENT_CATEGORIES
    };
  }

  private class ScheduleAdapter extends CursorAdapter {

    public ScheduleAdapter(Context context, Cursor c, int flags) {
      super(context, c, flags);
    }

    @Override public View newView(Context context, Cursor cursor, ViewGroup parent) {
      View v = getActivity().getLayoutInflater().inflate(R.layout.list_item_event, parent, false);

      ViewHolder viewHolder = new ViewHolder();
      viewHolder.eventTitleTextView = (TextView) v.findViewById(R.id.event_title);
      viewHolder.eventDateTextView = (TextView) v.findViewById(R.id.event_room);
      viewHolder.eventDescriptionTextView = (TextView) v.findViewById(R.id.event_description);

      v.setTag(viewHolder);

      return v;
    }

    @Override public void bindView(View view, Context context, Cursor cursor) {
      String eventTitle = cursor.getString(cursor.getColumnIndex(EventsContract.Columns.EVENT_TITLE));
      String eventCategory = cursor.getString((cursor.getColumnIndex(EventsContract.Columns.EVENT_CATEGORIES)));
      long eventStart = cursor.getLong(cursor.getColumnIndex(EventsContract.Columns.EVENT_START));
      long eventEnd = cursor.getLong(cursor.getColumnIndex(EventsContract.Columns.EVENT_END));
      String eventDescription = cursor.getString(cursor.getColumnIndex(EventsContract.Columns.EVENT_DESCRIPTION));
      String eventRoom = cursor.getString(cursor.getColumnIndex(EventsContract.Columns.EVENT_ROOM));

      ViewHolder viewHolder = (ViewHolder) view.getTag();

      String dateString = String.format("%s %s - %s",
          DateTools.getShortLocalizedTime(eventStart * 1000L, context),
          DateTools.get24hTime(eventStart * 1000),
          DateTools.get24hTime(eventEnd * 1000));
      String title;
      String description = "";

      if (TextUtils.isEmpty(eventTitle)) {
        if (TextUtils.isEmpty(eventRoom) && TextUtils.isEmpty(eventCategory)) {
          title = dateString;
          viewHolder.eventDateTextView.setVisibility(View.GONE);
        } else {
          title = eventCategory + " (" + eventRoom + ")";
          viewHolder.eventDateTextView.setText(dateString);
          viewHolder.eventDateTextView.setVisibility(View.VISIBLE);
        }
      } else {
        title = eventTitle;
        viewHolder.eventDateTextView.setText(dateString);
        viewHolder.eventDateTextView.setVisibility(View.VISIBLE);
        description = eventCategory + " (" + eventRoom + ")\n";
      }
      description += eventDescription;

      viewHolder.eventTitleTextView.setText(title.trim());
      viewHolder.eventDescriptionTextView.setText(description.trim());
    }
  }

  static class ViewHolder {
    TextView eventTitleTextView;
    TextView eventDescriptionTextView;
    TextView eventDateTextView;
  }

}
