/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.planner.presentation.mapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.elanev.studip.android.app.base.internal.di.PerActivity;
import de.elanev.studip.android.app.courses.presentation.mapper.CourseModelDataMapper;
import de.elanev.studip.android.app.planner.domain.Event;
import de.elanev.studip.android.app.planner.presentation.model.PlanerEventModel;

/**
 * @author joern
 */
@PerActivity
public class PlanerModelDataMapper {
  private final CourseModelDataMapper dataMapper;

  @Inject PlanerModelDataMapper(CourseModelDataMapper dataMapper) {this.dataMapper = dataMapper;}

  public List<PlanerEventModel> transform(List<Event> events) {
    ArrayList<PlanerEventModel> planerEventModels = new ArrayList<>(events.size());

    for (Event event : events) {
      if (event != null) {
        planerEventModels.add(transform(event));
      }
    }

    return planerEventModels;
  }

  public PlanerEventModel transform(Event event) {
    if (event == null) return null;

    PlanerEventModel planerEventModel = new PlanerEventModel();
    planerEventModel.setEventId(event.getEventId());
    planerEventModel.setTitle(event.getTitle());
    planerEventModel.setDescription(event.getDescription());
    planerEventModel.setColor(event.getColor());
    planerEventModel.setRoom(event.getRoom());

    if (event.getCourse() == null) throw new IllegalStateException("Course must not be null!");
    planerEventModel.setCourse(dataMapper.transform(event.getCourse()));

    planerEventModel.setStart(event.getStart());
    planerEventModel.setEnd(event.getEnd());
    planerEventModel.setCanceled(event.isCanceled());

    return planerEventModel;
  }
}
