/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.planner.presentation.mapper;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import de.elanev.studip.android.app.planner.domain.Event;
import de.elanev.studip.android.app.planner.presentation.model.EventModel;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.mockito.Mockito.mock;

/**
 * @author joern
 */
public class EventsDataMapperTest {
  private EventsDataMapper eventsDataMapper;

  @Before public void setUp() throws Exception {
    eventsDataMapper = new EventsDataMapper();
  }

  @Test public void transform() throws Exception {
    Event mockEvent1 = mock(Event.class);
    Event mockEvent2 = mock(Event.class);

    List<Event> list = new ArrayList<>(5);
    list.add(mockEvent1);
    list.add(mockEvent2);

    List<EventModel> eventModels = eventsDataMapper.transform(list);

    assertThat(eventModels.toArray()[0], is(instanceOf(EventModel.class)));
    assertThat(eventModels.toArray()[1], is(instanceOf(EventModel.class)));
    assertThat(eventModels.size(), is(2));
  }

}