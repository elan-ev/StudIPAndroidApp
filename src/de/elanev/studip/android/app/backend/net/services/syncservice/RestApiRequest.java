/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
/**
 * 
 */
package de.elanev.studip.android.app.backend.net.services.syncservice;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.os.Bundle;
import android.util.Log;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.net.oauth.OAuthConnector;

/**
 * @author joern
 * 
 */
public class RestApiRequest {

	public final static String TAG = RestApiRequest.class.getSimpleName();

	private OAuthConnector mConnector;
	private DefaultHttpClient mClient;

	public RestApiRequest() {
		// DEBUG
		java.util.logging.Logger.getLogger("org.apache.http.wire").setLevel(
				java.util.logging.Level.FINEST);
		java.util.logging.Logger.getLogger("org.apache.http.headers").setLevel(
				java.util.logging.Level.FINEST);

		System.setProperty("org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.SimpleLog");
		System.setProperty("org.apache.commons.logging.simplelog.showdatetime",
				"true");
		System.setProperty(
				"org.apache.commons.logging.simplelog.log.httpclient.wire",
				"debug");
		System.setProperty(
				"org.apache.commons.logging.simplelog.log.org.apache.http",
				"debug");
		System.setProperty(
				"org.apache.commons.logging.simplelog.log.org.apache.http.headers",
				"debug");

		mClient = new DefaultHttpClient();
		mConnector = OAuthConnector.getInstance();
	}

	public String get(String endpoint, String... id) {
		String resultBody = null;
		try {
			String reqString = String.format(mConnector.server.API_URL + "/"
					+ endpoint + ".json", (Object[]) id);
			Log.i(TAG, reqString);
			HttpGet request = new HttpGet(URI.create(reqString));
			mConnector.consumer.sign(request);
			HttpResponse response = mClient.execute(request);
			int resultCode = response.getStatusLine().getStatusCode();
			Log.d(TAG, String.valueOf(resultCode));
			if (resultCode == 200) {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					resultBody = EntityUtils.toString(entity);
				}
			}

		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return resultBody;

	}

	public ApiResponse post(String endpoint, Bundle postParameters) {
		ApiResponse result = null;
		String postUrl = String.format(mConnector.server.API_URL + "/"
				+ endpoint);

		try {
			HttpPost request = new HttpPost(URI.create(postUrl));
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
			Set<String> bundleContent = postParameters.keySet();
			if (bundleContent != null && !bundleContent.isEmpty()) {
				for (String string : bundleContent) {
					nameValuePairs.add(new BasicNameValuePair(string,
							postParameters.getString(string)));
				}

				request.setEntity(new UrlEncodedFormEntity(nameValuePairs,
						"UTF-8"));

				mConnector.consumer.sign(request);
				HttpResponse response = mClient.execute(request);

				BufferedReader rd = new BufferedReader(new InputStreamReader(
						response.getEntity().getContent()));
				String line = "";
				String resultBody = "";
				while ((line = rd.readLine()) != null) {
					resultBody += line;
				}

				result = new ApiResponse(response.getStatusLine()
						.getStatusCode(), resultBody, postUrl);
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		}
		return result;
	}

	public RestApiRequest.ApiResponse put(String endpoint, String... params) {
		ApiResponse result = null;
		String reqString = String.format(mConnector.server.API_URL + "/"
				+ endpoint, (Object[]) params);
		String resultBody = "";
		Log.v(TAG, reqString);

		try {
			HttpPut request = new HttpPut(URI.create(reqString));
			mConnector.consumer.sign(request);
			HttpResponse response = mClient.execute(request);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				BufferedReader rd = new BufferedReader(new InputStreamReader(
						entity.getContent()));
				String line = "";

				while ((line = rd.readLine()) != null) {
					resultBody += line;
				}
			}
			result = new ApiResponse(response.getStatusLine().getStatusCode(),
					resultBody, reqString);

		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	public RestApiRequest.ApiResponse delete(String endpoint, String... params) {
		ApiResponse result = null;
		String reqString = String.format(mConnector.server.API_URL + "/"
				+ endpoint, (Object[]) params);
		String resultBody = "";
		Log.v(TAG, reqString);

		try {
			HttpDelete request = new HttpDelete(URI.create(reqString));
			mConnector.consumer.sign(request);
			HttpResponse response = mClient.execute(request);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				BufferedReader rd = new BufferedReader(new InputStreamReader(
						entity.getContent()));
				String line = "";

				while ((line = rd.readLine()) != null) {
					resultBody += line;
				}
			}
			result = new ApiResponse(response.getStatusLine().getStatusCode(),
					resultBody, reqString);

		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	public class ApiResponse {
		// API StatusCodes
		public final static int SUCCESS_WITH_RESPONSE = 201;
		public final static int SUCCESS_WITH_NO_RESPONSE = 204;
		public final static int NO_PERMISSION = 403;
		public final static int NOT_FOUND = 404;
		public final static int WRONG_PARAMETER = 406;
		public final static int INTERNAL_ERROR = 500;

		private String body, request;
		private int code;

		private ApiResponse(int code, String body, String reqeuest) {
			this.body = body;
			this.code = code;
			this.request = reqeuest;
		}

		/**
		 * @return the body
		 */
		public String getBody() {
			return body;
		}

		/**
		 * @param body
		 *            the body to set
		 */
		public void setBody(String body) {
			this.body = body;
		}

		/**
		 * @return the code
		 */
		public int getCode() {
			return code;
		}

		/**
		 * @return the request
		 */
		public String getRequest() {
			return request;
		}

		/**
		 * @param request
		 *            the request to set
		 */
		public void setRequest(String request) {
			this.request = request;
		}

		/**
		 * @param code
		 *            the code to set
		 */
		public void setCode(int code) {
			this.code = code;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return String.format(Locale.getDefault(),
					"Code [%d],  Body [%s], Request[%s]", code, body, request);
		}

	}
}
