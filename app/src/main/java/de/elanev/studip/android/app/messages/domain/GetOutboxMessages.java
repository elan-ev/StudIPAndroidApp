/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.messages.domain;

import javax.inject.Inject;

import de.elanev.studip.android.app.base.UseCase;
import de.elanev.studip.android.app.base.domain.executor.PostExecutionThread;
import de.elanev.studip.android.app.base.domain.executor.ThreadExecutor;
import de.elanev.studip.android.app.base.internal.di.PerFragment;
import rx.Observable;

/**
 * @author joern
 */
@PerFragment
public class GetOutboxMessages extends UseCase {
  private final MessagesRepository messagesRepository;

  @Inject GetOutboxMessages(MessagesRepository messagesRepository, ThreadExecutor threadExecutor,
      PostExecutionThread postExecutionThread) {
    super(threadExecutor, postExecutionThread);

    this.messagesRepository = messagesRepository;
  }

  @Override protected Observable buildUseCaseObservable(boolean forceUpdate) {
    return messagesRepository.outboxMessages();
  }
}
