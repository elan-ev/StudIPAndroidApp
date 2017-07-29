/*
 * Copyright (c) 2017 ELAN e.V.
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
import de.elanev.studip.android.app.base.internal.di.PerFragment;
import de.elanev.studip.android.app.courses.domain.CourseUsers;
import de.elanev.studip.android.app.courses.presentation.mapper.CourseModelDataMapper;
import de.elanev.studip.android.app.courses.presentation.model.CourseUserModel;
import de.elanev.studip.android.app.courses.presentation.model.CourseUsersModel;
import de.elanev.studip.android.app.courses.presentation.view.CourseAttendeesView;
import rx.Subscriber;
import rx.Subscription;
import timber.log.Timber;

/**
 * @author joern
 */
@PerFragment
public class CourseAttendeesPresenter extends
    BaseRxLcePresenter<CourseAttendeesView, CourseUsersModel> {
  private final UseCase<CourseUsers> getCourseUsers;
  private final CourseModelDataMapper mapper;
  private Subscription subscription;

  @Inject public CourseAttendeesPresenter(@Named("getCourseUsers") UseCase getCourseUsers,
      CourseModelDataMapper mapper) {
    this.getCourseUsers = getCourseUsers;
    this.mapper = mapper;
  }

  @Override protected void unsubscribe() {
    this.subscription.unsubscribe();
  }

  public void getCourseUsers(boolean pullToRefresh) {
    this.subscription = getCourseUsers.get(pullToRefresh)
        .map(mapper::transform)
        .subscribe(new Subscriber<CourseUsersModel>() {
          @Override public void onCompleted() {
            CourseAttendeesPresenter.this.onCompleted();
          }

          @Override public void onError(Throwable e) {
            Timber.e(e, e.getLocalizedMessage());
            CourseAttendeesPresenter.this.onError(e, pullToRefresh);
          }

          @Override public void onNext(CourseUsersModel courseUsersModel) {
            CourseAttendeesPresenter.this.onNext(courseUsersModel);
          }
        });
  }

  @SuppressWarnings("ConstantConditions") public void viewUser(CourseUserModel courseUserModel) {
    if (isViewAttached()) {
      getView().viewUser(courseUserModel);
    }
  }
}
