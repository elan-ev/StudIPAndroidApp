/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.data.repository.datastore;

import android.annotation.SuppressLint;
import android.support.annotation.WorkerThread;

import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.authorization.data.entity.OAuthCredentialsEntity;
import de.elanev.studip.android.app.util.TextTools;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * @author joern
 */
@SuppressLint("NewApi")
@Singleton
public class RealmAuthorizationDataStore implements AuthorizationDataStore {
  private final RealmConfiguration realmConf;

  @Inject public RealmAuthorizationDataStore(RealmConfiguration realmConf) {
    this.realmConf = realmConf;
  }

  @WorkerThread @Override public void saveCredentials(OAuthCredentialsEntity credentialsEntity) {
    try (Realm realm = Realm.getInstance(realmConf)) {
      // Make sure every stored credential has a unique id
      if (TextTools.isEmpty(credentialsEntity.getId())) {
        credentialsEntity.setId(UUID.randomUUID()
            .toString());
      }

      realm.executeTransaction(tsRealm -> tsRealm.copyToRealmOrUpdate(credentialsEntity));
    }
  }

  @WorkerThread @Override public OAuthCredentialsEntity getCredentials() {
    try (Realm realm = Realm.getInstance(realmConf)) {
      OAuthCredentialsEntity entity = realm.where(OAuthCredentialsEntity.class)
          .findFirst();

      if (entity != null) {
        return realm.copyFromRealm(entity);
      } else {
        return null;
      }
    }
  }

  @WorkerThread @Override public void clearCredentials() {
    try (Realm realm = Realm.getInstance(realmConf)) {
      realm.executeTransaction(tsReam -> tsReam.delete(OAuthCredentialsEntity.class));
    }
  }
}
