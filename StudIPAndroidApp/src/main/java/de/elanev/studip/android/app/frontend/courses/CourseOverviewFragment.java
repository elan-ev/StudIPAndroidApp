/*
 * Copyright (c) 2014 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package de.elanev.studip.android.app.frontend.courses;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.squareup.picasso.Picasso;

import de.elanev.studip.android.app.BuildConfig;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.db.CoursesContract;
import de.elanev.studip.android.app.backend.db.EventsContract;
import de.elanev.studip.android.app.backend.db.NewsContract;
import de.elanev.studip.android.app.backend.db.UsersContract;
import de.elanev.studip.android.app.util.TextTools;

/**
 * @author joern
 */
public class CourseOverviewFragment extends SherlockFragment implements LoaderCallbacks<Cursor> {
  public static final String TAG = CourseOverviewFragment.class.getSimpleName();
  private static final int COURSE_LOADER = 101;
  protected final ContentObserver mObserverCourse = new ContentObserver(new Handler()) {

    @Override
    public void onChange(boolean selfChange) {
      if (getActivity() == null) {
        return;
      }

      Loader<Cursor> loader = getLoaderManager().getLoader(COURSE_LOADER);
      if (loader != null) {
        loader.forceLoad();
      }
    }
  };
  private static final int COURSE_EVENTS_LOADER = 102;
  protected final ContentObserver mObserverEvents = new ContentObserver(new Handler()) {

    @Override
    public void onChange(boolean selfChange) {
      if (getActivity() == null) {
        return;
      }

      Loader<Cursor> loader = getLoaderManager().getLoader(COURSE_EVENTS_LOADER);
      if (loader != null) {
        loader.forceLoad();
      }
    }
  };
  private static final int COURSE_NEWS_LOADER = 103;
  protected final ContentObserver mObserverNews = new ContentObserver(new Handler()) {

    @Override
    public void onChange(boolean selfChange) {
      if (getActivity() == null) {
        return;
      }

      Loader<Cursor> loader = getLoaderManager().getLoader(COURSE_NEWS_LOADER);
      if (loader != null) {
        loader.forceLoad();
      }
    }
  };
  private static final int COURSE_TEACHERS_LOADER = 104;
  private TextView mTitleTextView, mTeacherNameTextView, mDescriptionTextView, mNewsTitleTextView, mNewsAuthorTextView, mNewsTextTextView, mNewsShowMoreTextView, mTeacherCountTextView;
  private ImageView mUserImageView;
  private Context mContext;
  private Bundle mArgs;

  public CourseOverviewFragment() {}

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mArgs = getArguments();
    mContext = getActivity();
  }

  @Override
  public View onCreateView(LayoutInflater inflater,
      ViewGroup container,
      Bundle savedInstanceState) {

    View view = inflater.inflate(R.layout.fragment_course_details, null);

    mTitleTextView = (TextView) view.findViewById(R.id.course_title);
    mDescriptionTextView = (TextView) view.findViewById(R.id.course_description);
    mTeacherNameTextView = (TextView) view.findViewById(R.id.course_teacher_name);
    mTeacherCountTextView = (TextView) view.findViewById(R.id.course_teacher_count);
    mNewsTitleTextView = (TextView) view.findViewById(R.id.news_title);
    mNewsAuthorTextView = (TextView) view.findViewById(R.id.news_author);
    mNewsTextTextView = (TextView) view.findViewById(R.id.news_text);
    mNewsShowMoreTextView = (TextView) view.findViewById(R.id.show_news_body);
    mUserImageView = (ImageView) view.findViewById(R.id.user_image);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    // initialize CursorLoaders with IDs
    LoaderManager lm = getLoaderManager();
    lm.initLoader(COURSE_LOADER, mArgs, this);
    lm.initLoader(COURSE_TEACHERS_LOADER, mArgs, this);
    lm.initLoader(COURSE_EVENTS_LOADER, mArgs, this);
    lm.initLoader(COURSE_NEWS_LOADER, mArgs, this);

    mNewsShowMoreTextView.setOnClickListener(new OnClickListener() {

      public void onClick(View v) {
        toggleLatestNewsView();
      }

    });
  }

  public void toggleLatestNewsView() {
    if (mNewsShowMoreTextView != null && mNewsTextTextView != null) {
      int viewVisibility = mNewsTextTextView.getVisibility();
      switch (viewVisibility) {
        case View.VISIBLE:
          mNewsTextTextView.setVisibility(View.GONE);
          mNewsShowMoreTextView.setText(R.string.show_more);
          break;
        case View.GONE:
          mNewsTextTextView.setVisibility(View.VISIBLE);
          mNewsShowMoreTextView.setText(R.string.show_less);
          break;
      }
    }
  }

  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    final ContentResolver contentResolver = activity.getContentResolver();

    contentResolver.registerContentObserver(CoursesContract.CONTENT_URI, true, mObserverCourse);

    contentResolver.registerContentObserver(EventsContract.CONTENT_URI, true, mObserverEvents);

    contentResolver.registerContentObserver(NewsContract.CONTENT_URI, true, mObserverNews);
  }

  @Override
  public void onDetach() {
    super.onDetach();
    final ContentResolver contentResolver = getActivity().getContentResolver();
    contentResolver.unregisterContentObserver(mObserverCourse);
    contentResolver.unregisterContentObserver(mObserverEvents);
    contentResolver.unregisterContentObserver(mObserverNews);
  }

  public Loader<Cursor> onCreateLoader(int id, Bundle data) {

    String cid = data.getString(CoursesContract.Columns.Courses.COURSE_ID);
    long dbId = data.getLong(CoursesContract.Columns.Courses._ID);
    // Create loaders based on id
    switch (id) {
      case COURSE_LOADER:
        return new CursorLoader(mContext,
            CoursesContract.CONTENT_URI.buildUpon().appendPath(Long.toString(dbId)).build(),
            CourseItemQuery.projection,
            null,
            null,
            CoursesContract.DEFAULT_SORT_ORDER
        );

      case COURSE_EVENTS_LOADER:
        return new CursorLoader(mContext,
            CoursesContract.CONTENT_URI.buildUpon().appendPath("events").appendPath(cid).build(),
            CourseEventQuery.projection,
            EventsContract.Columns.EVENT_START + " >= strftime" +
                "('%s','now')",
            null,
            EventsContract.DEFAULT_SORT_ORDER + " LIMIT 1"
        );

      case COURSE_NEWS_LOADER:
        return new CursorLoader(mContext,
            NewsContract.CONTENT_URI.buildUpon().appendPath(cid).build(),
            CourseNewsQuery.projection,
            null,
            null,
            NewsContract.DEFAULT_SORT_ORDER + " LIMIT 1"
        );
      case COURSE_TEACHERS_LOADER:
        return new CursorLoader(mContext,
            UsersContract.CONTENT_URI.buildUpon().appendPath("course").appendPath(cid).build(),
            CourseUsersQuery.projection,
            CourseUsersQuery.selection,
            CourseUsersQuery.selectionArgs,
            CoursesContract.Qualified.CourseUsers.COURSES_USERS_TABLE_ID + " ASC"
        );
    }
    return null;

  }

  public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    if (getActivity() == null) {
      return;
    }
    cursor.moveToFirst();

    int loaderId = loader.getId();
    switch (loaderId) {
      case COURSE_LOADER:

        if (!cursor.isAfterLast()) {
          String courseTitle = cursor.getString(cursor.getColumnIndex(CoursesContract.Columns.Courses.COURSE_TITLE));
          String courseDescription = cursor.getString(cursor.getColumnIndex(CoursesContract.Columns.Courses.COURSE_DESCIPTION));
          mTitleTextView.setText(courseTitle);
          getSherlockActivity().setTitle(courseTitle);

          if (!TextUtils.isEmpty(courseDescription)) {
            mDescriptionTextView.setText(courseDescription);
            mDescriptionTextView.setMovementMethod(new ScrollingMovementMethod());
          }

        }
        break;
      case COURSE_EVENTS_LOADER:
        final TextView nextAppointmentTextView = (TextView) getView().findViewById(R.id.course_next_appointment);
        if (cursor.getCount() >= 1) {

          String room = cursor.getString(cursor.getColumnIndex(EventsContract.Columns.EVENT_ROOM));
          String title = cursor.getString(cursor.getColumnIndex(EventsContract.Columns.EVENT_TITLE));
          nextAppointmentTextView.setText(String.format("%s\n%s", title, room));
        }

        break;
      case COURSE_NEWS_LOADER:
        if (cursor.getCount() >= 1) {

          final String newsTopic = cursor.getString(cursor.getColumnIndex(NewsContract.Columns.NEWS_TOPIC));
          final Long newsDate = cursor.getLong(cursor.getColumnIndex(NewsContract.Columns.NEWS_MKDATE));
          final String newsBody = cursor.getString(cursor.getColumnIndex(NewsContract.Columns.NEWS_BODY));
          final String userForename = cursor.getString(cursor.getColumnIndex(UsersContract.Columns.USER_FORENAME));
          final String userLastname = cursor.getString(cursor.getColumnIndex(UsersContract.Columns.USER_LASTNAME));

          mNewsTitleTextView.setText(newsTopic);
          mNewsAuthorTextView.setText(TextTools.getLocalizedAuthorAndDateString(String.format(
                  "%s %s",
                  userForename,
                  userLastname), newsDate, getActivity()
          ));
          mNewsAuthorTextView.setVisibility(View.VISIBLE);
          mNewsShowMoreTextView.setVisibility(View.VISIBLE);
          mNewsTextTextView.setText(Html.fromHtml(newsBody));

        }
        break;
      case COURSE_TEACHERS_LOADER:
        if (!cursor.isAfterLast()) {
          String teacherAvatarUrl = cursor.getString(cursor.getColumnIndex(UsersContract.Columns.USER_AVATAR_NORMAL));
          mTeacherNameTextView.setText(
              cursor.getString(cursor.getColumnIndex(UsersContract.Columns.USER_TITLE_PRE)) + " " +
                  cursor.getString(cursor.getColumnIndex(UsersContract.Columns.USER_FORENAME)) +
                  " " +
                  cursor.getString(cursor.getColumnIndex(UsersContract.Columns.USER_LASTNAME)) +
                  " " +
                  cursor.getString(cursor.getColumnIndex(UsersContract.Columns.USER_TITLE_POST)));
          int teacherCount = cursor.getCount();
          if (teacherCount > 1) {
            mTeacherCountTextView.setText(String.format(getString(R.string.and_more_teachers,
                        teacherCount - 1)
                )
            );
            mTeacherCountTextView.setVisibility(View.VISIBLE);
          }
          Picasso picasso = Picasso.with(mContext);

          if (BuildConfig.DEBUG) {
            picasso.setDebugging(true);
          }

          picasso.load(teacherAvatarUrl)
              .resizeDimen(R.dimen.user_image_medium, R.dimen.user_image_medium)
              .centerCrop()
              .placeholder(R.drawable.nobody_normal)
              .into(mUserImageView);
        }
        break;
    }

  }

  public void onLoaderReset(Loader<Cursor> loader) {
    // nothing to do
  }

  private interface CourseItemQuery {
    String[] projection = {
        CoursesContract.Qualified.Courses.COURSES_COURSE_TITLE,
        CoursesContract.Qualified.Courses.COURSES_COURSE_DESCIPTION
    };
  }

  private interface CourseEventQuery {
    String[] projection = {
        EventsContract.Columns.EVENT_TITLE,
        EventsContract.Columns.EVENT_START,
        EventsContract.Columns.EVENT_END,
        EventsContract.Columns.EVENT_ROOM
    };
  }

  public interface CourseNewsQuery {

    String[] projection = {
        NewsContract.Qualified.NEWS_NEWS_TOPIC,
        NewsContract.Qualified.NEWS_NEWS_BODY,
        NewsContract.Qualified.NEWS_NEWS_MKDATE,
        UsersContract.Qualified.USERS_USER_FORENAME,
        UsersContract.Qualified.USERS_USER_LASTNAME
    };

  }

  public interface CourseUsersQuery {
    String[] projection = {
        UsersContract.Qualified.USERS_USER_TITLE_PRE,
        UsersContract.Qualified.USERS_USER_FORENAME,
        UsersContract.Qualified.USERS_USER_LASTNAME,
        UsersContract.Qualified.USERS_USER_TITLE_POST,
        UsersContract.Qualified.USERS_USER_AVATAR_NORMAL,
        CoursesContract.Qualified.CourseUsers.COURSES_USERS_TABLE_ID
    };

    String selection =
        CoursesContract.Qualified.CourseUsers.COURSES_USERS_TABLE_COURSE_USER_USER_ROLE + "= ?";

    String[] selectionArgs = {
        Integer.toString(CoursesContract.USER_ROLE_TEACHER)
    };
  }

}