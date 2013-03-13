/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.frontend.util;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.frontend.courses.CoursesFragment;
import de.elanev.studip.android.app.frontend.messages.MessagesFragment;
import de.elanev.studip.android.app.frontend.news.NewsFragment;

public class AbstractFragmentActivity extends BaseSlidingFragmentActivity {

	private Fragment mContent;
	private Fragment mFragment;

	/**
	 * @param titleRes
	 */
	public AbstractFragmentActivity() {
		super(R.string.app_name);
	}

	public AbstractFragmentActivity(int titleRes) {
		super(titleRes);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_frame);

		/* Content */
		Fragment frag = null;
		if (savedInstanceState != null)
			frag = getSupportFragmentManager().getFragment(savedInstanceState,
					"mContent");

		if (frag == null)
			frag = new NewsFragment();

		switchContent(frag);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onResume()
	 */
	@Override
	protected void onResume() {
		super.onResume();

		if (mFragment != null)
			switchContent(mFragment);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onNewIntent(android.content.Intent)
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		if (intent != null) {
			String fragName = intent.getStringExtra("frag");

			if (fragName != null) {
				if (fragName.equals(CoursesFragment.class.getName())) {
					mFragment = new CoursesFragment();
				} else if (fragName.equals(MessagesFragment.class.getName())) {
					mFragment = new MessagesFragment();
				} else {
					mFragment = new NewsFragment();
				}
			}

		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
	}

	public void switchContent(Fragment fragment) {
		mContent = fragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment, "content").commit();
		getSlidingMenu().showContent();
	}

}
