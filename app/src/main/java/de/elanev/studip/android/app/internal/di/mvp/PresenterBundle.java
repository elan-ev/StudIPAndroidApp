/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.internal.di.mvp;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashMap;

/**
 * @author joern
 */
public class PresenterBundle {
  private HashMap<String, Object> mData = new HashMap<>();

  @Nullable public String getString(String id, String defaultVal) {
    if (mData.containsKey(id)) {
      return mData.get(id)
          .toString();
    } else {
      return defaultVal;
    }
  }

  public void putString(@NonNull String id, @NonNull String data) {
    mData.put(id, data);
  }
}
