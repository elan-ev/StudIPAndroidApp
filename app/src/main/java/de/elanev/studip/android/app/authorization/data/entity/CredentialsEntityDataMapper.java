/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.data.entity;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.authorization.domain.model.OAuthCredentials;

/**
 * @author joern
 */
@Singleton
public class CredentialsEntityDataMapper {
  private final EndpointsEntityDataMapper endpointsMapper;

  @Inject public CredentialsEntityDataMapper(EndpointsEntityDataMapper endpointsMapper) {
    this.endpointsMapper = endpointsMapper;
  }

  public OAuthCredentialsEntity transform(OAuthCredentials credentials) {
    if (credentials == null) return null;

    OAuthCredentialsEntity entity = new OAuthCredentialsEntity();
    entity.setId(credentials.getId());
    entity.setAccessToken(credentials.getAccessToken());
    entity.setAccessTokenSecret(credentials.getAccessTokenSecret());
    entity.setEndpoint(endpointsMapper.transform(credentials.getEndpoint()));

    return entity;
  }

  public OAuthCredentials transform(OAuthCredentialsEntity credentialsEntity) {
    if (credentialsEntity == null) return null;

    OAuthCredentials credentials = new OAuthCredentials();
    credentials.setId(credentialsEntity.getId());
    credentials.setAccessToken(credentialsEntity.getAccessToken());
    credentials.setAccessTokenSecret(credentialsEntity.getAccessTokenSecret());
    credentials.setEndpoint(endpointsMapper.transform(credentialsEntity.getEndpoint()));

    return credentials;
  }
}
