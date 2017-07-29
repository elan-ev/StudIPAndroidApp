/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.data;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.authorization.data.entity.CredentialsEntityDataMapper;
import de.elanev.studip.android.app.authorization.data.entity.EndpointEntity;
import de.elanev.studip.android.app.authorization.data.entity.EndpointsEntityDataMapper;
import de.elanev.studip.android.app.authorization.data.entity.OAuthCredentialsEntity;
import de.elanev.studip.android.app.authorization.domain.AuthService;
import de.elanev.studip.android.app.authorization.domain.model.Endpoint;
import de.elanev.studip.android.app.authorization.domain.model.OAuthCredentials;
import rx.Emitter;
import rx.Observable;

/**
 * @author joern
 */
@Singleton
public class AuthServiceImpl implements AuthService {
  private final EndpointsEntityDataMapper endpointMapper;
  private final CredentialsEntityDataMapper credentialsMapper;
  private final OAuthConnector oAuthConnector;

  @Inject public AuthServiceImpl(OAuthConnector oAuthConnector,
      EndpointsEntityDataMapper endpointMapper, CredentialsEntityDataMapper credentialsMapper) {
    this.endpointMapper = endpointMapper;
    this.oAuthConnector = oAuthConnector;
    this.credentialsMapper = credentialsMapper;
  }

  @Override public Observable<String> auth(Endpoint endpoint) {
    EndpointEntity endpointEntity = endpointMapper.transform(endpoint);
    oAuthConnector.with(endpointEntity);
    return Observable.create(emitter -> {
      final OAuthConnector.OAuthRequestTokenCallbacks requestTokenCallbacks = new OAuthConnector.OAuthRequestTokenCallbacks() {
        @Override public void onRequestTokenReceived(String authUrl) {
          emitter.onNext(authUrl);
          emitter.onCompleted();
        }

        @Override public void onRequestTokenRequestError(OAuthConnector.OAuthError e) {
          emitter.onError(new Throwable(e.errorMessage));
        }
      };
      emitter.setCancellation(oAuthConnector::cancel);
      oAuthConnector.getRequestToken(requestTokenCallbacks);
    }, Emitter.BackpressureMode.NONE);
  }

  @Override public Observable<OAuthCredentials> accessToken() {
    return Observable.create(emitter -> {
      final OAuthConnector.OAuthAccessTokenCallbacks accessTokenCallbacks = new OAuthConnector.OAuthAccessTokenCallbacks() {
        @Override public void onAccessTokenRequestError(OAuthConnector.OAuthError e) {
          emitter.onError(new Throwable(e.errorMessage));
        }

        @Override public void onAccessTokenReceived(OAuthCredentialsEntity credentialsEntity) {
          emitter.onNext(credentialsMapper.transform(credentialsEntity));
          emitter.onCompleted();
        }
      };

      emitter.setCancellation(oAuthConnector::cancel);
      oAuthConnector.getAccessToken(accessTokenCallbacks);
    }, Emitter.BackpressureMode.NONE);
  }

}
