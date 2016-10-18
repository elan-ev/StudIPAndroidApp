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
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.elanev.studip.android.app.R;

public class MessageComposeActivity extends AppCompatActivity implements
    MessageComposeFragment.MessageComposeListener {

  public static final String TAG = MessageComposeActivity.class.getSimpleName();
  public static final String MESSAGE_ACTION_FLAG = "message_action_flag";
  public static final int MESSAGE_ACTION_FORWARD = 2000;
  public static final int MESSAGE_ACTION_REPLY = 2001;
  public static final String MESSAGE = "message";

  @BindView(R.id.toolbar) Toolbar mToolbar;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.content_frame);
    ButterKnife.bind(this);

    initToolbar();
    setTitle(R.string.compose_message);

    // We got called from an Intent
    if (savedInstanceState == null) {
      Intent intent = getIntent();
      handleInternalIntent(intent);

      // FIXME: Add this when the API user search feature is fixed
      //      String action = intent.getAction();
      //      String type = intent.getType();
      //
      //      if (Intent.ACTION_SEND.equals(action) && TextUtils.equals("text/plain", type)) {
      //        handleExternalIntent(intent);
      //      } else {
      //      }
    }
  }

  public void initToolbar() {
    setSupportActionBar(mToolbar);
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setHomeButtonEnabled(true);
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
  }

  // FIXME: Add this when the API user search feature is fixed
  //  private void handleExternalIntent(Intent intent) {
  //    String intentSubject = intent.getStringExtra(Intent.EXTRA_SUBJECT);
  //    String intentText = intent.getStringExtra(Intent.EXTRA_TEXT);
  //
  //
  //  }
  //

  private void handleInternalIntent(Intent intent) {
    Bundle extras = intent.getExtras();
    if (extras == null) {
      return;
    }

    Bundle args = new Bundle();

    args.putSerializable(MessageComposeFragment.MESSAGE, extras.getSerializable(MESSAGE));

    MessageComposeFragment fragment = MessageComposeFragment.newInstance(extras);
    getSupportFragmentManager().beginTransaction()
        .add(R.id.content_frame, fragment, MessageComposeFragment.class.getName())
        .commit();
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      // Respond to the action bar's Up/Home button
      case android.R.id.home:
        finish();
        //        NavUtils.navigateUpFromSameTask(this);
        return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override public void onMessageComposeFinished() {
    this.finish();
  }
}
