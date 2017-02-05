/*
 * Copyright (c) 2017 ELAN e.V.
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
import de.elanev.studip.android.app.base.internal.di.PerActivity;
import de.elanev.studip.android.app.util.TextTools;
import rx.Observable;

/**
 * @author joern
 */
@PerActivity
public class GetMessageDetails extends UseCase {

  private final String messageId;
  private final MessagesRepository messageRepository;

  @Inject public GetMessageDetails(String messageId, MessagesRepository messagesRepository,
      ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
    super(threadExecutor, postExecutionThread);

    this.messageId = messageId;
    this.messageRepository = messagesRepository;
  }

  @Override public Observable buildUseCaseObservable(boolean forceUpdate) {
    return messageRepository.message(messageId);
  }

  public boolean hasId() {
    return !TextTools.isEmpty(messageId);
  }
}
