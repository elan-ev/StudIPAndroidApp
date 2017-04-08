/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.messages.domain;

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
public class GetMessageDetailsTest {
  private static final String FAKE_ID = "123";
  private GetMessageDetails getMessageDetails;

  @Mock private MessagesRepository mockMessagesRepository;
  @Mock private ThreadExecutor mockThreadExecutor;
  @Mock private PostExecutionThread mockPostExecutionThread;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    getMessageDetails = new GetMessageDetails(FAKE_ID, mockMessagesRepository, mockThreadExecutor,
        mockPostExecutionThread);
  }

  @Test public void buildUseCaseObservable() throws Exception {
    given(mockThreadExecutor.getScheduler()).willReturn(Schedulers.immediate());
    given(mockPostExecutionThread.getScheduler()).willReturn(Schedulers.immediate());

    getMessageDetails.buildUseCaseObservable(true);

    verify(mockMessagesRepository).message(FAKE_ID);
    verifyNoMoreInteractions(mockMessagesRepository);

    verifyZeroInteractions(mockThreadExecutor);
    verifyZeroInteractions(mockPostExecutionThread);
  }

}