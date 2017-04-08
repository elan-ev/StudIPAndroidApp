/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.user.data.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.elanev.studip.android.app.user.data.entity.UserEntity;
import de.elanev.studip.android.app.user.data.entity.UserEntityDataMapper;
import de.elanev.studip.android.app.user.data.entity.UserEntityUtil;
import de.elanev.studip.android.app.user.data.repository.DataStore.UserCloudDataStore;
import de.elanev.studip.android.app.user.data.repository.DataStore.UserDataStore;
import de.elanev.studip.android.app.user.data.repository.DataStore.UserRealmDataStore;
import rx.Observable;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * @author joern
 */
public class UserDataRepositoryTest {
  @Mock UserCloudDataStore mockUserCloudDataStore;
  @Mock UserEntityDataMapper mockUserDataMapper;
  @Mock UserRealmDataStore mockUserRealmDataStore;
  @Mock UserDataStore mockUserDataStore;
  private UserDataRepository userDataRepository;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    userDataRepository = new UserDataRepository(mockUserDataMapper, mockUserCloudDataStore,
        mockUserRealmDataStore);
  }

  @Test public void shouldReturnUserOnValidUserId() throws Exception {
    UserEntity userEntity = UserEntityUtil.createFakeUserEntity();
    given(mockUserCloudDataStore.userEntity(UserEntityUtil.FAKE_USER_ID)).willReturn(
        Observable.just(userEntity));
    given(mockUserRealmDataStore.userEntity(UserEntityUtil.FAKE_USER_ID)).willReturn(
        Observable.just(userEntity));

    userDataRepository.user(UserEntityUtil.FAKE_USER_ID, false);

    verify(mockUserCloudDataStore).userEntity(UserEntityUtil.FAKE_USER_ID);
    verify(mockUserRealmDataStore).userEntity(UserEntityUtil.FAKE_USER_ID);
  }

}