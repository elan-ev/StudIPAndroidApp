/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.presentation.presenter;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import de.elanev.studip.android.app.base.BaseRxLcePresenter;
import de.elanev.studip.android.app.base.DefaultSubscriber;
import de.elanev.studip.android.app.base.UseCase;
import de.elanev.studip.android.app.base.internal.di.PerActivity;
import de.elanev.studip.android.app.courses.domain.DomainCourse;
import de.elanev.studip.android.app.courses.presentation.mapper.CourseModelDataMapper;
import de.elanev.studip.android.app.courses.presentation.model.CourseModel;
import de.elanev.studip.android.app.courses.presentation.view.CourseListView;
import timber.log.Timber;

/**
 * @author joern
 */

@PerActivity
public class CourseListPresenter extends BaseRxLcePresenter<CourseListView, List<CourseModel>> {
  private final UseCase<List<DomainCourse>> getCourseList;
  private final CourseModelDataMapper courseModelDataMapper;

  @Inject CourseListPresenter(@Named("courseList") UseCase getCourseList,
      CourseModelDataMapper courseModelDataMapper) {
    this.getCourseList = getCourseList;
    this.courseModelDataMapper = courseModelDataMapper;
  }

  @Override protected void unsubscribe() {
    getCourseList.unsubscribe();
  }

  public void loadCourses(boolean pullToRefresh) {
    this.getCourseList.execute(new CourseListSubscriber(pullToRefresh));
  }

  @SuppressWarnings("ConstantConditions") public void viewCourse(CourseModel course) {
    if (isViewAttached()) {
      getView().viewCourse(course);
    }
  }

  private class CourseListSubscriber extends DefaultSubscriber<List<DomainCourse>> {
    CourseListSubscriber(boolean pullToRefresh) {
      super(pullToRefresh);
    }

    @Override public void onCompleted() {
      CourseListPresenter.this.onCompleted();
    }

    @Override public void onError(Throwable e) {
      Timber.e(e, e.getLocalizedMessage());
      CourseListPresenter.this.onError(e, isPullToRefresh());
    }

    @Override public void onNext(List<DomainCourse> domainCourses) {
      CourseListPresenter.this.onNext(courseModelDataMapper.transform(domainCourses));
    }
  }
}
