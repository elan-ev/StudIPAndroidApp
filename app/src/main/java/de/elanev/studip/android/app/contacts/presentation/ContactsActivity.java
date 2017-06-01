/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.contacts.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import de.elanev.studip.android.app.AbstractStudIPApplication;
import de.elanev.studip.android.app.MainActivity;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.base.internal.di.components.HasComponent;
import de.elanev.studip.android.app.base.presentation.view.BaseLceFragment;
import de.elanev.studip.android.app.contacts.internal.di.ContactsComponent;
import de.elanev.studip.android.app.contacts.internal.di.ContactsModule;
import de.elanev.studip.android.app.contacts.internal.di.DaggerContactsComponent;
import de.elanev.studip.android.app.user.presentation.model.UserModel;
import de.elanev.studip.android.app.user.presentation.view.UserDetailsActivity;

/**
 * @author joern
 */
public class ContactsActivity extends MainActivity implements HasComponent<ContactsComponent>,
    ContactsGroupsFragment.ContactsListListener, BaseLceFragment.OnComponentNotFoundErrorListener {

  private ContactsComponent contactsComponent;

  @Override protected int getCurrentNavDrawerItem() {
    return R.id.navigation_contacts;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.initInjector();

    setContentView(R.layout.activity_contacts);

    overridePendingTransition(0, 0);
  }

  private void initInjector() {
    this.contactsComponent = DaggerContactsComponent.builder()
        .applicationComponent(((AbstractStudIPApplication) getApplication()).getAppComponent())
        .contactsModule(new ContactsModule())
        .build();
  }

  @Override public ContactsComponent getComponent() {
    return this.contactsComponent;
  }

  @Override public void onContactClicked(UserModel userModel) {
    Intent intent = new Intent();
    Bundle args = new Bundle();
    args.putString(UserDetailsActivity.USER_ID, userModel.getUserId());
    intent.setClass(this, UserDetailsActivity.class);
    intent.putExtras(args);
    startActivity(intent);
  }

  @Override public void onComponentNotFound() {
    Toast.makeText(this, R.string.unknown_error, Toast.LENGTH_SHORT)
        .show();
    finish();
  }
}
