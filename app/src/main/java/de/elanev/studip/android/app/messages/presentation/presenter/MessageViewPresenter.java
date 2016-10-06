/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.messages.presentation.presenter;

import javax.inject.Inject;
import javax.inject.Named;

import de.elanev.studip.android.app.base.BaseRxLcePresenter;
import de.elanev.studip.android.app.base.DefaultSubscriber;
import de.elanev.studip.android.app.base.UseCase;
import de.elanev.studip.android.app.base.internal.di.PerActivity;
import de.elanev.studip.android.app.base.internal.di.PerFragment;
import de.elanev.studip.android.app.messages.domain.Message;
import de.elanev.studip.android.app.messages.presentation.mapper.MessagesDataMapper;
import de.elanev.studip.android.app.messages.presentation.model.MessageModel;
import de.elanev.studip.android.app.messages.presentation.view.MessageView;

/**
 * @author joern
 */
@PerFragment
public class MessageViewPresenter extends BaseRxLcePresenter<MessageView, MessageModel> {

  private final UseCase getMessageDetails;
  private final MessagesDataMapper messagesDataMapper;
  private final UseCase deleteMessage;

  @Inject public MessageViewPresenter(@Named("messageDetails") UseCase getMessageDetails,
      @Named("deleteMessage") UseCase deleteMessageUseCase, MessagesDataMapper messagesDataMapper) {
    this.getMessageDetails = getMessageDetails;
    this.deleteMessage = deleteMessageUseCase;
    this.messagesDataMapper = messagesDataMapper;
  }

  @Override protected void unsubscribe() {
    getMessageDetails.unsubscribe();
  }

  public void loadNews() {
    getMessageDetails.execute(new MessageDetailsSubscriber(false));
  }

  public void deleteMessage() {
    deleteMessage.execute(new MessageDeleteSubscriber(false));
  }

  private class MessageDeleteSubscriber extends DefaultSubscriber<Void> {
    MessageDeleteSubscriber(boolean ptr) {
      super(ptr);
    }

    @Override public void onCompleted() {
      MessageViewPresenter.this.onCompleted();
    }

    @Override public void onError(Throwable e) {
      MessageViewPresenter.this.onError(e, ptr);
    }

    @Override public void onNext(Void aVoid) {}
  }

  private class MessageDetailsSubscriber extends DefaultSubscriber<Message> {
    MessageDetailsSubscriber(boolean ptr) {
      super(ptr);
    }

    @Override public void onCompleted() {
      MessageViewPresenter.this.onCompleted();
    }

    @Override public void onError(Throwable e) {
      MessageViewPresenter.this.onError(e, ptr);
    }

    @Override public void onNext(Message message) {
      MessageViewPresenter.this.onNext(
          MessageViewPresenter.this.messagesDataMapper.transform(message));
    }
  }
}
