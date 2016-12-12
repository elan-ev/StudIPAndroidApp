/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.presentation.presenter;

import javax.inject.Inject;
import javax.inject.Named;

import de.elanev.studip.android.app.base.BaseRxLcePresenter;
import de.elanev.studip.android.app.base.UseCase;
import de.elanev.studip.android.app.base.internal.di.PerActivity;
import de.elanev.studip.android.app.courses.presentation.view.CourseUnicensusView;
import rx.Subscriber;
import rx.Subscription;
import timber.log.Timber;

/**
 * @author joern
 */
@PerActivity
public class CourseUnicensusPresenter extends BaseRxLcePresenter<CourseUnicensusView, String> {

  private final UseCase<String> getCensusUrl;
  private Subscription subscription;

  @Inject public CourseUnicensusPresenter(@Named("getCourseCensusUrl") UseCase getCensusUrl) {
    this.getCensusUrl = getCensusUrl;
  }

  @Override protected void unsubscribe() {
    this.subscription.unsubscribe();
  }

  public void getCensusUrl(boolean pullToRefresh) {
    this.subscription = getCensusUrl.get(pullToRefresh)
        .subscribe(new Subscriber<String>() {
          @Override public void onCompleted() {
            CourseUnicensusPresenter.this.onCompleted();
          }

          @Override public void onError(Throwable e) {
            Timber.e(e, e.getLocalizedMessage());
            CourseUnicensusPresenter.this.onError(e, pullToRefresh);
          }

          @Override public void onNext(String s) {
            CourseUnicensusPresenter.this.onNext(s);
          }
        });
  }
}
