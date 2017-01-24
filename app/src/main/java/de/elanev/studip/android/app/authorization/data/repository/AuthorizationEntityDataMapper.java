/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.data.repository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.authorization.data.entity.EndpointEntity;
import de.elanev.studip.android.app.authorization.data.entity.OAuthCredentialsEntity;
import de.elanev.studip.android.app.authorization.data.entity.SettingsEntity;
import de.elanev.studip.android.app.authorization.domain.model.Endpoint;
import de.elanev.studip.android.app.authorization.domain.model.OAuthCredentials;
import de.elanev.studip.android.app.authorization.domain.model.Settings;

/**
 * @author joern
 */
@Singleton
public class AuthorizationEntityDataMapper {

  @Inject public AuthorizationEntityDataMapper() {}

  public OAuthCredentialsEntity transform(OAuthCredentials credentials) {
    if (credentials == null) return null;

    OAuthCredentialsEntity entity = new OAuthCredentialsEntity();
    entity.setId(credentials.getId());
    entity.setAccessToken(credentials.getAccessToken());
    entity.setAccessTokenSecret(credentials.getAccessTokenSecret());
    entity.setEndpoint(transform(credentials.getEndpoint()));

    return entity;
  }

  private EndpointEntity transform(Endpoint endpoint) {
    EndpointEntity entity = new EndpointEntity();
    entity.setId(endpoint.getId());
    entity.setName(endpoint.getName());
    entity.setConsumerKey(endpoint.getConsumerKey());
    entity.setConsumerSecret(endpoint.getConsumerSecret());
    entity.setBaseUrl(endpoint.getBaseUrl());
    entity.setContactEmail(endpoint.getContactEmail());
    entity.setIconRes(endpoint.getIconRes());

    return entity;
  }

  public OAuthCredentials transform(OAuthCredentialsEntity credentialsEntity) {
    if (credentialsEntity == null) return null;

    OAuthCredentials credentials = new OAuthCredentials();
    credentials.setId(credentialsEntity.getId());
    credentials.setAccessToken(credentialsEntity.getAccessToken());
    credentials.setAccessTokenSecret(credentialsEntity.getAccessTokenSecret());
    credentials.setEndpoint(transform(credentialsEntity.getEndpoint()));

    return credentials;
  }

  public Endpoint transform(EndpointEntity endpointEntity) {
    Endpoint endpoint = new Endpoint();
    endpoint.setId(endpointEntity.getId());
    endpoint.setName(endpointEntity.getName());
    endpoint.setConsumerKey(endpointEntity.getConsumerKey());
    endpoint.setConsumerSecret(endpointEntity.getConsumerSecret());
    endpoint.setBaseUrl(endpointEntity.getBaseUrl());
    endpoint.setContactEmail(endpointEntity.getContactEmail());
    endpoint.setIconRes(endpointEntity.getIconRes());

    return endpoint;
  }

  public List<Endpoint> transform(List<EndpointEntity> endpointEntities) {
    List<Endpoint> endpoints = new ArrayList<>(endpointEntities.size());
    for (EndpointEntity endpointEntity : endpointEntities) {
      endpoints.add(transform(endpointEntity));
    }

    return endpoints;
  }

  public Settings transform(SettingsEntity settingsEntity) {
    try {
      throw new Exception("TODO");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
