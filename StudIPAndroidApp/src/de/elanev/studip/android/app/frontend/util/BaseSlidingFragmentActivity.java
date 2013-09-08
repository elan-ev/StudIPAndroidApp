/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.frontend.util;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;

import de.elanev.studip.android.app.R;

/**
 * @author joern
 * 
 */
public class BaseSlidingFragmentActivity extends SlidingFragmentActivity {

	private int mTitleRes;
	protected Fragment mFrag;
	public static ActionBar mActionbar = null;

	public BaseSlidingFragmentActivity(int titleRes) {
		mTitleRes = titleRes;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mActionbar = getSupportActionBar();
		mActionbar.setDisplayHomeAsUpEnabled(false);
		mActionbar.setHomeButtonEnabled(true);
		setSlidingActionBarEnabled(true);

		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow_left); 
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		sm.setSelectorEnabled(true);
		sm.setSelectorDrawable(R.drawable.list_item_selector);

		setTitle(mTitleRes);

		// Sliding menu setup
		setBehindContentView(R.layout.menu_frame);

		if (savedInstanceState != null)
			return;

		FragmentManager fm = getSupportFragmentManager();
		mFrag = (ListFragment) fm.findFragmentById(R.id.menu_frame);

		if (mFrag == null)
			mFrag = MenuFragment
					.instantiate(this, MenuFragment.class.getName());

		fm.beginTransaction().replace(R.id.menu_frame, mFrag).commit();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			toggle();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
