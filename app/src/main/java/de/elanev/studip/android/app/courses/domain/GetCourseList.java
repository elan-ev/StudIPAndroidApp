/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.domain;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import de.elanev.studip.android.app.authorization.domain.SettingsRepository;
import de.elanev.studip.android.app.authorization.domain.model.Settings;
import de.elanev.studip.android.app.base.UseCase;
import de.elanev.studip.android.app.base.domain.executor.PostExecutionThread;
import de.elanev.studip.android.app.base.domain.executor.ThreadExecutor;
import rx.Observable;

/**
 * @author joern
 */
public class GetCourseList extends UseCase<List<DomainCourse>> {
  private final CoursesRepository coursesRepository;
  private final SettingsRepository settingsRepository;

  @Inject public GetCourseList(CoursesRepository coursesRepository, ThreadExecutor threadExecutor,
      PostExecutionThread postExecutionThread, SettingsRepository settingsRepository) {
    super(threadExecutor, postExecutionThread);

    this.coursesRepository = coursesRepository;
    this.settingsRepository = settingsRepository;
  }

  @Override protected Observable<List<DomainCourse>> buildUseCaseObservable(boolean forceUpdate) {
    Observable<Settings> settingsObs = settingsRepository.studipSettings(forceUpdate);
    Observable<List<DomainCourse>> coursesObs = coursesRepository.courses(forceUpdate);

    return settingsObs.flatMap(settings -> coursesObs.flatMap(
        domainCourses -> Observable.from(domainCourses)
            .map(domainCourse -> {
              HashMap<Integer, Settings.SeminarTypeData> semTypes = settings.getSemTypes();
              Settings.SeminarTypeData typeData = semTypes.get(domainCourse.getType());

              if (typeData != null) {
                domainCourse.setTypeString(typeData.getName());
              }

              return domainCourse;
            })))
        .toList();
  }
}
