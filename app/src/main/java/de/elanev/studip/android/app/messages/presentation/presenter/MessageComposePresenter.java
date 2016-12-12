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
import de.elanev.studip.android.app.base.internal.di.PerFragment;
import de.elanev.studip.android.app.messages.domain.Message;
import de.elanev.studip.android.app.messages.domain.SendMessage;
import de.elanev.studip.android.app.messages.presentation.mapper.MessagesDataMapper;
import de.elanev.studip.android.app.messages.presentation.model.MessageModel;
import de.elanev.studip.android.app.messages.presentation.view.MessageComposeView;
import de.elanev.studip.android.app.user.presentation.model.UserModel;

/**
 * @author joern
 */
@PerFragment
public class MessageComposePresenter extends BaseRxLcePresenter<MessageComposeView, MessageModel> {
  private final SendMessage sendMessagesUseCase;
  private final UseCase getMessageUseCase;
  private final MessagesDataMapper dataMapper;

  @Inject MessageComposePresenter(@Named("sendMessage") UseCase sendMessage,
      @Named("messageDetails") UseCase getMessageUseCase, MessagesDataMapper messagesDataMapper) {
    this.sendMessagesUseCase = (SendMessage) sendMessage;
    this.getMessageUseCase = getMessageUseCase;
    this.dataMapper = messagesDataMapper;
  }

  @Override protected void unsubscribe() {
    sendMessagesUseCase.unsubscribe();
  }

  public void send() {
    MessageModel messageModel = createMessageModel();
    this.sendMessagesUseCase.setMessage(dataMapper.transform(messageModel));
    this.sendMessagesUseCase.execute(new MessageSendSubscriber(false));
  }

  @SuppressWarnings("ConstantConditions") private MessageModel createMessageModel() {
    MessageModel messageModel = new MessageModel();

    if (isViewAttached()) {
      String receiverId = getView().getReceiverId();
      String subject = getView().getSubject();
      String message = getView().getMessage();


      UserModel receiver = new UserModel();
      receiver.setUserId(receiverId);

      messageModel.setReceiver(receiver);
      messageModel.setSubject(subject);
      messageModel.setMessage(message);
    }

    return messageModel;
  }

  public void load() {
    this.getMessageUseCase.execute(new MessageGetSubscriber(false));
  }

  @SuppressWarnings("ConstantConditions") private void sendOnComplete() {
    if (isViewAttached()) {
      getView().messageSend();
    }
  }

  private class MessageGetSubscriber extends DefaultSubscriber<Message> {

    MessageGetSubscriber(boolean ptr) {
      super(ptr);
    }

    @Override public void onCompleted() {
      MessageComposePresenter.this.onCompleted();
    }

    @Override public void onError(Throwable e) {
      MessageComposePresenter.this.onError(e, this.isPullToRefresh());
    }

    @Override public void onNext(Message message) {
      MessageComposePresenter.this.onNext(dataMapper.transform(message));
    }
  }

  private class MessageSendSubscriber extends DefaultSubscriber<Message> {

    MessageSendSubscriber(boolean ptr) {
      super(ptr);
    }

    @Override public void onCompleted() {
      MessageComposePresenter.this.sendOnComplete();
    }

    @Override public void onError(Throwable e) {
      MessageComposePresenter.this.onError(e, this.isPullToRefresh());
    }

    @Override public void onNext(Message message) {}
  }
}
