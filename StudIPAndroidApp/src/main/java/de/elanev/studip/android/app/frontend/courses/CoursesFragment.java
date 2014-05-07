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
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.db.CoursesContract;
import de.elanev.studip.android.app.backend.db.SemestersContract;
import de.elanev.studip.android.app.widget.ProgressSherlockListFragment;
import de.elanev.studip.android.app.widget.SectionedCursorAdapter;

/**
 * @author joern
 */
public class CoursesFragment extends ProgressSherlockListFragment implements LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {
  public static final String TAG = CoursesFragment.class.getSimpleName();

  private static final String ID = CoursesContract.Columns.Courses._ID;
  private static final String COURSE_ID = CoursesContract.Columns.Courses.COURSE_ID;
  private static final String COLOR = CoursesContract.Columns.Courses.COURSE_COLOR;
  private static final String TYPE = CoursesContract.Columns.Courses.COURSE_TYPE;
  private static final String SEMESTER_ID = CoursesContract.Columns.Courses.COURSE_SEMESERT_ID;
  private static final String SEMESTER_TITLE = SemestersContract.Columns.SEMESTER_TITLE;

  protected final ContentObserver mObserver = new ContentObserver(new Handler()) {
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
  private CoursesCursorAdapter mAdapter;

  public CoursesFragment() {}

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mContext = getSherlockActivity();
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getActivity().setTitle(R.string.Courses);

    setEmptyMessage(R.string.no_courses);

    mAdapter = new CoursesCursorAdapter(getActivity());
    mListView.setOnItemClickListener(this);
    mListView.setAdapter(mAdapter);

    getLoaderManager().initLoader(0, null, this);
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    activity.getContentResolver()
        .registerContentObserver(CoursesContract.CONTENT_URI, true, mObserver);
  }

  @Override
  public void onDetach() {
    super.onDetach();
    getActivity().getContentResolver().unregisterContentObserver(mObserver);
  }

  @Override
  public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Cursor cursor = (Cursor) mListView.getItemAtPosition(position);
    String courseId = cursor.getString(cursor.getColumnIndex(COURSE_ID));
    long cid = cursor.getLong(cursor.getColumnIndex(ID));
    Intent intent = new Intent();
    intent.setClass(getActivity(), CourseViewActivity.class);
    intent.putExtra(CoursesContract.Columns.Courses.COURSE_ID, courseId);
    intent.putExtra(CoursesContract.Columns.Courses._ID, cid);

    mContext.startActivity(intent);
  }

  @Override public Loader<Cursor> onCreateLoader(int id, Bundle args) {
    setLoadingViewVisible(true);
    Context c = getActivity();
    return new CursorLoader(getActivity(),
        CoursesContract.CONTENT_URI,
        CourseQuery.PROJECTION,
        CoursesContract.Columns.Courses.COURSE_ID + " != " + "'" +
            getString(R.string.restip_news_global_identifier) + "'",
        null,
        SemestersContract.Qualified.SEMESTERS_SEMESTER_BEGIN + " DESC"
    );
  }

  @Override public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    if (getActivity() == null) {
      return;
    }


    cursor.moveToFirst();
    if (!cursor.isAfterLast()) {
      ArrayList<SectionedCursorAdapter.Section> sections = new ArrayList<SectionedCursorAdapter.Section>();
      String prevSemesterId = "";
      String currSemesterId = "";

      int semesterIdIdx = cursor.getColumnIndex(SEMESTER_ID);
      int semesterTitleIdx = cursor.getColumnIndex(SEMESTER_TITLE);

      while (!cursor.isAfterLast()) {
        currSemesterId = cursor.getString(semesterIdIdx);
        if (!TextUtils.equals(prevSemesterId, currSemesterId)) {
          sections.add(new SectionedCursorAdapter.Section(cursor.getPosition(),
              cursor.getString(semesterTitleIdx)));
        }

        prevSemesterId = currSemesterId;
        cursor.moveToNext();
      }

      mAdapter.setSections(sections);
    }
    mAdapter.swapCursor(cursor);
    setLoadingViewVisible(false);
  }

  @Override public void onLoaderReset(Loader<Cursor> loader) {}

  /*
   * Interface which encapsulates the content provider query projection array
   */
  private static interface CourseQuery {

    String[] PROJECTION = {
        CoursesContract.Qualified.Courses.COURSES_ID,
        CoursesContract.Qualified.Courses.COURSES_COURSE_TITLE,
        CoursesContract.Qualified.Courses.COURSES_COURSE_ID,
        CoursesContract.Qualified.Courses.COURSES_COURSE_TYPE,
        CoursesContract.Qualified.Courses.COURSES_COURSE_COLOR,
        SemestersContract.Qualified.SEMESTERS_SEMESTER_ID,
        SemestersContract.Qualified.SEMESTERS_SEMESTER_TITLE
    };

  }

  private static class CoursesCursorAdapter extends SectionedCursorAdapter {
    private LayoutInflater mInflater;

    public CoursesCursorAdapter(Context context) {
      super(context);
      mInflater = LayoutInflater.from(context);
    }

    @Override public View newView(Context context, Cursor cursor, ViewGroup parent) {
      View row = mInflater.inflate(R.layout.list_item_single_text_icon, parent, false);
      ViewHolder holder = new ViewHolder();
      holder.title = (TextView) row.findViewById(R.id.text1);
      holder.icon = (ImageView) row.findViewById(R.id.icon1);
      row.setTag(holder);
      return row;
    }

    @Override public void bindView(View view, Context context, Cursor cursor) {
      int courseTitleColIdx = 1;
      int courseTypeColIdx = cursor.getColumnIndex(TYPE);
      int courseColorColIdx = cursor.getColumnIndex(COLOR);

      String title = cursor.getString(courseTitleColIdx);
      int type = cursor.getInt(courseTypeColIdx);
      String color = cursor.getString(courseColorColIdx);

      // get holder and update views with positions informations
      ViewHolder holder = (ViewHolder) view.getTag();
      holder.title.setText(title);
      if (type == 99) {
        if (TextUtils.equals(color, "#ffffff"))
          holder.icon.setImageResource(R.drawable.ic_studygroup_blue);
        else holder.icon.setImageResource(R.drawable.ic_studygroup);
      } else {
        if (TextUtils.equals(color, "#ffffff"))
          holder.icon.setImageResource(R.drawable.ic_seminar_blue);
        else holder.icon.setImageResource(R.drawable.ic_menu_courses);
      }

      if (color != null) try {
        int c = Color.parseColor(color);
        holder.icon.setBackgroundColor(c);
      } catch (Exception e) {
        Log.wtf(TAG, e.getMessage());
      }
    }

    private class ViewHolder {
      ImageView icon;
      TextView title;
    }
  }

}


