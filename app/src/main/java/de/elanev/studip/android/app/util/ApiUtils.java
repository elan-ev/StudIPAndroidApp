/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package de.elanev.studip.android.app.util;

import android.os.Build;

/**
 * Convenience class for easier performing API checks
 *
 * @author joern
 */
public class ApiUtils {

    /*
     * Checks if the device Android API Version is over 11 (Android 3.0, Honeycomb)
     */
    public static boolean isOverApi11() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB);
    }

    /*
     * Checks if the device Android API Version is over 15 (Android 4.0, ICS)
	 */
    public static boolean isOverApi14() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH);
    }

    /*
     * Checks if the device Android API Version is over 16 (Android 4.0.3, ICS MR1)
	 */
    public static boolean isOverApi15() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1);
    }

    /*
     * Checks if the device Android API Version is over 16 (Android 4.1, Jelly Bean)
	 */
    public static boolean isOverApi16() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN);
    }

    /*
     * Checks if the device Android API Version is over 17 (Android 4.2, Jelly Bean MR1)
	 */
    public static boolean isOverApi17() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1);
    }

    /*
     * Checks if the device Android API Version is over 18 (Android 4.3, Jelly Bean MR2)
	 */
    public static boolean isOverApi18() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2);
    }
}
