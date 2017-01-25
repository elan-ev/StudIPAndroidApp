/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.data.repository.datastore;

import java.util.List;

import de.elanev.studip.android.app.authorization.data.entity.EndpointEntity;
import rx.Observable;

/**
 * @author joern
 */

public interface EndpointsDataStore {
  Observable<List<EndpointEntity>> getEndpoints();

  Observable<EndpointEntity> getEndpoint(String endpointId);
}
