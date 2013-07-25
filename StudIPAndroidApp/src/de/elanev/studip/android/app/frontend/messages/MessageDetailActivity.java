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
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.frontend.util.BaseSlidingFragmentActivity;

public class MessageDetailActivity extends BaseSlidingFragmentActivity {

	/**
	 * @param titleRes
	 */
	public MessageDetailActivity() {
		super(R.string.Messages);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_frame);

		Bundle args = getIntent().getExtras();
		if (args != null) {
			FragmentManager fm = getSupportFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			Fragment frag = MessageDetailFragment.instantiate(this,
					MessageDetailFragment.class.getName());
			frag.setArguments(args);
			ft.replace(R.id.content_frame, frag, "messageDetailFragment")
					.commit();
		}
	}

}
