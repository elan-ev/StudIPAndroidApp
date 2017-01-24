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
import de.elanev.studip.android.app.courses.domain.CourseOverview;
import de.elanev.studip.android.app.courses.domain.CourseUsers;
import de.elanev.studip.android.app.courses.domain.DomainCourse;
import de.elanev.studip.android.app.courses.domain.DomainCourseModules;
import de.elanev.studip.android.app.courses.presentation.model.CourseModel;
import de.elanev.studip.android.app.courses.presentation.model.CourseModulesModel;
import de.elanev.studip.android.app.courses.presentation.model.CourseOverviewModel;
import de.elanev.studip.android.app.courses.presentation.model.CourseScheduleModel;
import de.elanev.studip.android.app.courses.presentation.model.CourseUserModel;
import de.elanev.studip.android.app.courses.presentation.model.CourseUsersModel;
import de.elanev.studip.android.app.news.presentation.mapper.NewsModelDataMapper;
import de.elanev.studip.android.app.planner.domain.Event;
import de.elanev.studip.android.app.user.domain.User;
import de.elanev.studip.android.app.user.presentation.mapper.UserModelDataMapper;

/**
 * @author joern
 */

@PerActivity
public class CourseModelDataMapper {
  private final UserModelDataMapper userModelDataMapper;
  private final CourseAdditionalDataModelDataMapper courseAdditionalDataModelDataMappeer;
  private final SemesterModelDataMapper semesterModelDataMapper;
  private final NewsModelDataMapper newsMapper;

  @Inject public CourseModelDataMapper(UserModelDataMapper userModelDataMapper,
      CourseAdditionalDataModelDataMapper courseAdditionalDataModelDataMappeer,
      SemesterModelDataMapper semesterModelDataMapper, NewsModelDataMapper newsMapper) {
    this.userModelDataMapper = userModelDataMapper;
    this.courseAdditionalDataModelDataMappeer = courseAdditionalDataModelDataMappeer;
    this.semesterModelDataMapper = semesterModelDataMapper;
    this.newsMapper = newsMapper;
  }

  public List<CourseModel> transform(List<DomainCourse> domainCourses) {
    ArrayList<CourseModel> courseModels = new ArrayList<>(domainCourses.size());

    for (DomainCourse domainCourse : domainCourses) {
      courseModels.add(transform(domainCourse));
    }

    return courseModels;
  }

  public CourseModel transform(DomainCourse domainCourse) {
    CourseModel courseModel = new CourseModel();
    courseModel.setCourseId(domainCourse.getCourseId());
    courseModel.setStartTime(domainCourse.getStartTime());
    courseModel.setDurationTime(domainCourse.getDurationTime());
    courseModel.setTitle(domainCourse.getTitle());
    courseModel.setSubtitle(domainCourse.getSubtitle());
    courseModel.setModules(transform(domainCourse.getModules()));
    courseModel.setDescription(domainCourse.getDescription());
    courseModel.setLocation(domainCourse.getLocation());
    courseModel.setTeachers(userModelDataMapper.transform(domainCourse.getTeacherEntities()));
    courseModel.setTutors(userModelDataMapper.transform(domainCourse.getTutorEntities()));
    courseModel.setStudents(userModelDataMapper.transform(domainCourse.getStudentEntities()));
    courseModel.setColor(domainCourse.getColor());
    courseModel.setType(domainCourse.getType());
    courseModel.setTypeString(domainCourse.getTypeString());
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

  public CourseOverviewModel transform(CourseOverview courseOverview) {
    CourseOverviewModel courseOverviewModel = new CourseOverviewModel();
    courseOverviewModel.setCourse(transform(courseOverview.getCourse()));
    courseOverviewModel.setCourseEvent(transform(courseOverview.getEvent()));
    courseOverviewModel.setCourseNews(newsMapper.transform(courseOverview.getNewsItem()));

    return courseOverviewModel;
  }

  private CourseScheduleModel transform(Event event) {
    if (event == null) return null;

    CourseScheduleModel courseScheduleModel = new CourseScheduleModel();
    courseScheduleModel.setTitle(event.getTitle());
    courseScheduleModel.setDescription(event.getDescription());
    courseScheduleModel.setRoom(event.getRoom());
    courseScheduleModel.setCategory(event.getCategory());
    courseScheduleModel.setStart(event.getStart());
    courseScheduleModel.setEnd(event.getEnd());

    return courseScheduleModel;
  }

  public List<CourseScheduleModel> transformCourseEvents(List<Event> events) {
    ArrayList<CourseScheduleModel> courseScheduleModels = new ArrayList<>(events.size());

    for (Event event : events) {
      courseScheduleModels.add(transform(event));
    }

    return courseScheduleModels;
  }

  public CourseUsersModel transform(CourseUsers courseUsers) {
    CourseUsersModel courseUsersModel = new CourseUsersModel();
    courseUsersModel.setTeachers(transformUsers(courseUsers.getTeachers()));
    courseUsersModel.setTutors(transformUsers(courseUsers.getTutors()));
    courseUsersModel.setStudents(transformUsers(courseUsers.getStudents()));

    return courseUsersModel;
  }

  private List<CourseUserModel> transformUsers(List<User> courseUsers) {
    if (courseUsers == null || courseUsers.size() == 0) return null;

    ArrayList<CourseUserModel> courseUserModel = new ArrayList<>(courseUsers.size());

    for (User courseUser : courseUsers) {
      CourseUserModel user = transform(courseUser);
      courseUserModel.add(user);
    }

    return courseUserModel;
  }

  private CourseUserModel transform(User courseUser) {
    if (courseUser == null) return null;

    CourseUserModel courseUserModel = new CourseUserModel();
    courseUserModel.setUserId(courseUser.getUserId());
    courseUserModel.setName(courseUser.getFullname());
    courseUserModel.setAvatarUrl(courseUser.getAvatarUrl());

    return courseUserModel;
  }
}
