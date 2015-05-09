/*
 * Copyright (c) 2015 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.frontend.messages;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Window;

import de.elanev.studip.android.app.R;

public class MessageComposeActivity extends AppCompatActivity {

  private static final String TAG = MessageComposeActivity.class.getSimpleName();

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // First request toolbar progrss indicator
    supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

    // Then set the content with toolbar
    setContentView(R.layout.content_frame);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    setTitle(R.string.compose_message);

    Bundle args = getIntent().getExtras();

    if (savedInstanceState == null) {
      MessageComposeFragment meesageComposeFragment = MessageComposeFragment.newInstance(args);
      getSupportFragmentManager().beginTransaction()
          .add(R.id.content_frame, meesageComposeFragment, MessageComposeFragment.class.getName())
          .commit();

    }

  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      // Respond to the action bar's Up/Home button
      case android.R.id.home:
        // Since this activity can be called from different other
        // activities, we call the back button to move back in stack history
        onBackPressed();
        return true;
    }

    return super.onOptionsItemSelected(item);
  }

}
