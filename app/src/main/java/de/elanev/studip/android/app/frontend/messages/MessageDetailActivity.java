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
import android.support.design.internal.NavigationMenu;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeoutException;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.StudIPConstants;
import de.elanev.studip.android.app.backend.datamodel.Message;
import de.elanev.studip.android.app.backend.datamodel.User;
import de.elanev.studip.android.app.backend.net.services.StudIpLegacyApiService;
import de.elanev.studip.android.app.util.DateTools;
import de.elanev.studip.android.app.util.Prefs;
import de.hdodenhof.circleimageview.CircleImageView;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MessageDetailActivity extends AppCompatActivity {
  public static final String MESSAGE = "message";
  public static final String SENDER_INFO = "sender-info";
  private static final String TAG = MessageDetailActivity.class.getSimpleName();

  @Bind(R.id.toolbar) Toolbar mToolbar;
  @Bind(R.id.message_subject) TextView mSubjectTextView;
  @Bind(R.id.message_body) TextView mBodyTextView;
  @Bind(R.id.user_image) CircleImageView mSenderImageView;
  @Bind(R.id.text1) TextView mSenderTextView;
  @Bind(R.id.text2) TextView mDateTextView;
  @Bind(R.id.speed_dial_fab) FabSpeedDial mFab;

  private Message mMessage;
  private User mSender;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Bundle extras = getIntent().getExtras();
    if (extras == null) {
      finish();
      return;
    }

    mMessage = (Message) extras.getSerializable(MESSAGE);
    mSender = (User) extras.getSerializable(SENDER_INFO);

    if (mMessage == null) {
      finish();
      return;
    }

    // Then set the content with toolbar
    setContentView(R.layout.activity_message_details);
    ButterKnife.bind(this);
    initToolbar();
    initFab();

    fillViews();
    setTitle(mMessage.subject);
  }

  public void initToolbar() {
    setSupportActionBar(mToolbar);
    ActionBar actionBar = getSupportActionBar();
    if (actionBar != null) {
      actionBar.setHomeButtonEnabled(true);
      actionBar.setDisplayHomeAsUpEnabled(true);
    }
  }

  private void initFab() {
    mFab.setMenuListener(new FabSpeedDial.MenuListener() {
      @Override public boolean onPrepareMenu(NavigationMenu navigationMenu) {
        if (mSender.userId.equals(StudIPConstants.STUDIP_SYSTEM_USER_ID)) {
          navigationMenu.removeItem(R.id.reply_message);
        }

        return true;
      }

      @Override public boolean onMenuItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
          case R.id.delete_message:
            deleteMessage();
            return true;
          case R.id.reply_message:
            replyMessage();
            return true;
          case R.id.forward_message:
            forwardMessage();
            return true;
        }

        return false;
      }


    });
  }

  private void fillViews() {
    if (mSender != null) {
      Picasso.with(this)
          .load(mSender.avatarNormal)
          .resizeDimen(R.dimen.user_image_icon_size, R.dimen.user_image_icon_size)
          .centerCrop()
          .placeholder(R.drawable.nobody_normal)
          .into(mSenderImageView);
      mSenderTextView.setText(mSender.getFullName());
    } else {
      mSenderTextView.setText(android.R.string.unknownName);
    }

    mSubjectTextView.setText(mMessage.subject);
    if (!TextUtils.isEmpty(mMessage.message)) {
      mBodyTextView.setText(Html.fromHtml(mMessage.message));
      mBodyTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    mDateTextView.setText(DateTools.getLocalizedRelativeTimeString(mMessage.mkdate));
  }

  private void deleteMessage() {
    String messageId = mMessage.messageId;

    StudIpLegacyApiService service = new StudIpLegacyApiService(Prefs.getInstance(this)
        .getServer(), this);

    service.deleteMessage(messageId)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Subscriber<Void>() {
          @Override public void onCompleted() {
            showToast(R.string.message_deleted);
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
          }

          @Override public void onNext(Void aVoid) {
            // Nothing to do
          }
        });
  }

  private void replyMessage() {
    Bundle extras = new Bundle();
    // Add flag for reply action
    extras.putInt(MessageComposeActivity.MESSAGE_ACTION_FLAG,
        MessageComposeActivity.MESSAGE_ACTION_REPLY);

    // Add needed information
    extras.putSerializable(MessageComposeActivity.MESSAGE_SENDER, mSender);
    extras.putSerializable(MessageComposeActivity.MESSAGE_RECEIVER, mSender);
    extras.putSerializable(MessageComposeActivity.MESSAGE_ORIGINAL, mMessage);

    startComposeActivity(extras);
  }

  private void forwardMessage() {
    Bundle extras = new Bundle();
    // Add flag for forward action
    extras.putInt(MessageComposeActivity.MESSAGE_ACTION_FLAG,
        MessageComposeActivity.MESSAGE_ACTION_FORWARD);

    // Add needed information
    extras.putSerializable(MessageComposeActivity.MESSAGE_SENDER, mSender);
    extras.putSerializable(MessageComposeActivity.MESSAGE_ORIGINAL, mMessage);

    startComposeActivity(extras);
  }

  private void showToast(int textRes) {
    Toast.makeText(MessageDetailActivity.this, textRes, Toast.LENGTH_SHORT)
        .show();
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

}
