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
import de.elanev.studip.android.app.planner.presentation.mapper.PlannerModelDataMapper;
import de.elanev.studip.android.app.planner.presentation.model.PlannerEventModel;
import de.elanev.studip.android.app.planner.presentation.view.PlannerListView;
import rx.Subscriber;
import timber.log.Timber;

/**
 * @author joern
 */
@PerActivity
public class PlannerListPresenter extends
    BaseRxLcePresenter<PlannerListView, List<PlannerEventModel>> {
  private final UseCase<List<Event>> getEventsList;
  private final PlannerModelDataMapper plannerModelDataMapper;

  @Inject PlannerListPresenter(UseCase getEventsListUseCase,
      PlannerModelDataMapper plannerModelDataMapper) {
    this.getEventsList = getEventsListUseCase;
    this.plannerModelDataMapper = plannerModelDataMapper;
  }

  public void loadEvents(final boolean ptr) {

    getEventsList.get(ptr)
        .map(plannerModelDataMapper::transform)
        .subscribe(new Subscriber<List<PlannerEventModel>>() {
          @Override public void onCompleted() {
            PlannerListPresenter.this.onCompleted();
          }

          @Override public void onError(Throwable e) {
            Timber.e(e, e.getLocalizedMessage());
            PlannerListPresenter.this.onError(e, ptr);
          }

          @Override public void onNext(List<PlannerEventModel> plannerEventModel) {
            PlannerListPresenter.this.onNext(plannerEventModel);
          }
        });
  }

  @Override protected void unsubscribe() {
    getEventsList.unsubscribe();
  }

  @SuppressWarnings("ConstantConditions") public void onEventClicked(
      PlannerEventModel plannerEventModel) {
    if (isViewAttached()) {
      getView().viewEvent(plannerEventModel);
    }
  }

  @SuppressWarnings("ConstantConditions") public void onEventLongClicked(
      PlannerEventModel plannerEventModel) {
    if (isViewAttached()) {
      getView().addEventToCalendar(plannerEventModel);
    }
  }
}
