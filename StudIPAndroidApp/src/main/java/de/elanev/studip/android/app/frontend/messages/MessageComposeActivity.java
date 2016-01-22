/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.frontend.messages;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import de.elanev.studip.android.app.MainActivity;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.db.MessagesContract;

public class MessageComposeActivity extends AppCompatActivity implements
    MainActivity.OnShowProgressBarListener {

  private static final String TAG = MessageComposeActivity.class.getSimpleName();
  protected static final String MESSAGE_TYPE_FLAG = MessageComposeActivity.class.getSimpleName()
      + "message_type_flag";
  protected static final String MESSAGE_ACTION_FLAG = MessageComposeActivity.class.getSimpleName()
      + "message_action_flag";
  protected static final int MESSAGE_FLAG_SEND = 1000;
  protected static final int MESSAGE_FLAG_SENDTO = 1001;
  protected static final int MESSAGE_ACTION_FORWARD = 2000;
  protected static final int MESSAGE_ACTION_REPLY = 2001;
  private ProgressBar mProgressBar;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Then set the content with toolbar
    setContentView(R.layout.content_frame);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    mProgressBar = (ProgressBar) findViewById(R.id.progress_spinner);

    setSupportActionBar(toolbar);
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setHomeButtonEnabled(true);
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
    setTitle(R.string.compose_message);


    Intent intent = getIntent();
    String action;
    String type;

    if (intent != null) {
      action = intent.getAction();
      type = intent.getType();

      if (Intent.ACTION_SEND.equals(action) && type != null) {
        if ("text/plain".equals(type)) {
          handleIntentSendText(intent);
          return;
        }
      }
    }

    if (savedInstanceState == null) {
      Bundle args = getIntent().getExtras();
      createComposeFragement(args);
    }

  }

  private void handleIntentSendText(Intent intent) {
    String intentSubject = intent.getStringExtra(Intent.EXTRA_SUBJECT);
    String intentText = intent.getStringExtra(Intent.EXTRA_TEXT);

    if (!TextUtils.isEmpty(intentSubject) && !TextUtils.isEmpty(intentText)) {
      Bundle arguments = new Bundle();
      arguments.putInt(MESSAGE_TYPE_FLAG, MESSAGE_FLAG_SEND);
      arguments.putString(MessagesContract.Columns.Messages.MESSAGE_SUBJECT, intentSubject);
      arguments.putString(MessagesContract.Columns.Messages.MESSAGE, intentText);

      createComposeFragement(arguments);
    }
  }

  private void createComposeFragement(Bundle arguments) {
    MessageComposeFragment meesageComposeFragment = MessageComposeFragment.newInstance(arguments);
    getSupportFragmentManager().beginTransaction()
        .add(R.id.content_frame, meesageComposeFragment, MessageComposeFragment.class.getName())
        .commit();
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      // Respond to the action bar's Up/Home button
      case android.R.id.home:
        // Since this activity can be called from different other
        // activities, we call the back button to move back in stack history
        onBackPressed();
        return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override public void onShowProgressBar(boolean show) {
    if (show) {
      mProgressBar.setVisibility(View.VISIBLE);
    } else {
      mProgressBar.setVisibility(View.GONE);
    }
  }
}
