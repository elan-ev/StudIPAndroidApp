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
import de.elanev.studip.android.app.base.DefaultSubscriber;
import de.elanev.studip.android.app.base.UseCase;
import de.elanev.studip.android.app.base.internal.di.PerActivity;
import de.elanev.studip.android.app.planner.domain.Event;
import de.elanev.studip.android.app.planner.presentation.mapper.EventsDataMapper;
import de.elanev.studip.android.app.planner.presentation.model.EventModel;
import de.elanev.studip.android.app.planner.presentation.view.PlannerListView;

/**
 * @author joern
 */
@PerActivity
public class PlannerListPresenter extends BaseRxLcePresenter<PlannerListView, List<EventModel>> {
  private final UseCase getEventsList;
  private final EventsDataMapper eventsDataMapper;

  @Inject PlannerListPresenter(UseCase getEventsListUseCase, EventsDataMapper eventsDataMapper) {
    this.getEventsList = getEventsListUseCase;
    this.eventsDataMapper = eventsDataMapper;
  }

  public void loadEvents(boolean ptr) {
    getEventsList.execute(new PlannerListSubscriber(ptr));
  }

  @Override protected void unsubscribe() {
    getEventsList.unsubscribe();
  }

  @SuppressWarnings("ConstantConditions") public void onEventClicked(EventModel eventModel) {
    if (isViewAttached()) {
      getView().viewEvent(eventModel);
    }
  }

  private final class PlannerListSubscriber extends DefaultSubscriber<List<Event>> {
    PlannerListSubscriber(boolean ptr) {
      super(ptr);
    }

    @Override public void onCompleted() {
      PlannerListPresenter.this.onCompleted();
    }

    @Override public void onError(Throwable e) {
      PlannerListPresenter.this.onError(e, this.isPullToRefresh());
    }

    @Override public void onNext(List<Event> events) {
      PlannerListPresenter.this.onNext(PlannerListPresenter.this.eventsDataMapper.transform(events));
    }
  }
}
