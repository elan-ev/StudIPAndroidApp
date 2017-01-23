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

import de.elanev.studip.android.app.authorization.domain.model.Endpoint;
import de.elanev.studip.android.app.authorization.domain.model.OAuthCredentials;

/**
 * @author joern
 */
@Singleton
public class AuthEntitiesDataMapper {

  @Inject public AuthEntitiesDataMapper() {}

  public OAuthCredentials transform(OAuthCredentialsEntity credentialsEntity) {
    OAuthCredentials oAuthCredentials = new OAuthCredentials();
    oAuthCredentials.setId(credentialsEntity.getId());
    oAuthCredentials.setAccessToken(credentialsEntity.getAccessToken());
    oAuthCredentials.setAccessTokenSecret(credentialsEntity.getAccessTokenSecret());
    oAuthCredentials.setEndpoint(transform(credentialsEntity.getEndpoint()));

    return oAuthCredentials;
  }

  private Endpoint transform(EndpointEntity endpointEntity) {
    Endpoint endpoint = new Endpoint();
    endpoint.setId(endpointEntity.getId());
    endpoint.setBaseUrl(endpointEntity.getBaseUrl());
    endpoint.setConsumerKey(endpointEntity.getConsumerKey());
    endpoint.setConsumerSecret(endpointEntity.getConsumerSecret());
    endpoint.setContactEmail(endpointEntity.getContactEmail());
    endpoint.setIconRes(endpointEntity.getIconRes());
    endpoint.setName(endpointEntity.getName());

    return endpoint;
  }

  public EndpointEntity transform(Endpoint endpoint) {
    EndpointEntity endpointEntity = new EndpointEntity();
    endpointEntity.setId(endpoint.getId());
    endpointEntity.setBaseUrl(endpoint.getBaseUrl());
    endpointEntity.setConsumerKey(endpoint.getConsumerKey());
    endpointEntity.setConsumerSecret(endpoint.getConsumerSecret());
    endpointEntity.setContactEmail(endpoint.getContactEmail());
    endpointEntity.setIconRes(endpoint.getIconRes());
    endpointEntity.setName(endpoint.getName());

    return endpointEntity;
  }
}
