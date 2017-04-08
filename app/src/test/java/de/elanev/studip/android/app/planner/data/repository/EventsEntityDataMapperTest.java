/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.planner.data.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import dagger.Module;
import de.elanev.studip.android.app.courses.data.entity.Course;
import de.elanev.studip.android.app.courses.data.repository.CourseEntityDataMapper;
import de.elanev.studip.android.app.planner.data.entity.EventEntity;
import de.elanev.studip.android.app.planner.domain.Event;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.mockito.Mockito.mock;

/**
 * @author joern
 */
public class EventsEntityDataMapperTest {
  private EventsEntityDataMapper eventsEntityDataMapper;

  @Mock private Course mockCourse;
  @Mock private CourseEntityDataMapper courseEntityDataMapper;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    eventsEntityDataMapper = new EventsEntityDataMapper(courseEntityDataMapper);
  }

  @Test public void shouldTransformEventEntitiesToEvents() throws Exception {
    EventEntity mockEventEntity1 = mock(EventEntity.class);
    EventEntity mockEventEntity2 = mock(EventEntity.class);

    List<EventEntity> list = new ArrayList<>(5);
    list.add(mockEventEntity1);
    list.add(mockEventEntity2);

    List<Event> events = eventsEntityDataMapper.transform(list);

    assertThat(events.toArray()[0], is(instanceOf(Event.class)));
    assertThat(events.toArray()[1], is(instanceOf(Event.class)));
    assertThat(events.size(), is(2));
  }

}