/*
 * Copyright (c) 2015 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.widget;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.lucasr.twowayview.widget.TwoWayView;

import de.elanev.studip.android.app.backend.net.services.StudIpLegacyApiService;
import rx.Observable;
import rx.android.app.AppObservable;
import rx.android.lifecycle.LifecycleEvent;
import rx.android.lifecycle.LifecycleObservable;
import rx.subjects.BehaviorSubject;
import rx.subscriptions.CompositeSubscription;

import static rx.android.schedulers.AndroidSchedulers.mainThread;

/**
 * @author joern
 */
public class ReactiveFragment extends Fragment {
  private static final String TAG = ReactiveListFragment.class.getSimpleName();
  protected final CompositeSubscription mCompositeSubscription = new CompositeSubscription();
  private final BehaviorSubject<LifecycleEvent> lifecycleSubject = BehaviorSubject.create();
  protected TwoWayView mRecyclerView;
  protected TextView mEmptyView;
  protected SwipeRefreshLayout mSwipeRefreshLayout;
  protected RecyclerView.AdapterDataObserver mObserver;
  protected StudIpLegacyApiService mApiService;
  protected boolean mRecreated = false;
  private String mTitle;

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);

    lifecycleSubject.onNext(LifecycleEvent.ATTACH);
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    lifecycleSubject.onNext(LifecycleEvent.CREATE);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    lifecycleSubject.onNext(LifecycleEvent.CREATE_VIEW);
  }

  @Override public void onStart() {
    super.onStart();

    lifecycleSubject.onNext(LifecycleEvent.PAUSE);
  }

  @Override public void onResume() {
    super.onResume();

    lifecycleSubject.onNext(LifecycleEvent.RESUME);
  }

  @Override public void onPause() {
    lifecycleSubject.onNext(LifecycleEvent.PAUSE);

    super.onPause();
  }

  @Override public void onStop() {
    lifecycleSubject.onNext(LifecycleEvent.STOP);

    super.onStop();
  }

  @Override public void onDestroyView() {
    lifecycleSubject.onNext(LifecycleEvent.DESTROY_VIEW);

    super.onDestroyView();
  }

  @Override public void onDestroy() {
    lifecycleSubject.onNext(LifecycleEvent.DESTROY);

    super.onDestroy();
  }

  @Override public void onDetach() {
    lifecycleSubject.onNext(LifecycleEvent.DETACH);

    super.onDetach();
  }

  protected <T> Observable<T> bind(Observable<T> observable) {
    Observable<T> boundObservable = AppObservable.bindSupportFragment(this, observable)
        .observeOn(mainThread());
    return LifecycleObservable.bindFragmentLifecycle(lifecycle(), boundObservable);
  }

  private Observable<LifecycleEvent> lifecycle() {
    return lifecycleSubject.asObservable();
  }
}
