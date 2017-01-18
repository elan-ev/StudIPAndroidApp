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
import de.elanev.studip.android.app.authorization.domain.AuthorizationRepository;
import de.elanev.studip.android.app.base.UseCase;
import de.elanev.studip.android.app.base.domain.executor.PostExecutionThread;
import de.elanev.studip.android.app.base.domain.executor.ThreadExecutor;
import de.elanev.studip.android.app.base.internal.di.PerActivity;
import rx.Observable;

/**
 * @author joern
 */
@PerActivity
public class SignInUser extends UseCase {
  private final AuthService authService;
  private final AuthorizationRepository authorizationRepository;

  @Inject public SignInUser(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread,
      AuthService authService, AuthorizationRepository repository) {
    super(threadExecutor, postExecutionThread);

    this.authService = authService;
    this.authorizationRepository = repository;
  }

  @Override protected Observable buildUseCaseObservable(boolean forceUpdate) {
    return this.authService.accessToken()
        .map(credentials -> {
          this.authorizationRepository.saveCredentials(credentials);

          return Observable.empty();
        });
  }


  //TODO: Load necessary data before continuing to news
  //    private void requestUserProfile() {
  //      Subscription subscription = apiService.get()
  //          .getCurrentUserInfo()
  //          .subscribeOn(Schedulers.io())
  //          .observeOn(AndroidSchedulers.mainThread())
  //          .subscribe(new Subscriber<User>() {
  //            @Override public void onCompleted() {
  //              requestSettings();
  //            }
  //
  //            @Override public void onError(Throwable e) {
  //              if (e != null && e.getLocalizedMessage() != null) {
  //                Timber.e(e, e.getLocalizedMessage());
  //
  //                mOnAuthListener.onAuthCanceled();
  //              }
  //            }
  //
  //            @Override public void onNext(User user) {
  //              mPrefs.setUserInfo(User.toJson(user));
  //            }
  //          });
  //    }
  //
  //    private void requestSettings() {
  //      Subscription subscription = apiService.get()
  //          .getSettings()
  //          .subscribeOn(Schedulers.io())
  //          .observeOn(AndroidSchedulers.mainThread())
  //          .subscribe(new Subscriber<Settings>() {
  //            @Override public void onCompleted() {
  //              mOnAuthListener.onAuthSuccess(mSelectedServer);
  //            }
  //
  //            @Override public void onError(Throwable e) {
  //              if (e != null && e.getLocalizedMessage() != null) {
  //                Timber.e(e, e.getLocalizedMessage());
  //
  //                mOnAuthListener.onAuthCanceled();
  //              }
  //            }
  //
  //            @Override public void onNext(Settings settings) {
  //              mPrefs.setApiSettings(settings.toJson());
  //            }
  //          });
  //    }
}
