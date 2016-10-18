/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.user.data.entity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.user.domain.User;

/**
 * @author joern
 */
@Singleton
public class UserEntityDataMapper {

  @Inject public UserEntityDataMapper() {}

  public List<User> transform(List<UserEntity> userEntities) {
    ArrayList<User> users = new ArrayList<>();

    for (UserEntity userEntity : userEntities) {
      users.add(transform(userEntity));
    }

    return users;
  }

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
      user.setShowSkypeOnline(userEntity.skypeShow);
      user.setPrivateAddress(userEntity.privadr);
    }

    return user;
  }

  public UserEntity transform(User user) {
    UserEntity userEntity = null;

    if (user != null) {
      userEntity = new UserEntity();
      userEntity.setUserId(user.getUserId());
      userEntity.setLastname(user.getFullname());
      userEntity.setUsername(user.getUsername());
      userEntity.setAvatarNormal(user.getAvatarUrl());
      userEntity.setEmail(user.getEmail());
      userEntity.setPhone(user.getPhone());
      userEntity.setHomepage(user.getHomepageUrl());
      userEntity.setSkype(user.getSkypeAddress());
      userEntity.setSkypeShow(user.showSkypeOnline());
      userEntity.setPrivadr(user.getPrivateAddress());
    }

    return userEntity;
  }
}
