/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.data;

import android.os.AsyncTask;

import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.authorization.data.entity.EndpointEntity;
import de.elanev.studip.android.app.authorization.data.entity.OAuthCredentialsEntity;
import oauth.signpost.OAuth;
import oauth.signpost.OAuthConsumer;
import oauth.signpost.OAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthProvider;
import timber.log.Timber;

/**
 * OAuth connector frame class to bundle all of the OAuth logic for easier access
 */
@Singleton
public class OAuthConnector {

  private EndpointEntity endpoint;
  private OkHttpOAuthProvider mProvider = null;
  private OkHttpOAuthConsumer mConsumer = null;
  private RequestTokenTask requestTokenTask;
  private AccessTokenTask accessTokenTask;

  /*
   * Creates a new, unauthorized OAuthConnector. The OAuthConsumer will be initialized with
   * the passed Server, but no access tokens will be set.
   *
   * @param server Server object to be used for the initialisation of the consumer
   */

  @Inject public OAuthConnector() {
  }

  public static String sign(String downloadUrl,
      OAuthCredentialsEntity credentialsEntity) throws OAuthCommunicationException, OAuthExpectationFailedException, OAuthMessageSignerException {
    EndpointEntity endpoint = credentialsEntity.getEndpoint();
    if (endpoint == null) throw new IllegalArgumentException("Endpoint must not be null!");

    OAuthConsumer consumer = new OkHttpOAuthConsumer(endpoint.getConsumerKey(),
        endpoint.getConsumerSecret());
    consumer.setTokenWithSecret(credentialsEntity.getAccessToken(),
        credentialsEntity.getAccessTokenSecret());

    return consumer.sign(downloadUrl);
  }

  public void with(EndpointEntity endpoint) {
    if (endpoint == null) throw new IllegalArgumentException("Endpoint must not be null!");
    this.endpoint = endpoint;

    this.mProvider = new OkHttpOAuthProvider(endpoint.getRequestUrl(), endpoint.getAccessUrl(),
        endpoint.getAuthorizationUrl());

    this.mConsumer = new OkHttpOAuthConsumer(endpoint.getConsumerKey(),
        endpoint.getConsumerSecret());
  }

  /*
   * Tells the OAuthConnector to request a new request token from the endpoint. For this to work
   * the OAuthConsumer has to be correctly initalized with a server. Since we request a
   * request token the access tokens don't need to be set.
   * Once the request was successfully finished the
   * {@link #onRequestTokenReceived(String)} from the passed callbacks will be called.
   * This call will have a url that has to be loaded in an browser to proceed the authentication
   * process.
   * If the request fails the {@link #onRequestTokenRequestError(.OAuthError)}
   * will be called with a {@link .OAuthError} object.
   *
   * @param callbacks {@link } that should be called once something happens
   */
  public void getRequestToken(OAuthRequestTokenCallbacks callbacks) {
    this.requestTokenTask = new RequestTokenTask(callbacks, mConsumer, mProvider);
    this.requestTokenTask.execute();
  }

  /*
   * Requests a new access token form the endpoint set in the OAuthConsumer.
   * For this to work the OAuthConsumer
   * has to be correctly initalized with a server.
   * Once the request was successfully finished the
   * {@link #onAccessTokenReceived(String, String)} from the passed callbacks will be called.
   * This call will have a url that has to be loaded in an browser to proceed the authentication
   * process.
   * If the request fails the {@link #onAccessTokenRequestError(.OAuthError)}
   * will be called with a {@link .OAuthError} object.
   *
   * @param callbacks {@link } that should be called once something happens
   */
  public void getAccessToken(OAuthAccessTokenCallbacks callbacks) {
    this.accessTokenTask = new AccessTokenTask(callbacks, mConsumer, mProvider);
    this.accessTokenTask.execute();
  }

  public void cancel() {
    if (this.requestTokenTask != null) {
      this.requestTokenTask.cancel(true);
    }

    if (this.accessTokenTask != null) {
      this.accessTokenTask.cancel(true);
    }
  }

  /**
   * Simple callback interface to get responses and status information from the OAuth request
   * tasks.
   */
  public interface OAuthRequestTokenCallbacks {
    /**
     * Called when the OAuth request token task finished successfully. The call will contain
     * the received url to call in a {@link android.webkit.WebView}
     * for proceeding the OAuth process.
     *
     * @param authUrl the authorization url String send from the endpoint
     */
    void onRequestTokenReceived(String authUrl);

    /**
     * This callback will be called when some error happened during the request token request
     * task. It's passing an {@link .OAuthError} object containing some information
     * about the error.
     *
     * @param e An {@link .OAuthError} object containing informations about the error
     */
    void onRequestTokenRequestError(OAuthError e);
  }

  public interface OAuthAccessTokenCallbacks {

    /**
     * This callback will be called when some error happened during the access token request
     * task. It's passing an {@link .OAuthError} object containing some information
     * about the error.
     *
     * @param e An {@link .OAuthError} object containing informations about the error
     */
    void onAccessTokenRequestError(OAuthConnector.OAuthError e);

    /**
     * Called when the OAuth access token request finished successfully. It will contain the
     * received access token and access token secret
     *
     * @param credentialsEntity the new credentials including accesstoken
     */
    void onAccessTokenReceived(OAuthCredentialsEntity credentialsEntity);
  }

  private static class RequestResult {
    private String[] mResult;
    private Exception mException;

    RequestResult(String[] result, Exception exception) {
      mResult = result;
      mException = exception;
    }

    public String[] getResult() {
      return mResult;
    }

    public void setResult(String[] result) {
      mResult = result;
    }

    public Exception getException() {
      return mException;
    }

    public void setException(Exception exception) {
      mException = exception;
    }
  }

  /**
   * Simple wrapper class for an OAuth request task error.
   */
  public class OAuthError {
    /**
     * A error message string describing the error
     */
    public String errorMessage;

    /**
     * Created a new OAuthError object containing the passed error message
     *
     * @param message Error message String
     */
    OAuthError(String message) {
      this.errorMessage = message;
    }
  }

  /*
   * AsyncTask for requesting the request token from the API
   */
  private class RequestTokenTask extends AsyncTask<String, Integer, RequestResult> {
    private final OAuthRequestTokenCallbacks mCallbacks;
    private final OAuthProvider provider;
    private final OAuthConsumer consumer;

    RequestTokenTask(OAuthRequestTokenCallbacks callbacks, OAuthConsumer consumer,
        OAuthProvider provider) {
      this.mCallbacks = callbacks;
      this.provider = provider;
      this.consumer = consumer;
    }

    @Override protected RequestResult doInBackground(String... params) {
      try {
        String requestToken = provider.retrieveRequestToken(consumer, OAuth.OUT_OF_BAND);
        if (!requestToken.isEmpty()) {
          return new RequestResult(new String[]{requestToken}, null);
        }

        return new RequestResult(null,
            new EmptyRequestTokenException("Received empty RequestToken response"));
      } catch (Exception e) {
        Timber.e(e.getMessage(), e);

        return new RequestResult(null, e);
      }
    }

    @Override protected void onPostExecute(RequestResult result) {
      // If the RequestToken request was successful, show the permission prompt
      if (mCallbacks != null && result != null) if (result.getResult() != null) {
        mCallbacks.onRequestTokenReceived(result.getResult()[0]);
      } else if (result.getException() != null) {
        Exception exception = result.getException();
        mCallbacks.onRequestTokenRequestError(
            new OAuthConnector.OAuthError(exception.getLocalizedMessage()));
      } else {
        mCallbacks.onRequestTokenRequestError(new OAuthConnector.OAuthError("Unknown error"));
      }
    }
  }

  /*
   * AsyncTask for requesting the access token from the API
   */
  private class AccessTokenTask extends AsyncTask<String, Integer, RequestResult> {
    private final OAuthAccessTokenCallbacks mCallbacks;
    private final OAuthProvider provider;
    private final OAuthConsumer consumer;

    AccessTokenTask(OAuthAccessTokenCallbacks callbacks, OAuthConsumer consumer,
        OAuthProvider provider) {
      this.mCallbacks = callbacks;
      this.provider = provider;
      this.consumer = consumer;
    }

    @Override protected RequestResult doInBackground(String... arg0) {
      if (provider == null) {
        return new RequestResult(null, new InvalidStateException("Provider must not be null"));
      }

      try {
        provider.retrieveAccessToken(consumer, OAuth.OUT_OF_BAND);
        if (!consumer.getToken()
            .isEmpty() && !consumer.getTokenSecret()
            .isEmpty()) {

          return new RequestResult(new String[]{consumer.getToken(), consumer.getTokenSecret()},
              null);
        }

        return new RequestResult(null, new AccessTokenRequestException("No AccessToken received"));
      } catch (Exception e) {
        Timber.e(e.getMessage(), e);

        return new RequestResult(null, e);
      }
    }

    @Override protected void onPostExecute(RequestResult result) {
      // If the access token was requested successfully, start the prefetch
      if (mCallbacks != null && result != null) {
        if (result.getResult() != null) {
          OAuthCredentialsEntity credentials = new OAuthCredentialsEntity();
          credentials.setId(UUID.randomUUID()
              .toString());
          credentials.setEndpoint(OAuthConnector.this.endpoint);
          credentials.setAccessToken(result.getResult()[0]);
          credentials.setAccessTokenSecret(result.getResult()[1]);

          mCallbacks.onAccessTokenReceived(credentials);
        } else if (result.getException() != null) {
          mCallbacks.onAccessTokenRequestError(new OAuthConnector.OAuthError(result.getException()
              .getLocalizedMessage()));
        } else {
          mCallbacks.onAccessTokenRequestError(new OAuthConnector.OAuthError("Unkown error"));
        }
      }
    }
  }

  public class EmptyRequestTokenException extends Exception {
    public EmptyRequestTokenException(String detailMessage) {
      super(detailMessage);
    }
  }

  public class AccessTokenRequestException extends Exception {
    public AccessTokenRequestException(String detailMessage) {
      super(detailMessage);
    }
  }

  public class InvalidStateException extends Exception {
    public InvalidStateException(String detailMessage) {
      super(detailMessage);
    }
  }
}
