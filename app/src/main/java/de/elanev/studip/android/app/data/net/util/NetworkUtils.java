/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package de.elanev.studip.android.app.data.net.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Convenience class for accessing the network connectivity status
 * 
 * @author joern
 * 
 */
public class NetworkUtils {
	public static final int NOT_CONNECTED = 100;
	public static final int WIFI_CONNECTED = 101;
	public static final int MOBILE_CONNECTED = 102;

	/**
	 * Checks the devices network connectivity status
	 * 
	 * @param context
	 *            Execution context
	 * @return connectivity status
	 */
	public static int getConnectivityStatus(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		if (networkInfo != null) {
			if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI)
				return WIFI_CONNECTED;

			if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE)
				return MOBILE_CONNECTED;
		}

		return NOT_CONNECTED;
	}
}
