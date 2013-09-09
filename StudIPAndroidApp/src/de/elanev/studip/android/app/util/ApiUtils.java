/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.util;

import android.os.Build;

/**
 * Convenience class for easier performing API checks
 * 
 * @author joern
 * 
 */
public class ApiUtils {

	/*
	 * Checks if the device Android API Version is over 11 to enable advanced
	 * features.
	 */
	public static boolean isOverApi11() {
		return (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB);
	}
}
