/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.frontend.forums;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.datamodel.Course;
import de.elanev.studip.android.app.backend.datamodel.ForumCategory;
import de.elanev.studip.android.app.backend.db.CoursesContract;
import de.elanev.studip.android.app.widget.ReactiveListFragment;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

/**
 * @author joern
 */
public class ForumCategoriesListFragment extends ReactiveListFragment {
  private static final String TAG = ForumCategoriesListFragment.class.getSimpleName();

  private String mCourseId;
  private ForumCategoriesAdapter mAdapter;

  public ForumCategoriesListFragment() {}

  public static ForumCategoriesListFragment newInstance(Bundle arguments) {
    ForumCategoriesListFragment fragment = new ForumCategoriesListFragment();

    fragment.setArguments(arguments);

    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Bundle args = getArguments();
    mCourseId = args.getString(CoursesContract.Columns.Courses.COURSE_ID);

    mAdapter = new ForumCategoriesAdapter(new ArrayList<ForumCategory>(), new ListItemClicks() {
      @Override public void onListItemClicked(View v, int position) {
        ForumCategory item = mAdapter.getItem(position);
        Bundle args = new Bundle();
        args.putString(CoursesContract.Columns.Courses.COURSE_ID, mCourseId);
        args.putString(ForumCategory.TITLE, item.entryName);
        args.putString(ForumCategory.ID, item.categoryId);
        args.putString(Course.ID, mCourseId);

        startActivity(args);
      }
    });
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    mEmptyView.setText(R.string.no_forum_categories);
    mRecyclerView.setAdapter(mAdapter);
    updateItems();
  }

  @Override protected void updateItems() {
    // Return immediately when no course id is set
    if (TextUtils.isEmpty(mCourseId)) {
      return;
    }
    final List<ForumCategory> categories = new ArrayList<>();

    mCompositeSubscription.add(
        bind(mApiService.getForumCategories(mCourseId))
            .subscribe(new Subscriber<ForumCategory>() {
              @Override public void onCompleted() {
                mAdapter.addAll(categories);
                setRefreshing(false);
              }

              @Override public void onError(Throwable e) {
                if (e instanceof TimeoutException) {
                  Toast.makeText(getActivity(), "Request timed out", Toast.LENGTH_SHORT)
                      .show();
                } else if (e instanceof HttpException) {
                  Toast.makeText(getActivity(), "HTTP exception", Toast.LENGTH_LONG)
                      .show();
                  Log.e(TAG, e.getLocalizedMessage());
                } else {
                  e.printStackTrace();
                  throw new RuntimeException("See inner exception");
                }

                setRefreshing(false);
              }

              @Override public void onNext(ForumCategory forumCategory) {
                categories.add(forumCategory);
              }
            }));
  }

  private void startActivity(Bundle args) {
    Intent intent = new Intent();
    intent.setClass(getActivity(), ForumAreasActivity.class);
    intent.putExtras(args);

    startActivity(intent);
  }

}