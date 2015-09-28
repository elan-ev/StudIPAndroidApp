/*
 * Copyright (c) 2015 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.widget;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.datamodel.Server;
import de.elanev.studip.android.app.backend.net.services.StudIpLegacyApiService;
import de.elanev.studip.android.app.util.Prefs;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by joern on 03.02.15.
 */
public abstract class ReactiveListFragment extends ReactiveFragment {
  private static final String TAG = ReactiveListFragment.class.getSimpleName();
  protected final CompositeSubscription mCompositeSubscription = new CompositeSubscription();
  protected RecyclerView mRecyclerView;
  protected RecyclerView.ItemDecoration mDividerItemDecoration;
  protected TextView mEmptyView;
  protected SwipeRefreshLayout mSwipeRefreshLayout;
  protected RecyclerView.AdapterDataObserver mObserver;
  protected StudIpLegacyApiService mApiService;
  protected boolean mRecreated = false;
  private String mTitle;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setHasOptionsMenu(true);
    Server server = Prefs.getInstance(getActivity()).getServer();
    mApiService = new StudIpLegacyApiService(server, getActivity());
  }

  @Override public void onResume() {
    super.onResume();

    if (!mRecreated) {
      mRecreated = true;
    }
  }

  @Override public View onCreateView(LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.recyclerview_list, container, false);
    mRecyclerView = (RecyclerView) v.findViewById(R.id.list);
        mEmptyView = (TextView) v.findViewById(R.id.empty);
    mSwipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_layout);

    return v;
  }

  @Override public void onActivityCreated(@Nullable Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    mEmptyView.setText(R.string.loading);
    setEmptyViewVisible(true);

    // Set RecyclerView up
    mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    mRecyclerView.setHasFixedSize(true);
    mRecyclerView.setLongClickable(true);
    mDividerItemDecoration = new SimpleDividerItemDecoration(getActivity().getApplicationContext());
    mRecyclerView.addItemDecoration(mDividerItemDecoration);

    // Set SwipeRefreshLayout up
    mSwipeRefreshLayout.setColorSchemeResources(R.color.studip_mobile_dark,
        R.color.studip_mobile_darker,
        R.color.studip_mobile_dark,
        R.color.studip_mobile_darker);
    mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override public void onRefresh() {
        updateItems();
      }
    });
    setRefreshing(true);
  }

  public void setEmptyViewVisible(boolean toggle) {
    if (toggle) {
      mEmptyView.setVisibility(View.VISIBLE);
      mSwipeRefreshLayout.setVisibility(View.GONE);
    } else {
      mEmptyView.setVisibility(View.GONE);
      mSwipeRefreshLayout.setVisibility(View.VISIBLE);
    }
  }

  public void removeDividerItemDecoratior() {
    mRecyclerView.removeItemDecoration(mDividerItemDecoration);
  }

  protected abstract void updateItems();

  public void setRefreshing(final boolean toggle) {
    if (getActivity() == null) {
      return;
    }
    // Workaround for: https://code.google.com/p/android/issues/detail?id=77712
    TypedValue typed_value = new TypedValue();
    getActivity().getTheme()
        .resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typed_value, true);
    mSwipeRefreshLayout.setProgressViewOffset(false,
        0,
        getResources().getDimensionPixelSize(typed_value.resourceId));

    mSwipeRefreshLayout.setRefreshing(toggle);
  }

  public void setTitle(String title) {
    getActivity().setTitle(title);
  }


  public static interface ListItemClicks {
    public void onListItemClicked(View v, int position);
  }

}
