/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.frontend.messages;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import de.elanev.studip.android.app.R;

public class MessageDetailActivity extends SherlockFragmentActivity {

    /*
     * (non-Javadoc)
     *
     * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        Bundle args = getIntent().getExtras();
        // No arguments, nothing to display, finish activity
        if (args == null) {
            finish();
            return;
        }

        setContentView(R.layout.content_frame);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {

            MessageDetailFragment messageDetailFragment = MessageDetailFragment.newInstance(args);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_frame, messageDetailFragment,
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
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
