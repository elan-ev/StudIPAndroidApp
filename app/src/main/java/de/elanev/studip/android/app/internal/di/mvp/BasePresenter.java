/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.internal.di.mvp;

import android.support.annotation.Nullable;
import android.support.annotation.UiThread;

import java.lang.ref.WeakReference;

import de.elanev.studip.android.app.BaseView;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author joern
 */
@SuppressWarnings("ConstantConditions")
public abstract class BasePresenter<V extends BaseView<M>, M> implements Presenter<V> {
  protected Subscriber<M> subscriber;
  private WeakReference<V> view;


  @UiThread @Override public void bindView(V view) { this.view = new WeakReference<>(view); }

  @UiThread @Override public void unbindView() {
    if (this.view != null) {
      view.clear();
      view = null;
    }
    unsubscribe();
  }

  protected void unsubscribe() {
    if (subscriber != null && !subscriber.isUnsubscribed()) {
      subscriber.unsubscribe();
    }

    subscriber = null;
  }

  public void subscribe(Observable<M> observable) {

    if (isViewAttached()) {
      getView().showLoading();
    }

    unsubscribe();

    subscriber = new Subscriber<M>() {

      @Override public void onCompleted() {
        BasePresenter.this.onCompleted();
      }

      @Override public void onError(Throwable e) {
        BasePresenter.this.onError(e);
      }

      @Override public void onNext(M m) {
        BasePresenter.this.onNext(m);
      }
    };

    observable.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(subscriber);
  }

  @UiThread public boolean isViewAttached() {
    return view != null && view.get() != null;
  }

  @UiThread @Nullable public V getView() {
    return view == null ? null : view.get();
  }

  protected void onCompleted() {
    if (isViewAttached()) {
      getView().showContent();
    }
    unsubscribe();
  }

  protected void onError(Throwable e) {
    if (isViewAttached()) {
      getView().showError(e);
    }
    unsubscribe();
  }

  protected void onNext(M data) {
    if (isViewAttached()) {
      getView().setData(data);
    }
  }
}
