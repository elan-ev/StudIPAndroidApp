/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.forums;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.courses.data.entity.ForumEntry;
import de.elanev.studip.android.app.data.datamodel.User;
import de.elanev.studip.android.app.widget.ReactiveListFragment;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import timber.log.Timber;

/**
 * @author joern
 */
public class ForumEntryFragment extends ReactiveListFragment {
  private static final String TAG = ForumEntryFragment.class.getSimpleName();
  private static final int CREATE_ENTRY = 3001;
  private static ForumEntry rootEntry;
  private final int visibleThreshold = 5;
  private String mEntryId;
  private String mContent;
  private String mSubject;
  private long mDate;
  private ForumEntryAdapter mAdapter;
  private int previousTotal = 0;
  private boolean loading = true;
  private int firstVisibleItem, visibleItemCount, totalItemCount;
  private int mOffset = 0;
  private String mFullName;
  private String mAvatar;
  private boolean mRefreshList = false;

  /**
   * Returns a new instance of ForumsListFragment and sets its arguments with the passed
   * bundle.
   *
   * @param arguments arguments to set to fragment
   * @return new instance of ForumsListFragment
   */
  public static ForumEntryFragment newInstance(Bundle arguments) {
    ForumEntryFragment fragment = new ForumEntryFragment();
    fragment.setArguments(arguments);

    return fragment;
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == CREATE_ENTRY && resultCode == Activity.RESULT_OK) {
      mRefreshList = true;
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
              ForumEntryComposeFragment.EntryType.REPLY_ENTRY);

          Intent intent = new Intent(getActivity(), ForumEntryComposeActivity.class);
          intent.putExtras(args);
          startActivityForResult(intent, CREATE_ENTRY);

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
    mEntryId = args.getString(ForumEntry.ID);
    mContent = args.getString(ForumEntry.CONTENT);
    mSubject = args.getString(ForumEntry.SUBJECT);
    mDate = args.getLong(ForumEntry.DATE);
    mFullName = args.getString(User.NAME);
    mAvatar = args.getString(User.AVATAR);


    mAdapter = new ForumEntryAdapter(new ArrayList<ForumEntry>(), new ListItemClicks() {

      @Override public void onListItemClicked(View v, int position) {}

    }, getActivity());
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    setTitle(mSubject);
    mEmptyView.setText(R.string.no_entries);
    mRecyclerView.setAdapter(mAdapter);
    mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override public void onRefresh() {
        mRefreshList = true;
        updateItems();
      }
    });
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
    if (mRefreshList) {
      mRefreshList = false;
      mOffset = 0;
    }

    if (mOffset <= 0) {
      rootEntry = new ForumEntry();
      rootEntry.subject = mSubject;
      rootEntry.content = mContent;
      rootEntry.mkdate = mDate;
      rootEntry.user = new User();
      rootEntry.user.lastname = mFullName;
      rootEntry.user.avatarNormal = mAvatar;
      mAdapter.clear();
      mAdapter.add(rootEntry);
    }
    // Return immediately when no course id is set
    if (TextUtils.isEmpty(mEntryId)) {
      return;
    }

    final ArrayList<ForumEntry> entries = new ArrayList<>();
    mCompositeSubscription.add(bind(mApiService.getForumTopicEntries(mEntryId, mOffset)).subscribe(
        new Subscriber<ForumEntry>() {
          @Override public void onCompleted() {

            mAdapter.addAll(entries);
            setRefreshing(false);
          }

          @Override public void onError(Throwable e) {
            if(e != null) {
              if (e instanceof TimeoutException) {
                Toast.makeText(getActivity(), "Request timed out", Toast.LENGTH_SHORT)
                    .show();
              } else if (e instanceof HttpException) {
                Toast.makeText(getActivity(), "HTTP exception", Toast.LENGTH_LONG)
                    .show();
                Timber.e(e, e.getLocalizedMessage());
              } else {
                Timber.e(e, e.getLocalizedMessage());
              }
            }
            setRefreshing(false);
          }

          @Override public void onNext(ForumEntry entry) {
            entries.add(entry);
          }
        }));
  }

}
