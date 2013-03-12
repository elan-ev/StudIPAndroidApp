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
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.frontend.news.NewsFragment;

public class AbstractFragmentActivity extends BaseSlidingFragmentActivity {

	private Fragment mContent;

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

		/* Content */
		if (savedInstanceState != null)
			mContent = getSupportFragmentManager().getFragment(
					savedInstanceState, "mContent");
		if (mContent == null)
			mContent = new NewsFragment();

		setContentView(R.layout.content_frame);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, mContent, "content").commit();
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
