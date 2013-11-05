/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

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

    public Server() {
    }

    public Server(String name, String consumerKey, String consumerSecret,
                  String baseUrl) {
        this.name = name;
        this.consumerKey = consumerKey;
        this.consumerSecret = consumerSecret;
        this.baseUrl = baseUrl;
        this.accessUrl = this.baseUrl + "/oauth/access_token";
        this.authorizationUrl = this.baseUrl + "/oauth/authorize";
        this.requestUrl = this.baseUrl + "/oauth/request_token";
        this.apiUrl = this.baseUrl + "/api";
    }

    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    public String getConsumerKey() {
        return consumerKey;
    }

    @JsonProperty("consumer_key")
    public void setConsumerKey(String consumer_key) {
        this.consumerKey = consumer_key;
    }

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
    }

    public String getAccessUrl() {
        return accessUrl;
    }

    @JsonProperty("access_url")
    public void setAccessUrl(String accessUrl) {
        this.accessUrl = accessUrl;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    @JsonProperty("request_url")
    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getAuthorizationUrl() {
        return authorizationUrl;
    }

    @JsonProperty("authorization_url")
    public void setAuthorizationUrl(String authorizationUrl) {
        this.authorizationUrl = authorizationUrl;
    }

    public String getApiUrl() {
        return apiUrl;
    }

    @JsonProperty("api_url")
    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public String getID() {
        return baseUrl;
    }
}
