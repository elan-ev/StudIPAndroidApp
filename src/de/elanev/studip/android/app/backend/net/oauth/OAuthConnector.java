/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.net.oauth;

import de.elanev.studip.android.app.backend.net.Server;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;

public class OAuthConnector {

	public static final String REQUEST_TOKEN_ENDPOINT_URL = "de.elanev.studip.android.app.backend.net.oauth.REQUEST_TOKEN_ENDPOINT_URL";
	public static final String ACCESS_TOKEN_ENDPOINT_URL = "de.elanev.studip.android.app.backend.net.oauth.ACCESS_TOKEN_ENDPOINT_URL";
	public static final String AUTHORIZE_WEBSITE_URL = "de.elanev.studip.android.app.backend.net.oauth.AUTHORIZE_WEBSITE_URL";
	public static final String REQUEST_TOKEN_REQUEST = "de.elanev.studip.android.app.backend.net.oauth.REQUEST_TOKEN";
	public static final String AUTH_URL = "de.elanev.studip.android.app.backend.net.oauth.AUTH_URL";
	public static final String ACCESS_TOKEN_REQUEST = "de.elanev.studip.android.app.backend.net.oauth.ACCESS_TOKEN_REQUEST";
	public static final String RESTIP_CONSUMER_KEY = "de.elanev.studip.android.app.backend.net.oauth.RESTIP_CONSUMER_KEY";
	public static final String RESTIP_CONSUMER_SECRET = "de.elanev.studip.android.app.backend.net.oauth.RESTIP_CONSUMER_SECRET";

	public String accessToken;
	public String accessSecret;

	private static OAuthConnector instance = null;
	public Server server = null;
	public OAuthProvider provider = null;
	public OAuthConsumer consumer = null;
	public String authUrl = null;

	private OAuthConnector() {

	}

	public static OAuthConnector getInstance() {
		if (instance == null) {
			instance = new OAuthConnector();
		}
		return instance;
	}

	public void init(Server s) {
		this.server = s;
		this.consumer = new CommonsHttpOAuthConsumer(s.CONSUMER_KEY,
				s.CONSUMER_SECRET);
		this.provider = new CommonsHttpOAuthProvider(s.REQUEST_URL,
				s.ACCESS_URL, s.AUTHORIZATION_URL);
	}

	public void setAccessToken(String accessToken, String accessSecret) {
		consumer.setTokenWithSecret(accessToken, accessSecret);
	}

}
