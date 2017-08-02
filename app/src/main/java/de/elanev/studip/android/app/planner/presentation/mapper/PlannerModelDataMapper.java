/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.planner.presentation.mapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.elanev.studip.android.app.base.internal.di.PerFragment;
import de.elanev.studip.android.app.courses.presentation.mapper.CourseModelDataMapper;
import de.elanev.studip.android.app.planner.domain.Event;
import de.elanev.studip.android.app.planner.presentation.model.PlannerEventModel;

/**
 * @author joern
 */
@PerFragment
public class PlannerModelDataMapper {
  private final CourseModelDataMapper dataMapper;

  @Inject PlannerModelDataMapper(CourseModelDataMapper dataMapper) {this.dataMapper = dataMapper;}

  public List<PlannerEventModel> transform(List<Event> events) {
    ArrayList<PlannerEventModel> plannerEventModels = new ArrayList<>(events.size());

    for (Event event : events) {
      if (event != null) {
        plannerEventModels.add(transform(event));
      }
    }

    return plannerEventModels;
  }

  public PlannerEventModel transform(Event event) {
    if (event == null) return null;

    PlannerEventModel plannerEventModel = new PlannerEventModel();
    plannerEventModel.setEventId(event.getEventId());
    plannerEventModel.setTitle(event.getTitle());
    plannerEventModel.setDescription(event.getDescription());
    plannerEventModel.setColor(event.getColor());
    plannerEventModel.setRoom(event.getRoom());

    if (event.getCourse() == null) throw new IllegalStateException("Course must not be null!");
    plannerEventModel.setCourse(dataMapper.transform(event.getCourse()));

    plannerEventModel.setStart(event.getStart());
    plannerEventModel.setEnd(event.getEnd());
    plannerEventModel.setCanceled(event.isCanceled());

    return plannerEventModel;
  }
}
