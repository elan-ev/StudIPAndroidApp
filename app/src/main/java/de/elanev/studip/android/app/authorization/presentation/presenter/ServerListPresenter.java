/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.presentation.presenter;

import java.util.List;

import javax.inject.Inject;

import de.elanev.studip.android.app.authorization.domain.usecase.GetEndpointsList;
import de.elanev.studip.android.app.authorization.presentation.mapper.AuthModelDataMapper;
import de.elanev.studip.android.app.authorization.presentation.model.EndpointModel;
import de.elanev.studip.android.app.authorization.presentation.view.ServerListView;
import de.elanev.studip.android.app.base.BaseRxLcePresenter;
import de.elanev.studip.android.app.base.internal.di.PerActivity;
import rx.Subscriber;

/**
 * @author joern
 */
@PerActivity
public class ServerListPresenter extends BaseRxLcePresenter<ServerListView, List<EndpointModel>> {
  private final GetEndpointsList getEndpointsList;
  private final AuthModelDataMapper dataMapper;

  @Inject public ServerListPresenter(GetEndpointsList getEndpointsList, AuthModelDataMapper
      dataMapper) {
    this.getEndpointsList = getEndpointsList;
    this.dataMapper = dataMapper;
  }

  public void loadEndpoints(final boolean pullToRefresh) {
    this.getEndpointsList.get(pullToRefresh)
        .map(dataMapper::transform)
        .subscribe(new Subscriber<List<EndpointModel>>() {
          @Override public void onCompleted() {
            ServerListPresenter.this.onCompleted();
          }

          @Override public void onError(Throwable e) {
            ServerListPresenter.this.onError(e, pullToRefresh);
          }

          @Override public void onNext(List<EndpointModel> endpointModels) {
            ServerListPresenter.this.onNext(endpointModels);
          }
        });
  }

  @Override protected void unsubscribe() {
    getEndpointsList.unsubscribe();
  }

  public void onEndpointClicked(EndpointModel endpointModel) {
    if (isViewAttached()) {
      getView().signInTo(endpointModel);
    }
  }
}
