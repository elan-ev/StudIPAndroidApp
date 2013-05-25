/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.frontend.news;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.text.Html;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.db.NewsContract;
import de.elanev.studip.android.app.backend.db.UsersContract;
import de.elanev.studip.android.app.frontend.util.BaseSlidingFragmentActivity;
import de.elanev.studip.android.app.frontend.util.MenuFragment;
import de.elanev.studip.android.app.util.TextTools;

/**
 * @author joern
 * 
 */
public class NewsItemView extends BaseSlidingFragmentActivity {

	/**
	 * @param titleRes
	 */
	public NewsItemView() {
		super(R.string.News);
	}

	String mTitle;
	String mBody;
	Long mTimestamp;
	String mAuthor;

	protected ListFragment mFrag;
	public static ActionBar mActionbar = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mActionbar = getSupportActionBar();
		mActionbar.setDisplayHomeAsUpEnabled(true);

		Intent intent = this.getIntent();
		Bundle extras = intent.getExtras();
		mTitle = extras.getString(NewsContract.Columns.NEWS_TOPIC);
		mBody = extras.getString(NewsContract.Columns.NEWS_BODY);
		mTimestamp = extras.getLong(NewsContract.Columns.NEWS_DATE, 0);
		mAuthor = extras.getString(UsersContract.Columns.USER_FORENAME);
		setTitle(mTitle);

		setSlidingActionBarEnabled(true);

		// Sliding menu setup
		setBehindContentView(R.layout.menu_frame);
		FragmentTransaction t = this.getSupportFragmentManager()
				.beginTransaction();
		mFrag = new MenuFragment();
		t.replace(R.id.menu_frame, mFrag);
		t.commit();

		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow_left);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		setContentView(R.layout.news_item_view_activity);
		((TextView) findViewById(R.id.news_title)).setText(mTitle);
		((TextView) findViewById(R.id.news_author)).setText(TextTools
				.getLocalizedAuthorAndDateString(mAuthor, mTimestamp, this));
		((TextView) findViewById(R.id.news_body)).setText(Html.fromHtml(mBody));
	}

}
