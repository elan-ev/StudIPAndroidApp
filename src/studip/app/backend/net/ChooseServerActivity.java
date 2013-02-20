/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package studip.app.backend.net;

import java.util.ArrayList;

import studip.app.frontend.slideout.MenuActivity;
import studip.app.frontend.slideout.SlideoutActivity;
import studip.app.frontend.util.ArrayAdapterItem;
import studip.app.frontend.util.GeneralListFragment;
import studip.app.frontend.util.TextItem;
import studip.app.util.TempServerDeclares;
import StudIPApp.app.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * 
 * @author joern
 * 
 */
// TODO refactoring, extending an AbstractActivity
// FIXME buttons not showing up correct
public class ChooseServerActivity extends FragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	this.setContentView(R.layout.general_view);
	((TextView) this.findViewById(R.id.title))
		.setText(getString(R.string.choose_server));
	((ImageButton) this.findViewById(R.id.slide_button))
		.setOnClickListener(new OnClickListener() {
		    // @Override
		    public void onClick(View v) {
			slideButtonPressed(v);
		    }
		});

	GeneralListFragment glf = new GeneralListFragment();
	glf.itemList = this.getItems();

	getSupportFragmentManager().beginTransaction()
		.add(R.id.placeholder, glf, getString(R.string.choose_server))
		.commit();
    }

    public void slideButtonPressed(View view) {
	view.setSelected(false);
	int width = (int) TypedValue.applyDimension(
		TypedValue.COMPLEX_UNIT_DIP, 60, getResources()
			.getDisplayMetrics());
	SlideoutActivity.prepare(this, R.id.inner_content, width);
	startActivity(new Intent(this, MenuActivity.class));
	overridePendingTransition(0, 0);
    }

    public ArrayList<ArrayAdapterItem> getItems() {

	ArrayList<ArrayAdapterItem> items = new ArrayList<ArrayAdapterItem>();

	/*
	 * WARNING: you need your own TempServerDeclares Class in the
	 * studip.app.util package see:
	 * studip.app.util.TempServerDeclaresExample
	 */
	items.add(new TextItem(getString(R.string.Development)));
	items.add(new ServerItem(this, TempServerDeclares.neueTestumgebung));
	items.add(new ServerItem(this, TempServerDeclares.alteTestumgebung));

	return items;
    }
}
