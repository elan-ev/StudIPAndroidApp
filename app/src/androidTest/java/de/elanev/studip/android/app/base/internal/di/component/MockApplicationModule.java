/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.base.internal.di.component;

import android.app.Application;

import de.elanev.studip.android.app.authorization.data.repository.MockSettingsRepository;
import de.elanev.studip.android.app.authorization.data.repository.SettingsDataRepository;
import de.elanev.studip.android.app.authorization.domain.SettingsRepository;
import de.elanev.studip.android.app.base.internal.di.modules.ApplicationModule;
import de.elanev.studip.android.app.course.data.repository.MockCourseRepository;
import de.elanev.studip.android.app.courses.data.repository.CoursesDataRepository;
import de.elanev.studip.android.app.courses.domain.CoursesRepository;
import de.elanev.studip.android.app.messages.data.repository.MessagesDataRepository;
import de.elanev.studip.android.app.messages.data.repository.MockMessagesRepository;
import de.elanev.studip.android.app.messages.domain.MessagesRepository;
import de.elanev.studip.android.app.news.data.repository.MockNewsRepository;
import de.elanev.studip.android.app.news.data.repository.NewsDataRepository;
import de.elanev.studip.android.app.news.domain.NewsRepository;
import de.elanev.studip.android.app.user.data.repository.MockUserRepository;
import de.elanev.studip.android.app.user.data.repository.UserDataRepository;
import de.elanev.studip.android.app.user.domain.UserRepository;

/**
 * @author joern
 */

public class MockApplicationModule extends ApplicationModule {
  public MockApplicationModule(Application application) {
    super(application);
  }

  @Override public NewsRepository provideNewsRepository(NewsDataRepository newsDataRepository) {
    return new MockNewsRepository();
  }

  @Override public UserRepository provideUserRepository(UserDataRepository userDataRepository) {
    return new MockUserRepository();
  }

  @Override public CoursesRepository provideCoursesRepository(
      CoursesDataRepository coursesDataRepository) {
    return new MockCourseRepository();
  }

  @Override public MessagesRepository provideMessagesRepository(
      MessagesDataRepository messagesDataRepository) {
    return new MockMessagesRepository();
  }

  @Override public SettingsRepository provideSettingsRepository(
      SettingsDataRepository settingsDataRepository) {
    return new MockSettingsRepository();
  }
}
