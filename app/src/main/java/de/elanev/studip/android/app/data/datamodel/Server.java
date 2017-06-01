/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package de.elanev.studip.android.app.data.datamodel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

import de.elanev.studip.android.app.R;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Server implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = -8829174284201506171L;
  private String name;
  private String consumerKey;
  private String consumerSecret;
  private String baseUrl;
  private String accessUrl;   // = OAUTH_URL + "/access_token";
  private String requestUrl;  // = OAUTH_URL + "/request_token";
  private String authorizationUrl;    // = OAUTH_URL + "/authorize";
  private String apiUrl;      // = BASE_URL + "/api";
  private String contactEmail;
  private int iconRes = R.drawable.ic_institute_blue;

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

  private String accessToken;
  private String accessTokenSecret;

  public Server() {
  }

  public Server(String name,
      String consumerKey,
      String consumerSecret,
      String baseUrl,
      String contactEmail) {
    this.name = name;
    this.consumerKey = consumerKey;
    this.consumerSecret = consumerSecret;
    this.baseUrl = baseUrl;
    this.accessUrl = baseUrl + "/oauth/access_token";
    this.authorizationUrl = baseUrl + "/oauth/authorize";
    this.requestUrl = baseUrl + "/oauth/request_token";
    this.apiUrl = baseUrl + "/api/";
    this.contactEmail = contactEmail;
  }

  public Server(String name,
      String consumerKey,
      String consumerSecret,
      String baseUrl,
      String contactEmail,
      String accessToken,
      String accessTokenSecret) {
    this(name, consumerKey, consumerSecret, baseUrl, contactEmail);
    this.accessToken = accessToken;
    this.accessTokenSecret = accessTokenSecret;
  }

  public String getName() {
    return name;
  }

  @JsonProperty("name")
  public void setName(String name) {
    this.name = name;
  }

  @JsonProperty("consumer_key")
  public String getConsumerKey() {
    return consumerKey;
  }

  @JsonProperty("consumer_key")
  public void setConsumerKey(String consumer_key) {
    this.consumerKey = consumer_key;
  }

  @JsonProperty("consumer_secret")
  public String getConsumerSecret() {
    return consumerSecret;
  }

  @JsonProperty("consumer_secret")
  public void setConsumerSecret(String consumer_secret) {
    this.consumerSecret = consumer_secret;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  @JsonProperty("base_url")
  public void setBaseUrl(String base_url) {
    this.baseUrl = base_url;
    this.accessUrl = base_url + "/oauth/access_token";
    this.authorizationUrl = base_url + "/oauth/authorize";
    this.requestUrl = base_url + "/oauth/request_token";
    this.apiUrl = base_url + "/api/";
  }

  public String getAccessUrl() {
    return accessUrl;
  }

  public void setAccessUrl(String accessUrl) {
    this.accessUrl = accessUrl;
  }

  public String getRequestUrl() {
    return requestUrl;
  }

  public void setRequestUrl(String requestUrl) {
    this.requestUrl = requestUrl;
  }

  public String getAuthorizationUrl() {
    return authorizationUrl;
  }

  public void setAuthorizationUrl(String authorizationUrl) {
    this.authorizationUrl = authorizationUrl;
  }

  public String getApiUrl() {
    return apiUrl;
  }

  public void setApiUrl(String apiUrl) {
    this.apiUrl = apiUrl;
  }

  @JsonProperty("contact_email")
  public String getContactEmail() {
    return contactEmail;
  }

  @JsonProperty("contact_email")
  public void setContactEmail(String contactEmail) {
    this.contactEmail = contactEmail;
  }


  @Override public String toString() {

    return String.format("Name: %s\nUrl: %s\nConsumer Key:%s\nConsumer Secret:%s\nContact: %s",
        name, baseUrl, consumerKey, consumerSecret, contactEmail);
  }
  public int getIconRes() {
    return iconRes;
  }

  public void setIconRes(int iconRes) {
    this.iconRes = iconRes;
  }
}