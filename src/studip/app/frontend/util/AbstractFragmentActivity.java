/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package studip.app.frontend.util;

import java.util.ArrayList;

import studip.app.backend.net.services.syncservice.activitys.AbstractRestIPResultReceiver;
import studip.app.frontend.slideout.MenuActivity;
import studip.app.frontend.slideout.SlideoutActivity;
import StudIPApp.app.R;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

public abstract class AbstractFragmentActivity<T extends AbstractRestIPResultReceiver<?, ?>>
	extends FragmentActivity {

    public String title;
    protected ArrayAdapter<String> mAdapter;
    public ArrayList<ArrayAdapterItem> mItemList;
    public ProgressBar mProgressBar;
    public ImageButton mRefreshButton;
    protected TextView mText;
    protected T mResponderFragment;

    public AbstractFragmentActivity(T fragment) {
	this.mResponderFragment = fragment;
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

    public abstract void refreshArrayList();
}
