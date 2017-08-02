/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.planner.data.repository;

import java.util.Arrays;
import java.util.List;

import de.elanev.studip.android.app.planner.domain.Event;
import de.elanev.studip.android.app.planner.domain.PlannerRepository;
import rx.Observable;

import static de.elanev.studip.android.app.course.data.repository.MockCourseRepository.COURSE;

/**
 * @author joern
 */

public class MockPlannerRepository implements PlannerRepository {
  public static final Event VALID_EVENT = new Event("validEventId1", COURSE,
      (System.currentTimeMillis() + (24 * 60 * 60)) / 1000L,
      (System.currentTimeMillis() + (24 * 60 * 60) + (60 * 2)) / 1000L, "Valid event title",
      "A valid event", "Event Room1", "#efefef", "Event", COURSE.getCourseId(), false);
  public static final Event CANCELED_EVENT = new Event("canceledEventId1", COURSE,
      (System.currentTimeMillis() + (24 * 60 * 60)) / 1000L,
      (System.currentTimeMillis() + (24 * 60 * 60) + (60 * 2)) / 1000L, "Canceled event title",
      "A canceled event", "Event Room1", "#efefef", "Event", COURSE.getCourseId(), true);

  @Override public Observable<List<Event>> eventsList(boolean forceUpdate) {
    return Observable.just(Arrays.asList(VALID_EVENT, CANCELED_EVENT));
  }
}
