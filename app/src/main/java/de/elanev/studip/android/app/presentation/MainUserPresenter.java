/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.presentation;

import javax.inject.Inject;
import javax.inject.Named;

import de.elanev.studip.android.app.base.BaseRxLcePresenter;
import de.elanev.studip.android.app.base.UseCase;
import de.elanev.studip.android.app.base.internal.di.PerActivity;
import de.elanev.studip.android.app.domain.usecase.GetCurrentUserDetails;
import de.elanev.studip.android.app.presentation.view.MainUserView;
import de.elanev.studip.android.app.user.domain.User;
import de.elanev.studip.android.app.user.presentation.mapper.UserModelDataMapper;
import de.elanev.studip.android.app.user.presentation.model.UserModel;
import rx.Subscriber;

/**
 * @author joern
 */
@PerActivity
public class MainUserPresenter extends BaseRxLcePresenter<MainUserView, UserModel> {
  private final GetCurrentUserDetails getCurrentUserDetails;
  private final UserModelDataMapper dataMapper;

  @Inject public MainUserPresenter(
      @Named("getCurrentUserDetails") UseCase<User> getCurrentUserDetails,
      UserModelDataMapper dataMapper) {
    this.getCurrentUserDetails = (GetCurrentUserDetails) getCurrentUserDetails;
    this.dataMapper = dataMapper;
  }

  @Override protected void unsubscribe() {
    getCurrentUserDetails.unsubscribe();
  }

  public void loadUser(final boolean forceUpdate) {
    this.getCurrentUserDetails.get(forceUpdate)
        .map(dataMapper::transform)
        .subscribe(new Subscriber<UserModel>() {
          @Override public void onCompleted() {
            MainUserPresenter.this.onCompleted();
          }

          @Override public void onError(Throwable e) {
            MainUserPresenter.this.onError(e, forceUpdate);
          }

          @Override public void onNext(UserModel userModel) {
            MainUserPresenter.this.onNext(userModel);
          }
        });
  }
}
