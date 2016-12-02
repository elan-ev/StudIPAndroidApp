/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.presentation.presenter;

import javax.inject.Inject;
import javax.inject.Named;

import de.elanev.studip.android.app.base.BaseRxLcePresenter;
import de.elanev.studip.android.app.base.UseCase;
import de.elanev.studip.android.app.base.internal.di.PerActivity;
import de.elanev.studip.android.app.courses.domain.CourseOverview;
import de.elanev.studip.android.app.courses.presentation.mapper.CourseModelDataMapper;
import de.elanev.studip.android.app.courses.presentation.model.CourseOverviewModel;
import de.elanev.studip.android.app.courses.presentation.view.CourseOverviewView;
import rx.Subscriber;
import rx.Subscription;
import timber.log.Timber;

/**
 * @author joern
 */
@PerActivity
public class CourseOverviewPresenter extends
    BaseRxLcePresenter<CourseOverviewView, CourseOverviewModel> {
  private final UseCase<CourseOverview> getCourseOverview;
  private final CourseModelDataMapper courseModelDataMapper;
  private Subscription subscription;

  @Inject public CourseOverviewPresenter(@Named("courseOverview") UseCase getCourseOverview,
      CourseModelDataMapper courseModelDataMapper) {
    this.getCourseOverview = getCourseOverview;
    this.courseModelDataMapper = courseModelDataMapper;
  }

  @Override protected void unsubscribe() {
    this.subscription.unsubscribe();
  }

  public void getCourse(boolean pullToRefresh) {
    this.subscription = this.getCourseOverview.get(pullToRefresh)
        .map(courseModelDataMapper::transform)
        .subscribe(new Subscriber<CourseOverviewModel>() {
          @Override public void onCompleted() {
            CourseOverviewPresenter.this.onCompleted();
          }

          @Override public void onError(Throwable e) {
            Timber.e(e, e.getLocalizedMessage());
            CourseOverviewPresenter.this.onError(e, pullToRefresh);
          }

          @Override public void onNext(CourseOverviewModel courseOverviewModel) {
            CourseOverviewPresenter.this.onNext(courseOverviewModel);
          }
        });
  }
}
