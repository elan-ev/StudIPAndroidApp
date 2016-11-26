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
import de.elanev.studip.android.app.planner.presentation.view.PlannerTimetableView;

/**
 * @author joern
 */
@PerActivity
public class PlannerTimetablePresenter extends
    BaseRxLcePresenter<PlannerTimetableView, List<EventModel>> {

  private final EventsDataMapper eventsDataMapper;
  private final UseCase getEventsList;

  @Inject public PlannerTimetablePresenter(UseCase getEventsList,
      EventsDataMapper eventsDataMapper) {
    this.getEventsList = getEventsList;
    this.eventsDataMapper = eventsDataMapper;
  }

  public void loadData(boolean ptr) {
    this.getEventsList.execute(new PlannerTimetableSubscriber(ptr));
  }

  @Override protected void unsubscribe() {
    this.getEventsList.unsubscribe();
  }

  private final class PlannerTimetableSubscriber extends DefaultSubscriber<List<Event>> {

    public PlannerTimetableSubscriber(boolean ptr) {
      super(ptr);
    }

    @Override public void onCompleted() {
      PlannerTimetablePresenter.this.onCompleted();
    }

    @Override public void onError(Throwable e) {
      PlannerTimetablePresenter.this.onError(e, this.isPullToRefresh());
    }

    @Override public void onNext(List<Event> events) {
      PlannerTimetablePresenter.this.onNext(
          PlannerTimetablePresenter.this.eventsDataMapper.transform(events));
    }
  }
}
