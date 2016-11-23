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

import de.elanev.studip.android.app.base.DefaultSubscriber;
import de.elanev.studip.android.app.base.domain.executor.PostExecutionThread;
import de.elanev.studip.android.app.base.domain.executor.ThreadExecutor;
import de.elanev.studip.android.app.user.domain.User;
import rx.schedulers.Schedulers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * @author joern
 */
public class SendMessageTest {
  private SendMessage sendMessage;


  @Mock private MessagesRepository mockMessagesRepository;
  @Mock private ThreadExecutor mockThreadExecutor;
  @Mock private PostExecutionThread mockPostExecutionThread;
  @Mock private DefaultSubscriber<Message> mockSubScriber;
  @Mock private User mockUser;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    sendMessage = new SendMessage(mockMessagesRepository, mockThreadExecutor,
        mockPostExecutionThread);
  }

  @Test public void executeShouldFailIfMessageIsEmpty() throws
      Exception {
    sendMessage.execute(mockSubScriber);
    verify(mockSubScriber).onError(any(IllegalStateException.class));
  }

  @Test public void executeShouldFailIfMessageReceiverIsEmpty() throws
      Exception {
    sendMessage.setMessage(new Message());
    sendMessage.execute(mockSubScriber);
    verify(mockSubScriber).onError(any(IllegalStateException.class));
  }

  @Test public void buildUseCaseObservable() throws Exception {
    given(mockThreadExecutor.getScheduler()).willReturn(Schedulers.immediate());
    given(mockPostExecutionThread.getScheduler()).willReturn(Schedulers.immediate());

    sendMessage.buildUseCaseObservable();

    verify(mockMessagesRepository).send(null);
    verifyNoMoreInteractions(mockMessagesRepository);

    verifyZeroInteractions(mockThreadExecutor);
    verifyZeroInteractions(mockPostExecutionThread);
  }

  @Test public void setMessage() throws Exception {
    Message message = new Message();
    sendMessage.setMessage(message);

    assertThat(sendMessage.getMessage(), is(message));
  }

}