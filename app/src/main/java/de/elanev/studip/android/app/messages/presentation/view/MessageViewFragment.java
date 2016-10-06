/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.messages.presentation.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.internal.NavigationMenu;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.RetainingLceViewState;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.StudIPConstants;
import de.elanev.studip.android.app.base.presentation.view.BaseLceFragment;
import de.elanev.studip.android.app.messages.internal.di.MessagesComponent;
import de.elanev.studip.android.app.messages.presentation.model.MessageModel;
import de.elanev.studip.android.app.messages.presentation.presenter.MessageViewPresenter;
import de.elanev.studip.android.app.user.presentation.model.UserModel;
import de.elanev.studip.android.app.util.DateTools;
import de.hdodenhof.circleimageview.CircleImageView;
import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

/**
 * @author joern
 */

public class MessageViewFragment extends
    BaseLceFragment<ScrollView, MessageModel, MessageView, MessageViewPresenter> implements
    MessageView {

  @BindView(R.id.message_subject) TextView mSubjectTextView;
  @BindView(R.id.message_body) TextView mBodyTextView;
  @BindView(R.id.user_image) CircleImageView mSenderImageView;
  @BindView(R.id.text1) TextView mSenderTextView;
  @BindView(R.id.text2) TextView mDateTextView;
  @BindView(R.id.speed_dial_fab) FabSpeedDial mFab;
  @BindView(R.id.info_container) View mInfoContainer;
  @Inject MessageViewPresenter presenter;
  private MessageModel message;

  public MessageViewFragment() {
    setRetainInstance(true);
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    this.getComponent(MessagesComponent.class)
        .inject(this);
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater,
      @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_message_details, container, false);
    ButterKnife.bind(this, v);

    return v;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    mInfoContainer.setVisibility(View.GONE);
    initFab();
  }

  private void initFab() {
    mFab.setMenuListener(new SimpleMenuListenerAdapter() {
      @Override public boolean onPrepareMenu(NavigationMenu navigationMenu) {
        UserModel sender = message.getSender();

        if (sender == null || TextUtils.isEmpty(sender.getUserId()) ||
            TextUtils.equals(sender.getUserId(), StudIPConstants.STUDIP_SYSTEM_USER_ID)) {
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
            //            replyMessage();
            return true;
          case R.id.forward_message:
            //            forwardMessage();
            return true;
        }

        return false;
      }
    });
    mFab.setVisibility(View.GONE);
  }

  private void deleteMessage() {
    this.presenter.deleteMessage();
  }

  @Override protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
    return e.getLocalizedMessage();
  }

  @NonNull @Override public LceViewState<MessageModel, MessageView> createViewState() {
    return new RetainingLceViewState<>();
  }

  @Override public void showContent() {
    mInfoContainer.setVisibility(View.VISIBLE);
    mFab.setVisibility(View.VISIBLE);

    super.showContent();
  }

  @Override public MessageModel getData() {
    return this.message;
  }

  @Override public void setData(MessageModel message) {
    this.message = message;

    fillViews();
  }

  private void fillViews() {
    if (message != null) {

      UserModel sender = message.getSender();
      if (sender != null) {
        Picasso.with(getContext())
            .load(message.getSender()
                .getAvatarUrl())
            .resizeDimen(R.dimen.user_image_icon_size, R.dimen.user_image_icon_size)
            .centerCrop()
            .placeholder(R.drawable.nobody_normal)
            .into(mSenderImageView);
        mSenderTextView.setText(sender.getFullName());
      } else {
        mSenderTextView.setText(android.R.string.unknownName);
      }

      mSubjectTextView.setText(message.getSubject());
      if (!TextUtils.isEmpty(message.getMessage())) {
        mBodyTextView.setText(Html.fromHtml(message.getMessage()));
        mBodyTextView.setMovementMethod(LinkMovementMethod.getInstance());
      }

      mDateTextView.setText(DateTools.getLocalizedRelativeTimeString(message.getDate()));
    }
  }

  @Override public void loadData(boolean pullToRefresh) {
    this.presenter.loadNews();
  }

  @NonNull @Override public MessageViewPresenter createPresenter() {
    return this.presenter;
  }
}
