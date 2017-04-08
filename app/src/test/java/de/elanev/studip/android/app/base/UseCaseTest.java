/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.base;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import de.elanev.studip.android.app.base.domain.executor.PostExecutionThread;
import de.elanev.studip.android.app.base.domain.executor.ThreadExecutor;
import rx.Observable;
import rx.Subscriber;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;

/**
 * @author joern
 */
public class UseCaseTest {
  @Mock PostExecutionThread mockPostExecutionThread;
  @Mock private ThreadExecutor mockThreadExecutor;
  private UseCaseTestClass userCase;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    this.userCase = new UseCaseTestClass(mockThreadExecutor, mockPostExecutionThread);
  }

  @Test public void buildUseCaseObservable() throws Exception {
    given(mockThreadExecutor.getScheduler()).willReturn(Schedulers.immediate());
    given(mockPostExecutionThread.getScheduler()).willReturn(Schedulers.immediate());

    TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();

    userCase.execute(testSubscriber);
    testSubscriber.awaitTerminalEvent();
    testSubscriber.assertReceivedOnNext(Arrays.asList(1, 2, 3));
  }

  @Test public void execute() throws Exception {
    given(mockThreadExecutor.getScheduler()).willReturn(Schedulers.immediate());
    given(mockPostExecutionThread.getScheduler()).willReturn(Schedulers.immediate());

    TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();

    userCase.execute(testSubscriber);

    assertThat(userCase.isUnsubscribed(), is(false));
  }

  @Test public void unsubscribe() throws Exception {
    given(mockThreadExecutor.getScheduler()).willReturn(Schedulers.immediate());
    given(mockPostExecutionThread.getScheduler()).willReturn(Schedulers.immediate());

    TestSubscriber<Integer> testSubscriber = new TestSubscriber<>();

    userCase.execute(testSubscriber);
    userCase.unsubscribe();

    assertThat(userCase.isUnsubscribed(), is(true));
  }

  private static class UseCaseTestClass extends UseCase<Integer> {

    UseCaseTestClass(ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
      super(threadExecutor, postExecutionThread);
    }

    @Override public void execute(Subscriber<Integer> subscriber) {
      super.execute(subscriber);
    }

    @Override public Observable<Integer> buildUseCaseObservable(boolean forceUpdate) {
      return Observable.just(1, 2, 3)
          .delay(10, TimeUnit.MILLISECONDS);
    }
  }
}