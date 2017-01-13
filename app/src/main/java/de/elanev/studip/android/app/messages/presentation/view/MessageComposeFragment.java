/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.messages.presentation.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ScrollView;

import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.RetainingLceViewState;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.elanev.studip.android.app.AbstractStudIPApplication;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.base.presentation.view.BaseLceFragment;
import de.elanev.studip.android.app.messages.internal.di.DaggerMessagesComponent;
import de.elanev.studip.android.app.messages.internal.di.MessagesComponent;
import de.elanev.studip.android.app.messages.internal.di.MessagesModule;
import de.elanev.studip.android.app.messages.presentation.model.MessageModel;
import de.elanev.studip.android.app.messages.presentation.presenter.MessageComposePresenter;
import de.elanev.studip.android.app.user.presentation.model.UserModel;
import de.elanev.studip.android.app.util.DateTools;

/**
 * @author joern
 */

public class MessageComposeFragment extends
    BaseLceFragment<ScrollView, MessageModel, MessageComposeView, MessageComposePresenter> implements
    MessageComposeView {

  static final String MESSAGE = "message";

  @Inject MessageComposePresenter presenter;
  @BindView(R.id.message_subject) EditText mSubjectEditText;
  @BindView(R.id.message_body) EditText mBodyEditText;
  @BindView(R.id.message_receiver) AutoCompleteTextView mAutoCompleteTextView;
  @BindView(R.id.message_receiver_text_input_layout) TextInputLayout mReceiverTextInputLayout;
  @BindView(R.id.message_subject_text_input_layout) TextInputLayout mSubjectTextInputLayout;
  @BindView(R.id.message_body_text_input_layout) TextInputLayout mBodyTextInputLayout;

  private MessageModel message;
  private MessagesComponent messagesComponent;
  private MessageComposeListener messageComposeListener;

  public MessageComposeFragment() {
    setRetainInstance(true);
  }

  public static MessageComposeFragment newInstance(Bundle extras) {
    MessageComposeFragment fragment = new MessageComposeFragment();
    fragment.setArguments(extras);

    return fragment;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Bundle arguments = getArguments();
    if (arguments == null) {
      return;
    }

    message = (MessageModel) arguments.getSerializable(MESSAGE);
    if (message == null) {
      throw new IllegalStateException("Message must not be null!");
    }
    String messageId = message.getMessageId();

    initInjector(messageId);
    messagesComponent.inject(this);

    setHasOptionsMenu(true);
  }

  private void initInjector(String messageId) {
    if (!TextUtils.isEmpty(messageId)) {
      this.messagesComponent = DaggerMessagesComponent.builder()
          .applicationComponent(
              ((AbstractStudIPApplication) getActivity().getApplication()).getAppComponent())
          .messagesModule(new MessagesModule(messageId))
          .build();
    } else {
      this.messagesComponent = DaggerMessagesComponent.builder()
          .applicationComponent(
              ((AbstractStudIPApplication) getActivity().getApplication()).getAppComponent())
          .messagesModule(new MessagesModule())
          .build();
    }
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater,
      @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_message_compose, container, false);
    ButterKnife.bind(this, v);

    return v;
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.message_compose_menu, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      // Respond to send button
      case R.id.send_icon:
        if (validateFormFields()) {
          sendMessage();
        }
        return true;
    }

    return super.onOptionsItemSelected(item);
  }

  public boolean validateFormFields() {// Check if all fields are filled
    Object user = mAutoCompleteTextView.getTag();
    boolean isValid = true;

    if (user == null || TextUtils.isEmpty(mAutoCompleteTextView.getText())) {
      mReceiverTextInputLayout.setError(getString(R.string.select_valid_user));
      isValid = false;
    }

    if (TextUtils.isEmpty(mSubjectEditText.getText())) {
      mSubjectTextInputLayout.setError(getString(R.string.enter_subject));
      isValid = false;
    }

    if (TextUtils.isEmpty(mBodyEditText.getText())) {
      mBodyTextInputLayout.setError(getString(R.string.enter_message));
      isValid = false;
    }

    return isValid;
  }

  private void sendMessage() {
    this.presenter.send();
  }

  @NonNull @Override public LceViewState<MessageModel, MessageComposeView> createViewState() {
    return new RetainingLceViewState<>();
  }

  @Override public void showContent() {
    super.showContent();

    fillFields();
  }

  private void fillFields() {
    if (this.message != null) {
      UserModel receiver = this.message.getReceiver();
      UserModel originalSender = this.message.getSender();

      if (!TextUtils.isEmpty(message.getSubject()))
        mSubjectEditText.setText(formatSubject(message));
      if (!TextUtils.isEmpty(message.getMessage()))
        mBodyEditText.setText(formatMessage(message, originalSender));

      if (receiver != null) {
        mReceiverTextInputLayout.setEnabled(false);
        mAutoCompleteTextView.setText(receiver.getFullName());
        mAutoCompleteTextView.setTag(receiver);
        mAutoCompleteTextView.setEnabled(false);
      }
    }
  }

  private String formatSubject(MessageModel message) {
    String subject;

    if (!TextUtils.isEmpty(message.getSubject())) {
      subject = String.format("%s: %s", getString(R.string.message_reply_string),
          message.getSubject());
    } else {
      subject = message.getSubject();
    }

    return subject;
  }

  private String formatMessage(MessageModel message, UserModel sender) {
    StringBuilder messageBodyBuilder = new StringBuilder();
    String formattedDate = DateTools.getShortLocalizedTime(message.getDate(), getContext());
    String quotationString = "";

    if (sender != null) {
      String senderName = sender.getFullName();
      String quotationFormat = getString(R.string.message_quotation_with_sender);
      quotationString = String.format(quotationFormat, senderName, formattedDate);
    } else {
      String quotationFormat = getString(R.string.message_quotation_without_sender);
      quotationString = String.format(quotationFormat, formattedDate);
    }

    messageBodyBuilder.append(quotationString)
        .append("\n")
        .append(message.getMessage());

    return messageBodyBuilder.toString();
  }

  @Override public MessageModel getData() {
    return this.message;
  }

  @Override public void setData(MessageModel data) {
    this.message = data;
  }

  @Override public void loadData(boolean pullToRefresh) {
    this.presenter.load();
  }

  @Override protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
    return e.getLocalizedMessage();
  }

  @NonNull @Override public MessageComposePresenter createPresenter() {
    return this.presenter;
  }

  @Override public void onResume() {
    super.onResume();
    // prevent the dropDown to show up on start
    mAutoCompleteTextView.dismissDropDown();
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);

    if (activity instanceof MessageComposeListener) {
      this.messageComposeListener = (MessageComposeListener) activity;
    }
  }

  @Override public String getReceiverId() {
    return this.message.getReceiver()
        .getUserId();
  }

  @Override public String getSubject() {
    return this.mSubjectEditText.getText()
        .toString();
  }

  @Override public String getMessage() {
    return this.mBodyTextInputLayout.getEditText()
        .getText()
        .toString();
  }

  @Override public void messageSend() {
    if (this.messageComposeListener != null) {
      this.messageComposeListener.onMessageComposeFinished();
    }
  }

  interface MessageComposeListener {
    void onMessageComposeFinished();
  }
}
