/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.base;


import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;

/**
 * A presenter for RxJava, that assumes that only one Observable is subscribed by this presenter.
 */
@SuppressWarnings("ConstantConditions")
public abstract class BaseRxLcePresenter<V extends MvpLceView<T>, T> extends
    com.hannesdorfmann.mosby.mvp.MvpBasePresenter<V> implements
    com.hannesdorfmann.mosby.mvp.MvpPresenter<V> {

  protected void onCompleted() {
    if (isViewAttached()) {
      getView().showContent();
    }
    unsubscribe();
  }

  protected abstract void unsubscribe();

  protected void onError(Throwable e, boolean pullToRefresh) {
    if (isViewAttached()) {
      getView().showError(e, pullToRefresh);
    }
    unsubscribe();
  }

  protected void onNext(T data) {
    if (isViewAttached()) {
      getView().setData(data);
    }
  }

  @Override public void detachView(boolean retainInstance) {
    super.detachView(retainInstance);
    if (!retainInstance) {
      unsubscribe();
    }
  }

}