/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.messages.internal.di;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import de.elanev.studip.android.app.base.UseCase;
import de.elanev.studip.android.app.base.domain.executor.PostExecutionThread;
import de.elanev.studip.android.app.base.domain.executor.ThreadExecutor;
import de.elanev.studip.android.app.base.internal.di.PerActivity;
import de.elanev.studip.android.app.base.internal.di.PerFragment;
import de.elanev.studip.android.app.messages.domain.GetInboxMessages;
import de.elanev.studip.android.app.messages.domain.GetMessageDetails;
import de.elanev.studip.android.app.messages.domain.GetOutboxMessages;
import de.elanev.studip.android.app.messages.domain.MessagesRepository;

/**
 * @author joern
 */
@Module
public class MessagesModule {
  private String messageId = "";

  public MessagesModule() {}

  public MessagesModule(String messageId) {
    this.messageId = messageId;
  }

  @Provides @PerFragment @Named("inbox") UseCase providesInboxMessagesUseCase(
      GetInboxMessages getInboxMessages) {

    return getInboxMessages;
  }

  @Provides @PerFragment @Named("outbox") UseCase providesOutboxMessagesUseCase(
      GetOutboxMessages getOutboxMessages) {

    return getOutboxMessages;
  }

  @Provides @PerFragment @Named("messageDetails") UseCase providesGetNewsDetailsUseCase(
      MessagesRepository messagesRepository, ThreadExecutor threadExecutor,
      PostExecutionThread postExecutionThread) {

    return new GetMessageDetails(messageId, messagesRepository, threadExecutor,
        postExecutionThread);
  }

  @Provides @PerFragment @Named("deleteMessage") UseCase providesDeleteMessageUseCase(
      MessagesRepository messagesRepository, ThreadExecutor threadExecutor,
      PostExecutionThread postExecutionThread) {

    return new GetMessageDetails(messageId, messagesRepository, threadExecutor,
        postExecutionThread);
  }
}
