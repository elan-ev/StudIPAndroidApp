/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.internal.di;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import de.elanev.studip.android.app.authorization.domain.SettingsRepository;
import de.elanev.studip.android.app.base.UseCase;
import de.elanev.studip.android.app.base.domain.executor.PostExecutionThread;
import de.elanev.studip.android.app.base.domain.executor.ThreadExecutor;
import de.elanev.studip.android.app.base.internal.di.PerActivity;
import de.elanev.studip.android.app.courses.domain.CoursesRepository;
import de.elanev.studip.android.app.courses.domain.GetCourseCensusUrl;
import de.elanev.studip.android.app.courses.domain.GetCourseList;
import de.elanev.studip.android.app.courses.domain.GetCourseOverview;
import de.elanev.studip.android.app.courses.domain.GetCourseSchedule;
import de.elanev.studip.android.app.courses.domain.GetCourseUsers;
import de.elanev.studip.android.app.news.domain.GetNewsList;
import de.elanev.studip.android.app.user.domain.UserRepository;

/**
 * @author joern
 */

@Module
public class CoursesModule {
  private String id = "";

  public CoursesModule() {}

  public CoursesModule(String id) {
    this.id = id;
  }

  @Provides @PerActivity @Named("courseList") UseCase providesGetCourseListUseCase(
      GetCourseList getCourseList) {

    return getCourseList;
  }

  @Provides @PerActivity @Named("courseOverview") UseCase providesGetCourseOverviewUseCase(
      GetNewsList getNewsList, CoursesRepository coursesRepository, UserRepository userRepository,
      SettingsRepository settingsRepository, ThreadExecutor threadExecutor,
      PostExecutionThread postExecutionThread) {

    return new GetCourseOverview(this.id, getNewsList, coursesRepository, threadExecutor,
        postExecutionThread, userRepository, settingsRepository);
  }

  @Provides @PerActivity @Named("getCourseSchedule") UseCase providesGetCourseScheduleUseCase(
      CoursesRepository coursesRepository, ThreadExecutor threadExecutor,
      PostExecutionThread postExecutionThread) {

    return new GetCourseSchedule(this.id, coursesRepository, threadExecutor, postExecutionThread);
  }

  @Provides @PerActivity @Named("getCourseUsers") UseCase providesGetCourseUsersUseCase(
      UserRepository userRepository, CoursesRepository coursesRepository,
      ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {

    return new GetCourseUsers(this.id, userRepository, coursesRepository, threadExecutor,
        postExecutionThread);
  }

  @Provides @PerActivity @Named("getCourseCensusUrl") UseCase providesCourseCensusUrl(
      CoursesRepository coursesRepository, ThreadExecutor threadExecutor,
      PostExecutionThread postExecutionThread) {

    return new GetCourseCensusUrl(this.id, coursesRepository, threadExecutor, postExecutionThread);
  }
}
