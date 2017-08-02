/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.messages.presentation.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.elanev.studip.android.app.MainActivity;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.StudIPConstants;
import de.elanev.studip.android.app.base.presentation.view.BaseLceFragment;
import de.elanev.studip.android.app.messages.presentation.model.MessageModel;
import de.elanev.studip.android.app.widget.FragmentsAdapter;

/**
 * @author joern
 */
public class MessagesActivity extends MainActivity implements
    MessagesListFragment.MessageListListener, BaseLceFragment.OnComponentNotFoundErrorListener {
  public static final String BOX_TYPE = "box-type";
  @BindView(R.id.sliding_tabs) TabLayout mTabLayout;
  @BindView(R.id.pager) ViewPager mViewPager;
  // @BindView(R.id.fab) FloatingActionButton mFab;

  @Override protected int getCurrentNavDrawerItem() {
    return R.id.navigation_messages;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_message);
    setTitle(R.string.Messages);

    ButterKnife.bind(this);
    FragmentsAdapter pagerAdapter = new FragmentsAdapter(getSupportFragmentManager(), getTabs());
    mViewPager.setAdapter(pagerAdapter);
    mTabLayout.setupWithViewPager(mViewPager);

    //FIXME: Reenable when the API user search is fixed
    //    mFab.setOnClickListener(v -> startComposeActivity());

    overridePendingTransition(0, 0);
  }

  private ArrayList<FragmentsAdapter.Tab> getTabs() {
    ArrayList<FragmentsAdapter.Tab> tabs = new ArrayList<>();

    Bundle inboxExtras = new Bundle();
    inboxExtras.putString(BOX_TYPE, StudIPConstants.STUDIP_MESSAGES_INBOX_IDENTIFIER);
    tabs.add(new FragmentsAdapter.Tab(getString(R.string.Inbox), MessagesListFragment.class,
        inboxExtras));

    Bundle outboxExtras = new Bundle();
    outboxExtras.putString(BOX_TYPE, StudIPConstants.STUDIP_MESSAGES_OUTBOX_IDENTIFIER);
    tabs.add(new FragmentsAdapter.Tab(getString(R.string.Outbox), MessagesListFragment.class,
        outboxExtras));

    return tabs;
  }

  private void startComposeActivity() {
    Intent intent = new Intent(MessagesActivity.this, MessageComposeActivity.class);
    startActivity(intent);
  }

  @Override public void onMessageSelected(MessageModel messageModel) {
    Intent intent = new Intent();
    intent.setClass(this, MessageDetailActivity.class);

    Bundle extras = new Bundle();
    extras.putSerializable(MessageDetailActivity.MESSAGE_ID, messageModel);
    //    extras.putSerializable(MessageDetailActivity.SENDER_INFO, messageModel.getSender());
    intent.putExtras(extras);

    startActivity(intent);
  }

  @Override public void onComponentNotFound() {
    Toast.makeText(this, R.string.unknown_error, Toast.LENGTH_SHORT)
        .show();
    finish();
  }
}
