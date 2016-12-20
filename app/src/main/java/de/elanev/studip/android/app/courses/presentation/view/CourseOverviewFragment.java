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
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.RetainingLceViewState;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.base.presentation.view.BaseLceFragment;
import de.elanev.studip.android.app.courses.internal.di.CoursesComponent;
import de.elanev.studip.android.app.courses.presentation.model.CourseModel;
import de.elanev.studip.android.app.courses.presentation.model.CourseOverviewModel;
import de.elanev.studip.android.app.courses.presentation.model.CourseScheduleModel;
import de.elanev.studip.android.app.courses.presentation.presenter.CourseOverviewPresenter;
import de.elanev.studip.android.app.data.datamodel.Settings;
import de.elanev.studip.android.app.news.presentation.model.NewsModel;
import de.elanev.studip.android.app.util.DateTools;
import de.elanev.studip.android.app.util.Prefs;

/**
 * @author joern
 */
public class CourseOverviewFragment extends
    BaseLceFragment<ScrollView, CourseOverviewModel, CourseOverviewView, CourseOverviewPresenter> implements
    CourseOverviewView {

  @BindView(R.id.course_title) TextView courseTitle;
  @BindView(R.id.course_description) TextView courseDescription;
  @BindView(R.id.text1) TextView teacherName;
  @BindView(R.id.text2) TextView teacherCount;
  @BindView(R.id.user_image) ImageView teacherImage;
  @BindView(R.id.news_title) TextView newsTitle;
  @BindView(R.id.news_author) TextView newsAuthor;
  @BindView(R.id.news_text) TextView newsBody;
  @BindView(R.id.show_news_body) TextView showNewsBodyButton;
  @BindView(R.id.course_next_appointment) TextView nextEvent;
  @BindView(R.id.course_type) TextView courseType;
  @BindView(R.id.user_info_container) View userInfo;
  @BindView(R.id.appointment_view) View eventView;
  @BindView(R.id.news_view) View newsView;
  @BindView(R.id.description_view) View descriptionView;

  @Inject CourseOverviewPresenter presenter;
  @Inject Prefs prefs;
  private CourseOverviewModel courseOverviewModel;

  public CourseOverviewFragment() {setRetainInstance(true);}

  public static Fragment newInstance(Bundle args) {
    Fragment fragment = new CourseOverviewFragment();
    fragment.setArguments(args);

    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    this.getComponent(CoursesComponent.class)
        .inject(this);
  }

  @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_course_details, container, false);
    ButterKnife.bind(this, view);

    return view;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    userInfo.setVisibility(View.GONE);
    eventView.setVisibility(View.GONE);
    newsView.setVisibility(View.GONE);
    descriptionView.setVisibility(View.GONE);
  }

  @Override protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
    return e.getLocalizedMessage();
  }

  @NonNull @Override public CourseOverviewPresenter createPresenter() {
    return this.presenter;
  }

  @OnClick(R.id.show_news_body) public void toggleLatestNewsView() {
    if (showNewsBodyButton != null && newsBody != null) {
      int viewVisibility = newsBody.getVisibility();
      switch (viewVisibility) {
        case View.VISIBLE:
          newsBody.setVisibility(View.GONE);
          showNewsBodyButton.setText(R.string.show_more);
          break;
        case View.GONE:
          newsBody.setVisibility(View.VISIBLE);
          showNewsBodyButton.setText(R.string.show_less);
          break;
      }
    }
  }

  @NonNull @Override public LceViewState<CourseOverviewModel, CourseOverviewView> createViewState() {
    return new RetainingLceViewState<>();
  }

  @Override public void showContent() {
    super.showContent();

    userInfo.setVisibility(View.VISIBLE);
    eventView.setVisibility(View.VISIBLE);
    newsView.setVisibility(View.VISIBLE);
    descriptionView.setVisibility(View.VISIBLE);
  }

  @Override public void showLoading(boolean pullToRefresh) {
    super.showLoading(pullToRefresh);

    userInfo.setVisibility(View.GONE);
    eventView.setVisibility(View.GONE);
    newsView.setVisibility(View.GONE);
    descriptionView.setVisibility(View.GONE);
  }

  @Override public CourseOverviewModel getData() {
    return this.courseOverviewModel;
  }

  @Override public void setData(CourseOverviewModel data) {
    this.courseOverviewModel = data;

    fillFieldsWithData();
  }

  private void fillFieldsWithData() {
    if (courseOverviewModel.getCourse() != null) {
      fillCourseFields(courseOverviewModel.getCourse());
    }
    if (courseOverviewModel.getCourseEvent() != null) {
      fillEventsList(courseOverviewModel.getCourseEvent());
    }
    if (courseOverviewModel.getCourseNews() != null) {
      fillNewsList(courseOverviewModel.getCourseNews());
    }
  }

  private void fillCourseFields(CourseModel course) {
    courseTitle.setText(course.getTitle());
    getActivity().setTitle(course.getTitle());

    String courseTypeString = "";
    Settings settings = Settings.fromJson(prefs.getApiSettings());
    if (settings != null && settings.semTypes != null) {
      courseTypeString = settings.semTypes.get(course.getType()).name;
    }
    courseType.setText(courseTypeString);
    if (!TextUtils.isEmpty(course.getDescription())) {
      courseDescription.setText(course.getDescription());
      courseDescription.setMovementMethod(new ScrollingMovementMethod());
    }

    if (course.getTeachers() != null && course.getTeachers()
        .size() > 0) {
      teacherName.setText(course.getTeachers()
          .get(0)
          .getFullName());

      if (course.getTeachers()
          .size() > 1) {
        teacherCount.setText(String.format(getString(R.string.and_more_teachers),
            (course.getTeachers()
                .size() - 1)));

        teacherCount.setVisibility(View.VISIBLE);
      }

      Picasso.with(getContext())
          .load(course.getTeachers()
              .get(0)
              .getAvatarUrl())
          .resizeDimen(R.dimen.user_image_icon_size, R.dimen.user_image_icon_size)
          .centerCrop()
          .placeholder(R.drawable.nobody_normal)
          .into(teacherImage);
    }
  }

  private void fillEventsList(CourseScheduleModel courseEvent) {
    DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(getContext());
    DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getContext());
    String timeString = "(" + timeFormat.format(courseEvent.getStart() * 1000L) + " - "
        + timeFormat.format(courseEvent.getEnd() * 1000L) + ")";
    String dateString = dateFormat.format(courseEvent.getStart() * 1000L);

    nextEvent.setText(String.format("%s %s\n%s\n%s", dateString, timeString, courseEvent.getTitle(),
        courseEvent.getRoom()));
  }

  private void fillNewsList(NewsModel courseNews) {
    newsTitle.setText(courseNews.title);

    if (newsAuthor != null) {
      newsAuthor.setText(DateTools.getLocalizedAuthorAndDateString(
          String.format("%s", courseNews.author.getFullName()), courseNews.date, getContext()));
      newsAuthor.setVisibility(View.VISIBLE);
    }

    showNewsBodyButton.setVisibility(View.VISIBLE);
    newsBody.setText(Html.fromHtml(courseNews.body));
  }

  @Override public void loadData(boolean pullToRefresh) {
    this.presenter.getCourse(pullToRefresh);
  }
}