/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.net.oauth;

import android.os.AsyncTask;
import android.os.Bundle;

import de.elanev.studip.android.app.backend.datamodel.Server;
import de.elanev.studip.android.app.util.Prefs;
import oauth.signpost.OAuth;
import oauth.signpost.commonshttp.CommonsHttpOAuthProvider;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

/**
 * OAuth connector frame class to bundle all of the OAuth logic for easier access
 */
public class OAuthConnector {

    /**
     * Returns the server the OAuthConnector is currently connected to
     *
     * @return Server object containing all Information about the Server the connector is connected to
     */
    public Server getServer() {
        return mServer;
    }

    /**
     * Set the Server the OAuthConnect should be connected to
     *
     * @param server Server object to be connected to
     */
    public void setServer(Server server) {
        this.mServer = server;
    }

    /**
     * Returns the current consumer the connector is holding
     *
     * @return the current VolleyOAuthConsumer Object
     */
    public VolleyOAuthConsumer getConsumer() {
        return mConsumer;
    }

    /**
     * Sets a new VolleyOAuthConsumer for signing VolleyRequests
     *
     * @param consumer VolleyOAuthConsumer to set in this connector
     */
    public void setConsumer(VolleyOAuthConsumer consumer) {
        this.mConsumer = consumer;
    }

    private Server mServer = null;
    private VolleyOAuthConsumer mConsumer = null;
    private CommonsHttpOAuthProvider mProvider = null;

    /**
     * Creates a new OAuthConnector which will be initialized with the passed Server information and
     * access tokens passed with the Bundle
     *
     * @param server       Server object to be used for the initialisation of the consumer
     * @param accessTokens the access tokens to be used for initialisation of the consumer
     */
    public OAuthConnector(Server server, Bundle accessTokens) {
        this.mServer = server;
        // Initialize the consumer with the Server
        this.mConsumer = new VolleyOAuthConsumer(
                server.getConsumerKey(),
                server.getConsumerSecret());

        // set the access tokens for this consumer
        this.mConsumer.setTokenWithSecret(
                accessTokens.getString(Prefs.ACCESS_TOKEN),
                accessTokens.getString(Prefs.ACCESS_TOKEN_SECRET));
    }

    /**
     * Creates a new, unauthorized OAuthConnector. The VolleyOAuthConsumer will be initialized with
     * the passed Server, but no access tokens will be set.
     *
     * @param server Server object to be used for the initialisation of the consumer
     */
    public OAuthConnector(Server server) {
        this.mServer = server;
        this.mConsumer = new VolleyOAuthConsumer(
                server.getConsumerKey(),
                server.getConsumerSecret());
    }

    /**
     * Tells the OAuthConnector to request a new request token from the endpoint. For this to work
     * the VolleyOAuthConsumer has to be correctly initalized with a server. Since we request a
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

    /**
     * Requests a new access token form the endpoint set in the {@link de.elanev.studip.android.app.backend.net.oauth.VolleyOAuthConsumer}.
     * For this to work the {@link de.elanev.studip.android.app.backend.net.oauth.VolleyOAuthConsumer}
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

    /**
     * Simple callback interface to get responses and status information from the OAuth request
     * tasks.
     */
    public interface OAuthCallbacks {
        /**
         * Called when the OAuth request token task finished successfully. The call will contain
         * the received url to call in a {@link de.elanev.studip.android.app.backend.net.oauth.WebViewActivity}
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
    private class RequestTokenTask extends AsyncTask<String, Integer, String> {

        OAuthCallbacks mCallbacks;

        RequestTokenTask(OAuthCallbacks callbacks) {
            this.mCallbacks = callbacks;
        }

        @Override
        protected String doInBackground(String... params) {
            if (mProvider == null) {
                mProvider = new CommonsHttpOAuthProvider(
                        mServer.getRequestUrl(),
                        mServer.getAccessUrl(),
                        mServer.getAuthorizationUrl());
            }
            try {
                return mProvider.retrieveRequestToken(mConsumer, OAuth.OUT_OF_BAND);
            } catch (OAuthMessageSignerException e) {
                e.printStackTrace();
            } catch (OAuthNotAuthorizedException e) {
                e.printStackTrace();
            } catch (OAuthExpectationFailedException e) {
                e.printStackTrace();
            } catch (OAuthCommunicationException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {

            // If the RequestToken request was successful, show the
            // permission prompt
            if (mCallbacks != null)
                if (result != null) {
                    mCallbacks.onRequestTokenReceived(result);
                } else {
                    mCallbacks.onRequestTokenRequestError(new OAuthCallbacks.OAuthError("Something went wrong"));
                }
        }
    }

    /*
     * AsyncTask for requesting the access token from the API
     */
    private class AccessTokenTask extends AsyncTask<String, Integer, String[]> {
        OAuthCallbacks mCallbacks;

        AccessTokenTask(OAuthCallbacks callbacks) {
            this.mCallbacks = callbacks;
        }

        @Override
        protected String[] doInBackground(String... arg0) {
            if (mProvider != null)
                try {
                    mProvider.retrieveAccessToken(mConsumer, OAuth.OUT_OF_BAND);

                    return new String[]{mConsumer.getToken(), mConsumer.getTokenSecret()};
                } catch (OAuthMessageSignerException e) {
                    e.printStackTrace();
                } catch (OAuthNotAuthorizedException e) {
                    e.printStackTrace();
                } catch (OAuthExpectationFailedException e) {
                    e.printStackTrace();
                } catch (OAuthCommunicationException e) {
                    e.printStackTrace();
                }
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {

            // If the access token was requested successfully, start the
            // prefetching
            if (mCallbacks != null)
                if (result != null) {
                    mCallbacks.onAccessTokenReceived(result[0], result[1]);
                } else {
                    mCallbacks.onAccesTokenRequestError(new OAuthCallbacks.OAuthError("AccessToken error"));
                }
        }
    }
}
