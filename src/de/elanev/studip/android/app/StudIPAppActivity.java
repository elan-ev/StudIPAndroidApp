/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import de.elanev.studip.android.app.backend.net.oauth.SignInActivity;
import de.elanev.studip.android.app.util.VolleyHttp;

public class StudIPAppActivity extends Activity {

	public static final String TAG = StudIPAppActivity.class.getSimpleName();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, " onCreate");

		init();
		/*
		 * Clear shared prefs for debugging
		 */
		// Prefs.getInstance(getApplicationContext()).clearPrefs();
		Intent intent = new Intent(StudIPAppActivity.this, SignInActivity.class);
		this.startActivity(intent);
		this.finish();
	}

	/**
	 * 
	 */
	private void init() {
		VolleyHttp.init(this);
	}
}
