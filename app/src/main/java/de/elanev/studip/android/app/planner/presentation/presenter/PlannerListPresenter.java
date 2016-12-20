/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.planner.presentation.presenter;

import java.util.List;

import javax.inject.Inject;

import de.elanev.studip.android.app.base.BaseRxLcePresenter;
import de.elanev.studip.android.app.base.UseCase;
import de.elanev.studip.android.app.base.internal.di.PerActivity;
import de.elanev.studip.android.app.planner.domain.Event;
import de.elanev.studip.android.app.planner.presentation.mapper.PlanerModelDataMapper;
import de.elanev.studip.android.app.planner.presentation.model.PlanerEventModel;
import de.elanev.studip.android.app.planner.presentation.view.PlannerListView;
import rx.Subscriber;
import timber.log.Timber;

/**
 * @author joern
 */
@PerActivity
public class PlannerListPresenter extends
    BaseRxLcePresenter<PlannerListView, List<PlanerEventModel>> {
  private final UseCase<List<Event>> getEventsList;
  private final PlanerModelDataMapper planerModelDataMapper;

  @Inject PlannerListPresenter(UseCase getEventsListUseCase,
      PlanerModelDataMapper planerModelDataMapper) {
    this.getEventsList = getEventsListUseCase;
    this.planerModelDataMapper = planerModelDataMapper;
  }

  public void loadEvents(final boolean ptr) {

    getEventsList.get(ptr)
        .map(planerModelDataMapper::transform)
        .subscribe(new Subscriber<List<PlanerEventModel>>() {
          @Override public void onCompleted() {
            PlannerListPresenter.this.onCompleted();
          }

          @Override public void onError(Throwable e) {
            Timber.e(e, e.getLocalizedMessage());
            PlannerListPresenter.this.onError(e, ptr);
          }

          @Override public void onNext(List<PlanerEventModel> planerEventModel) {
            PlannerListPresenter.this.onNext(planerEventModel);
          }
        });
  }

  @Override protected void unsubscribe() {
    getEventsList.unsubscribe();
  }

  @SuppressWarnings("ConstantConditions") public void onEventClicked(
      PlanerEventModel planerEventModel) {
    if (isViewAttached()) {
      getView().viewEvent(planerEventModel);
    }
  }

  @SuppressWarnings("ConstantConditions") public void onEventLongClicked(
      PlanerEventModel planerEventModel) {
    if (isViewAttached()) {
      getView().addEventToCalendar(planerEventModel);
    }
  }
}
