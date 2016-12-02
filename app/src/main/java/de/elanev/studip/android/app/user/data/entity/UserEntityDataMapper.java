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

import de.elanev.studip.android.app.base.data.db.realm.RealmString;
import de.elanev.studip.android.app.user.domain.User;
import io.realm.RealmList;

/**
 * @author joern
 */
@Singleton
public class UserEntityDataMapper {

  @Inject public UserEntityDataMapper() {}

  public List<User> transform(List<UserEntity> userEntities) {
    if (userEntities == null) return null;

    ArrayList<User> users = new ArrayList<>();

    for (UserEntity userEntity : userEntities) {
      users.add(transform(userEntity));
    }

    return users;
  }

  public User transform(UserEntity userEntity) {
    User user = null;

    if (userEntity != null) {
      user = new User(userEntity.getUserId());
      user.setFullname(userEntity.getFullName());
      user.setUsername(userEntity.getUsername());
      user.setAvatarUrl(userEntity.getAvatarNormal());
      user.setEmail(userEntity.getEmail());
      user.setPhone(userEntity.getPhone());
      user.setHomepageUrl(userEntity.getHomepage());
      user.setSkypeAddress(userEntity.getSkype());
      user.setPrivateAddress(userEntity.getPrivadr());
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
      userEntity.setPrivadr(user.getPrivateAddress());
    }

    return userEntity;
  }

  public RealmList<RealmUserEntity> transformToRealm(List<UserEntity> userEntities) {
    if (userEntities == null) return null;

    RealmList<RealmUserEntity> realmList = new RealmList<>();

    for (UserEntity userEntity : userEntities) {
      RealmUserEntity realmUserEntity = transformToRealm(userEntity);
      if (realmUserEntity != null) {
        realmList.add(realmUserEntity);
      }
    }

    return realmList;
  }

  public RealmUserEntity transformToRealm(UserEntity userEntity) {
    if (userEntity == null) return null;

    RealmUserEntity realmUserEntity = new RealmUserEntity();
    realmUserEntity.setUserId(userEntity.getUserId());
    realmUserEntity.setUsername(userEntity.getUsername());
    realmUserEntity.setTitlePre(userEntity.getTitlePre());
    realmUserEntity.setForename(userEntity.getForename());
    realmUserEntity.setLastname(userEntity.getLastname());
    realmUserEntity.setTitlePost(userEntity.getTitlePost());
    realmUserEntity.setEmail(userEntity.getEmail());
    realmUserEntity.setAvatarNormal(userEntity.getAvatarNormal());
    realmUserEntity.setPhone(userEntity.getPhone());
    realmUserEntity.setHomepage(userEntity.getHomepage());
    realmUserEntity.setPrivadr(userEntity.getPrivadr());
    realmUserEntity.setSkype(userEntity.getSkype());

    return realmUserEntity;
  }

  public List<UserEntity> transformFromRealm(RealmList<RealmUserEntity> realmUserEntities) {
    if (realmUserEntities == null) return null;

    ArrayList<UserEntity> userEntities = new ArrayList<>(realmUserEntities.size());

    for (RealmUserEntity realmUserEntity : realmUserEntities) {
      userEntities.add(transformFromRealm(realmUserEntity));
    }

    return userEntities;
  }

  public UserEntity transformFromRealm(RealmUserEntity realmUserEntity) {
    if (realmUserEntity == null) return null;

    UserEntity userEntity = new UserEntity();
    userEntity.setUserId(realmUserEntity.getUserId());
    userEntity.setUsername(realmUserEntity.getUsername());
    userEntity.setTitlePre(realmUserEntity.getTitlePre());
    userEntity.setForename(realmUserEntity.getForename());
    userEntity.setLastname(realmUserEntity.getLastname());
    userEntity.setTitlePost(realmUserEntity.getTitlePost());
    userEntity.setEmail(realmUserEntity.getEmail());
    userEntity.setAvatarNormal(realmUserEntity.getAvatarNormal());
    userEntity.setPhone(realmUserEntity.getPhone());
    userEntity.setHomepage(realmUserEntity.getHomepage());
    userEntity.setPrivadr(realmUserEntity.getPrivadr());
    userEntity.setSkype(realmUserEntity.getSkype());

    return userEntity;
  }
}
