/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.domain.usecase;

import com.fernandocejas.frodo.annotation.RxLogObservable;

import javax.inject.Inject;

import de.elanev.studip.android.app.authorization.domain.AuthorizationRepository;
import de.elanev.studip.android.app.base.UseCase;
import de.elanev.studip.android.app.base.domain.executor.PostExecutionThread;
import de.elanev.studip.android.app.base.domain.executor.ThreadExecutor;
import de.elanev.studip.android.app.base.internal.di.PerActivity;
import de.elanev.studip.android.app.util.Prefs;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import rx.Observable;

/**
 * @author joern
 */
@PerActivity
public class LogoutUser extends UseCase {
  private final AuthorizationRepository authRepo;
  private final Prefs prefs;
  private final RealmConfiguration realmConfig;

  @Inject protected LogoutUser(ThreadExecutor threadExecutor,
      PostExecutionThread postExecutionThread, AuthorizationRepository repository, Prefs prefs,
      RealmConfiguration realmConfig) {
    super(threadExecutor, postExecutionThread);

    this.authRepo = repository;
    this.prefs = prefs;
    this.realmConfig = realmConfig;
  }

  @RxLogObservable @Override protected Observable buildUseCaseObservable(boolean forceUpdate) {
    return authRepo.clearCredentials()
        .doOnNext(aVoid -> prefs.clearPrefs())
        .doOnNext(aVoid -> Realm.deleteRealm(realmConfig));
  }
}
