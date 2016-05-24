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

import de.elanev.studip.android.app.BaseView;

/**
 * @author joern
 */
public interface Presenter<T extends BaseView> {
  void onCreate(@Nullable PresenterBundle bundle);

  void onSaveInstanceState(@NonNull PresenterBundle bundle);

  void onDestroy();

  void bindView(T view);

  void unbindView();
}
