/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.user.data.repository.DataStore;

import de.elanev.studip.android.app.user.data.entity.UserEntity;
import rx.Observable;

/**
 * @author joern
 */
public interface UserDataStore {
  Observable<UserEntity> userEntity(String id);
}
