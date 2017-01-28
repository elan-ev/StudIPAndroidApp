/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.domain;


import de.elanev.studip.android.app.authorization.domain.model.OAuthCredentials;
import rx.Observable;

/**
 * @author joern
 */

public interface CredentialsRepository {
  void save(OAuthCredentials credentials);

  OAuthCredentials credentials();

  Observable<Void> clear();
}