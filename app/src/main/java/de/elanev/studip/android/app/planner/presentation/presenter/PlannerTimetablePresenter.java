/*
 * Copyright (c) 2017 ELAN e.V.
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
import de.elanev.studip.android.app.planner.domain.GetEventsList;
import de.elanev.studip.android.app.planner.presentation.mapper.PlanerModelDataMapper;
import de.elanev.studip.android.app.planner.presentation.model.PlanerEventModel;
import de.elanev.studip.android.app.planner.presentation.view.PlannerTimetableView;
import rx.Subscriber;
import timber.log.Timber;

/**
 * @author joern
 */
@PerActivity
public class PlannerTimetablePresenter extends
    BaseRxLcePresenter<PlannerTimetableView, List<PlanerEventModel>> {

  private final PlanerModelDataMapper planerModelDataMapper;
  private final GetEventsList getEventsList;

  @Inject public PlannerTimetablePresenter(UseCase getEventsList,
      PlanerModelDataMapper planerModelDataMapper) {
    this.getEventsList = (GetEventsList) getEventsList;
    this.planerModelDataMapper = planerModelDataMapper;
  }

  public void loadData(boolean ptr) {
    this.getEventsList.get(ptr)
        .map(planerModelDataMapper::transform)
        .subscribe(new Subscriber<List<PlanerEventModel>>() {
          @Override public void onCompleted() {
            PlannerTimetablePresenter.this.onCompleted();
          }

          @Override public void onError(Throwable e) {
            Timber.e(e, e.getLocalizedMessage());
            PlannerTimetablePresenter.this.onError(e, ptr);
          }

          @Override public void onNext(List<PlanerEventModel> planerEventModel) {
            PlannerTimetablePresenter.this.onNext(planerEventModel);
          }
        });
  }

  @Override protected void unsubscribe() {
    this.getEventsList.unsubscribe();
  }
}
