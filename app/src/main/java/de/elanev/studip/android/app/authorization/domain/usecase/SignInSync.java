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

import dagger.Lazy;
import de.elanev.studip.android.app.authorization.domain.SettingsRepository;
import de.elanev.studip.android.app.base.UseCase;
import de.elanev.studip.android.app.base.domain.executor.PostExecutionThread;
import de.elanev.studip.android.app.base.domain.executor.ThreadExecutor;
import de.elanev.studip.android.app.base.internal.di.PerActivity;
import de.elanev.studip.android.app.user.domain.UserRepository;
import de.elanev.studip.android.app.util.Prefs;
import rx.Observable;
import timber.log.Timber;

/**
 * @author joern
 */
@PerActivity
public class SignInSync extends UseCase {
  private final Lazy<SettingsRepository> settingsRepository;
  private final Lazy<UserRepository> userRepository;
  private final Prefs prefs;

  @Inject public SignInSync(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread,
      Lazy<SettingsRepository> settingsRepository, Lazy<UserRepository> userRepository,
      Prefs prefs) {
    super(threadExecutor, postExecutionThread);
    this.settingsRepository = settingsRepository;
    this.userRepository = userRepository;
    this.prefs = prefs;
  }

  @RxLogObservable @Override public Observable buildUseCaseObservable(boolean forceUpdate) {
    Observable userObs = Observable.defer(() -> userRepository.get()
        .currentUser(forceUpdate)
        .doOnNext(user -> {
          Timber.d("CurrentUser: " + user);
          prefs.setCurrentUserId(user.getUserId());
        }));

    Observable settingsObs = Observable.defer(() -> this.settingsRepository.get()
        .studipSettings(true));

    return Observable.merge(userObs, settingsObs);
  }
}
