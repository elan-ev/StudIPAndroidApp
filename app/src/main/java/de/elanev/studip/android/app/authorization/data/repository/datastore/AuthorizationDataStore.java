/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.data.repository.datastore;

import de.elanev.studip.android.app.authorization.data.entity.EndpointEntity;
import de.elanev.studip.android.app.authorization.data.entity.OAuthCredentialsEntity;
import de.elanev.studip.android.app.authorization.domain.Endpoint;
import rx.Observable;

/**
 * @author joern
 */
public interface AuthorizationDataStore {
  void saveCredentials(OAuthCredentialsEntity credentialsEntity);

  OAuthCredentialsEntity getCredentials();

  void clearCredentials();

  Observable<EndpointEntity> getEndpoint(String endpointId);
}
