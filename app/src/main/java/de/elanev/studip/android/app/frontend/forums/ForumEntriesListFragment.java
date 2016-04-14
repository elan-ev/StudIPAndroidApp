/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.frontend.forums;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.datamodel.Course;
import de.elanev.studip.android.app.backend.datamodel.ForumArea;
import de.elanev.studip.android.app.backend.datamodel.ForumEntry;
import de.elanev.studip.android.app.backend.datamodel.User;
import de.elanev.studip.android.app.widget.ReactiveListFragment;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

/**
 * @author joern
 */
public class ForumEntriesListFragment extends ReactiveListFragment {
  private static final int CREATE_FORUM_AREA_ENTRY = 1000;
  private static final String TAG = ForumAreasListFragment.class.getSimpleName();
  private final int visibleThreshold = 5;
  private String mEntryTitle;
  private String mEntryId;
  private ForumEntriesAdapter mAdapter;
  private int previousTotal = 0;
  private boolean loading = true;
  private int firstVisibleItem, visibleItemCount, totalItemCount;
  private int mOffset = 0;
  private String mCourseId;

  public ForumEntriesListFragment() {}

  /**
   * Returns a new instance of ForumEntriesListFragment and sets its arguments with the passed
   * bundle.
   *
   * @param arguments arguments to set to fragment
   * @return new instance of ForumEntriesListFragment
   */
  public static ForumEntriesListFragment newInstance(Bundle arguments) {
    ForumEntriesListFragment fragment = new ForumEntriesListFragment();
    fragment.setArguments(arguments);

    return fragment;
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == CREATE_FORUM_AREA_ENTRY && resultCode == Activity.RESULT_OK) {
      updateItems();
    }
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.forum_entries_menu, menu);

    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (isAdded()) {
      switch (item.getItemId()) {
        case android.R.id.home:
          getActivity().onBackPressed();

          return true;
        case R.id.forum_area_add:
          Bundle args = new Bundle();
          args.putString(ForumEntry.ID, mEntryId);
          args.putSerializable(ForumEntryComposeFragment.ENTRY_TYPE,
              ForumEntryComposeFragment.EntryType.NEW_ENTRY);

          Intent intent = new Intent(getActivity(), ForumEntryComposeActivity.class);
          intent.putExtras(args);
          startActivityForResult(intent, CREATE_FORUM_AREA_ENTRY);

          return true;
        default:
          return super.onOptionsItemSelected(item);
      }
    }
    return true;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Bundle args = getArguments();
    if (args != null) {
      mEntryId = args.getString(ForumArea.ID);
      mEntryTitle = args.getString(ForumArea.TITLE);
      mCourseId = args.getString(Course.ID);
    }

    mAdapter = new ForumEntriesAdapter(new ArrayList<ForumEntry>(), new ListItemClicks() {

      @Override public void onListItemClicked(View v, int position) {
        ForumEntry item = mAdapter.getItem(position);
        Bundle args = new Bundle();
        args.putString(ForumEntry.ID, item.topicId);
        args.putString(ForumArea.ID, item.topicId);
        args.putString(ForumEntry.SUBJECT, item.subject);
        args.putString(ForumEntry.CONTENT, item.content);
        args.putLong(ForumEntry.DATE, item.mkdate);
        args.putString(User.NAME, item.user.getFullName());
        args.putString(User.AVATAR, item.user.avatarNormal);

// FIXME: Currently throws more errors than anything else. Marking the whole forum for the course
// is useless anyway. Has to be fixed when the route is actually usable
//        if (item.isNew) {
//          mApiService.setForumRead(mCourseId, new Callback() {
//            @Override public void success(Object o, Response response) {}
//
//            @Override public void failure(RetrofitError error) {
//              if (getActivity() != null) {
//                Toast.makeText(getActivity(), error.getLocalizedMessage(), Toast.LENGTH_LONG)
//                    .show();
//              }
//            }
//          });
//        }

        startActivity(args);
      }

    }, getActivity().getBaseContext());
  }

  private void startActivity(Bundle args) {
    Intent intent = new Intent();
    intent.setClass(getActivity(), ForumEntryActivity.class);
    intent.putExtras(args);

    startActivity(intent);
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    setTitle(mEntryTitle);
    mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override public void onRefresh() {
        mOffset = 0;
        updateItems();
      }
    });

    mEmptyView.setText(R.string.no_entries);
    mRecyclerView.setAdapter(mAdapter);
    mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        visibleItemCount = mRecyclerView.getChildCount();
        totalItemCount = layoutManager.getItemCount();
        firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

        if (loading) {
          if (totalItemCount > previousTotal) {
            loading = false;
            previousTotal = totalItemCount;
          }
        }
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem
            + visibleThreshold)) {

          mOffset = totalItemCount;
          setRefreshing(true);
          updateItems();
          loading = true;
        }
      }
    });
    if (!mRecreated) {
      updateItems();
    }

  }

  @Override protected void updateItems() {
    // Return immediately when no course id is set
    if (TextUtils.isEmpty(mEntryId)) {
      return;
    }

    final ArrayList<ForumEntry> entries = new ArrayList<>();
    mCompositeSubscription.add(bind(mApiService.getForumTopicEntries(mEntryId, mOffset)).subscribe(
        new Subscriber<ForumEntry>() {
          @Override public void onCompleted() {
            if (mOffset <= 0) {
              mAdapter.clear();
            }
            mAdapter.addAll(entries);
            entries.clear();
            mSwipeRefreshLayout.setRefreshing(false);
          }

          @Override public void onError(Throwable e) {
            if (e instanceof TimeoutException) {
              Toast.makeText(getActivity(), "Request timed out", Toast.LENGTH_SHORT).show();
            } else if (e instanceof HttpException) {
              Toast.makeText(getActivity(), "HTTP exception", Toast.LENGTH_LONG).show();
              Log.e(TAG, e.getLocalizedMessage());
            } else {
              e.printStackTrace();
              throw new RuntimeException("See inner exception");
            }

            mSwipeRefreshLayout.setRefreshing(false);
          }

          @Override public void onNext(ForumEntry entry) {
            entries.add(entry);
          }
        }));
  }
}
