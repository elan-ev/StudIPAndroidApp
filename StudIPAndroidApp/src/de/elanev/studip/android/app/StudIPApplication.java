package de.elanev.studip.android.app;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

/**
 * @author joern
 * 
 *         Extends the Application class to enable crash reports through ACRA
 */

public class StudIPApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();

		// Trigger initialization of Crashlytics
		Crashlytics.start(this);
	}
}
