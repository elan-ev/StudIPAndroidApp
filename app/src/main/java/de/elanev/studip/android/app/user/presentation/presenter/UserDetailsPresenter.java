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
import de.elanev.studip.android.app.user.domain.GetUserDetails;
import de.elanev.studip.android.app.user.domain.mapper.UserModelDataMapper;
import de.elanev.studip.android.app.user.presentation.model.UserModel;
import de.elanev.studip.android.app.user.presentation.view.UserDetailsView;

/**
 * @author joern
 */
public class UserDetailsPresenter extends BaseRxLcePresenter<UserDetailsView, UserModel> {
  private final UserModelDataMapper userModelDataMapper;
  private final GetUserDetails userDetailsUseCase;

  @Inject public UserDetailsPresenter(GetUserDetails getUserDetailsUseCase,
      UserModelDataMapper userModelDataMapper) {
    this.userDetailsUseCase = getUserDetailsUseCase;
    this.userModelDataMapper = userModelDataMapper;
  }

  public void loadUser() {
    this.userDetailsUseCase.get()
        .map(userModelDataMapper::transform)
        .subscribe(new DefaultSubscriber(false));
  }
}
