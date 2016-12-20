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
import de.elanev.studip.android.app.planner.presentation.mapper.PlanerModelDataMapper;
import de.elanev.studip.android.app.planner.presentation.model.PlanerEventModel;
import de.elanev.studip.android.app.planner.presentation.view.PlannerTimetableView;
import timber.log.Timber;

/**
 * @author joern
 */
@PerActivity
public class PlannerTimetablePresenter extends
    BaseRxLcePresenter<PlannerTimetableView, List<PlanerEventModel>> {

  private final PlanerModelDataMapper planerModelDataMapper;
  private final UseCase getEventsList;

  @Inject public PlannerTimetablePresenter(UseCase getEventsList,
      PlanerModelDataMapper planerModelDataMapper) {
    this.getEventsList = getEventsList;
    this.planerModelDataMapper = planerModelDataMapper;
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
      Timber.e(e, e.getLocalizedMessage());
      PlannerTimetablePresenter.this.onError(e, this.isPullToRefresh());
    }

    @Override public void onNext(List<Event> events) {
      PlannerTimetablePresenter.this.onNext(
          PlannerTimetablePresenter.this.planerModelDataMapper.transform(events));
    }
  }
}
