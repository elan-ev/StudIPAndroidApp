/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.forums;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import de.elanev.studip.android.app.R;

/**
 * @author joern
 */
public class ForumEntryActivity extends AppCompatActivity {
  public static final String USER_NAME = "user-name";
  public static final String USER_AVATAR = "user-avatar";

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.content_frame);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    Bundle args = getIntent().getExtras();
    if (args == null) {
      finish();
      return;
    }

    if (savedInstanceState == null) {
      ForumEntryFragment forumEntryFragment = ForumEntryFragment.newInstance(args);
      getSupportFragmentManager().beginTransaction()
          .add(R.id.content_frame, forumEntryFragment, ForumEntryFragment.class.getName())
          .commit();
    }
  }
}
