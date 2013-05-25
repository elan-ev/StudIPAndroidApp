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
import android.support.v4.app.FragmentTransaction;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.frontend.util.BaseSlidingFragmentActivity;

public class MessageComposeActivity extends BaseSlidingFragmentActivity {

	/**
	 * @param titleRes
	 */
	public MessageComposeActivity() {
		super(R.string.Messages);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_frame);

		Bundle args = getIntent().getExtras();

		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Fragment frag = MessageComposeFragment.instantiate(this,
				MessageComposeFragment.class.getName());
		frag.setArguments(args);
		ft.replace(R.id.content_frame, frag, "messageComposeFragment").commit();

	}

}
