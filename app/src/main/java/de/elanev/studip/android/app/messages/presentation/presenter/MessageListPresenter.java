/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.messages.presentation.presenter;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import de.elanev.studip.android.app.StudIPConstants;
import de.elanev.studip.android.app.base.BaseRxLcePresenter;
import de.elanev.studip.android.app.base.DefaultSubscriber;
import de.elanev.studip.android.app.base.UseCase;
import de.elanev.studip.android.app.base.internal.di.PerFragment;
import de.elanev.studip.android.app.messages.domain.Message;
import de.elanev.studip.android.app.messages.presentation.mapper.MessagesDataMapper;
import de.elanev.studip.android.app.messages.presentation.model.MessageModel;
import de.elanev.studip.android.app.messages.presentation.view.MessageListView;
import timber.log.Timber;

/**
 * @author joern
 */
@PerFragment
public class MessageListPresenter extends BaseRxLcePresenter<MessageListView, List<MessageModel>> {
  private final UseCase getInboxMessages;
  private final UseCase getOutboxMessages;
  private final MessagesDataMapper dataMapper;

  @Inject public MessageListPresenter(@Named("inbox") UseCase getInboxMessages,
      @Named("outbox") UseCase getOutboxMessages, MessagesDataMapper messagesDataMapper) {
    this.getInboxMessages = getInboxMessages;
    this.getOutboxMessages = getOutboxMessages;
    this.dataMapper = messagesDataMapper;
  }

  public void loadMessages(boolean ptr, String box) {
    if (box.equals(StudIPConstants.STUDIP_MESSAGES_INBOX_IDENTIFIER)) {
      this.getInboxMessages.execute(new MessagesListSubscriber(ptr));
    } else {
      this.getOutboxMessages.execute(new MessagesListSubscriber(ptr));
    }
  }

  @Override protected void unsubscribe() {
    getInboxMessages.unsubscribe();
    getOutboxMessages.unsubscribe();
  }

  public void onMessageClicked(MessageModel messageModel) {
    if (isViewAttached()) {
      getView().viewMessage(messageModel);
    }
  }

  public void loadNext() {
    Timber.d("Loading next!");
  }

  private final class MessagesListSubscriber extends DefaultSubscriber<List<Message>> {
    MessagesListSubscriber(boolean ptr) {
      super(ptr);
    }

    @Override public void onCompleted() {
      MessageListPresenter.this.onCompleted();
    }

    @Override public void onError(Throwable e) {
      MessageListPresenter.this.onError(e, this.isPullToRefresh());
    }

    @Override public void onNext(List<Message> messages) {
      MessageListPresenter.this.onNext(MessageListPresenter.this.dataMapper.transform(messages));
    }
  }
}
