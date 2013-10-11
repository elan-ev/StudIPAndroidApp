/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.frontend.courses;

import android.app.Activity;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import java.util.ArrayList;
import java.util.List;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.db.CoursesContract;
import de.elanev.studip.android.app.backend.db.UsersContract;
import de.elanev.studip.android.app.backend.net.SyncHelper;
import de.elanev.studip.android.app.frontend.util.SimpleSectionedListAdapter;
import de.elanev.studip.android.app.widget.ListAdapterUsers;
import de.elanev.studip.android.app.widget.UserListFragment;

/**
 * @author joern
 */
public class CourseAttendeesFragment extends UserListFragment implements
        LoaderCallbacks<Cursor> {
    public static final String TAG = CourseAttendeesFragment.class
            .getSimpleName();
    private final ContentObserver mObserver = new ContentObserver(
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
    private ListAdapterUsers mUsersAdapter;
    private SimpleSectionedListAdapter mAdapter;
    private Bundle mArgs;
    private String mCourseId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mArgs = getArguments();

        mCourseId = mArgs.getString(CoursesContract.Columns.Courses.COURSE_ID);
        // Creating the adapters for the listview

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setEmptyMessage(R.string.no_attendees);

        mUsersAdapter = new ListAdapterUsers(mContext);
        mAdapter = new SimpleSectionedListAdapter(mContext,
                R.layout.list_item_header, mUsersAdapter);
        setListAdapter(mAdapter);

        // initialize CursorLoader
        getLoaderManager().initLoader(0, mArgs, this);

        SyncHelper.getInstance(mContext).loadUsersForCourse(mCourseId);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.getContentResolver().registerContentObserver(
                UsersContract.CONTENT_URI, true, mObserver);
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
     * android.support.v4.app.LoaderManager.LoaderCallbacks#onCreateLoader(int,
     * android.os.Bundle)
     */
    public Loader<Cursor> onCreateLoader(int id, Bundle data) {
        setLoadingViewVisible(true);
        return new CursorLoader(
                mContext,
                UsersContract.CONTENT_URI.buildUpon().appendPath("course")
                        .appendPath(mCourseId).build(),
                UsersQuery.projection,
                null, // UsersContract.Qualified.USERS_USER_ID + " NOT NULL",
                null,
                CoursesContract.Qualified.CourseUsers.COURSES_USERS_TABLE_COURSE_USER_USER_ROLE
                        + " ASC, " + UsersContract.DEFAULT_SORT_ORDER);
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
        int prevRole = -1;
        int currRole = -1;
        while (!cursor.isAfterLast()) {
            currRole = cursor
                    .getInt(cursor
                            .getColumnIndex(CoursesContract.Columns.CourseUsers.COURSE_USER_USER_ROLE));
            if (currRole != prevRole) {
                String role = null;
                switch (currRole) {
                    case CoursesContract.USER_ROLE_TEACHER:
                        role = getString(R.string.Teacher);
                        break;
                    case CoursesContract.USER_ROLE_TUTOR:
                        role = getString(R.string.Tutor);
                        break;
                    case CoursesContract.USER_ROLE_STUDENT:
                        role = getString(R.string.Student);
                        break;
                    default:
                        throw new UnknownError("unknown role type");
                }
                sections.add(new SimpleSectionedListAdapter.Section(cursor
                        .getPosition(), role));
            }

            prevRole = currRole;

            cursor.moveToNext();
        }

        mUsersAdapter.changeCursor(cursor);

        SimpleSectionedListAdapter.Section[] dummy = new SimpleSectionedListAdapter.Section[sections
                .size()];
        mAdapter.setSections(sections.toArray(dummy));

        setLoadingViewVisible(false);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.support.v4.app.LoaderManager.LoaderCallbacks#onLoaderReset(android
     * .support.v4.content.Loader)
     */
    public void onLoaderReset(Loader<Cursor> loader) {
        mUsersAdapter.swapCursor(null);
    }

    private interface UsersQuery {
        String[] projection = {
                UsersContract.Qualified.USERS_ID,
                UsersContract.Qualified.USERS_USER_ID,
                UsersContract.Qualified.USERS_USER_TITLE_PRE,
                UsersContract.Qualified.USERS_USER_FORENAME,
                UsersContract.Qualified.USERS_USER_LASTNAME,
                UsersContract.Qualified.USERS_USER_TITLE_POST,
                UsersContract.Qualified.USERS_USER_AVATAR_NORMAL,
                CoursesContract.Qualified.CourseUsers.COURSES_USERS_TABLE_COURSE_USER_USER_ROLE};
    }
}
