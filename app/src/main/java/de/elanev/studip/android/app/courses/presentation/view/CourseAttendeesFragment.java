/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.presentation.view;

import android.app.Activity;
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

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.base.presentation.view.BaseLceFragment;
import de.elanev.studip.android.app.courses.internal.di.CoursesComponent;
import de.elanev.studip.android.app.courses.presentation.model.CourseUserModel;
import de.elanev.studip.android.app.courses.presentation.model.CourseUsersModel;
import de.elanev.studip.android.app.courses.presentation.presenter.CourseAttendeesPresenter;
import de.elanev.studip.android.app.widget.EmptyRecyclerView;
import de.elanev.studip.android.app.widget.SimpleDividerItemDecoration;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

/**
 * @author joern
 */
public class CourseAttendeesFragment extends
    BaseLceFragment<SwipeRefreshLayout, CourseUsersModel, CourseAttendeesView, CourseAttendeesPresenter> implements
    CourseAttendeesView, SwipeRefreshLayout.OnRefreshListener {
  @Inject CourseAttendeesPresenter presenter;
  @BindView(R.id.list) EmptyRecyclerView emptyRecyclerView;
  @BindView(R.id.emptyView) TextView emptyView;
  private SectionedRecyclerViewAdapter adapter;
  private CourseUsersModel data;
  private CourseUsersListListener courseUsersListListener;
  private CourseUsersAdapter.CourseUserClickListener onUserClickListener = courseUserModel -> {
    if (CourseAttendeesFragment.this.presenter != null && courseUserModel != null) {
      CourseAttendeesFragment.this.presenter.viewUser(courseUserModel);
    }
  };

  public CourseAttendeesFragment() {setRetainInstance(true);}

  public static CourseAttendeesFragment newInstance(Bundle args) {
     CourseAttendeesFragment fragment = new CourseAttendeesFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @NonNull @Override public CourseAttendeesPresenter createPresenter() {
    return this.presenter;
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);

    if (activity instanceof CourseUsersListListener) {
      this.courseUsersListListener = (CourseUsersListListener) activity;
    }
  }

  @NonNull @Override public LceViewState<CourseUsersModel, CourseAttendeesView> createViewState() {
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

  @Override public CourseUsersModel getData() {
    return this.data;
  }

  @Override public void setData(CourseUsersModel data) {
    this.data = data;

    if (this.adapter == null) {
      this.adapter = new SectionedRecyclerViewAdapter();
    }
    this.emptyRecyclerView.setAdapter(adapter);


    this.adapter.removeAllSections();
    if (data.getTeachers() != null && data.getTeachers()
        .size() > 0) {
      CourseUsersAdapter section = new CourseUsersAdapter(getString(R.string.Teacher),
          getContext());
      section.setData(data.getTeachers());
      section.setOnClickListener(onUserClickListener);
      adapter.addSection(section);
    }

    if (data.getTutors() != null && data.getTutors()
        .size() > 0) {
      CourseUsersAdapter section = new CourseUsersAdapter(getString(R.string.Tutor), getContext());
      section.setData(data.getTutors());
      section.setOnClickListener(onUserClickListener);

      adapter.addSection(section);
    }

    if (data.getStudents() != null && data.getStudents()
        .size() > 0) {
      CourseUsersAdapter section = new CourseUsersAdapter(getString(R.string.Student),
          getContext());
      section.setData(data.getStudents());
      section.setOnClickListener(onUserClickListener);
      adapter.addSection(section);
    }

    this.adapter.notifyDataSetChanged();
  }

  @Override public void loadData(boolean pullToRefresh) {
    this.presenter.getCourseUsers(pullToRefresh);
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.getComponent(CoursesComponent.class)
        .inject(this);
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater,
      @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_course_attendees, container, false);
    ButterKnife.bind(this, v);

    return v;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    this.contentView.setOnRefreshListener(this);
    setupRecyclerView();
  }

  @Override protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
    return e.getLocalizedMessage();
  }

  private void setupRecyclerView() {
    this.emptyView.setText(R.string.no_attendees);
    this.emptyRecyclerView.setEmptyView(emptyView);
    this.emptyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    this.emptyRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
    this.emptyRecyclerView.setHasFixedSize(true);
  }

  @Override public void onRefresh() {
    this.loadData(true);
  }

  @Override public void viewUser(CourseUserModel courseUserModel) {
    if (this.courseUsersListListener != null) {
      this.courseUsersListListener.onCourseUserClicked(courseUserModel);
    }
  }

  public interface CourseUsersListListener {
    void onCourseUserClicked(CourseUserModel courseUserModel);
  }
}
