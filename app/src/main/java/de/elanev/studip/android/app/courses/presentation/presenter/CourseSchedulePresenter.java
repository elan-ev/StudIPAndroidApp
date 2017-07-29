/*
 * Copyright (c) 2017 ELAN e.V.
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
import de.elanev.studip.android.app.base.UseCase;
import de.elanev.studip.android.app.base.internal.di.PerFragment;
import de.elanev.studip.android.app.courses.presentation.mapper.CourseModelDataMapper;
import de.elanev.studip.android.app.courses.presentation.model.CourseScheduleModel;
import de.elanev.studip.android.app.courses.presentation.view.CourseScheduleView;
import de.elanev.studip.android.app.planner.domain.Event;
import rx.Subscriber;
import rx.Subscription;
import timber.log.Timber;

/**
 * @author joern
 */
@PerFragment
public class CourseSchedulePresenter extends
    BaseRxLcePresenter<CourseScheduleView, List<CourseScheduleModel>> {
  private final UseCase<List<Event>> getCourseSchedule;
  private final CourseModelDataMapper mapper;
  private Subscription subscription;

  @Inject public CourseSchedulePresenter(@Named("getCourseSchedule") UseCase getCourseSchedule,
      CourseModelDataMapper mapper) {
    this.getCourseSchedule = getCourseSchedule;
    this.mapper = mapper;
  }

  @Override protected void unsubscribe() {
    this.subscription.unsubscribe();
  }

  public void getSchedule(boolean pullToRefresh) {
    this.subscription = getCourseSchedule.get(pullToRefresh)
        .map(mapper::transformCourseEvents)
        .subscribe(new Subscriber<List<CourseScheduleModel>>() {
          @Override public void onCompleted() {
            CourseSchedulePresenter.this.onCompleted();
          }

          @Override public void onError(Throwable e) {
            Timber.e(e, e.getLocalizedMessage());
            CourseSchedulePresenter.this.onError(e, pullToRefresh);
          }

          @Override public void onNext(List<CourseScheduleModel> courseScheduleModels) {
            CourseSchedulePresenter.this.onNext(courseScheduleModels);
          }
        });
  }
}
