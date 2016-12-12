/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.data.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author joern
 */
public class OAuthCredentialsEntity extends RealmObject {
  @PrimaryKey private String id;
  private String accessToken;
  private String accessTokenSecret;
  private EndpointEntity endpoint;

  public EndpointEntity getEndpoint() {
    return endpoint;
  }

  public void setEndpoint(EndpointEntity endpoint) {
    this.endpoint = endpoint;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getAccessToken() {
    return accessToken;
  }

  public void setAccessToken(String accessToken) {
    this.accessToken = accessToken;
  }

  public String getAccessTokenSecret() {
    return accessTokenSecret;
  }

  public void setAccessTokenSecret(String accessTokenSecret) {
    this.accessTokenSecret = accessTokenSecret;
  }

}
