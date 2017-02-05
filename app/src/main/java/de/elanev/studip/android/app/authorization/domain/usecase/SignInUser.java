/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.domain.usecase;

import javax.inject.Inject;

import de.elanev.studip.android.app.authorization.domain.AuthService;
import de.elanev.studip.android.app.authorization.domain.CredentialsRepository;
import de.elanev.studip.android.app.base.UseCase;
import de.elanev.studip.android.app.base.domain.executor.PostExecutionThread;
import de.elanev.studip.android.app.base.domain.executor.ThreadExecutor;
import de.elanev.studip.android.app.base.internal.di.PerActivity;
import de.elanev.studip.android.app.util.Prefs;
import rx.Observable;

/**
 * @author joern
 */
@PerActivity
public class SignInUser extends UseCase {
  private final AuthService authService;
  private final CredentialsRepository credentialsRepository;
  private final Prefs prefs;

  @Inject public SignInUser(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread,
      AuthService authService, CredentialsRepository credentialsRepository, Prefs prefs) {
    super(threadExecutor, postExecutionThread);

    this.authService = authService;
    this.credentialsRepository = credentialsRepository;
    this.prefs = prefs;
  }

  @Override public Observable buildUseCaseObservable(boolean forceUpdate) {
    return this.authService.accessToken()
        .doOnNext(credentials -> {
          this.credentialsRepository.save(credentials);
          this.prefs.setAppAuthorized(true);
          this.prefs.setEndpointName(credentials.getEndpoint()
              .getName());
          this.prefs.setEndpointEmail(credentials.getEndpoint()
              .getContactEmail());
          this.prefs.setBaseUrl(credentials.getEndpoint()
              .getApiUrl());
        });
  }
}
