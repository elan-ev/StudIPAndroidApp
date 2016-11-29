/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.presentation.view;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.data.db.CoursesContract;
import de.elanev.studip.android.app.data.db.UnizensusContract;
import de.elanev.studip.android.app.util.ApiUtils;


/**
 * @author joern
 */
public class CourseUnizensusFragment extends Fragment implements
    LoaderManager.LoaderCallbacks<Cursor> {
  public static final String TAG = CourseUnizensusFragment.class.getSimpleName();

  private String mCourseId;
  private WebView mWebView;

  public CourseUnizensusFragment() {}

  public static CourseUnizensusFragment newInstance(Bundle arguments) {
    CourseUnizensusFragment fragment = new CourseUnizensusFragment();

    fragment.setArguments(arguments);

    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mCourseId = getArguments().getString(CoursesContract.Columns.Courses.COURSE_ID);
  }

  @Override public View onCreateView(LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.webview_view, container, false);
    mWebView = (WebView) v.findViewById(R.id.webView);

    return v;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    // initialize CursorLoader
    getLoaderManager().initLoader(0, null, this);
  }

  @Override public Loader<Cursor> onCreateLoader(int id, Bundle data) {
    String[] projection = new String[]{
        UnizensusContract.Columns.Unizensus.ZENSUS_URL
    };
    String[] selectionArgs = new String[]{
        mCourseId
    };

    return new CursorLoader(getActivity(),
        UnizensusContract.CONTENT_URI,
        projection,
        UnizensusContract.Columns.Unizensus.ZENSUS_COURSE_ID + " = ?",
        selectionArgs,
        null);
  }

  @Override public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
    if (getActivity() != null) {
      if (cursor.getCount() > 0) {
        cursor.moveToFirst();

        String zensusUrl = cursor.getString(cursor.getColumnIndex(UnizensusContract.Columns.Unizensus.ZENSUS_URL));

        // Workaround for embedded WebView Bug in Android 2.3,
        // https://code.google.com/p/android/issues/detail?id=7189
        if (!ApiUtils.isOverApi11()) {
          mWebView.requestFocus(View.FOCUS_DOWN);
          mWebView.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
              switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_UP:
                  if (!v.hasFocus()) {
                    v.requestFocus();
                  }
                  break;
              }
              return false;
            }
          });
        }

        mWebView.loadUrl(zensusUrl);
      }
    }


  }

  @Override public void onLoaderReset(Loader<android.database.Cursor> cursorLoader) {
  }
}
