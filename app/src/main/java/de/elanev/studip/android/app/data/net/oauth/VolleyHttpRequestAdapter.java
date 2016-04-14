/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package de.elanev.studip.android.app.data.net.oauth;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import oauth.signpost.http.HttpRequest;

import com.android.volley.AuthFailureError;
import com.android.volley.Request.Method;

/**
 * @author joern
 * 
 */
public class VolleyHttpRequestAdapter implements HttpRequest {

	private VolleySignPostRequestWrapper<?> request;

	/**
	 * @param request
	 */
	public VolleyHttpRequestAdapter(VolleySignPostRequestWrapper<?> request) {
		this.request = request;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oauth.signpost.http.HttpRequest#getMethod()
	 */
	public String getMethod() {
		switch (request.getMethod()) {
		case Method.GET:
			return "GET";
		case Method.POST:
			return "POST";
		case Method.PUT:
			return "PUT";
		case Method.DELETE:
			return "DELETE";

		default:
			return "GET";
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oauth.signpost.http.HttpRequest#getRequestUrl()
	 */
	public String getRequestUrl() {
		return request.getUrl();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oauth.signpost.http.HttpRequest#setRequestUrl(java.lang.String)
	 */
	public void setRequestUrl(String url) {
		request.setUrl(url);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oauth.signpost.http.HttpRequest#setHeader(java.lang.String,
	 * java.lang.String)
	 */
	public void setHeader(String name, String value) {
		request.setHeader(name, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oauth.signpost.http.HttpRequest#getHeader(java.lang.String)
	 */
	public String getHeader(String name) {
		return request.getHeader(name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oauth.signpost.http.HttpRequest#getAllHeaders()
	 */
	public Map<String, String> getAllHeaders() {
		try {
			return request.getHeaders();
		} catch (AuthFailureError e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oauth.signpost.http.HttpRequest#getMessagePayload()
	 */
	public InputStream getMessagePayload() throws IOException {
		try {
			byte[] body = request.getBody();
			if (body != null)
				return new ByteArrayInputStream(request.getBody());

		} catch (AuthFailureError e) {
			e.printStackTrace();
		}
		return new ByteArrayInputStream(new byte[] {});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oauth.signpost.http.HttpRequest#getContentType()
	 */
	public String getContentType() {
		if (request.getBodyContentType() != null)
			return request.getBodyContentType();

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see oauth.signpost.http.HttpRequest#unwrap()
	 */
	public Object unwrap() {
		return request;
	}

}
