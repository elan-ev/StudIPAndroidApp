/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.user.domain;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.elanev.studip.android.app.base.domain.executor.PostExecutionThread;
import de.elanev.studip.android.app.base.domain.executor.ThreadExecutor;
import rx.schedulers.Schedulers;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * @author joern
 */
public class GetUserDetailsTest {
  private static final String FAKE_USER_ID = "123";
  @Mock PostExecutionThread mockPostExecutionThread;
  @Mock UserRepository mockUserRepository;
  @Mock ThreadExecutor mockThreadExecutor;
  private GetUserDetails getUserDetails;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    getUserDetails = new GetUserDetails(FAKE_USER_ID, mockUserRepository, mockThreadExecutor,
        mockPostExecutionThread);
  }

  @Test public void buildUseCaseObservable() throws Exception {
    given(mockPostExecutionThread.getScheduler()).willReturn(Schedulers.immediate());
    given(mockThreadExecutor.getScheduler()).willReturn(Schedulers.immediate());

    getUserDetails.buildUseCaseObservable();

    verify(mockUserRepository).user(FAKE_USER_ID);
    verifyNoMoreInteractions(mockUserRepository);

    verifyZeroInteractions(mockThreadExecutor);
    verifyZeroInteractions(mockPostExecutionThread);
  }

}