/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.planner.presentation.view;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;

import java.util.List;

import de.elanev.studip.android.app.planner.presentation.model.EventModel;

/**
 * @author joern
 */
public interface PlannerListView extends MvpLceView<List<EventModel>> {
  void viewEvent(EventModel eventModel);
}
