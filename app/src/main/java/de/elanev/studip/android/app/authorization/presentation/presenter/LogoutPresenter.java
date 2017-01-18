/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.presentation.presenter;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;

import javax.inject.Inject;
import javax.inject.Named;

import de.elanev.studip.android.app.authorization.domain.usecase.LogoutUser;
import de.elanev.studip.android.app.authorization.presentation.view.LogoutView;
import de.elanev.studip.android.app.base.UseCase;
import de.elanev.studip.android.app.base.internal.di.PerActivity;
import rx.Subscriber;

/**
 * @author joern
 */
@PerActivity
public class LogoutPresenter extends MvpBasePresenter<LogoutView> implements
    MvpPresenter<LogoutView> {

  private final LogoutUser logoutUser;

  @Inject public LogoutPresenter(@Named("logoutUser") UseCase logoutUser) {
    this.logoutUser = (LogoutUser) logoutUser;
  }

  public void logout() {
    this.logoutUser.execute(new Subscriber<Void>() {
      @Override public void onCompleted() {
        LogoutPresenter.this.onCompleted();
      }

      @Override public void onError(Throwable e) {
        LogoutPresenter.this.onError(e);
      }

      @Override public void onNext(Void data) {
        //NoOp
      }
    });
  }

  private void onCompleted() {
    if (isViewAttached()) {
      getView().logoutSuccess();
    }
    unsubscribe();
  }

  private void onError(Throwable e) {
    if (isViewAttached()) {
      getView().showError(e);
    }
    this.unsubscribe();
  }

  private void unsubscribe() {
    this.logoutUser.unsubscribe();
  }

  @Override public void detachView(boolean retainInstance) {
    super.detachView(retainInstance);
    if (!retainInstance) {
      this.unsubscribe();
    }
  }

}
