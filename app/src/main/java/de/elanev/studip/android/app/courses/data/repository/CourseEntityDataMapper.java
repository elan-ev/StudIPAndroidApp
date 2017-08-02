/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.data.repository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.base.data.db.realm.RealmDataMapper;
import de.elanev.studip.android.app.courses.data.entity.Course;
import de.elanev.studip.android.app.courses.data.entity.CourseModules;
import de.elanev.studip.android.app.courses.data.entity.RealmCourseEntity;
import de.elanev.studip.android.app.courses.data.entity.RealmCourseModulesEntity;
import de.elanev.studip.android.app.courses.domain.DomainCourse;
import de.elanev.studip.android.app.courses.domain.DomainCourseModules;
import de.elanev.studip.android.app.user.data.entity.UserEntityDataMapper;
import io.realm.RealmList;

/**
 * @author joern
 */
@Singleton
public class CourseEntityDataMapper {

  private final UserEntityDataMapper userEntityDataMapper;
  private final SemesterEntityDataMapper semesterEntityDataMapper;
  private final CourseAdditionalDataEntityDataMapper courseAdditionalDataEntityDataMapper;
  private final RealmDataMapper realmMapper;

  @Inject public CourseEntityDataMapper(UserEntityDataMapper userEntityDataMapper,
      SemesterEntityDataMapper semesterEntityDataMapper,
      CourseAdditionalDataEntityDataMapper courseAdditionalDataEntityDataMapper,
      RealmDataMapper realmMapper) {
    this.userEntityDataMapper = userEntityDataMapper;
    this.semesterEntityDataMapper = semesterEntityDataMapper;
    this.courseAdditionalDataEntityDataMapper = courseAdditionalDataEntityDataMapper;
    this.realmMapper = realmMapper;
  }

  public List<Course> transformFromRealm(List<RealmCourseEntity> realmCourseEntities) {
    ArrayList<Course> courses = new ArrayList<>(realmCourseEntities.size());

    for (RealmCourseEntity realCourseEntity : realmCourseEntities) {
      courses.add(transformFromRealm(realCourseEntity));
    }

    return courses;
  }

  public Course transformFromRealm(RealmCourseEntity realmCourseEntity) {
    if (realmCourseEntity == null) return null;

    Course course = new Course();
    course.setCourseId(realmCourseEntity.getCourseId());
    course.setStartTime(realmCourseEntity.getStartTime());
    course.setDurationTime(realmCourseEntity.getDurationTime());
    course.setTitle(realmCourseEntity.getTitle());
    course.setSubtitle(realmCourseEntity.getSubtitle());
    course.setDescription(realmCourseEntity.getDescription());
    course.setLocation(realmCourseEntity.getLocation());
    course.setColor(realmCourseEntity.getColor());
    course.setType(realmCourseEntity.getType());
    course.setTeachers(realmMapper.transformFromRealm(realmCourseEntity.getTeachers()));
    course.setTutors(realmMapper.transformFromRealm(realmCourseEntity.getTutors()));
    course.setStudents(realmMapper.transformFromRealm(realmCourseEntity.getStudents()));
    course.setCourseAdditionalData(courseAdditionalDataEntityDataMapper.transformFromRealm(
        realmCourseEntity.getCourseAdditionalData()));
    course.setModules(transformFromRealm(realmCourseEntity.getModules()));
    course.setSemesterEntity(
        semesterEntityDataMapper.transformFromRealm(realmCourseEntity.getSemester()));

    return course;
  }

  private CourseModules transformFromRealm(RealmCourseModulesEntity realmCourseModulesEntity) {
    CourseModules courseModules = new CourseModules();
    courseModules.setDocuments(realmCourseModulesEntity.isDocuments());
    courseModules.setSchedule(realmCourseModulesEntity.isSchedule());
    courseModules.setRecordings(realmCourseModulesEntity.isRecordings());
    courseModules.setParticipants(realmCourseModulesEntity.isParticipants());
    courseModules.setUnizensus(realmCourseModulesEntity.isUnizensus());
    courseModules.setForum(realmCourseModulesEntity.isForum());

    return courseModules;
  }

  public List<DomainCourse> transform(List<Course> courses) {
    ArrayList<DomainCourse> domainCourses = new ArrayList<>(courses.size());

    for (Course course : courses) {
      domainCourses.add(transform(course));
    }

    return domainCourses;
  }

  public DomainCourse transform(Course course) {
    DomainCourse domainCourse = new DomainCourse();
    domainCourse.setCourseId(course.getCourseId());
    domainCourse.setStartTime(course.getStartTime());
    domainCourse.setDurationTime(course.getDurationTime());
    domainCourse.setTitle(course.getTitle());
    domainCourse.setSubtitle(course.getSubtitle());
    domainCourse.setModules(transform(course.getModules()));
    domainCourse.setDescription(course.getDescription());
    domainCourse.setLocation(course.getLocation());
    domainCourse.setTeachers(course.getTeachers());
    domainCourse.setTutors(course.getTutors());
    domainCourse.setStudents(course.getStudents());
    domainCourse.setColor(course.getColor());
    domainCourse.setType(course.getType());
    domainCourse.setCourseAdditionalData(
        courseAdditionalDataEntityDataMapper.transform(course.getCourseAdditionalData()));
    domainCourse.setSemester(semesterEntityDataMapper.transform(course.getSemesterEntity()));

    return domainCourse;
  }

  private DomainCourseModules transform(CourseModules modules) {
    DomainCourseModules domainCourseModules = new DomainCourseModules();
    domainCourseModules.setDocuments(modules.isDocuments());
    domainCourseModules.setSchedule(modules.isSchedule());
    domainCourseModules.setRecordings(modules.isRecordings());
    domainCourseModules.setParticipants(modules.isParticipants());
    domainCourseModules.setUnizensus(modules.isUnizensus());
    domainCourseModules.setForum(modules.isForum());

    return domainCourseModules;
  }

  public RealmList<RealmCourseEntity> transformToRealm(List<Course> courses) {
    RealmList<RealmCourseEntity> realmCourseEntities = new RealmList<>();

    for (Course course : courses) {
      realmCourseEntities.add(transformToRealm(course));
    }

    return realmCourseEntities;
  }

  public RealmCourseEntity transformToRealm(Course course) {
    if (course == null) return null;

    RealmCourseEntity realmCourseEntity = new RealmCourseEntity();
    realmCourseEntity.setCourseId(course.getCourseId());
    realmCourseEntity.setDurationTime(course.getDurationTime());
    realmCourseEntity.setStartTime(course.getStartTime());
    realmCourseEntity.setTitle(course.getTitle());
    realmCourseEntity.setSubtitle(course.getSubtitle());
    realmCourseEntity.setDescription(course.getDescription());
    realmCourseEntity.setLocation(course.getLocation());
    realmCourseEntity.setColor(course.getColor());
    realmCourseEntity.setType(course.getType());
    realmCourseEntity.setTeachers(realmMapper.transformToRealm(course.getTeachers()));
    realmCourseEntity.setTutors(realmMapper.transformToRealm(course.getTutors()));
    realmCourseEntity.setStudents(realmMapper.transformToRealm(course.getStudents()));
    realmCourseEntity.setCourseAdditionalData(
        courseAdditionalDataEntityDataMapper.transformToRealm(course.getCourseAdditionalData()));
    realmCourseEntity.setModules(transformToRealm(course.getModules()));
    realmCourseEntity.setSemester(
        semesterEntityDataMapper.transformToRealm(course.getSemesterEntity()));

    return realmCourseEntity;
  }

  private RealmCourseModulesEntity transformToRealm(CourseModules modules) {
    RealmCourseModulesEntity realmCourseModulesEntity = new RealmCourseModulesEntity();
    realmCourseModulesEntity.setDocuments(modules.isDocuments());
    realmCourseModulesEntity.setSchedule(modules.isSchedule());
    realmCourseModulesEntity.setRecordings(modules.isRecordings());
    realmCourseModulesEntity.setParticipants(modules.isParticipants());
    realmCourseModulesEntity.setUnizensus(modules.isUnizensus());
    realmCourseModulesEntity.setForum(modules.isForum());

    return realmCourseModulesEntity;
  }
}
