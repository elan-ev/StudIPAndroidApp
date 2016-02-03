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
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.TimeoutException;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.datamodel.Message;
import de.elanev.studip.android.app.backend.datamodel.MessageItem;
import de.elanev.studip.android.app.backend.datamodel.User;
import de.elanev.studip.android.app.backend.net.services.StudIpLegacyApiService;
import de.elanev.studip.android.app.util.DateTools;
import de.elanev.studip.android.app.util.Prefs;
import retrofit2.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MessageComposeActivity extends AppCompatActivity {

  public static final String TAG = MessageComposeActivity.class.getSimpleName();
  public static final String MESSAGE_SENDER = "message-sender";
  public static final String MESSAGE_RECEIVER = "message-receiver";
  public static final String MESSAGE_ORIGINAL = "message-original";
  public static final String MESSAGE_ACTION_FLAG = "message_action_flag";
  public static final int MESSAGE_ACTION_FORWARD = 2000;
  public static final int MESSAGE_ACTION_REPLY = 2001;

  @Bind(R.id.toolbar) Toolbar mToolbar;
  @Bind(R.id.message_subject) EditText mSubjectEditText;
  @Bind(R.id.message_body) EditText mBodyEditText;
  @Bind(R.id.message_receiver) AutoCompleteTextView mAutoCompleteTextView;
  @Bind(R.id.message_receiver_text_input_layout) TextInputLayout mReceiverTextInputLayout;
  @Bind(R.id.message_subject_text_input_layout) TextInputLayout mSubjectTextInputLayout;
  @Bind(R.id.message_body_text_input_layout) TextInputLayout mBodyTextInputLayout;

  private StudIpLegacyApiService mApiService;
  private int mAction = -1;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_message_compose);
    ButterKnife.bind(this);
    initToolbar();
    setTitle(R.string.compose_message);

    mApiService = new StudIpLegacyApiService(Prefs.getInstance(this)
        .getServer(), this);

    if (savedInstanceState == null) {
      Intent intent = getIntent();
      String action = intent.getAction();
      String type = intent.getType();

      if (Intent.ACTION_SEND.equals(action) && TextUtils.equals("text/plain", type)) {
        handleExternalIntent(intent);
      } else {
        handleInternalIntent(intent);
      }
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

  private void handleExternalIntent(Intent intent) {
    String intentSubject = intent.getStringExtra(Intent.EXTRA_SUBJECT);
    String intentText = intent.getStringExtra(Intent.EXTRA_TEXT);

    mSubjectEditText.setText(intentSubject);
    mBodyEditText.setText(intentText);
  }

  private void handleInternalIntent(Intent intent) {
    Bundle extras = intent.getExtras();
    if (extras == null) {
      return;
    }

    mAction = extras.getInt(MESSAGE_ACTION_FLAG);
    User receiver = (User) extras.getSerializable(MESSAGE_RECEIVER);
    User originalSender = (User) extras.getSerializable(MESSAGE_SENDER);
    Message message = (Message) extras.getSerializable(MESSAGE_ORIGINAL);

    if (message != null) {
      mSubjectEditText.setText(formatSubject(message));
      mBodyEditText.setText(formatMessage(message, originalSender));
    }

    if (mAction == MESSAGE_ACTION_REPLY && receiver != null) {
      mReceiverTextInputLayout.setEnabled(false);
      mAutoCompleteTextView.setText(receiver.getFullName());
      mAutoCompleteTextView.setTag(receiver);
      mAutoCompleteTextView.setEnabled(false);
    }
  }

  private String formatSubject(Message message) {
    String subject;

    if (mAction == MESSAGE_ACTION_FORWARD) {
      subject = String.format("%s: %s", getString(R.string.message_forward_string),
          message.subject);
    } else if (mAction == MESSAGE_ACTION_REPLY) {
      subject = String.format("%s: %s", getString(R.string.message_reply_string), message.subject);
    } else {
      subject = message.subject;
    }

    return subject;
  }

  private String formatMessage(Message message, User sender) {
    StringBuilder messageBodyBuilder = new StringBuilder();
    String formattedDate = DateTools.getShortLocalizedTime(message.mkdate, this);
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
        .append(message.message);

    return messageBodyBuilder.toString();
  }

  @Override public void onResume() {
    super.onResume();
    // prevent the dropDown to show up on start
    mAutoCompleteTextView.dismissDropDown();
  }

  @Override public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.message_compose_menu, menu);

    return super.onCreateOptionsMenu(menu);

  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      // Respond to send button
      case R.id.send_icon:
        if (validateFormFields()) {
          sendMessage();
        }
        return true;

      // Respond to the action bar's Up/Home button
      case android.R.id.home:
        // Since this activity can be called from different other
        // activities, we call the back button to move back in stack history
        onBackPressed();
        return true;
    }

    return super.onOptionsItemSelected(item);
  }

  public boolean validateFormFields() {// Check if all fields are filled
    User user = (User) mAutoCompleteTextView.getTag();
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
    User user = (User) mAutoCompleteTextView.getTag();
    String subject = mSubjectEditText.getText()
        .toString();
    String message = mBodyEditText.getText()
        .toString();

    mApiService.sendMessage(user.userId, subject, message)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<MessageItem>() {
          @Override public void onCompleted() {
            showToast(R.string.message_sent);
            finish();
          }

          @Override public void onError(Throwable e) {
            if (e instanceof TimeoutException) {
              showToast(R.string.error_timeout);
              Log.e(TAG, e.getLocalizedMessage());
            } else if (e instanceof HttpException) {
              showToast(R.string.error_http_data_error);
              Log.e(TAG, e.getLocalizedMessage());
            } else {
              e.printStackTrace();
              throw new RuntimeException("See inner exception");
            }

            //            setRefreshing(false);
          }

          @Override public void onNext(MessageItem message) {
            //TODO: What should we do with the new message......
          }
        });
  }

  private void showToast(int textRes) {
    Toast.makeText(MessageComposeActivity.this, textRes, Toast.LENGTH_SHORT)
        .show();
  }

}
