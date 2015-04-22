/*
 * Copyright (c) 2015 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.frontend.forums;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;

import java.util.List;

import de.elanev.studip.android.app.R;

/**
 * @author joern
 */
public class ForumEntryActivity extends ActionBarActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Bundle args = getIntent().getExtras();
    if (args == null) {
      finish();
      return;
    }

    setContentView(R.layout.content_frame);
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    if (savedInstanceState == null) {
      ForumEntryFragment forumEntryFragment = ForumEntryFragment.newInstance(args);
      getSupportFragmentManager().beginTransaction()
          .add(R.id.content_frame, forumEntryFragment, ForumEntryFragment.class.getName())
          .commit();
    }
  }
}
