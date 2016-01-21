/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.frontend.news;

import android.os.Bundle;

import de.elanev.studip.android.app.MainActivity;
import de.elanev.studip.android.app.R;

/**
 * @author joern
 */
public class NewsActivity extends MainActivity {

  @Override protected int getCurrentNavDrawerItem() {
    return R.id.navigation_news;
  }

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_news);

    overridePendingTransition(0, 0);
  }
}
