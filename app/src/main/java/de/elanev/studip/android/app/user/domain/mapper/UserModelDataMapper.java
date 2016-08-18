/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.user.domain.mapper;

import javax.inject.Inject;

import de.elanev.studip.android.app.base.internal.di.PerActivity;
import de.elanev.studip.android.app.user.domain.User;
import de.elanev.studip.android.app.user.presentation.model.UserModel;

/**
 * @author joern
 */
@PerActivity
public class UserModelDataMapper {

  @Inject public UserModelDataMapper() {}

  public UserModel transform(User user) {
    UserModel userModel = null;

    if (user != null) {
      userModel = new UserModel();
      userModel.setUserId(user.getUserId());
      userModel.setFullName(user.getFullname());
      userModel.setAvatarUrl(user.getAvatarUrl());
      userModel.setPhone(user.getPhone());
      userModel.setAddress(user.getPrivateAddress());
      userModel.setEmail(user.getEmail());
      userModel.setHomepage(user.getHomepageUrl());
      userModel.setSkype(user.getSkypeAddress());
    }

    return userModel;
  }
}