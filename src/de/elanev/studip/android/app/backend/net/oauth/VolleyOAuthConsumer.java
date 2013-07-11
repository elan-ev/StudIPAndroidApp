/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.net.oauth;

import oauth.signpost.AbstractOAuthConsumer;
import oauth.signpost.http.HttpRequest;

/**
 * @author joern
 * 
 */
public class VolleyOAuthConsumer extends AbstractOAuthConsumer {

	/**
	 * @param consumerKey
	 * @param consumerSecret
	 */
	public VolleyOAuthConsumer(String consumerKey, String consumerSecret) {
		super(consumerKey, consumerSecret);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see oauth.signpost.AbstractOAuthConsumer#wrap(java.lang.Object)
	 */
	@Override
	protected HttpRequest wrap(Object request) {
		// check if this is an instance of a volley request
		if (!(request instanceof com.android.volley.Request<?>)) {
			throw new IllegalArgumentException(
					"This consumer expects requests of type "
							+ de.elanev.studip.android.app.backend.net.oauth.VolleyHttpRequestAdapter.class
									.getCanonicalName());
		}

		return new VolleyHttpRequestAdapter(
				(VolleySignPostRequestWrapper<?>) request);
	}
}
