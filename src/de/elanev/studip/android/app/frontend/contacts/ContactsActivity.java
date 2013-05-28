/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.frontend.contacts;

import com.actionbarsherlock.app.ActionBar;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.frontend.util.BaseSlidingFragmentActivity;
import de.elanev.studip.android.app.frontend.util.TabbedSlidingFragmentActivity.BasePagerTabsAdapter;

/**
 * @author joern
 * 
 */
public class ContactsActivity extends BaseSlidingFragmentActivity {

	ViewPager mPager;
	BasePagerTabsAdapter mPagerAdapter;

	public ContactsActivity() {
		super(R.string.Contacts);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setting ABS to tabs mode
		mActionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// ViewPager, PagetAdapter setup
		mPager = new ViewPager(this);
		mPager.setId("ContactsVP".hashCode());
		setContentView(mPager);

		// Creating PagerAdapter and adding the Fragments
		mPagerAdapter = new BasePagerTabsAdapter(this,
				getSupportFragmentManager(), mPager, mActionbar);
		mPagerAdapter.addTab(mActionbar.newTab(), R.string.Favorites,
				ContactsFavoritesFragment.class, null);
		mPagerAdapter.addTab(mActionbar.newTab(), R.string.Groups,
				ContactsGroupsFragment.class, null);
		mPagerAdapter.addTab(mActionbar.newTab(), R.string.All,
				ContactsAllFragment.class, null);
	}

}