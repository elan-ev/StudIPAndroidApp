/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.presentation.view;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import javax.inject.Inject;

import de.elanev.studip.android.app.base.presentation.view.activity.BaseActivity;
import de.elanev.studip.android.app.data.db.AbstractContract;
import de.elanev.studip.android.app.data.net.sync.SyncHelper;
import de.elanev.studip.android.app.util.Prefs;

/**
 * @author joern
 */
//FIXME: Embed this whole stuff here in a proper use case
// This whole class is currently only a workaround to get the dependencies out of the
// MainActivity.
public class LogoutActivity extends BaseActivity {

  @Inject SyncHelper syncHelper;
  @Inject Prefs prefs;

  public static Intent getCallingIntent(Context context) {
    return new Intent(context, LogoutActivity.class);
  }

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getApplicationComponent().inject(this);
    logout();
    this.navigator.navigateToSignIn(this);
    finish();
  }

  private void logout() {

    // Resetting the SyncHelper
    syncHelper.resetSyncHelper();

    // Clear the app preferences
    prefs.clearPrefs();

    // Delete the app database
    getContentResolver().delete(AbstractContract.BASE_CONTENT_URI, null, null);
  }
}
