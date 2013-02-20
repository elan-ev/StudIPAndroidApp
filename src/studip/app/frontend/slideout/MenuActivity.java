/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package studip.app.frontend.slideout;

import java.util.ArrayList;

import studip.app.frontend.util.ArrayAdapterItem;
import studip.app.frontend.util.GeneralListFragment;
import studip.app.frontend.util.TextItem;
import StudIPApp.app.R;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;

public class MenuActivity extends FragmentActivity {

    private SlideoutHelper mSlideoutHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	mSlideoutHelper = new SlideoutHelper(this);
	mSlideoutHelper.activate();

	ArrayList<ArrayAdapterItem> itemList = new ArrayList<ArrayAdapterItem>();

	itemList.add(new MenuItem(R.drawable.activity, MenuItem.ACTIVITIES_ID,
		this));

	itemList.add(new TextItem(MenuActivity.this.getResources()
		.getString(R.string.General).toString()));

	itemList.add(new MenuItem(R.drawable.news, MenuItem.NEWS_ID, this));
	itemList.add(new MenuItem(R.drawable.seminar, MenuItem.COURSES_ID, this));
	itemList.add(new MenuItem(R.drawable.schedule, MenuItem.EVENTS_ID, this));
	itemList.add(new MenuItem(R.drawable.mail, MenuItem.MESSAGES_ID, this));
	itemList.add(new MenuItem(R.drawable.community, MenuItem.CONTACTS_ID,
		this));
	itemList.add(new MenuItem(R.drawable.files, MenuItem.DOCUMENTS_ID, this));
	itemList.add(new TextItem(""));
	itemList.add(new MenuItem(R.drawable.admin, MenuItem.SETTINGS_ID, this));
	itemList.add(new MenuItem(R.drawable.question_circle, MenuItem.HELP_ID,
		this));
	itemList.add(new MenuItem(R.drawable.info_circle,
		MenuItem.INFORMATION_ID, this));
	itemList.add(new MenuItem(android.R.drawable.ic_menu_revert,
		MenuItem.LOGOUT_ID, this));

	GeneralListFragment glf = new GeneralListFragment();
	glf.itemList = itemList;

	getSupportFragmentManager().beginTransaction()
		.add(R.id.slideout_placeholder, glf, getString(R.string.menu))
		.commit();
	mSlideoutHelper.open();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_BACK) {
	    mSlideoutHelper.close(-1);
	    return true;
	}
	return super.onKeyDown(keyCode, event);
    }

    public SlideoutHelper getSlideoutHelper() {
	return mSlideoutHelper;
    }

    public static void start(int position) {

    }
}
