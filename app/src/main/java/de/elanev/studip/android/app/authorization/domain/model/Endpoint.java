/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.domain.model;

import de.elanev.studip.android.app.R;

/**
 * @author joern
 */
public class Endpoint {
  private String id;
  private String name;
  private String consumerKey;
  private String consumerSecret;
  private String baseUrl;
  private String contactEmail;
  private int iconRes = R.drawable.ic_institute_blue;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public String getRequestUrl() {
    return baseUrl + "/oauth/request_token";
  }

  public String getAuthorizationUrl() {
    return baseUrl + "/oauth/authorize";
  }

  public String getAccessUrl() {
    return baseUrl + "/oauth/access_token";
  }

  public String getApiUrl() {
    return baseUrl + "/api";
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getConsumerKey() {
    return consumerKey;
  }

  public void setConsumerKey(String consumerKey) {
    this.consumerKey = consumerKey;
  }

  public String getConsumerSecret() {
    return consumerSecret;
  }

  public void setConsumerSecret(String consumerSecret) {
    this.consumerSecret = consumerSecret;
  }

  public String getContactEmail() {
    return contactEmail;
  }

  public void setContactEmail(String contactEmail) {
    this.contactEmail = contactEmail;
  }

  public int getIconRes() {
    return iconRes;
  }

  public void setIconRes(int iconRes) {
    this.iconRes = iconRes;
  }
}
