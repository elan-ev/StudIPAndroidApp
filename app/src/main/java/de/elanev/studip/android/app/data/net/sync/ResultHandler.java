/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package de.elanev.studip.android.app.data.net.sync;

import android.content.ContentProviderOperation;

import java.util.ArrayList;

/**
 * @author joern
 *
 */
public interface ResultHandler {
	public ArrayList<ContentProviderOperation> parse();
}
