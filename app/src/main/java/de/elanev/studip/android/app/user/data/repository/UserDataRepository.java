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

import de.elanev.studip.android.app.user.data.entity.UserEntity;
import de.elanev.studip.android.app.user.data.entity.UserEntityDataMapper;
import de.elanev.studip.android.app.user.data.repository.DataStore.UserCloudDataStore;
import de.elanev.studip.android.app.user.data.repository.DataStore.UserRealmDataStore;
import de.elanev.studip.android.app.user.domain.User;
import de.elanev.studip.android.app.user.domain.UserRepository;
import rx.Observable;

/**
 * @author joern
 */
@Singleton
public class UserDataRepository implements UserRepository {

  private final UserEntityDataMapper userEntityDataMapper;
  private final UserCloudDataStore cloudDataStore;
  private final UserRealmDataStore realmDataStore;

  @Inject public UserDataRepository(UserEntityDataMapper userEntityDataMapper,
      UserCloudDataStore cloudDataStore, UserRealmDataStore realmDataStore) {
    this.userEntityDataMapper = userEntityDataMapper;
    this.cloudDataStore = cloudDataStore;
    this.realmDataStore = realmDataStore;
  }

  @Override public Observable<User> user(String userId, boolean forceUpdate) {
    Observable<UserEntity> cloudDataObs = cloudDataStore.userEntity(userId)
        .doOnNext(userEntity -> realmDataStore.save(userEntity));
    Observable<UserEntity> localDataObs = realmDataStore.userEntity(userId);

    return localDataObs.exists(userEntity -> userEntity != null)
        .flatMap(isInDb -> (isInDb && !forceUpdate) ? localDataObs : cloudDataObs)
        .map(userEntityDataMapper::transform);
  }

  @Override public Observable<List<User>> getUsers(List<String> userIds, boolean forceUpdate) {
    return Observable.from(userIds)
        .flatMap(s -> this.user(s, forceUpdate))
        .toList();
  }

  @Override public Observable<User> currentUser(boolean forceUpdate) {
    return cloudDataStore.currentUserEntity()
        .doOnNext(userEntity -> realmDataStore.save(userEntity))
        .map(userEntityDataMapper::transform);
  }
}
