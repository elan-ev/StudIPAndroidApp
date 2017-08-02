/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.messages.presentation.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.RetainingLceViewState;

import java.util.List;

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
import de.elanev.studip.android.app.messages.presentation.presenter.MessageListPresenter;
import de.elanev.studip.android.app.messages.presentation.view.adapter.MessagesAdapter;
import de.elanev.studip.android.app.widget.EmptyRecyclerView;
import de.elanev.studip.android.app.widget.SimpleDividerItemDecoration;

public class MessagesListFragment extends
    BaseLceFragment<SwipeRefreshLayout, List<MessageModel>, MessageListView, MessageListPresenter> implements
    MessageListView, SwipeRefreshLayout.OnRefreshListener {
  public MessagesAdapter messagesAdapter;
  @Inject MessageListPresenter presenter;
  @BindView(R.id.list) EmptyRecyclerView recyclerView;
  @BindView(R.id.emptyView) TextView emptyView;
  @BindView(R.id.contentView) SwipeRefreshLayout contentView;
  private String mBoxType;
  private MessagesAdapter.MessageClickListener onClickListener = messageModel -> {
    if (MessagesListFragment.this.presenter != null && messageModel != null) {
      MessagesListFragment.this.presenter.onMessageClicked(messageModel);
    }
  };
  private MessageListListener messageListListener;
  private MessagesComponent messagesComponent;
  private List<MessageModel> data;

  public MessagesListFragment() {
    setRetainInstance(true);
  }

  public static MessagesListFragment newInstance(Bundle args) {
    MessagesListFragment fragment = new MessagesListFragment();
    fragment.setArguments(args);

    return fragment;
  }

  @NonNull @Override public MessageListPresenter createPresenter() {
    return this.presenter;
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);

    if (activity instanceof MessageListListener) {
      this.messageListListener = (MessageListListener) activity;
    }
  }

  @Override public void onSaveInstanceState(Bundle outState) {
    outState.putString(MessagesActivity.BOX_TYPE, mBoxType);

    super.onSaveInstanceState(outState);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    this.contentView.setOnRefreshListener(this);
    initRecyclerView();
  }

  private void initRecyclerView() {
    this.emptyView.setText(R.string.no_messages);
    this.recyclerView.setEmptyView(emptyView);
    this.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    this.recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
    this.recyclerView.setHasFixedSize(true);
  }

  @Override protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
    return e.getLocalizedMessage();
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater,
      @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View v = inflater.inflate(R.layout.fragment_message_list, container, false);
    ButterKnife.bind(this, v);

    return v;
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.messages_list_menu, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (isAdded()) {
      switch (item.getItemId()) {
        case R.id.compose_icon:

          Intent intent = new Intent(getContext(), MessageComposeActivity.class);
          startActivity(intent);
          break;

        default:
          return super.onOptionsItemSelected(item);
      }
    }

    return true;
  }

  @NonNull @Override public LceViewState<List<MessageModel>, MessageListView> createViewState() {
    return new RetainingLceViewState<>();
  }

  @Override public void showContent() {
    super.showContent();
    contentView.setRefreshing(false);
  }

  @Override public void showError(Throwable e, boolean pullToRefresh) {
    super.showError(e, pullToRefresh);
    contentView.setRefreshing(false);
  }

  @Override public List<MessageModel> getData() {
    return this.data;
  }

  @Override public void setData(List<MessageModel> data) {
    this.data = data;

    if (this.messagesAdapter == null) {
      this.messagesAdapter = new MessagesAdapter(getContext());
      this.messagesAdapter.setOnItemClickListener(onClickListener);
    }
    this.recyclerView.setAdapter(messagesAdapter);

    this.messagesAdapter.setData(data);
  }

  @Override public void loadData(boolean pullToRefresh) {
    this.presenter.loadMessages(pullToRefresh, mBoxType);
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initInjector();
    messagesComponent.inject(this);

    setHasOptionsMenu(true);

    Bundle arguments = getArguments();
    if (arguments == null) {
      return;
    }

    mBoxType = arguments.getString(MessagesActivity.BOX_TYPE);
  }

  private void initInjector() {
    this.messagesComponent = DaggerMessagesComponent.builder()
        .applicationComponent(
            ((AbstractStudIPApplication) getActivity().getApplication()).getAppComponent())
        .messagesModule(new MessagesModule())
        .build();
  }

  @Override public void onRefresh() {
    this.loadData(true);
  }

  @Override public void viewMessage(MessageModel messageModel) {
    if (this.messageListListener != null) {
      this.messageListListener.onMessageSelected(messageModel);
    }
  }

  public interface MessageListListener {
    void onMessageSelected(MessageModel messageModel);
  }
}
