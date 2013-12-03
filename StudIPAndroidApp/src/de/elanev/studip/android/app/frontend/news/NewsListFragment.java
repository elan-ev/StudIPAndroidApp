/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.frontend.news;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.db.CoursesContract;
import de.elanev.studip.android.app.backend.db.NewsContract;
import de.elanev.studip.android.app.backend.db.UsersContract;
import de.elanev.studip.android.app.util.TextTools;
import de.elanev.studip.android.app.widget.ProgressSherlockListFragment;
import de.elanev.studip.android.app.widget.SectionedCursorAdapter;

/**
 * @author joern
 */
public class NewsListFragment extends ProgressSherlockListFragment implements
        LoaderCallbacks<Cursor>, AdapterView.OnItemClickListener {

    public static final String TAG = NewsListFragment.class.getSimpleName();
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
    private NewsAdapter mNewsAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle(R.string.News);

        setEmptyMessage(R.string.no_news);
        mListView.setOnItemClickListener(this);

        mNewsAdapter = new NewsAdapter(mContext);
        mListView.setAdapter(mNewsAdapter);
        // initialize CursorLoader
        getLoaderManager().initLoader(0, null, this);
    }

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
     * @see de.elanev.studip.android.app.frontend.news.GeneralNewsFragment#
     * onCreateLoader (int, android.os.Bundle)
     */
    public Loader<Cursor> onCreateLoader(int id, Bundle data) {
        setLoadingViewVisible(true);
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

        List<SectionedCursorAdapter.Section> sections = new ArrayList<SectionedCursorAdapter.Section>();
        cursor.moveToFirst();
        String prevCourseId = null;
        String currentCourseId = null;
        while (!cursor.isAfterLast()) {
            currentCourseId = cursor
                    .getString(cursor
                            .getColumnIndex(CoursesContract.Columns.Courses.COURSE_TITLE));
            if (!TextUtils.equals(prevCourseId, currentCourseId)) {
                SectionedCursorAdapter.Section section = new SectionedCursorAdapter.Section(
                        cursor.getPosition(),
                        cursor.getString(cursor
                                .getColumnIndex(CoursesContract.Columns.Courses.COURSE_TITLE)));

                sections.add(section);
            }
            prevCourseId = currentCourseId;
            cursor.moveToNext();
        }

        mNewsAdapter.setSections(sections);
        mNewsAdapter.swapCursor(cursor);

        setLoadingViewVisible(false);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.LoaderManager.LoaderCallbacks#onLoaderReset
     * (android .support.v4.content.Loader)
     */
    public void onLoaderReset(Loader<Cursor> loader) {
        mNewsAdapter.swapCursor(null);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Cursor c = (Cursor) mListView.getItemAtPosition(position);
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
        String userImageUrl = c.getString(c
                .getColumnIndex(UsersContract.Columns.USER_AVATAR_NORMAL));
        long date = c.getLong(c.getColumnIndex(NewsContract.Columns.NEWS_DATE));

        Bundle args = new Bundle();
        args.putString(NewsContract.Columns.NEWS_TOPIC, topic);
        args.putString(NewsContract.Columns.NEWS_BODY, body);
        args.putLong(NewsContract.Columns.NEWS_DATE, date);
        args.putString(UsersContract.Columns.USER_FORENAME, name);
        args.putString(UsersContract.Columns.USER_AVATAR_NORMAL, userImageUrl);

        Intent intent = new Intent();
        intent.setClass(getActivity(), NewsItemViewActivity.class);
        intent.putExtras(args);
        startActivity(intent);
    }

    private interface NewsQuery {

        String[] PROJECTION = {NewsContract.Qualified.NEWS_ID,
                NewsContract.Qualified.NEWS_NEWS_TOPIC,
                NewsContract.Qualified.NEWS_NEWS_BODY,
                NewsContract.Qualified.NEWS_NEWS_DATE,
                NewsContract.Qualified.NEWS_NEWS_COURSE_ID,
                UsersContract.Qualified.USERS_USER_TITLE_PRE,
                UsersContract.Qualified.USERS_USER_TITLE_POST,
                UsersContract.Qualified.USERS_USER_FORENAME,
                UsersContract.Qualified.USERS_USER_LASTNAME,
                UsersContract.Qualified.USERS_USER_AVATAR_NORMAL,
                CoursesContract.Qualified.Courses.COURSES_COURSE_TITLE};

    }

    private class NewsAdapter extends SectionedCursorAdapter {


        public NewsAdapter(Context context) {
            super(context);
        }

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
            newsAuthorView.setText(TextTools.getLocalizedAuthorAndDateString(
                    String.format("%s %s", userForename, userLastname),
                    newsDate, getActivity()));
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return getActivity().getLayoutInflater().inflate(
                    R.layout.list_item_two_text_icon, parent, false);
        }

    }

}
