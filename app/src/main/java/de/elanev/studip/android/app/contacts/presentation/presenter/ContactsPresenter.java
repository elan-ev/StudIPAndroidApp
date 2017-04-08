/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.contacts.presentation.presenter;

import java.util.List;

import javax.inject.Inject;

import de.elanev.studip.android.app.base.BaseRxLcePresenter;
import de.elanev.studip.android.app.base.DefaultSubscriber;
import de.elanev.studip.android.app.base.UseCase;
import de.elanev.studip.android.app.base.internal.di.PerActivity;
import de.elanev.studip.android.app.contacts.domain.ContactGroup;
import de.elanev.studip.android.app.contacts.presentation.mapper.ContactsModelModelDataMapper;
import de.elanev.studip.android.app.contacts.presentation.model.ContactGroupModel;
import de.elanev.studip.android.app.contacts.presentation.view.ContactsView;
import de.elanev.studip.android.app.user.presentation.model.UserModel;

/**
 * @author joern
 */
@PerActivity
public class ContactsPresenter extends BaseRxLcePresenter<ContactsView, List<ContactGroupModel>> {

  private final UseCase getContactGroups;
  private final ContactsModelModelDataMapper dataMapper;

  @Inject ContactsPresenter(UseCase getContactGroupsUseCase,
      ContactsModelModelDataMapper dataMapper) {
    this.getContactGroups = getContactGroupsUseCase;
    this.dataMapper = dataMapper;
  }

  public void loadContacts(boolean pullToRefresh) {
    getContactGroups.execute(new ContactGroupsSubscriber(pullToRefresh));
  }

  @Override protected void unsubscribe() {
    getContactGroups.unsubscribe();
  }

  @SuppressWarnings("ConstantConditions") public void onContactClicked(UserModel userModel) {
    if (isViewAttached()) {
      getView().viewUser(userModel);
    }
  }

  private final class ContactGroupsSubscriber extends DefaultSubscriber<List<ContactGroup>> {

    ContactGroupsSubscriber(boolean ptr) {
      super(ptr);
    }

    @Override public void onCompleted() {
      ContactsPresenter.this.onCompleted();
    }

    @Override public void onError(Throwable e) {
      ContactsPresenter.this.onError(e, this.isPullToRefresh());
    }

    @Override public void onNext(List<ContactGroup> contactGroups) {
      ContactsPresenter.this.onNext(ContactsPresenter.this.dataMapper.transform(contactGroups));
    }
  }
}
