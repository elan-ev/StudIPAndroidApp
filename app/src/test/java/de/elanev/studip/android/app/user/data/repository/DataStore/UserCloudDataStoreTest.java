/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.user.data.repository.DataStore;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.elanev.studip.android.app.base.data.net.StudIpLegacyApiService;
import de.elanev.studip.android.app.user.data.entity.UserEntity;
import de.elanev.studip.android.app.user.data.entity.UserEntityUtil;
import rx.Observable;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * @author joern
 */
public class UserCloudDataStoreTest {

  @Mock StudIpLegacyApiService mockApiService;
  private UserCloudDataStore userCloudDataStore;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    userCloudDataStore = new UserCloudDataStore(mockApiService);
  }

  @Test public void userEntity() throws Exception {
    UserEntity userEntity = new UserEntity();
    given(mockApiService.getUserEntity(UserEntityUtil.FAKE_USER_ID)).willReturn(
        Observable.just(userEntity));

    userCloudDataStore.userEntity(UserEntityUtil.FAKE_USER_ID);
    verify(mockApiService).getUserEntity(UserEntityUtil.FAKE_USER_ID);
  }

}