/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.presentation.mapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.elanev.studip.android.app.authorization.domain.model.Endpoint;
import de.elanev.studip.android.app.authorization.presentation.model.EndpointModel;
import de.elanev.studip.android.app.base.internal.di.PerActivity;

/**
 * @author joern
 */
@PerActivity
public class AuthModelDataMapper {
  @Inject public AuthModelDataMapper() {}

  public List<EndpointModel> transform(List<Endpoint> endpoints) {
    List<EndpointModel> endpointModels = new ArrayList<>();

    for (Endpoint endpoint : endpoints) {
      endpointModels.add(transform(endpoint));
    }

    return endpointModels;
  }

  private EndpointModel transform(Endpoint endpoint) {
    EndpointModel endpointModel = new EndpointModel();
    endpointModel.setId(endpoint.getId());
    endpointModel.setName(endpoint.getName());
    endpointModel.setConsumerKey(endpoint.getConsumerKey());
    endpointModel.setConsumerSecret(endpoint.getConsumerSecret());
    endpointModel.setBaseUrl(endpoint.getBaseUrl());
    endpointModel.setContactEmail(endpoint.getContactEmail());
    endpointModel.setIconRes(endpoint.getIconRes());

    return endpointModel;
  }
}
