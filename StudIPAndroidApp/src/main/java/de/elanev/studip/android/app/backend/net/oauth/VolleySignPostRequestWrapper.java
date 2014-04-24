/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.net.oauth;

import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response.ErrorListener;

/**
 * @author joern
 * @param <T>
 * 
 */
public abstract class VolleySignPostRequestWrapper<T> extends Request<T> {

	private String mUrl;
	private Map<String, String> mHeaders = new HashMap<String, String>();
	private Map<String, String> mParams = new HashMap<String, String>();

	/**
	 * @param method
	 * @param url
	 * @param listener
	 */
	public VolleySignPostRequestWrapper(int method, String url,
			ErrorListener listener) {
		super(method, url, listener);
		mUrl = url;
	}

	/**
	 * Sets the URL of this request. Used by {
	 */
	public void setUrl(String url) {
		mUrl = url;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.android.volley.Request#getUrl()
	 */
	@Override
	public String getUrl() {
		return mUrl;
	}

	public String getHeader(String name) {
		return mHeaders.get(name);
	}

	public void setHeader(String name, String value) {
		mHeaders.put(name, value);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.android.volley.Request#getHeaders()
	 */
	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return mHeaders;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.android.volley.Request#getParams()
	 */
	@Override
	protected Map<String, String> getParams() throws AuthFailureError {
		return mParams;
	}

	public void setParams(Map<String, String> params) {
		mParams.putAll(params);
	}

	public void addParam(String key, String value) {
		mParams.put(key, value);
	}
}
