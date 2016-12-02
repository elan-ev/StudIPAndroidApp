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
import de.elanev.studip.android.app.planner.domain.Event;
import de.elanev.studip.android.app.planner.presentation.model.EventModel;

/**
 * @author joern
 */
@PerActivity
public class EventsDataMapper {

  @Inject EventsDataMapper() {}

  public List<EventModel> transform(List<Event> events) {
    ArrayList<EventModel> eventModels = new ArrayList<>(events.size());

    for (Event event : events) {
      if (event != null) {
        eventModels.add(transform(event));
      }
    }

    return eventModels;
  }

  public EventModel transform(Event event) {
    if (event == null) return null;

    EventModel eventModel = new EventModel();
    eventModel.setEventId(event.getEventId());
    eventModel.setTitle(event.getTitle());
    eventModel.setDescription(event.getDescription());
    eventModel.setColor(event.getColor());
    eventModel.setRoom(event.getRoom());
    eventModel.setCourse(event.getCourse());
    eventModel.setStart(event.getStart());
    eventModel.setEnd(event.getEnd());

    return eventModel;
  }
}
