/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.data.datamodel.Course;
import de.elanev.studip.android.app.data.datamodel.RealmCourseEntity;

/**
 * @author joern
 */
@Singleton
public class CourseEntityDataMapper {

  @Inject public CourseEntityDataMapper() {}

  public Course transform(RealmCourseEntity realmCourseEntity) {
    if (realmCourseEntity == null) return null;

    Course course = new Course();
    course.setCourseId(realmCourseEntity.getCourseId());
    course.setTitle(realmCourseEntity.getTitle());
    course.setSubtitle(realmCourseEntity.getSubtitle());
    course.setDescription(realmCourseEntity.getDescription());
    course.setLocation(realmCourseEntity.getLocation());
    course.setCourseId(realmCourseEntity.getCourseId());
    course.setType(realmCourseEntity.getType());

    return course;
  }

  public RealmCourseEntity transformToRealm(Course course) {
    if (course == null) return null;

    RealmCourseEntity realCourseEntity = new RealmCourseEntity();
    realCourseEntity.setCourseId(course.getCourseId());
    realCourseEntity.setTitle(course.getTitle());
    realCourseEntity.setSubtitle(course.getSubtitle());
    realCourseEntity.setDescription(course.getDescription());
    realCourseEntity.setLocation(course.getLocation());
    realCourseEntity.setCourseId(course.getCourseId());
    realCourseEntity.setType(course.getType());

    return realCourseEntity;
  }
}
