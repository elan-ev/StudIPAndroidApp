/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.forums;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import de.elanev.studip.android.app.MainActivity;
import de.elanev.studip.android.app.R;

/**
 * @author joern
 */
public class ForumEntryComposeActivity extends AppCompatActivity implements
    MainActivity.OnShowProgressBarListener {

  private ProgressBar mProgressBar;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.content_frame);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    mProgressBar = (ProgressBar) findViewById(R.id.progress_spinner);

    setSupportActionBar(toolbar);
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    Bundle args = getIntent().getExtras();
    setTitle(R.string.Create_new_entry);

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

  @Override public void onShowProgressBar(boolean show) {
    if (show) {
      mProgressBar.setVisibility(View.VISIBLE);
    } else {
      mProgressBar.setVisibility(View.GONE);
    }
  }
}
