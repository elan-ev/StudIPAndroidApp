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
import android.support.v4.app.Fragment;
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
import de.elanev.studip.android.app.courses.presentation.model.CourseModel;
import de.elanev.studip.android.app.courses.presentation.presenter.CourseListPresenter;
import de.elanev.studip.android.app.data.datamodel.Settings;
import de.elanev.studip.android.app.util.Prefs;
import de.elanev.studip.android.app.widget.EmptyRecyclerView;
import de.elanev.studip.android.app.widget.SimpleDividerItemDecoration;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

/**
 * @author joern
 */
public class CoursesFragment extends
    BaseLceFragment<SwipeRefreshLayout, List<CourseModel>, CourseListView, CourseListPresenter> implements
    CourseListView, SwipeRefreshLayout.OnRefreshListener {

  @BindView(R.id.list) EmptyRecyclerView mRecyclerView;
  @BindView(R.id.emptyView) TextView mEmptyView;
  @Inject CourseListPresenter presenter;
  @Inject Prefs prefs;
  private SectionedRecyclerViewAdapter adapter;
  private CourseSection.CourseClickListener onClickListener = course -> {
    if (CoursesFragment.this.presenter != null && course != null) {
      CoursesFragment.this.presenter.viewCourse(course);
    }
  };
  private CourseListListener courseListListener;
  private Settings settings;
  private List<CourseModel> data;

  public CoursesFragment() {setRetainInstance(true);}

  public static Fragment newInstance(Bundle args) {
    CoursesFragment fragment = new CoursesFragment();
    fragment.setArguments(args);

    return fragment;
  }

  @NonNull @Override public CourseListPresenter createPresenter() {
    return this.presenter;
  }

  @Override public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    getActivity().setTitle(R.string.Courses);
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);

    if (activity instanceof CourseListListener) {
      courseListListener = (CourseListListener) activity;
    }
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater,
      @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_courses_list, container, false);
    ButterKnife.bind(this, v);

    return v;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    this.contentView.setOnRefreshListener(this);
    setupRecyclerView();
    settings = Settings.fromJson(prefs.getApiSettings());
  }

  private void setupRecyclerView() {
    mEmptyView.setText(R.string.no_courses);
    this.mRecyclerView.setEmptyView(mEmptyView);
    this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    this.mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
    this.mRecyclerView.setHasFixedSize(true);
  }

  @Override protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
    return e.getLocalizedMessage();
  }

  @NonNull @Override public LceViewState<List<CourseModel>, CourseListView> createViewState() {
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

  @Override public List<CourseModel> getData() {
    return this.data;
  }

  @Override public void setData(List<CourseModel> data) {
    // Let data survive configuration change
    this.data = data;

    if (this.adapter == null) {
      this.adapter = new SectionedRecyclerViewAdapter();
    }

    this.mRecyclerView.setAdapter(adapter);
    this.adapter.removeAllSections();

    CourseSection unlimitedSection = new CourseSection(
        getString(R.string.course_without_duration_limit), settings);
    unlimitedSection.setOnItemClickListener(onClickListener);

    for (CourseModel courseModel : data) {
      if (courseModel.getDurationTime() == -1L) {
        unlimitedSection.add(courseModel);
        continue;
      }

      CourseSection section = (CourseSection) adapter.getSection(courseModel.getSemester()
          .getSemesterId());

      if (section == null) {
        section = new CourseSection(courseModel.getSemester()
            .getTitle(), settings);
        section.setOnItemClickListener(onClickListener);
        section.add(courseModel);
      } else {
        section.add(courseModel);
      }

      this.adapter.addSection(courseModel.getSemester()
          .getSemesterId(), section);
    }

    this.adapter.addSection(unlimitedSection);
    this.adapter.notifyDataSetChanged();
  }

  @Override public void loadData(boolean pullToRefresh) {
    this.presenter.loadCourses(pullToRefresh);
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Inject this Fragment into the NewsComponent
    this.getComponent(CoursesComponent.class)
        .inject(this);
  }

  @Override public void onRefresh() {
    this.loadData(true);
  }

  @Override public void viewCourse(CourseModel course) {
    if (this.courseListListener != null) {
      this.courseListListener.onCourseClicked(course);
    }
  }

  public interface CourseListListener {
    void onCourseClicked(CourseModel course);
  }
}



