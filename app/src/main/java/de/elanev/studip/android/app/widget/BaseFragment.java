/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import javax.inject.Inject;

import de.elanev.studip.android.app.AbstractStudIPApplication;
import de.elanev.studip.android.app.data.net.sync.SyncHelper;

/**
 * @author joern
 */
public class BaseFragment extends Fragment {

  @Inject protected Context mContext;
  @Inject protected SyncHelper mSyncHelper;

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    ((AbstractStudIPApplication)getActivity().getApplication()).getComponent().inject(this);
  }
}
