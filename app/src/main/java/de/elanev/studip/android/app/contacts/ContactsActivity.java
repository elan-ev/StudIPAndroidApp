/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.contacts;

import android.os.Bundle;

import de.elanev.studip.android.app.MainActivity;
import de.elanev.studip.android.app.R;

/**
 * @author joern
 */
public class ContactsActivity extends MainActivity {

  @Override protected int getCurrentNavDrawerItem() {
    return R.id.navigation_contacts;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_contacts);

    overridePendingTransition(0, 0);
  }
}
