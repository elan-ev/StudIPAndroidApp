/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.user.data.repository.DataStore;

import android.annotation.SuppressLint;
import android.support.annotation.WorkerThread;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.user.data.entity.RealmUserEntity;
import de.elanev.studip.android.app.user.data.entity.UserEntity;
import de.elanev.studip.android.app.user.data.entity.UserEntityDataMapper;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import rx.Observable;

/**
 * @author joern
 */
@Singleton
@SuppressLint("NewApi")
public class UserRealmDataStore implements UserDataStore {
  private final RealmConfiguration realmConfiguration;
  private final UserEntityDataMapper mapper;

  @Inject public UserRealmDataStore(RealmConfiguration realmConfiguration,
      UserEntityDataMapper mapper) {
    this.realmConfiguration = realmConfiguration;
    this.mapper = mapper;
  }

  @WorkerThread @Override public Observable<UserEntity> userEntity(String id) {
    try (Realm realm = Realm.getInstance(realmConfiguration)) {
      RealmUserEntity realmUserEntity = realm.where(RealmUserEntity.class)
          .equalTo("userId", id)
          .findFirst();

      if (realmUserEntity == null) return Observable.empty();

      return Observable.just(realm.copyFromRealm(realmUserEntity))
          .map(mapper::transformFromRealm);
    }
  }

  @WorkerThread public void save(UserEntity userEntity) {
    try (Realm realm = Realm.getInstance(realmConfiguration)) {
      realm.executeTransaction(realm1 -> {
        realm1.insertOrUpdate(mapper.transformToRealm(userEntity));
      });
    }
  }
}
