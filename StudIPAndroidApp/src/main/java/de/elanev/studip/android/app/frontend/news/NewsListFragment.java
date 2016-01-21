/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package de.elanev.studip.android.app.frontend.news;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.db.CoursesContract;
import de.elanev.studip.android.app.backend.db.InstitutesContract;
import de.elanev.studip.android.app.backend.db.NewsContract;
import de.elanev.studip.android.app.backend.db.UsersContract;
import de.elanev.studip.android.app.backend.net.SyncHelper;
import de.elanev.studip.android.app.util.DateTools;
import de.elanev.studip.android.app.widget.ProgressListFragment;
import de.elanev.studip.android.app.widget.SectionedCursorAdapter;

/**
 * @author joern
 */
public class NewsListFragment extends ProgressListFragment implements LoaderCallbacks<Cursor>,
    AdapterView.OnItemClickListener, SwipeRefreshLayout.OnRefreshListener,
    SyncHelper.SyncHelperCallbacks {

  public static final String TAG = NewsListFragment.class.getSimpleName();
  public static final String NEWS_SELECTOR = "news_selector";

  protected final ContentObserver mObserver = new ContentObserver(new Handler()) {
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
  private NewsAdapter mNewsAdapter;

  public NewsListFragment() {}

  public static Fragment newInstance(int newsSelector) {
    NewsListFragment fragment = new NewsListFragment();

    Bundle args = new Bundle();
    args.putInt(NEWS_SELECTOR, newsSelector);

    fragment.setArguments(args);

    return fragment;
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getActivity().setTitle(R.string.News);
    int newsSelector = getArguments().getInt(NEWS_SELECTOR);

    setEmptyMessage(R.string.no_news);

    mNewsAdapter = new NewsAdapter(mContext);
    if (newsSelector == NewsTabsAdapter.NEWS_GLOBAL) {
      mNewsAdapter.setShowSections(false);
      mListView.setAreHeadersSticky(false);
    }

    mListView.setOnItemClickListener(this);
    mListView.setAdapter(mNewsAdapter);

    mSwipeRefreshLayoutListView.setOnRefreshListener(this);
    mSwipeRefreshLayoutListView.setRefreshing(true);

    SyncHelper.getInstance(mContext).performNewsSync(this);
    // initialize CursorLoader
    getLoaderManager().initLoader(newsSelector, null, this);
  }

  @Override public void onAttach(Context context) {
    super.onAttach(context);
    context.getContentResolver()
        .registerContentObserver(NewsContract.CONTENT_URI, true, mObserver);
  }

  @Override public void onDetach() {
    super.onDetach();
    getActivity().getContentResolver().unregisterContentObserver(mObserver);
  }

  public Loader<Cursor> onCreateLoader(int id, Bundle data) {
    setLoadingViewVisible(true);
    Uri uri = NewsContract.CONTENT_URI;
    String[] projection = null;

    switch (id) {
      case NewsTabsAdapter.NEWS_COURSES:
        uri = uri.buildUpon().appendPath("courses").build();
        projection = NewsQuery.PROJECTION_COURSES;
        break;
      case NewsTabsAdapter.NEWS_INSTITUTES:
        uri = uri.buildUpon().appendPath("institutes").build();
        projection = NewsQuery.PROJECTION_INSTITUTES;
        break;
      case NewsTabsAdapter.NEWS_GLOBAL:
        uri = uri.buildUpon().appendPath("global").build();
        projection = NewsQuery.PROJECTION_GLOBAL;
        break;
    }

    return new CursorLoader(getActivity(),
        uri,
        projection,
        NewsContract.Columns.NEWS_EXPIRE + " > (strftime('%s','now')-"
            + NewsContract.Columns.NEWS_DATE + ")",
        null,
        NewsContract.DEFAULT_SORT_ORDER);
  }

  public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
    if (getActivity() == null) {
      return;
    }

    int idColumnIdx = 0;
    int titleColumnIdx = 0;

    int newsSelector = loader.getId();

    switch (newsSelector) {
      case NewsTabsAdapter.NEWS_COURSES:
        idColumnIdx = cursor.getColumnIndex(CoursesContract.Columns.Courses.COURSE_ID);

        titleColumnIdx = cursor.getColumnIndex(CoursesContract.Columns.Courses.COURSE_TITLE);

        break;
      case NewsTabsAdapter.NEWS_INSTITUTES:
        idColumnIdx = cursor.getColumnIndex(InstitutesContract.Columns.INSTITUTE_ID);

        titleColumnIdx = cursor.getColumnIndex(InstitutesContract.Columns.INSTITUTE_NAME);

        break;
      case NewsTabsAdapter.NEWS_GLOBAL:
        idColumnIdx = -1;
        titleColumnIdx = -1;
        break;
    }

    List<SectionedCursorAdapter.Section> sections = new ArrayList<SectionedCursorAdapter.Section>();
    if (idColumnIdx != -1 || titleColumnIdx != -1) {
      cursor.moveToFirst();
      String previousId = null;
      String currentId;
      while (!cursor.isAfterLast()) {

        currentId = cursor.getString(idColumnIdx);

        if (!TextUtils.equals(previousId, currentId)) {

          SectionedCursorAdapter.Section section = new SectionedCursorAdapter.Section(cursor.getPosition(),
              cursor.getString(titleColumnIdx));

          sections.add(section);
        }
        previousId = currentId;
        cursor.moveToNext();
      }
    }

    mNewsAdapter.setSections(sections);
    mNewsAdapter.swapCursor(cursor);

    setLoadingViewVisible(false);
  }

  public void onLoaderReset(Loader<Cursor> loader) {
    mNewsAdapter.swapCursor(null);
  }

  @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    Cursor c = (Cursor) mListView.getItemAtPosition(position);
    String topic = c.getString(c.getColumnIndex(NewsContract.Columns.NEWS_TOPIC));
    String body = c.getString(c.getColumnIndex(NewsContract.Columns.NEWS_BODY));
    String name = String.format("%s %s %s %s",
        c.getString(c.getColumnIndex(UsersContract.Columns.USER_TITLE_PRE)),
        c.getString(c.getColumnIndex(UsersContract.Columns.USER_FORENAME)),
        c.getString(c.getColumnIndex(UsersContract.Columns.USER_LASTNAME)),
        c.getString(c.getColumnIndex(UsersContract.Columns.USER_TITLE_POST)));
    String userImageUrl = c.getString(c.getColumnIndex(UsersContract.Columns.USER_AVATAR_NORMAL));
    long date = c.getLong(c.getColumnIndex(NewsContract.Columns.NEWS_MKDATE));

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

  @Override public void onRefresh() {
    SyncHelper.getInstance(mContext).performNewsSync(this);
  }

  @Override public void onSyncStarted() {
    mSwipeRefreshLayoutListView.setRefreshing(true);
  }

  @Override public void onSyncStateChange(int status) {
  }

  @Override public void onSyncFinished(int status) {
    if (status == SyncHelper.SyncHelperCallbacks.FINISHED_NEWS_SYNC) {
      mSwipeRefreshLayoutListView.setRefreshing(false);
    }
  }

  @Override public void onSyncError(int status, String errorMsg, int errorCode) {
    if (status == SyncHelper.SyncHelperCallbacks.ERROR_NEWS_SYNC && errorCode != 404) {
      if (getActivity() != null) {
        Toast.makeText(mContext, R.string.sync_error_default, Toast.LENGTH_LONG).show();
      }
      mSwipeRefreshLayoutListView.setRefreshing(false);
    }
  }

  private interface NewsQuery {

    String[] PROJECTION_COURSES = {
        NewsContract.Qualified.NEWS_ID,
        NewsContract.Qualified.NEWS_NEWS_TOPIC,
        NewsContract.Qualified.NEWS_NEWS_BODY,
        NewsContract.Qualified.NEWS_NEWS_MKDATE,
        NewsContract.Qualified.NEWS_NEWS_RANGE_ID,
        UsersContract.Qualified.USERS_USER_TITLE_PRE,
        UsersContract.Qualified.USERS_USER_TITLE_POST,
        UsersContract.Qualified.USERS_USER_FORENAME,
        UsersContract.Qualified.USERS_USER_LASTNAME,
        UsersContract.Qualified.USERS_USER_AVATAR_NORMAL,
        CoursesContract.Qualified.Courses.COURSES_COURSE_ID,
        CoursesContract.Qualified.Courses.COURSES_COURSE_TITLE
    };

    String[] PROJECTION_INSTITUTES = {
        NewsContract.Qualified.NEWS_ID,
        NewsContract.Qualified.NEWS_NEWS_TOPIC,
        NewsContract.Qualified.NEWS_NEWS_BODY,
        NewsContract.Qualified.NEWS_NEWS_MKDATE,
        NewsContract.Qualified.NEWS_NEWS_RANGE_ID,
        UsersContract.Qualified.USERS_USER_TITLE_PRE,
        UsersContract.Qualified.USERS_USER_TITLE_POST,
        UsersContract.Qualified.USERS_USER_FORENAME,
        UsersContract.Qualified.USERS_USER_LASTNAME,
        UsersContract.Qualified.USERS_USER_AVATAR_NORMAL,
        InstitutesContract.Qualified.INSTITUTES_INSTITUTE_ID,
        InstitutesContract.Qualified.INSTITUTES_INSTITUTE_NAME
    };

    String[] PROJECTION_GLOBAL = {
        NewsContract.Qualified.NEWS_ID,
        NewsContract.Qualified.NEWS_NEWS_TOPIC,
        NewsContract.Qualified.NEWS_NEWS_BODY,
        NewsContract.Qualified.NEWS_NEWS_MKDATE,
        NewsContract.Qualified.NEWS_NEWS_RANGE_ID,
        UsersContract.Qualified.USERS_USER_TITLE_PRE,
        UsersContract.Qualified.USERS_USER_TITLE_POST,
        UsersContract.Qualified.USERS_USER_FORENAME,
        UsersContract.Qualified.USERS_USER_LASTNAME,
        UsersContract.Qualified.USERS_USER_AVATAR_NORMAL
    };

  }

  private class NewsAdapter extends SectionedCursorAdapter {


    public NewsAdapter(Context context) {
      super(context);
    }

    @Override public View newView(Context context, Cursor cursor, ViewGroup parent) {
      return getActivity().getLayoutInflater()
          .inflate(R.layout.list_item_two_text_icon, parent, false);
    }

    @Override public void bindView(View view, Context context, final Cursor cursor) {
      final String newsTopic = cursor.getString(cursor.getColumnIndex(NewsContract.Columns.NEWS_TOPIC));
      final Long newsDate = cursor.getLong(cursor.getColumnIndex(NewsContract.Columns.NEWS_MKDATE));
      final String userForename = cursor.getString(cursor.getColumnIndex(UsersContract.Columns.USER_FORENAME));
      final String userLastname = cursor.getString(cursor.getColumnIndex(UsersContract.Columns.USER_LASTNAME));
      final String courseId = cursor.getString(cursor.getColumnIndex(NewsContract.Columns.NEWS_RANGE_ID));

      final TextView newsTopicView = (TextView) view.findViewById(R.id.text1);
      final TextView newsAuthorView = (TextView) view.findViewById(R.id.text2);
      final ImageView icon = (ImageView) view.findViewById(R.id.icon);

      switch (getArguments().getInt(NEWS_SELECTOR)) {
        //TODO: Separate icons for different course types and global
        // Seminar icon if switch fails oder entry is seminar type
        default:
        case NewsTabsAdapter.NEWS_COURSES:
          icon.setImageResource(R.drawable.ic_seminar);
          break;
        // Institute icon if institute or global entry type
        case NewsTabsAdapter.NEWS_INSTITUTES:
        case NewsTabsAdapter.NEWS_GLOBAL:
          icon.setImageResource(R.drawable.ic_action_global);
          break;
      }

      icon.setColorFilter(ContextCompat.getColor(getContext(), R.color.studip_mobile_dark),
          PorterDuff.Mode.SRC_IN);

      newsTopicView.setText(newsTopic);
      newsAuthorView.setText(DateTools.getLocalizedAuthorAndDateString(
          String.format("%s %s", userForename, userLastname), newsDate, getActivity()));
    }

  }

}
