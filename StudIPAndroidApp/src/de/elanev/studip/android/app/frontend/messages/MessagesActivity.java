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

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.net.services.syncservice.activitys.MessagesResponderFragment;
import de.elanev.studip.android.app.frontend.util.BaseSlidingFragmentActivity;

/**
 * @author joern
 * 
 */
public class MessagesActivity extends BaseSlidingFragmentActivity {

	public MessagesActivity() {
		super(R.string.Messages);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setting up SlidingMenu
		SlidingMenu slidingMenu = getSlidingMenu();
		slidingMenu.setMode(SlidingMenu.LEFT_RIGHT);
		slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		slidingMenu.setSecondaryMenu(R.layout.menu_frame_messages);
		slidingMenu.setSecondaryShadowDrawable(R.drawable.shadow_right);

		setContentView(R.layout.content_frame);

		if (savedInstanceState != null) {
			return;
		}

		// setting up Fragments
		FragmentManager fm = getSupportFragmentManager();
		MessagesListFragment messagesListFragment = null;
		MessageFoldersMenuFragment messageFoldersFragment = null;

		messagesListFragment = (MessagesListFragment) fm
				.findFragmentByTag(MessagesListFragment.class.getName());
		messageFoldersFragment = (MessageFoldersMenuFragment) fm
				.findFragmentByTag(MessageFoldersMenuFragment.class.getName());

		if (messagesListFragment == null) {
			messagesListFragment = (MessagesListFragment) MessagesListFragment
					.instantiate(this, MessagesListFragment.class.getName());
		}
		if (messageFoldersFragment == null) {
			messageFoldersFragment = (MessageFoldersMenuFragment) MessageFoldersMenuFragment
					.instantiate(this,
							MessageFoldersMenuFragment.class.getName());
		}

		fm.beginTransaction()
				.add(R.id.content_frame, messagesListFragment,
						MessagesListFragment.class.getName())
				.replace(R.id.menu_messages_frame, messageFoldersFragment)
				.commit();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onBackPressed()
	 */
	@Override
	public void onBackPressed() {
		if (getSlidingMenu().isMenuShowing())
			getSlidingMenu().toggle();
	}

	public void startLoading() {
		FragmentManager fm = getSupportFragmentManager();
		// reload data only if new activity
		Fragment responderFragment = (MessagesResponderFragment) fm
				.findFragmentByTag("messagesResponder");
		if (responderFragment == null) {
			responderFragment = new MessagesResponderFragment();
			fm.beginTransaction().add(responderFragment, "messagesResponder")
					.commit();
		} else {
			((MessagesResponderFragment) responderFragment).loadData();
		}
	}

}