/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.frontend.messages;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.Window;

import de.elanev.studip.android.app.R;

public class MessageComposeActivity extends ActionBarActivity {

    private static final String TAG = MessageComposeActivity.class.getSimpleName();

    /*
         * (non-Javadoc)
         * 
         * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
         */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        Bundle args = getIntent().getExtras();

		setContentView(R.layout.content_frame);
		setTitle(R.string.compose_message);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//
//		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//		Fragment frag = MessageComposeFragment.instantiate(this,
//				MessageComposeFragment.class.getName());
//		frag.setArguments(args);
//		ft.replace(R.id.content_frame, frag, "messageComposeFragment").commit();

        if (savedInstanceState == null) {
            MessageComposeFragment meesageComposeFragment = MessageComposeFragment.newInstance(args);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_frame, meesageComposeFragment,
                            MessageComposeFragment.class.getName())
                    .commit();

        }

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.actionbarsherlock.app.SherlockFragmentActivity#onOptionsItemSelected
	 * (com.actionbarsherlock.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
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
