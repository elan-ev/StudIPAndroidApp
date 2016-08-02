/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.base.view;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import com.hannesdorfmann.mosby.mvp.viewstate.lce.MvpLceViewStateFragment;

import de.elanev.studip.android.app.base.internal.di.components.HasComponent;

/**
 * @author joern
 */
public abstract class BaseLceFragment<CV extends View, M, V extends MvpLceView<M>, P extends MvpPresenter<V>> extends
    MvpLceViewStateFragment<CV, M, V, P> {
  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

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
