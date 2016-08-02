/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.base;

/**
 * Copyright (C) 2015 Fernando Cejas Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

/**
 * Abstract class for a Use Case (Interactor in terms of Clean Architecture).
 * This interface represents a execution unit for different use cases (this means any use case
 * in the application should implement this contract).
 *
 * By convention each UseCase implementation will return the result using a {@link rx.Subscriber}
 * that will execute its job in a background thread and will post the result in the UI thread.
 */
public abstract class UseCase<T> {


  private Subscription subscription = Subscriptions.empty();

  protected UseCase() { }

  /**
   * Executes the current use case.
   *
   * @param UseCaseSubscriber The guy who will be listen to the observable build
   *                          with {@link #buildUseCaseObservable()}.
   */
  @SuppressWarnings("unchecked") public void execute(Subscriber<T> UseCaseSubscriber) {
    this.subscription = get().subscribe(UseCaseSubscriber);
  }

  final public Observable<T> get() {
    return buildUseCaseObservable().compose(applySchedulers());
  }

  /**
   * Builds an {@link rx.Observable} which will be used when executing the current {@link UseCase}.
   */
  protected abstract Observable<T> buildUseCaseObservable();

  private Observable.Transformer<T, T> applySchedulers() {
    return new Observable.Transformer<T, T>() {
      @Override public Observable<T> call(Observable<T> tObservable) {
        return tObservable.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread());
      }
    };
  }

  /**
   * Unsubscribes from current {@link rx.Subscription}.
   */
  public void unsubscribe() {
    if (!subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }
}