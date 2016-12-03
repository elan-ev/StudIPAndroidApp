/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import javax.inject.Inject;

import de.elanev.studip.android.app.base.presentation.view.activity.BaseActivity;
import de.elanev.studip.android.app.util.Prefs;

/**
 * @author joern
 */

public class StartupActivity extends BaseActivity {
  @Inject Prefs mPrefs;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    getApplicationComponent().inject(this);

    if (!mPrefs.isAppAuthorized()) {
      this.navigator.navigateToSignIn(this);
      finish();
      return;
    } else {
      this.navigator.navigateToNews(this);
    }
  }
}
