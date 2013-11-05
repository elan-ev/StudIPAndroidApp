/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.net.oauth;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import de.elanev.studip.android.app.backend.datamodel.Server;

public class OAuthConnector {

	public static final String REQUEST_TOKEN_ENDPOINT_URL = "de.elanev.studip.android.app.backend.net.oauth.REQUEST_TOKEN_ENDPOINT_URL";
	public static final String ACCESS_TOKEN_ENDPOINT_URL = "de.elanev.studip.android.app.backend.net.oauth.ACCESS_TOKEN_ENDPOINT_URL";
	public static final String AUTHORIZE_WEBSITE_URL = "de.elanev.studip.android.app.backend.net.oauth.AUTHORIZE_WEBSITE_URL";
	public static final String REQUEST_TOKEN_REQUEST = "de.elanev.studip.android.app.backend.net.oauth.REQUEST_TOKEN";
	public static final String AUTH_URL = "de.elanev.studip.android.app.backend.net.oauth.AUTH_URL";
	public static final String ACCESS_TOKEN_REQUEST = "de.elanev.studip.android.app.backend.net.oauth.ACCESS_TOKEN_REQUEST";
	public static final String RESTIP_CONSUMER_KEY = "de.elanev.studip.android.app.backend.net.oauth.RESTIP_CONSUMER_KEY";
	public static final String RESTIP_CONSUMER_SECRET = "de.elanev.studip.android.app.backend.net.oauth.RESTIP_CONSUMER_SECRET";

	private static OAuthConnector sInstance = null;
	private static Server sServer = null;
	private static OAuthProvider sProvider = null;
	private static OAuthConsumer sConsumer = null;
	private static String sAuthUrl = null;

	/*
	 * Empty
	 */
	private OAuthConnector() {
	}

	public static OAuthConnector getInstance() {
		if (sInstance == null) {
			sInstance = new OAuthConnector();
		}
		return sInstance;
	}

	public static void init(Server s) {
		setServer(s);
		setConsumer(new CommonsHttpOAuthConsumer(s.getConsumerKey(),
				s.getConsumerSecret()));
		setProvider(new CommonsHttpOAuthProvider(s.getRequestUrl(), s.getAccessUrl(),
				s.getAuthorizationUrl()));
	}

	public static void setAccessToken(String accessToken, String accessSecret) {
		getConsumer().setTokenWithSecret(accessToken, accessSecret);
	}

	/**
	 * @return the sServer
	 */
	public static Server getServer() {
		return sServer;
	}

	/**
	 * @param sServer
	 *            the sServer to set
	 */
	public static void setServer(Server sServer) {
		OAuthConnector.sServer = sServer;
	}

	/**
	 * @return the sProvider
	 */
	public static OAuthProvider getProvider() {
		return sProvider;
	}

	/**
	 * @param sProvider
	 *            the sProvider to set
	 */
	public static void setProvider(OAuthProvider sProvider) {
		OAuthConnector.sProvider = sProvider;
	}

	/**
	 * @return the sConsumer
	 */
	public static OAuthConsumer getConsumer() {
		return sConsumer;
	}

	/**
	 * @param sConsumer
	 *            the sConsumer to set
	 */
	public static void setConsumer(OAuthConsumer sConsumer) {
		OAuthConnector.sConsumer = sConsumer;
	}

	/**
	 * @return the sAuthUrl
	 */
	public static String getAuthUrl() {
		return sAuthUrl;
	}

	/**
	 * @param sAuthUrl
	 *            the sAuthUrl to set
	 */
	public static void setAuthUrl(String sAuthUrl) {
		OAuthConnector.sAuthUrl = sAuthUrl;
	}

}
