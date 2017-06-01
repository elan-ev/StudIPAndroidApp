/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.contacts.presentation;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hannesdorfmann.mosby.mvp.viewstate.lce.LceViewState;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.data.RetainingLceViewState;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.base.presentation.view.BaseLceFragment;
import de.elanev.studip.android.app.contacts.internal.di.ContactsComponent;
import de.elanev.studip.android.app.contacts.presentation.model.ContactGroupModel;
import de.elanev.studip.android.app.contacts.presentation.presenter.ContactsPresenter;
import de.elanev.studip.android.app.contacts.presentation.view.ContactsView;
import de.elanev.studip.android.app.user.presentation.model.UserModel;
import de.elanev.studip.android.app.widget.EmptyRecyclerView;
import de.elanev.studip.android.app.widget.SectionedDividerItemDecorator;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

/**
 * @author joern
 */
public class ContactsGroupsFragment extends
    BaseLceFragment<SwipeRefreshLayout, List<ContactGroupModel>, ContactsView, ContactsPresenter> implements
    ContactsView, SwipeRefreshLayout.OnRefreshListener {

  @Inject ContactsPresenter presenter;
  private final ContactsSection.ContactClickListener onClickListener = userModel -> {
    if (this.presenter != null && userModel != null) {
      this.presenter.onContactClicked(userModel);
    }
  };
  @BindView(R.id.list) EmptyRecyclerView mRecyclerView;
  @BindView(R.id.emptyView) TextView mEmptyView;
  @BindView(R.id.contentView) SwipeRefreshLayout contentView;
  private SectionedRecyclerViewAdapter contactsAdapter;
  private ContactsListListener contactsListListener;
  private List<ContactGroupModel> sectionsData;

  public ContactsGroupsFragment() {
    setRetainInstance(true);
  }

  @NonNull @Override public LceViewState<List<ContactGroupModel>, ContactsView> createViewState() {
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

  @Override public List<ContactGroupModel> getData() {
    return this.sectionsData;
  }

  @Override public void setData(List<ContactGroupModel> data) {
    // Cache data in fragment so it survives configuration changes
    this.sectionsData = data;

    if (this.contactsAdapter == null) {
      this.contactsAdapter = new SectionedRecyclerViewAdapter();
    }
    this.mRecyclerView.setAdapter(contactsAdapter);

    this.contactsAdapter.removeAllSections();
    for (ContactGroupModel groupModel : this.sectionsData) {
      ContactsSection section = new ContactsSection(groupModel.getName(), groupModel.getMembers(),
          getContext());
      section.setOnClickListener(onClickListener);

      this.contactsAdapter.addSection(section);
    }

    this.contactsAdapter.notifyDataSetChanged();
  }

  @Override public void loadData(boolean pullToRefresh) {
    this.presenter.loadContacts(pullToRefresh);
  }

  @NonNull @Override public ContactsPresenter createPresenter() {
    return this.presenter;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    contentView.setOnRefreshListener(this);
    setUpRecyclerView();
  }

  @Override protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
    return e.getLocalizedMessage();
  }

  private void setUpRecyclerView() {
    mRecyclerView.setEmptyView(mEmptyView);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    mRecyclerView.addItemDecoration(new SectionedDividerItemDecorator(getContext()));
    mRecyclerView.setHasFixedSize(true);
  }

  @Override public void onRefresh() {
    loadData(true);
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ContactsComponent component = this.getComponent(ContactsComponent.class);
    if (component != null) {
      component.inject(this);
    } else {
      componentNotFound();
    }
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);

    if (activity instanceof ContactsListListener) {
      this.contactsListListener = (ContactsListListener) activity;
    }
  }

  @Nullable @Override public View onCreateView(LayoutInflater inflater,
      @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_contacts, container, false);
    ButterKnife.bind(this, view);

    return view;
  }

  @Override public void viewUser(UserModel userModel) {
    if (this.contactsListListener != null) {
      this.contactsListListener.onContactClicked(userModel);
    }
  }

  public interface ContactsListListener {
    void onContactClicked(UserModel userModel);
  }
}
