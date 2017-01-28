/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.base.presentation.view;

import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpBasePresenter;
import com.hannesdorfmann.mosby.mvp.MvpFragment;
import com.hannesdorfmann.mosby.mvp.MvpView;

import de.elanev.studip.android.app.base.internal.di.components.HasComponent;

/**
 * @author joern
 */

public abstract class BaseMvpFragment<V extends MvpView, P extends MvpBasePresenter<V>> extends
    MvpFragment<V, P> {

  protected void showToast(String message) {
    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT)
        .show();
  }

  /**
   * Gets a component for dependency injection by its type.
   */
  @SuppressWarnings("unchecked") protected <C> C getComponent(Class<C> componentType) {
    return componentType.cast(((HasComponent<C>) getActivity()).getComponent());
  }
}
