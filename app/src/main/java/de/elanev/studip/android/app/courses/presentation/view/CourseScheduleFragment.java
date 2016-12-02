/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.presentation.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.RetainingLceViewState;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.base.presentation.view.BaseLceFragment;
import de.elanev.studip.android.app.courses.internal.di.CoursesComponent;
import de.elanev.studip.android.app.courses.presentation.model.CourseScheduleModel;
import de.elanev.studip.android.app.courses.presentation.presenter.CourseSchedulePresenter;
import de.elanev.studip.android.app.widget.EmptyRecyclerView;
import de.elanev.studip.android.app.widget.SimpleDividerItemDecoration;

/**
 * @author joern
 */
public class CourseScheduleFragment extends
    BaseLceFragment<SwipeRefreshLayout, List<CourseScheduleModel>, CourseScheduleView, CourseSchedulePresenter> implements
    CourseScheduleView, SwipeRefreshLayout.OnRefreshListener {
  @Inject CourseSchedulePresenter presenter;
  @BindView(R.id.list) EmptyRecyclerView mRecyclerView;
  @BindView(R.id.emptyView) TextView mEmptyView;
  CourseScheduleListAdapter adapter;
  private List<CourseScheduleModel> courseEvents;

  public CourseScheduleFragment() {setRetainInstance(true);}

  @NonNull @Override public CourseSchedulePresenter createPresenter() {
    return this.presenter;
  }

  @NonNull @Override public LceViewState<List<CourseScheduleModel>, CourseScheduleView> createViewState() {
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

  @Override public List<CourseScheduleModel> getData() {
    return this.courseEvents;
  }

  @Override public void setData(List<CourseScheduleModel> data) {
    if (this.adapter == null) {
      this.adapter = new CourseScheduleListAdapter(getContext());
      this.mRecyclerView.setAdapter(adapter);
    }

    this.courseEvents = data;
    this.adapter.setData(data);
  }

  @Override public void loadData(boolean pullToRefresh) {
    this.presenter.getSchedule(pullToRefresh);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_schedule_list, container, false);
    ButterKnife.bind(this, v);

    return v;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    contentView.setOnRefreshListener(this);
    setupRecyclerView();
  }

  private void setupRecyclerView() {
    mEmptyView.setText(R.string.no_appointments);
    this.mRecyclerView.setEmptyView(mEmptyView);
    this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    this.mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
    this.mRecyclerView.setHasFixedSize(true);
  }

  @Override protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
    return e.getLocalizedMessage();
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.getComponent(CoursesComponent.class)
        .inject(this);
  }

  @Override public void onRefresh() {
    this.loadData(true);
  }
}
