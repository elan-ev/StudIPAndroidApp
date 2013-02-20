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
import StudIPApp.app.R;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class ChooseServerActivity extends FragmentActivity {

    private String title;

    public ChooseServerActivity() {
	this.title = getString(R.string.choose_facility);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	this.setContentView(R.layout.general_view);

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
		.add(R.id.placeholder, glf, title).commit();
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

	// TODO load serverlist...

	Server alteTestumgebung = new Server("Testumgebung (alt)",
		"a358cf5509c989479be1bdaafe79adfd050a4c012",
		"5b7bee2fe2b3a4e3ae25a314b963b3f1",
		"http://coll.virtuos.uos.de/studip/aklassen/trunk/plugins.php/restipplugin");
	Server neueTestumgebung = new Server("Testumgebung (neu)",
		"ee3f4ce2e4fe589a3b5ea681bc00c8a905118d078",
		"60faf16c8442f09d9736c57646720c99",
		"http://vm036.rz.uos.de/studip/aklassen/studip-rest/plugins.php/restipplugin");

	items.add(new TextItem(getString(R.string.Development)));
	items.add(new ServerItem(this, neueTestumgebung));
	items.add(new ServerItem(this, alteTestumgebung));

	return items;
    }
}
