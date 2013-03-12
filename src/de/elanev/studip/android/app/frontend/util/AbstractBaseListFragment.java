/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.frontend.util;

import android.database.Cursor;
import android.widget.BaseAdapter;

import com.actionbarsherlock.app.SherlockListFragment;

/**
 * @author joern
 * 
 */
public abstract class AbstractBaseListFragment extends SherlockListFragment {
	public abstract BaseAdapter getNewListAdapter();

	public abstract Cursor getNewCursor();
}
