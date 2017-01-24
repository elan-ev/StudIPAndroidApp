/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.presentation.view;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;

import java.util.List;

import de.elanev.studip.android.app.authorization.presentation.model.EndpointModel;

/**
 * @author joern
 */

public interface ServerListView extends MvpLceView<List<EndpointModel>> {
  void signInTo(EndpointModel endpointModel);
}
