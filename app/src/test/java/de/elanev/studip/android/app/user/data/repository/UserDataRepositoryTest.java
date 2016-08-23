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
import de.elanev.studip.android.app.user.data.repository.DataStore.UserDataStore;
import de.elanev.studip.android.app.user.data.repository.DataStore.UserDataStoreFactory;
import rx.Observable;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * @author joern
 */
public class UserDataRepositoryTest {
  @Mock UserDataStoreFactory mockUserDataStoreFactory;
  @Mock UserEntityDataMapper mockUserDataMapper;
  @Mock UserDataStore mockUserDataStore;
  private UserDataRepository userDataRepository;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    given(mockUserDataStoreFactory.create()).willReturn(mockUserDataStore);

    userDataRepository = new UserDataRepository(mockUserDataMapper, mockUserDataStoreFactory);
  }

  @Test public void shouldReturnUserOnValidUserId() throws Exception {
    UserEntity userEntity = new UserEntity();
    given(mockUserDataStore.userEntity(UserEntityUtil.FAKE_USER_ID)).willReturn(
        Observable.just(userEntity));

    userDataRepository.user(UserEntityUtil.FAKE_USER_ID);

    verify(mockUserDataStoreFactory).create();
    verify(mockUserDataStore).userEntity(UserEntityUtil.FAKE_USER_ID);
  }

}