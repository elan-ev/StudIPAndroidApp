/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.base;

import de.elanev.studip.android.app.base.domain.executor.PostExecutionThread;
import de.elanev.studip.android.app.base.domain.executor.ThreadExecutor;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;

/**
 * Abstract class for a Use Case (Interactor in terms of Clean Architecture).
 * This interface represents a execution unit for different use cases (this means any use case
 * in the application should implement this contract).
 *
 * By convention each UseCase implementation will return the result using a {@link rx.Subscriber}
 * that will execute its job in a background thread and will post the result in the UI thread.
 */
public abstract class UseCase<T> {

  private final ThreadExecutor threadExecutor;
  private final PostExecutionThread postExecutionThread;
  private Subscription subscription;

  protected UseCase(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
    this.threadExecutor = threadExecutor;
    this.postExecutionThread = postExecutionThread;
  }

  public void execute(Subscriber<T> subscriber) {
    if (subscriber instanceof DefaultSubscriber) {
      this.subscription = this.get(((DefaultSubscriber) subscriber).isPullToRefresh())
          .subscribe(subscriber);
    } else {
      this.subscription = this.get(false)
          .subscribe(subscriber);
    }
  }

  final public Observable<T> get(boolean forceUpdate) {
    return buildUseCaseObservable(forceUpdate).compose(applySchedulers());
  }

  /**
   * Builds an {@link rx.Observable} which will be used when executing the current {@link UseCase}.
   */
  public abstract Observable<T> buildUseCaseObservable(boolean forceUpdate);

  private Observable.Transformer<T, T> applySchedulers() {
    return tObservable -> tObservable.subscribeOn(threadExecutor.getScheduler())
        .observeOn(postExecutionThread.getScheduler());
  }

  public void unsubscribe() {
    if (subscription != null && !subscription.isUnsubscribed()) {
      subscription.unsubscribe();
    }
  }

  public boolean isUnsubscribed() {
    return subscription.isUnsubscribed();
  }
}