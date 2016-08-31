/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.planner.data.repository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.planner.data.entity.EventEntity;
import de.elanev.studip.android.app.planner.domain.Event;

/**
 * @author joern
 */
@Singleton
class EventsEntityDataMapper {

  @Inject public EventsEntityDataMapper() {}

  public List<Event> transform(List<EventEntity> eventEntities) {
    ArrayList<Event> eventArrayList = new ArrayList<>();

    for (EventEntity eventEntity : eventEntities) {
      if (eventEntity != null) {
        eventArrayList.add(transform(eventEntity));
      }
    }

    return eventArrayList;
  }

  private Event transform(EventEntity eventEntity) {
    Event event = new Event();
    event.setEventId(eventEntity.getEventId());
    event.setTitle(eventEntity.getTitle());
    event.setDescription(eventEntity.getDescription());
    event.setColor(eventEntity.getColor());
    event.setRoom(eventEntity.getRoom());
    event.setCourse(eventEntity.getCourse());
    event.setStart(eventEntity.getStart());
    event.setEnd(eventEntity.getEnd());

    return event;
  }
}
