/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.data.repository.datastore;

import android.util.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.authorization.data.entity.EndpointEntity;
import de.elanev.studip.android.app.authorization.data.entity.OAuthCredentialsEntity;
import de.elanev.studip.android.app.util.ServerData;
import rx.Observable;
import timber.log.Timber;

/**
 * @author joern
 */
@Singleton
public class FileAuthorizationDataStore implements AuthorizationDataStore {
  private final ObjectMapper mapper;

  @Inject public FileAuthorizationDataStore(ObjectMapper mapper) {this.mapper = mapper;}

  @Override public void saveCredentials(OAuthCredentialsEntity credentialsEntity) {
    //NoOp
  }

  @Override public OAuthCredentialsEntity getCredentials() {
    //NoOp
    return null;
  }

  @Override public void clearCredentials() {
    //NoOp
  }

  @Override public Observable<EndpointEntity> getEndpoint(String endpointId) {
    //NoOp
    return null;
  }

  @Override public Observable<List<EndpointEntity>> getEndpoints() {
    List<EndpointEntity> endpoints = new ArrayList<>();
    try {
      endpoints = Arrays.asList(mapper.readValue(ServerData.serverJson, EndpointEntity[].class));
    } catch (IOException e) {
      Timber.e(e.getLocalizedMessage(), e);
    }

    return Observable.from(endpoints)
        .map(endpointEntity -> {
          String id = Base64.encodeToString(endpointEntity.getName()
              .getBytes(), Base64.NO_PADDING | Base64.NO_WRAP);
          endpointEntity.setId(id);
          return endpointEntity;
        })
        .toList();
  }
}
