/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.messages.presentation.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.elanev.studip.android.app.AbstractStudIPApplication;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.base.internal.di.components.HasComponent;
import de.elanev.studip.android.app.messages.internal.di.DaggerMessagesComponent;
import de.elanev.studip.android.app.messages.internal.di.MessagesComponent;
import de.elanev.studip.android.app.messages.internal.di.MessagesModule;
import de.elanev.studip.android.app.messages.presentation.model.MessageModel;

public class MessageDetailActivity extends AppCompatActivity implements
    HasComponent<MessagesComponent> {
  public static final String MESSAGE_ID = "message-id";
  @BindView(R.id.toolbar) Toolbar mToolbar;

  private MessageModel message;
  private MessagesComponent messagesComponent;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.content_frame);

    Bundle extras = getIntent().getExtras();
    if (extras == null) {
      finish();
      return;
    }

    message = (MessageModel) extras.getSerializable(MESSAGE_ID);

    if (message == null) {
      finish();
      return;
    }


    this.initInjector();

    // Then set the content with toolbar
    ButterKnife.bind(this);
    initToolbar();

    if (savedInstanceState == null) {
      MessageViewFragment messageViewFragment = new MessageViewFragment();
      getSupportFragmentManager().beginTransaction()
          .add(R.id.content_frame, messageViewFragment, MessageViewFragment.class.getName())
          .commit();
    }
  }

  private void initInjector() {
    this.messagesComponent = DaggerMessagesComponent.builder()
        .applicationComponent(((AbstractStudIPApplication) getApplication()).getAppComponent())
        .messagesModule(new MessagesModule(message.getMessageId()))
        .build();
  }

  public void initToolbar() {
    setSupportActionBar(mToolbar);
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setHomeButtonEnabled(true);
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
  }

  private void startComposeActivity(Bundle extras) {
    Intent intent = new Intent(this, MessageComposeActivity.class);
    intent.putExtras(extras);

    startActivity(intent);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      // Respond to the action bar's Up/Home button
      case android.R.id.home:
        NavUtils.navigateUpFromSameTask(this);
        return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override public MessagesComponent getComponent() {
    return messagesComponent;
  }

  //  private void replyMessage() {
  //    Bundle extras = new Bundle();
  //    // Add flag for reply action
  //    extras.putInt(MessageComposeActivity.MESSAGE_ACTION_FLAG,
  //        MessageComposeActivity.MESSAGE_ACTION_REPLY);
  //
  //    // Add needed information
  //    extras.putSerializable(MessageComposeActivity.MESSAGE_SENDER, mSender);
  //    extras.putSerializable(MessageComposeActivity.MESSAGE_RECEIVER, mSender);
  //    extras.putSerializable(MessageComposeActivity.MESSAGE_ORIGINAL, message);
  //
  //    composeMessage(extras);
  //  }
  //
  //  private void forwardMessage() {
  //    Bundle extras = new Bundle();
  //    // Add flag for forward action
  //    extras.putInt(MessageComposeActivity.MESSAGE_ACTION_FLAG,
  //        MessageComposeActivity.MESSAGE_ACTION_FORWARD);
  //
  //    // Add needed information
  //    extras.putSerializable(MessageComposeActivity.MESSAGE_SENDER, mSender);
  //    extras.putSerializable(MessageComposeActivity.MESSAGE_ORIGINAL, message);
  //
  //    composeMessage(extras);
  //  }
}
