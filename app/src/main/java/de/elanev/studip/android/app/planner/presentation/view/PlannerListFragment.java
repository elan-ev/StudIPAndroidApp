/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.planner.presentation.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.RetainingLceViewState;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.base.presentation.view.BaseLceFragment;
import de.elanev.studip.android.app.courses.presentation.model.CourseModel;
import de.elanev.studip.android.app.courses.presentation.model.CourseModulesModel;
import de.elanev.studip.android.app.planner.internal.di.PlannerComponent;
import de.elanev.studip.android.app.planner.presentation.model.PlanerEventModel;
import de.elanev.studip.android.app.planner.presentation.presenter.PlannerListPresenter;
import de.elanev.studip.android.app.planner.presentation.view.adapter.PlanerEventsAdapter;
import de.elanev.studip.android.app.widget.EmptyRecyclerView;
import de.elanev.studip.android.app.widget.SimpleDividerItemDecoration;

/**
 * @author joern
 *         <p/>
 *         Fragment for showing data related to the /events route of the api.
 *         In Stud.IP known as Planner.
 */
public class PlannerListFragment extends
    BaseLceFragment<SwipeRefreshLayout, List<PlanerEventModel>, PlannerListView, PlannerListPresenter> implements
    PlannerListView, SwipeRefreshLayout.OnRefreshListener, PlannerScrollToCurrentListener {

  @Inject PlannerListPresenter presenter;
  private final PlanerEventsAdapter.EventClickListener onClickListener = eventModel -> {
    if (PlannerListFragment.this.presenter != null && eventModel != null) {
      PlannerListFragment.this.presenter.onEventClicked(eventModel);
    }
  };
  private final PlanerEventsAdapter.EventAddClickListener onLongClickListener = eventModel -> {
    if (PlannerListFragment.this.presenter != null && eventModel != null) {
      PlannerListFragment.this.presenter.onEventLongClicked(eventModel);
    }
  };
  @BindView(R.id.emptyView) TextView mEmptyView;
  @BindView(R.id.list) EmptyRecyclerView mRecyclerView;
  private PlanerEventsAdapter mAdapter;
  private List<PlanerEventModel> data;
  private PlannerEventListener plannerEventListener;

  public PlannerListFragment() {
    setRetainInstance(true);
  }

  public static Fragment newInstance(Bundle args) {
    PlannerListFragment fragment = new PlannerListFragment();
    fragment.setArguments(args);

    return fragment;
  }

  @NonNull @Override public PlannerListPresenter createPresenter() {
    return presenter;
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    setHasOptionsMenu(true);
    getActivity().setTitle(R.string.Planner);
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);

    if (activity instanceof PlannerEventListener) {
      this.plannerEventListener = (PlannerEventListener) activity;
    }
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater,
      @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_planner_list, container, false);
    ButterKnife.bind(this, v);

    return v;
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.planner_listview_menu, menu);

    super.onCreateOptionsMenu(menu, inflater);
  }

  @NonNull @Override public LceViewState<List<PlanerEventModel>, PlannerListView> createViewState() {
    return new RetainingLceViewState<>();
  }

  @Override public void showContent() {
    super.showContent();
    contentView.setRefreshing(false);
  }

  @Override public void showError(Throwable e, boolean pullToRefresh) {
    super.showError(e, pullToRefresh);
    contentView.setRefreshing(false);
  }

  @Override public List<PlanerEventModel> getData() {
    return data;
  }

  @Override public void setData(List<PlanerEventModel> eventModels) {
    this.data = eventModels;

    if (this.mAdapter == null) {
      this.mAdapter = new PlanerEventsAdapter(getContext());
      this.mAdapter.setOnItemClickListener(onClickListener);
      this.mAdapter.setOnAddIconClickedListener(onLongClickListener);
    }
    this.mRecyclerView.setAdapter(mAdapter);

    mAdapter.setData(this.data);
    mAdapter.notifyDataSetChanged();
  }

  @Override public void loadData(boolean pullToRefresh) {
    this.presenter.loadEvents(pullToRefresh);
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    PlannerComponent component = getComponent(PlannerComponent.class);
    component.inject(this);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    this.contentView.setOnRefreshListener(this);
    initRecyclerView();
  }

  @Override protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
    return e.getLocalizedMessage();
  }

  private void initRecyclerView() {
    this.mEmptyView.setText(R.string.no_schedule);
    this.mRecyclerView.setEmptyView(mEmptyView);
    this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    this.mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
    this.mRecyclerView.setHasFixedSize(true);
  }

  @Override public void onRefresh() {
    this.presenter.loadEvents(true);
  }

  @Override public void viewEvent(PlanerEventModel planerEventModel) {
    if (this.plannerEventListener != null) {
      this.plannerEventListener.onPlannerEventSelected(planerEventModel);
    }
  }

  @Override public void addEventToCalendar(PlanerEventModel planerEventModel) {
    if (this.plannerEventListener != null) {
      this.plannerEventListener.onPlannerEventAddToCalendarSelected(planerEventModel);
    }
  }

  @Override public void onScrollToCurrent() {
    // Since the first item is always the most recent, just scroll to top
    mRecyclerView.smoothScrollToPosition(0);
  }
}
