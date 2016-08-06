/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.data.entity;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.news.domain.User;

/**
 * @author joern
 */
@Singleton
public class UserEntityDataMapper {

  @Inject public UserEntityDataMapper() {}

  public User transform(UserEntity userEntity) {
    User user = null;

    if (userEntity != null) {
      user = new User(userEntity.userId);
      user.setFullname(userEntity.getFullName());
      user.setUsername(userEntity.username);
      user.setAvatarUrl(userEntity.avatarNormal);
      user.setEmail(userEntity.email);
      user.setPhone(userEntity.phone);
      user.setHomepageUrl(userEntity.homepage);
      user.setSkypeAddress(userEntity.skype);
      user.setShowSkypeAddress(userEntity.skypeShow);
      user.setPrivateAddress(userEntity.privadr);
    }

    return user;
  }
}
