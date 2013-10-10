/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.frontend.courses;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.db.CoursesContract;
import de.elanev.studip.android.app.backend.db.SemestersContract;
import de.elanev.studip.android.app.frontend.util.SimpleSectionedListAdapter;
import de.elanev.studip.android.app.widget.ProgressSherlockListFragment;

/**
 * @author joern
 */
public class CoursesFragment extends ProgressSherlockListFragment implements
        LoaderCallbacks<Cursor> {
    public static final String TAG = CoursesFragment.class.getSimpleName();
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
    private CourseAdapter mCourseAdapter;
    private SimpleSectionedListAdapter mAdapter;

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.Fragment#onActivityCreated(android.os.Bundle)
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.Courses);

        setEmptyMessage(R.string.no_courses);

        mCourseAdapter = new CourseAdapter(mContext);
        mAdapter = new SimpleSectionedListAdapter(mContext,
                R.layout.list_item_header, mCourseAdapter);
        setListAdapter(mAdapter);

        getLoaderManager().initLoader(0, null, this);
    }

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

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Object selectedObject = l.getItemAtPosition(position);
        if (selectedObject instanceof CursorWrapper) {
            Intent intent = new Intent();
            intent.setClass(getActivity(), CourseViewActivity.class);
            intent.putExtra(
                    CoursesContract.Columns.Courses.COURSE_ID,
                    ((CursorWrapper) selectedObject).getString(((CursorWrapper) selectedObject)
                            .getColumnIndex(CoursesContract.Columns.Courses.COURSE_ID)));
            intent.putExtra(
                    CoursesContract.Columns.Courses._ID,
                    ((CursorWrapper) selectedObject).getString(((CursorWrapper) selectedObject)
                            .getColumnIndex(CoursesContract.Columns.Courses._ID)));

            mContext.startActivity(intent);
        }
    }

    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        setLoadingViewVisible(true);
        return new CursorLoader(getActivity(), CoursesContract.CONTENT_URI,
                CourseQuery.PROJECTION,
                CoursesContract.Columns.Courses.COURSE_ID + " != " + "'"
                        + getString(R.string.restip_news_global_identifier)
                        + "'", null,
                SemestersContract.Qualified.SEMESTERS_SEMESTER_BEGIN + " DESC");
    }

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
                            .getColumnIndex(CoursesContract.Columns.Courses.COURSE_SEMESERT_ID));
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

        setLoadingViewVisible(false);
    }

    public void onLoaderReset(Loader<Cursor> loader) {
    }

    /*
     * Interface which encapsulates the content provider query projection array
     */
    private interface CourseQuery {

        String[] PROJECTION = {CoursesContract.Qualified.Courses.COURSES_ID,
                CoursesContract.Qualified.Courses.COURSES_COURSE_TITLE,
                CoursesContract.Qualified.Courses.COURSES_COURSE_ID,
                SemestersContract.Qualified.SEMESTERS_SEMESTER_ID,
                SemestersContract.Qualified.SEMESTERS_SEMESTER_TITLE,
                SemestersContract.Qualified.SEMESTERS_SEMESTER_BEGIN};

    }

    /*
     * CursorAdapter subclass to display the courses list item properly
     */
    private class CourseAdapter extends CursorAdapter {

        /**
         * Constructs the CourseAdapter with the passed context
         *
         * @param context execution context
         */
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
                    .findViewById(R.id.text1);
            final ImageView icon = (ImageView) view.findViewById(R.id.icon1);

            courseTitleTextVew.setText(courseTitle);
            icon.setImageResource(R.drawable.ic_menu_courses);
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
                    R.layout.list_item_single_text_icon, parent, false);
        }

    }

}
