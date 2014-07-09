/*
 * Copyright (c) 2014 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.frontend.courses;


import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.actionbarsherlock.app.SherlockListFragment;

/**
 * Fragment that loads the list of recordings for a specific course and displays it.
 *
 * @author Jörn
 */
public class CourseRecordingsFragment extends SherlockListFragment implements
    LoaderManager.LoaderCallbacks<Cursor> {
  @Override public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
    return null;
  }

  @Override public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

  }

  @Override public void onLoaderReset(Loader<Cursor> cursorLoader) {

  }
}
