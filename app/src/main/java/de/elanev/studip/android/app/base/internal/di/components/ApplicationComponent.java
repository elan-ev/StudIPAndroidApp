/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.base.internal.di.components;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Component;
import de.elanev.studip.android.app.AbstractStudIPApplication;
import de.elanev.studip.android.app.MainActivity;
import de.elanev.studip.android.app.StartupActivity;
import de.elanev.studip.android.app.authorization.domain.AuthService;
import de.elanev.studip.android.app.authorization.domain.CredentialsRepository;
import de.elanev.studip.android.app.authorization.domain.EndpointsRepository;
import de.elanev.studip.android.app.authorization.domain.SettingsRepository;
import de.elanev.studip.android.app.base.domain.executor.PostExecutionThread;
import de.elanev.studip.android.app.base.domain.executor.ThreadExecutor;
import de.elanev.studip.android.app.base.internal.di.modules.ApplicationModule;
import de.elanev.studip.android.app.base.internal.di.modules.NetworkModule;
import de.elanev.studip.android.app.base.navigation.Navigator;
import de.elanev.studip.android.app.base.presentation.view.activity.BaseActivity;
import de.elanev.studip.android.app.contacts.domain.ContactsRepository;
import de.elanev.studip.android.app.courses.domain.CoursesRepository;
import de.elanev.studip.android.app.courses.presentation.view.CourseDocumentsFragment;
import de.elanev.studip.android.app.courses.presentation.view.CourseViewActivity;
import de.elanev.studip.android.app.feedback.FeedbackActivity;
import de.elanev.studip.android.app.messages.domain.MessagesRepository;
import de.elanev.studip.android.app.messages.presentation.view.MessageDetailActivity;
import de.elanev.studip.android.app.news.domain.NewsRepository;
import de.elanev.studip.android.app.planner.domain.PlannerRepository;
import de.elanev.studip.android.app.planner.presentation.view.PlannerActivity;
import de.elanev.studip.android.app.user.domain.UserRepository;
import de.elanev.studip.android.app.user.presentation.view.UserDetailsActivity;
import de.elanev.studip.android.app.util.Prefs;
import de.elanev.studip.android.app.widget.BaseFragment;
import de.elanev.studip.android.app.widget.ReactiveListFragment;
import io.realm.RealmConfiguration;

/**
 * @author joern
 */

@Singleton
@Component(modules = {ApplicationModule.class, NetworkModule.class})
public interface ApplicationComponent {
  void inject(AbstractStudIPApplication target);

  void inject(StartupActivity target);

  void inject(BaseActivity target);

  void inject(MainActivity target);

  void inject(UserDetailsActivity userDetailsActivity);

  void inject(BaseFragment target);

  void inject(FeedbackActivity target);

  void inject(PlannerActivity target);

  void inject(CourseViewActivity target);

  //FIXME: Just workaround
  void inject(ReactiveListFragment target);

  void inject(MessageDetailActivity target);

  void inject(CourseDocumentsFragment target);

  // Expose to subcomponents
  Context context();

  Prefs prefs();

  Navigator navigator();

  RealmConfiguration realConfiguration();

  NewsRepository newsRepository();

  UserRepository userRepository();

  ContactsRepository contactsRepository();

  PlannerRepository plannerRepository();

  MessagesRepository messagesRepository();

  CoursesRepository coursesRepository();

  CredentialsRepository authorizationRepository();

  SettingsRepository settingsRepository();

  EndpointsRepository endpointsRepository();

  AuthService authService();

  ThreadExecutor threadExecutor();

  PostExecutionThread postExecutionThread();

  AbstractStudIPApplication abstractStudIPApplication();
}