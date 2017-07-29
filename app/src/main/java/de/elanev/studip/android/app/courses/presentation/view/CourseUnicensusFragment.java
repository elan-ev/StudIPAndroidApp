/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.presentation.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.RetainingLceViewState;

import javax.inject.Inject;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.base.presentation.view.BaseLceFragment;
import de.elanev.studip.android.app.courses.internal.di.CoursesComponent;
import de.elanev.studip.android.app.courses.presentation.presenter.CourseUnicensusPresenter;


/**
 * @author joern
 */
public class CourseUnicensusFragment extends
    BaseLceFragment<WebView, String, CourseUnicensusView, CourseUnicensusPresenter> implements
    CourseUnicensusView {

  @Inject CourseUnicensusPresenter presenter;
  private String censusUrl;

  public CourseUnicensusFragment() {setRetainInstance(true);}

  public static CourseUnicensusFragment newInstance(Bundle args) {
    CourseUnicensusFragment fragment = new CourseUnicensusFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @NonNull @Override public CourseUnicensusPresenter createPresenter() {
    return this.presenter;
  }

  @Override protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
    return null;
  }

  @NonNull @Override public LceViewState<String, CourseUnicensusView> createViewState() {
    return new RetainingLceViewState<>();
  }

  @Override public String getData() {
    return this.censusUrl;
  }

  @Override public void setData(String data) {
    this.censusUrl = data;
    this.contentView.loadUrl(this.censusUrl);
  }

  @Override public void loadData(boolean pullToRefresh) {
    this.presenter.getCensusUrl(pullToRefresh);
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    CoursesComponent component = getComponent(CoursesComponent.class);

    if (component != null) {
      component.inject(this);
    } else {
      componentNotFound();
    }

  }

  @Override public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {

    return inflater.inflate(R.layout.fragment_course_census, container, false);
  }
}
