/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.presentation.mapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.elanev.studip.android.app.base.internal.di.PerActivity;
import de.elanev.studip.android.app.courses.domain.DomainCourse;
import de.elanev.studip.android.app.courses.domain.DomainCourseModules;
import de.elanev.studip.android.app.courses.presentation.model.CourseModel;
import de.elanev.studip.android.app.courses.presentation.model.CourseModulesModel;
import de.elanev.studip.android.app.user.presentation.mapper.UserModelDataMapper;

/**
 * @author joern
 */

@PerActivity
public class CourseModelDataMapper {
  private final UserModelDataMapper userModelDataMapper;
  private final CourseAdditionalDataModelDataMapper courseAdditionalDataModelDataMappeer;
  private final SemesterModelDataMapper semesterModelDataMapper;

  @Inject public CourseModelDataMapper(UserModelDataMapper userModelDataMapper,
      CourseAdditionalDataModelDataMapper courseAdditionalDataModelDataMappeer,
      SemesterModelDataMapper semesterModelDataMapper) {
    this.userModelDataMapper = userModelDataMapper;
    this.courseAdditionalDataModelDataMappeer = courseAdditionalDataModelDataMappeer;
    this.semesterModelDataMapper = semesterModelDataMapper;
  }

  public List<CourseModel> transform(List<DomainCourse> domainCourses) {
    ArrayList<CourseModel> courseModels = new ArrayList<>(domainCourses.size());

    for (DomainCourse domainCourse : domainCourses) {
      courseModels.add(transform(domainCourse));
    }

    return courseModels;
  }

  private CourseModel transform(DomainCourse domainCourse) {
    CourseModel courseModel = new CourseModel();
    courseModel.setCourseId(domainCourse.getCourseId());
    courseModel.setStartTime(domainCourse.getStartTime());
    courseModel.setDurationTime(domainCourse.getDurationTime());
    courseModel.setTitle(domainCourse.getTitle());
    courseModel.setSubtitle(domainCourse.getSubtitle());
    courseModel.setModules(transform(domainCourse.getModules()));
    courseModel.setDescription(domainCourse.getDescription());
    courseModel.setLocation(domainCourse.getLocation());
    courseModel.setTeachers(userModelDataMapper.transform(domainCourse.getTeachers()));
    courseModel.setTutors(userModelDataMapper.transform(domainCourse.getTutors()));
    courseModel.setStudents(userModelDataMapper.transform(domainCourse.getStudents()));
    courseModel.setColor(domainCourse.getColor());
    courseModel.setType(domainCourse.getType());
    courseModel.setCourseAdditionalData(
        courseAdditionalDataModelDataMappeer.transform(domainCourse.getCourseAdditionalData()));
    courseModel.setSemester(semesterModelDataMapper.transform(domainCourse.getSemester()));

    return courseModel;
  }

  private CourseModulesModel transform(DomainCourseModules domainCourseModules) {
    CourseModulesModel courseModules = new CourseModulesModel();
    courseModules.setDocuments(domainCourseModules.isDocuments());
    courseModules.setSchedule(domainCourseModules.isSchedule());
    courseModules.setRecordings(domainCourseModules.isRecordings());
    courseModules.setParticipants(domainCourseModules.isParticipants());
    courseModules.setUnizensus(domainCourseModules.isUnizensus());
    courseModules.setForum(domainCourseModules.isForum());

    return courseModules;
  }
}
