/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.elanev.studip.android.app.R;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author joern
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EndpointEntity extends RealmObject {
  @PrimaryKey private String id;
  private String name;
  private String consumerKey;
  private String consumerSecret;
  private String baseUrl;
  private String contactEmail;
  private int iconRes = R.drawable.ic_institute_blue;

  @JsonIgnore public String getId() {
    return id;
  }

  @JsonIgnore public void setId(String id) {
    this.id = id;
  }

  @JsonProperty("base_url") public String getBaseUrl() {
    return baseUrl;
  }

  @JsonProperty("base_url") public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  @JsonIgnore public String getRequestUrl() {
    return baseUrl + "/oauth/request_token";
  }

  @JsonIgnore public String getAuthorizationUrl() {
    return baseUrl + "/oauth/authorize";
  }

  @JsonIgnore public String getAccessUrl() {
    return baseUrl + "/oauth/access_token";
  }

  @JsonIgnore public String getApiUrl() {
    return baseUrl + "/api";
  }

  @JsonProperty("name") public String getName() {
    return name;
  }

  @JsonProperty("name") public void setName(String name) {
    this.name = name;
  }

  @JsonProperty("consumer_key") public String getConsumerKey() {
    return consumerKey;
  }

  @JsonProperty("consumer_key") public void setConsumerKey(String consumerKey) {
    this.consumerKey = consumerKey;
  }

  @JsonProperty("consumer_secret") public String getConsumerSecret() {
    return consumerSecret;
  }

  @JsonProperty("consumer_secret") public void setConsumerSecret(String consumerSecret) {
    this.consumerSecret = consumerSecret;
  }

  @JsonProperty("contact_email") public String getContactEmail() {
    return contactEmail;
  }

  @JsonProperty("contact_email") public void setContactEmail(String contactEmail) {
    this.contactEmail = contactEmail;
  }

  @JsonIgnore public int getIconRes() {
    return iconRes;
  }

  @JsonIgnore public void setIconRes(int iconRes) {
    this.iconRes = iconRes;
  }
}
