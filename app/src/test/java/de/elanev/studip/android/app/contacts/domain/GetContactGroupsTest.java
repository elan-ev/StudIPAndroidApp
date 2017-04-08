/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.contacts.domain;

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
public class GetContactGroupsTest {
  @Mock private ThreadExecutor mockThreadExecutor;
  @Mock private PostExecutionThread mockPostExecutionThread;
  @Mock private ContactsRepository mockContactsRepository;

  private GetContactGroups getContactGroups;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    getContactGroups = new GetContactGroups(mockContactsRepository, mockThreadExecutor,
        mockPostExecutionThread);
  }

  @Test public void buildUseCaseObservable() throws Exception {
    given(mockThreadExecutor.getScheduler()).willReturn(Schedulers.immediate());
    given(mockPostExecutionThread.getScheduler()).willReturn(Schedulers.immediate());

    getContactGroups.buildUseCaseObservable(true);

    verify(mockContactsRepository).contactGroups();
    verifyNoMoreInteractions(mockContactsRepository);

    verifyZeroInteractions(mockThreadExecutor);
    verifyZeroInteractions(mockPostExecutionThread);
  }

}