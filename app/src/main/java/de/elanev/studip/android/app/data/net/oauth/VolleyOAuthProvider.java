/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package de.elanev.studip.android.app.data.net.oauth;

import oauth.signpost.AbstractOAuthProvider;
import oauth.signpost.http.HttpRequest;
import oauth.signpost.http.HttpResponse;

/**
 * @author joern
 *
 */
public class VolleyOAuthProvider extends AbstractOAuthProvider {

	/**
	 * @param requestTokenEndpointUrl
	 * @param accessTokenEndpointUrl
	 * @param authorizationWebsiteUrl
	 */
	public VolleyOAuthProvider(String requestTokenEndpointUrl,
			String accessTokenEndpointUrl, String authorizationWebsiteUrl) {
		super(requestTokenEndpointUrl, accessTokenEndpointUrl, authorizationWebsiteUrl);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/* (non-Javadoc)
	 * @see oauth.signpost.AbstractOAuthProvider#createRequest(java.lang.String)
	 */
	@Override
	protected HttpRequest createRequest(String endpointUrl) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see oauth.signpost.AbstractOAuthProvider#sendRequest(oauth.signpost.http.HttpRequest)
	 */
	@Override
	protected HttpResponse sendRequest(HttpRequest request) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
