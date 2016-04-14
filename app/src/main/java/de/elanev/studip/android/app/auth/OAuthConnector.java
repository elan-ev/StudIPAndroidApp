/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.auth;

import android.os.AsyncTask;
import android.text.TextUtils;

import de.elanev.studip.android.app.data.datamodel.Server;
import oauth.signpost.OAuth;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;
import oauth.signpost.http.HttpRequest;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;

/**
 * OAuth connector frame class to bundle all of the OAuth logic for easier access
 */
public class OAuthConnector {

  public static final String TAG = OAuthConnector.class.getSimpleName();

  private static OAuthConnector sInstance;

  public static synchronized OAuthConnector with(Server server) {
    if (server == null) throw new IllegalArgumentException("Server must not be null.");

    if (sInstance == null) sInstance = new OAuthConnector(server);

    sInstance.setServer(server);


    return sInstance;
  }

  private CommonsHttpOAuthProvider mProvider = null;
  private Server mServer = null;
  private OkHttpOAuthConsumer mConsumer = null;

  private void setServer(Server server) {
    if (!TextUtils.equals(server.getBaseUrl(), mServer.getBaseUrl())) {
      mServer = server;
      mConsumer = new OkHttpOAuthConsumer(server.getConsumerKey(), server.getConsumerSecret());
    }
  }


  /*
   * Creates a new, unauthorized OAuthConnector. The OAuthConsumer will be initialized with
   * the passed Server, but no access tokens will be set.
   *
   * @param server Server object to be used for the initialisation of the consumer
   */
  private OAuthConnector(Server server) {
    this.mServer = server;
    this.mConsumer = new OkHttpOAuthConsumer(server.getConsumerKey(), server.getConsumerSecret());

    String accessToken = mServer.getAccessToken();
    String accessTokenSecret = mServer.getAccessTokenSecret();

    if (accessToken != null || accessTokenSecret != null)
      this.mConsumer.setTokenWithSecret(accessToken, accessTokenSecret);
  }

  /*
   * Tells the OAuthConnector to request a new request token from the endpoint. For this to work
   * the OAuthConsumer has to be correctly initalized with a server. Since we request a
   * request token the access tokens don't need to be set.
   * Once the request was successfully finished the
   * {@link OAuthCallbacks#onRequestTokenReceived(String)} from the passed callbacks will be called.
   * This call will have a url that has to be loaded in an browser to proceed the authentication
   * process.
   * If the request fails the {@link OAuthCallbacks#onRequestTokenRequestError(OAuthCallbacks.OAuthError)}
   * will be called with a {@link OAuthCallbacks.OAuthError} object.
   *
   * @param callbacks {@link OAuthCallbacks} that should be called once something happens
   */
  public void getRequestToken(OAuthCallbacks callbacks) {
    new RequestTokenTask(callbacks).execute();
  }

  /*
   * Requests a new access token form the endpoint set in the OAuthConsumer.
   * For this to work the OAuthConsumer
   * has to be correctly initalized with a server.
   * Once the request was successfully finished the
   * {@link OAuthCallbacks#onAccessTokenReceived(String, String)} from the passed callbacks will be called.
   * This call will have a url that has to be loaded in an browser to proceed the authentication
   * process.
   * If the request fails the {@link OAuthCallbacks#onAccesTokenRequestError(OAuthCallbacks.OAuthError)}
   * will be called with a {@link OAuthCallbacks.OAuthError} object.
   *
   * @param callbacks {@link OAuthCallbacks} that should be called once something happens
   */
  public void getAccessToken(OAuthCallbacks callbacks) {
    new AccessTokenTask(callbacks).execute();
  }

  public HttpRequest sign(Object request) throws OAuthCommunicationException, OAuthExpectationFailedException, OAuthMessageSignerException, OAuthNotAuthorizedException {
    if (mConsumer.getToken() == null || mConsumer.getTokenSecret() == null)
      throw new OAuthNotAuthorizedException();

    return mConsumer.sign(request);
  }

  public String sign(String requestUrl) throws OAuthCommunicationException, OAuthExpectationFailedException, OAuthMessageSignerException, OAuthNotAuthorizedException {
    if (mConsumer.getToken() == null || mConsumer.getTokenSecret() == null)
      throw new OAuthNotAuthorizedException();

    return mConsumer.sign(requestUrl);
  }

  /**
   * Simple callback interface to get responses and status information from the OAuth request
   * tasks.
   */
  public interface OAuthCallbacks {
    /**
     * Called when the OAuth request token task finished successfully. The call will contain
     * the received url to call in a {@link WebViewActivity}
     * for proceeding the OAuth process.
     *
     * @param authUrl the authorization url String send from the endpoint
     */
    public void onRequestTokenReceived(String authUrl);

    /**
     * Called when the OAuth access token request finished successfully. It will contain the
     * received access token and access token secret
     *
     * @param accessToken       access token String returned from the endpoint
     * @param accessTokenSecret access token secret String returned from the endpoint
     */
    public void onAccessTokenReceived(String accessToken, String accessTokenSecret);

    /**
     * This callback will be called when some error happened during the request token request
     * task. It's passing an {@link OAuthCallbacks.OAuthError} object containing some information
     * about the error.
     *
     * @param e An {@link OAuthCallbacks.OAuthError} object containing informations about the error
     */
    public void onRequestTokenRequestError(OAuthError e);

    /**
     * This callback will be called when some error happened during the access token request
     * task. It's passing an {@link OAuthCallbacks.OAuthError} object containing some information
     * about the error.
     *
     * @param e An {@link OAuthCallbacks.OAuthError} object containing informations about the error
     */
    public void onAccesTokenRequestError(OAuthError e);

    /**
     * Simple wrapper class for an OAuth request task error.
     */
    class OAuthError {
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
  }

  /*
   * AsyncTask for requesting the request token from the API
   */
  private class RequestTokenTask extends AsyncTask<String, Integer, RequestResult> {
    OAuthCallbacks mCallbacks;

    RequestTokenTask(OAuthCallbacks callbacks) {
      this.mCallbacks = callbacks;
    }

    @Override protected RequestResult doInBackground(String... params) {
      mProvider = new CommonsHttpOAuthProvider(mServer.getRequestUrl(),
          mServer.getAccessUrl(),
          mServer.getAuthorizationUrl());

      try {
        String requestToken = mProvider.retrieveRequestToken(mConsumer, OAuth.OUT_OF_BAND);
        if (!requestToken.isEmpty()) {

          return new RequestResult(new String[]{requestToken}, null);
        }

        return new RequestResult(null,
            new EmptyRequestTokenException("Received empty RequestToken response"));
      } catch (Exception e) {
        e.printStackTrace();

        return new RequestResult(null, e);
      }
    }

    @Override protected void onPostExecute(RequestResult result) {
      // If the RequestToken request was successful, show the permission prompt
      if (mCallbacks != null && result != null) if (result.getResult() != null) {
        mCallbacks.onRequestTokenReceived(result.getResult()[0]);
      } else if (result.getException() != null) {
        Exception exception = result.getException();
        mCallbacks.onRequestTokenRequestError(new OAuthCallbacks.OAuthError(exception.getLocalizedMessage()));
      } else {
        mCallbacks.onRequestTokenRequestError(new OAuthCallbacks.OAuthError("Unknown error"));
      }
    }
  }

  /*
   * AsyncTask for requesting the access token from the API
   */
  private class AccessTokenTask extends AsyncTask<String, Integer, RequestResult> {
    OAuthCallbacks mCallbacks;

    AccessTokenTask(OAuthCallbacks callbacks) {
      this.mCallbacks = callbacks;
    }

    @Override protected RequestResult doInBackground(String... arg0) {
      if (mProvider == null) {

        return new RequestResult(null, new InvalidStateException("Provider must not be null"));
      }

      try {
        mProvider.retrieveAccessToken(mConsumer, OAuth.OUT_OF_BAND);
        if (!mConsumer.getToken().isEmpty() && !mConsumer.getTokenSecret().isEmpty()) {

          return new RequestResult(new String[]{mConsumer.getToken(), mConsumer.getTokenSecret()},
              null);
        }

        return new RequestResult(null, new AccessTokenRequestException("No AccessToken received"));
      } catch (Exception e) {
        e.printStackTrace();

        return new RequestResult(null, e);
      }
    }

    @Override protected void onPostExecute(RequestResult result) {
      // If the access token was requested successfully, start the prefetching
      if (mCallbacks != null && result != null) {
        if (result.getResult() != null) {
          mCallbacks.onAccessTokenReceived(result.getResult()[0], result.getResult()[1]);
        } else if (result.getException() != null) {
          mCallbacks.onAccesTokenRequestError(new OAuthCallbacks.OAuthError(result.getException()
              .getLocalizedMessage()));
        } else {
          mCallbacks.onAccesTokenRequestError(new OAuthCallbacks.OAuthError("Unkown error"));
        }
      }
    }
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
