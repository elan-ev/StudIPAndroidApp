/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.user.data.repository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.user.data.entity.UserEntityDataMapper;
import de.elanev.studip.android.app.user.data.repository.DataStore.UserDataStoreFactory;
import de.elanev.studip.android.app.user.domain.User;
import de.elanev.studip.android.app.user.domain.UserRepository;
import rx.Observable;

/**
 * @author joern
 */
@Singleton
public class UserDataRepository implements UserRepository {

  private final UserEntityDataMapper userEntityDataMapper;
  private final UserDataStoreFactory userDataStoreFactory;

  @Inject public UserDataRepository(UserEntityDataMapper userEntityDataMapper,
      UserDataStoreFactory userDataStoreFactory) {
    this.userEntityDataMapper = userEntityDataMapper;
    this.userDataStoreFactory = userDataStoreFactory;
  }

  @Override public Observable<User> user(String userId) {
    return userDataStoreFactory.create()
        .userEntity(userId)
        .map(userEntityDataMapper::transform);
  }

  @Override public Observable<List<User>> getUsers(List<String> userIds) {
    return Observable.from(userIds).flatMap(this::user).toList();
  }
}
