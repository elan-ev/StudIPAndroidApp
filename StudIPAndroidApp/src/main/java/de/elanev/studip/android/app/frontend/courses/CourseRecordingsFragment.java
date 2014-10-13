/*
 * Copyright (c) 2014 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.frontend.courses;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import de.elanev.studip.android.app.BuildConfig;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.db.CoursesContract;
import de.elanev.studip.android.app.backend.db.RecordingsContract;
import de.elanev.studip.android.app.backend.net.SyncHelper;
import de.elanev.studip.android.app.widget.ProgressListFragment;
import de.elanev.studip.android.app.widget.SectionedCursorAdapter;

/**
 * Fragment that loads the list of recordings for a specific course and displays it.
 *
 * @author Jörn
 */
public class CourseRecordingsFragment extends ProgressListFragment implements
    LoaderManager.LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener,
    SyncHelper.SyncHelperCallbacks, SwipeRefreshLayout.OnRefreshListener {
  public static final String TAG = CourseRecordingsFragment.class.getSimpleName();

  private ListAdapterRecordings mAdapter;
  private String mCourseId;

  private final ContentObserver mObserver = new ContentObserver(new Handler()) {
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

  public CourseRecordingsFragment() {}

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mContext = getActivity();
    mAdapter = new ListAdapterRecordings(mContext);
    mCourseId = getArguments().getString(CoursesContract.Columns.Courses.COURSE_ID);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    setEmptyMessage(R.string.no_recordings);
    mListView.setAdapter(mAdapter);
    mListView.setOnItemClickListener(this);
    mSwipeRefreshLayoutListView.setOnRefreshListener(this);
    getLoaderManager().initLoader(0, getArguments(), this);
  }

  @Override public void onStart() {
    super.onStart();
    mSwipeRefreshLayoutListView.setRefreshing(true);
    SyncHelper.getInstance(mContext).requestRecordingsForCourse(mCourseId, this);
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);

    activity.getContentResolver()
        .registerContentObserver(RecordingsContract.CONTENT_URI, true, mObserver);
  }

  @Override public void onDetach() {
    super.onDetach();

    getActivity().getContentResolver().unregisterContentObserver(mObserver);
  }

  @Override public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
    setLoadingViewVisible(true);
    return new CursorLoader(mContext,
        CoursesContract.CONTENT_URI.buildUpon()
            .appendPath(RecordingsContract.PATH_RECORDINGS)
            .appendPath(mCourseId)
            .build(),
        RecordingsQuery.projection,
        null,
        null,
        RecordingsContract.DEFAULT_SORT_ORDER);
  }

  @Override public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
    if (getActivity() != null) {
      mAdapter.swapCursor(cursor);
      setLoadingViewVisible(false);
    }
  }

  @Override public void onLoaderReset(Loader<Cursor> cursorLoader) {
    mAdapter.swapCursor(null);
  }

  @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Cursor c = (Cursor) mAdapter.getItem(position);
    String url = c.getString(c.getColumnIndex(RecordingsContract.Columns.Recordings.RECORDING_PRESENTATION_DOWNLOAD));

    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setDataAndType(Uri.parse(url), "video/*");
    startActivity(Intent.createChooser(intent,
        mContext.getString(R.string.recordings_chooser_title)));
  }

  @Override public void onSyncStarted() {
    mSwipeRefreshLayoutListView.setRefreshing(true);
  }

  @Override public void onSyncStateChange(int status) {
  }

  @Override public void onSyncFinished(int status) {
    if (status == SyncHelper.SyncHelperCallbacks.FINISHED_RECORDINGS_SYNC) {
      mSwipeRefreshLayoutListView.setRefreshing(false);
    }
  }

  @Override public void onSyncError(int status, VolleyError error) {
    if (status == SyncHelper.SyncHelperCallbacks.ERROR_RECORDINGS_SYNC && error != null
        && error.networkResponse != null && error.networkResponse.statusCode != 404) {
      if (getActivity() != null) {
        Toast.makeText(mContext, R.string.sync_error_generic, Toast.LENGTH_LONG).show();
      }
      mSwipeRefreshLayoutListView.setRefreshing(false);
    }
  }

  @Override public void onRefresh() {
    SyncHelper.getInstance(mContext).requestRecordingsForCourse(mCourseId, this);
  }

  private interface RecordingsQuery {
    String[] projection = {
        RecordingsContract.Qualified.Recordings.RECORDINGS_ID,
        RecordingsContract.Qualified.Recordings.RECORDINGS_RECORDING_TITLE,
        RecordingsContract.Qualified.Recordings.RECORDINGS_RECORDING_START,
        RecordingsContract.Qualified.Recordings.RECORDINGS_RECORDING_PRESENTATION_DOWNLOAD,
        RecordingsContract.Qualified.Recordings.RECORDINGS_RECORDING_AUTHOR,
        RecordingsContract.Qualified.Recordings.RECORDINGS_RECORDING_PREVIEW,
        RecordingsContract.Qualified.Recordings.RECORDINGS_RECORDING_DURATION
    };
  }

  private class ListAdapterRecordings extends SectionedCursorAdapter {

    SimpleDateFormat mDateParser = new SimpleDateFormat("yyyy-MM-d'T'HH:mm:ss'Z'");

    public ListAdapterRecordings(Context context) {
      super(context);
    }

    @Override public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
      View v = LayoutInflater.from(mContext)
          .inflate(R.layout.list_item_recording, viewGroup, false);
      RecordingHolder holder = new RecordingHolder();
      holder.preview = (ImageView) v.findViewById(R.id.preview_image);
      holder.author = (TextView) v.findViewById(R.id.author);
      holder.title = (TextView) v.findViewById(R.id.title);
      holder.duration = (TextView) v.findViewById(R.id.duration);
      holder.date = (TextView) v.findViewById(R.id.date);

      v.setTag(holder);
      return v;
    }

    @Override public void bindView(View view, Context context, Cursor cursor) {
      final String author = cursor.getString(cursor.getColumnIndex(RecordingsContract.Columns.Recordings.RECORDING_AUTHOR));
      final long duration = cursor.getLong(cursor.getColumnIndex(RecordingsContract.Columns.Recordings.RECORDING_DURATION));
      final String title = cursor.getString(cursor.getColumnIndex(RecordingsContract.Columns.Recordings.RECORDING_TITLE));
      final String previewUrl = cursor.getString(cursor.getColumnIndex(RecordingsContract.Columns.Recordings.RECORDING_PREVIEW));
      final String start = cursor.getString(cursor.getColumnIndex(RecordingsContract.Columns.Recordings.RECORDING_START));

      RecordingHolder holder = (RecordingHolder) view.getTag();

      holder.author.setText(author);
      holder.title.setText(title);
      String durationString = String.format("%02d:%02d:%02d",
          TimeUnit.MILLISECONDS.toHours(duration),
          TimeUnit.MILLISECONDS.toMinutes(duration)
              - TimeUnit.HOURS.toMinutes((TimeUnit.MILLISECONDS).toHours(duration)),
          TimeUnit.MILLISECONDS.toSeconds(duration)
              - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)));
      holder.duration.setText(durationString);

      try {
        Date startDate = mDateParser.parse(start);
        holder.date.setText(DateUtils.formatDateTime(mContext,
            startDate.getTime(),
            DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_NUMERIC_DATE
                | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY));
      } catch (ParseException e) {
        e.printStackTrace();
      }

      Picasso picasso = Picasso.with(mContext);

      if (BuildConfig.DEBUG) {
        picasso.setDebugging(true);
      }

      picasso.load(previewUrl)
          .resizeDimen(R.dimen.preview_image_width, R.dimen.preview_image_height)
          .centerCrop()
          .placeholder(R.drawable.nobody_normal)
          .into(holder.preview);
    }

    @Override public View getHeaderView(int position, View view, ViewGroup viewGroup) {
      if (view == null) {
        view = LayoutInflater.from(mContext).inflate(R.layout.list_item_header, viewGroup, false);
      }
      view.findViewById(R.id.list_item_header_textview).setVisibility(View.GONE);
      return view;
    }

    @Override public long getHeaderId(int position) {
      return 0;
    }

    private class RecordingHolder {
      ImageView preview;
      TextView title;
      TextView author;
      TextView date;
      TextView duration;
    }
  }
}
