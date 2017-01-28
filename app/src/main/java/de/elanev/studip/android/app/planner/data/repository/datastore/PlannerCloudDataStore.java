/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.planner.data.repository.datastore;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.base.data.net.StudIpLegacyApiService;
import de.elanev.studip.android.app.planner.data.entity.EventEntity;
import rx.Observable;

/**
 * @author joern
 */
@Singleton
public class PlannerCloudDataStore implements PlannerDataStore {
  private final StudIpLegacyApiService apiService;

  @Inject PlannerCloudDataStore(StudIpLegacyApiService apiService) {
    this.apiService = apiService;
  }

  @Override public Observable<List<EventEntity>> eventEntityList() {
    return apiService.getEvents();
  }
}
