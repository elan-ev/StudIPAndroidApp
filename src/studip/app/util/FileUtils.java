/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package studip.app.util;

import java.text.DecimalFormat;

/**
 * Utilities for working with files
 * 
 * @author joern
 */
public class FileUtils {

    /**
     * Prints human readable file size
     * 
     * @param size
     *            unreadable size in long
     * @return readable size string
     */
    public static String readableFileSize(long size) {
	if (size <= 0)
	    return "0";
	final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
	int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
	return new DecimalFormat("#,##0.#").format(size
		/ Math.pow(1024, digitGroups))
		+ " " + units[digitGroups];
    }
}
