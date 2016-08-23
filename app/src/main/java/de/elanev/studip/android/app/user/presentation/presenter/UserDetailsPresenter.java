/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.user.presentation.presenter;

import javax.inject.Inject;

import de.elanev.studip.android.app.base.BaseRxLcePresenter;
import de.elanev.studip.android.app.base.DefaultSubscriber;
import de.elanev.studip.android.app.base.UseCase;
import de.elanev.studip.android.app.base.internal.di.PerActivity;
import de.elanev.studip.android.app.user.domain.User;
import de.elanev.studip.android.app.user.presentation.mapper.UserModelDataMapper;
import de.elanev.studip.android.app.user.presentation.model.UserModel;
import de.elanev.studip.android.app.user.presentation.view.UserDetailsView;

/**
 * @author joern
 */
@PerActivity
public class UserDetailsPresenter extends BaseRxLcePresenter<UserDetailsView, UserModel> {
  private final UserModelDataMapper userModelDataMapper;
  private final UseCase userDetailsUseCase;

  @Inject UserDetailsPresenter(UseCase getUserDetailsUseCase,
      UserModelDataMapper userModelDataMapper) {
    this.userDetailsUseCase = getUserDetailsUseCase;
    this.userModelDataMapper = userModelDataMapper;
  }

  public void loadUser() {
    this.userDetailsUseCase.execute(new UserDetailsSubscriber(false));
  }

  @Override protected void unsubscribe() {
    userDetailsUseCase.unsubscribe();
  }

  private final class UserDetailsSubscriber extends DefaultSubscriber<User> {

    UserDetailsSubscriber(boolean ptr) {
      super(ptr);
    }

    @Override public void onCompleted() {
      UserDetailsPresenter.this.onCompleted();
    }

    @Override public void onError(Throwable e) {
      UserDetailsPresenter.this.onError(e, ptr);
    }

    @Override public void onNext(User userModel) {
      UserDetailsPresenter.this.onNext(userModelDataMapper.transform(userModel));
    }
  }
}
