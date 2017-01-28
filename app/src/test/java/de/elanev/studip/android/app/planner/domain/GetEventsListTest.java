/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.planner.domain;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import de.elanev.studip.android.app.base.domain.executor.PostExecutionThread;
import de.elanev.studip.android.app.base.domain.executor.ThreadExecutor;
import de.elanev.studip.android.app.courses.domain.CoursesRepository;
import de.elanev.studip.android.app.courses.domain.DomainCourse;
import rx.Observable;
import rx.schedulers.Schedulers;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * @author joern
 */
public class GetEventsListTest {
  private GetEventsList getEventsList;
  @Mock private PlannerRepository mockPlannerRepository;
  @Mock private CoursesRepository mockCoursesRepository;
  @Mock private ThreadExecutor mockThreadExecutor;
  @Mock private PostExecutionThread mockPostExecutionThread;


  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    getEventsList = new GetEventsList(mockPlannerRepository, mockThreadExecutor,
        mockPostExecutionThread, mockCoursesRepository);

  }

  @Test public void buildUseCaseObservable() throws Exception {
    given(mockThreadExecutor.getScheduler()).willReturn(Schedulers.immediate());
    given(mockPostExecutionThread.getScheduler()).willReturn(Schedulers.immediate());

    List<Event> mockEventList = new ArrayList<>(1);
    mockEventList.add(mock(Event.class));
    given(mockPlannerRepository.eventsList(true)).willReturn(Observable.just(mockEventList));

    List<DomainCourse> mockCoursesList = new ArrayList<>(1);
    mockCoursesList.add(mock(DomainCourse.class));
    given(mockCoursesRepository.courses(true)).willReturn(Observable.just(mockCoursesList));

    getEventsList.buildUseCaseObservable(true);

    verify(mockPlannerRepository).eventsList(true);
    verifyNoMoreInteractions(mockPlannerRepository);

    verifyZeroInteractions(mockThreadExecutor);
    verifyZeroInteractions(mockPostExecutionThread);
  }

}