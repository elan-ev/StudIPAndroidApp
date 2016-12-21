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
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import de.elanev.studip.android.app.courses.presentation.mapper.CourseModelDataMapper;
import de.elanev.studip.android.app.planner.domain.Event;
import de.elanev.studip.android.app.planner.presentation.model.PlannerEventModel;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.mockito.Mockito.mock;

/**
 * @author joern
 */
public class PlannerModelDataMapperTest {
  @Mock CourseModelDataMapper mockCourseModelDataMapper;

  private PlannerModelDataMapper plannerModelDataMapper;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);
    
    plannerModelDataMapper = new PlannerModelDataMapper(mockCourseModelDataMapper);
  }

  @Test public void transform() throws Exception {
    Event mockEvent1 = mock(Event.class);
    Event mockEvent2 = mock(Event.class);

    List<Event> list = new ArrayList<>(5);
    list.add(mockEvent1);
    list.add(mockEvent2);

    List<PlannerEventModel> plannerEventModels = plannerModelDataMapper.transform(list);

    assertThat(plannerEventModels.toArray()[0], is(instanceOf(PlannerEventModel.class)));
    assertThat(plannerEventModels.toArray()[1], is(instanceOf(PlannerEventModel.class)));
    assertThat(plannerEventModels.size(), is(2));
  }

}