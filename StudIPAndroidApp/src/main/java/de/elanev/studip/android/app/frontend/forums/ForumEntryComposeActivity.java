/*
 * Copyright (c) 2015 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.frontend.forums;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Window;

import de.elanev.studip.android.app.R;

/**
 * @author joern
 */
public class ForumEntryComposeActivity extends ActionBarActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

    Bundle args = getIntent().getExtras();
    setContentView(R.layout.content_frame);
    setTitle(R.string.Create_new_entry);
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    if (savedInstanceState == null) {
      ForumEntryComposeFragment forumEntryComposeFragment = ForumEntryComposeFragment.newInstance(
          args);
      getSupportFragmentManager().beginTransaction()
          .add(R.id.content_frame,
              forumEntryComposeFragment,
              ForumEntryComposeFragment.class.getName())
          .commit();

    }
  }

}
