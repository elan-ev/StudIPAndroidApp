/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.net;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.frontend.util.BaseSlidingFragmentActivity;

/**
 * 
 * @author joern
 * 
 */
public class ChooseServerActivity extends BaseSlidingFragmentActivity {

	/**
	 * @param titleRes
	 */
	public ChooseServerActivity() {
		super(R.string.choose_server);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.choose_server);
		this.setContentView(R.layout.content_frame);
		FragmentManager fm = getSupportFragmentManager();
		ChooseServerFragment frag = null;
		if (savedInstanceState == null) {
			frag = (ChooseServerFragment) ChooseServerFragment.instantiate(
					this, ChooseServerFragment.class.getName());
		} else {
			frag = (ChooseServerFragment) fm
					.findFragmentByTag(ChooseServerFragment.class.getName());
		}
		fm.beginTransaction()
				.add(R.id.content_frame, frag,
						ChooseServerFragment.class.getName()).commit();
	}

}
