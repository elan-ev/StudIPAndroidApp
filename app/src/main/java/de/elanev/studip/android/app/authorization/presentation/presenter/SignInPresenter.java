/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.presentation.presenter;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;

import javax.inject.Inject;
import javax.inject.Named;

import de.elanev.studip.android.app.authorization.domain.usecase.RequestUserAuth;
import de.elanev.studip.android.app.authorization.domain.usecase.SignInUser;
import de.elanev.studip.android.app.authorization.presentation.view.SignInView;
import de.elanev.studip.android.app.base.UseCase;
import de.elanev.studip.android.app.base.internal.di.PerActivity;
import rx.Subscriber;

/**
 * @author joern
 */
@PerActivity
public class SignInPresenter extends MvpBasePresenter<SignInView> {
  private final SignInUser signInUser;
  private final RequestUserAuth requestUserAuth;
  private String authUrl;

  @Inject public SignInPresenter(@Named("signInUser") UseCase signInUser,
      @Named("requestUserAuth") UseCase<String> requestUserAuth) {
    this.signInUser = (SignInUser) signInUser;
    this.requestUserAuth = (RequestUserAuth) requestUserAuth;
  }

  public void startAuthProcess() {
    this.showLoading();
    this.requestUserAuth.execute(new Subscriber<String>() {
      @Override public void onCompleted() {
        SignInPresenter.this.showUserAuthRequest();
      }

      @Override public void onError(Throwable e) {
        SignInPresenter.this.onError(e);
      }

      @Override public void onNext(String s) {
        SignInPresenter.this.setAuthUrl(s);
      }
    });
  }

  private void showLoading() {
    if (isViewAttached()) {
      getView().showLoading();
    }
  }

  private void showUserAuthRequest() {
    if (isViewAttached()) {
      getView().showAuthDialog(this.authUrl);
    }
    unsubscribe();
  }

  private void onError(Throwable e) {
    if (isViewAttached()) {
      getView().showError(e);
    }
    unsubscribe();
  }

  private void setAuthUrl(String s) {
    this.authUrl = s;
  }

  private void unsubscribe() {
    requestUserAuth.unsubscribe();
    signInUser.unsubscribe();
  }

  public void signInUser() {
    this.signInUser.execute(new Subscriber() {
      @Override public void onCompleted() {
        SignInPresenter.this.signInUserSuccess();
      }

      @Override public void onError(Throwable e) {
        SignInPresenter.this.onError(e);
      }

      @Override public void onNext(Object o) {
        //NoOp
      }
    });
  }

  private void signInUserSuccess() {
    if (isViewAttached()) {
      getView().authSuccess();
    }
    unsubscribe();
  }
}
