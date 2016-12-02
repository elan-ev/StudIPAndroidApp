/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.data.repository.datastore;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.courses.data.entity.Course;
import de.elanev.studip.android.app.data.net.services.StudIpLegacyApiService;
import de.elanev.studip.android.app.planner.data.entity.EventEntity;
import rx.Observable;

/**
 * @author joern
 */
@Singleton
public class CoursesCloudDataStore implements CoursesDataStore {
  private final StudIpLegacyApiService apiService;

  @Inject public CoursesCloudDataStore(StudIpLegacyApiService apiService) {
    this.apiService = apiService;
  }

  @Override public Observable<List<Course>> courses() {
    return apiService.getCourses();
  }

  @Override public Observable<Course> course(String courseId) {
    return apiService.getCourse(courseId);
  }

  @Override public Observable<List<EventEntity>> courseEvents(String courseId) {
    return apiService.getEvents(courseId);
  }
}
