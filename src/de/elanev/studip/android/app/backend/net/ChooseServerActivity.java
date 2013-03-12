/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.net;

import java.util.ArrayList;

import android.os.Bundle;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.frontend.util.ArrayAdapterItem;
import de.elanev.studip.android.app.frontend.util.BaseSlidingFragmentActivity;
import de.elanev.studip.android.app.frontend.util.GeneralListFragment;
import de.elanev.studip.android.app.frontend.util.TextItem;
import de.elanev.studip.android.app.util.TempServerDeclares;

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
		GeneralListFragment glf = new GeneralListFragment();
		glf.itemList = this.getItems();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.content_frame, glf, "glf").commit();
	}

	public ArrayList<ArrayAdapterItem> getItems() {

		ArrayList<ArrayAdapterItem> items = new ArrayList<ArrayAdapterItem>();

		/*
		 * WARNING: you need your own TempServerDeclares Class in the
		 * de.elanev.studip.android.app.util package see:
		 * de.elanev.studip.android.app.util.TempServerDeclaresExample
		 */
		items.add(new TextItem(getString(R.string.Development)));
		items.add(new ServerItem(this, TempServerDeclares.neueTestumgebung));
		items.add(new ServerItem(this, TempServerDeclares.alteTestumgebung));

		return items;
	}
}
