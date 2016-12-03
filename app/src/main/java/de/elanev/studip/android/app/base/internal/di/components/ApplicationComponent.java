/*
 * Copyright (c) 2016 ELAN e.V.
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
import de.elanev.studip.android.app.auth.ServerListFragment;
import de.elanev.studip.android.app.auth.SignInFragment;
import de.elanev.studip.android.app.auth.SignInSyncFragment;
import de.elanev.studip.android.app.authorization.presentation.view.LogoutActivity;
import de.elanev.studip.android.app.base.domain.executor.PostExecutionThread;
import de.elanev.studip.android.app.base.domain.executor.ThreadExecutor;
import de.elanev.studip.android.app.base.internal.di.modules.ApplicationModule;
import de.elanev.studip.android.app.base.internal.di.modules.NetworkModule;
import de.elanev.studip.android.app.base.presentation.view.activity.BaseActivity;
import de.elanev.studip.android.app.contacts.domain.ContactsRepository;
import de.elanev.studip.android.app.courses.domain.CoursesRepository;
import de.elanev.studip.android.app.feedback.FeedbackActivity;
import de.elanev.studip.android.app.messages.domain.MessagesRepository;
import de.elanev.studip.android.app.messages.presentation.view.MessageDetailActivity;
import de.elanev.studip.android.app.news.domain.NewsRepository;
import de.elanev.studip.android.app.planner.domain.PlannerRepository;
import de.elanev.studip.android.app.user.domain.UserRepository;
import de.elanev.studip.android.app.user.presentation.view.UserDetailsActivity;
import de.elanev.studip.android.app.util.Prefs;
import de.elanev.studip.android.app.widget.BaseFragment;
import de.elanev.studip.android.app.widget.ReactiveListFragment;

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

  //TODO: Make it extend BaseFragment
  void inject(SignInFragment target);

  void inject(ServerListFragment target);

  void inject(SignInSyncFragment target);

  //FIXME: Just workaround
  void inject(ReactiveListFragment target);

  void inject(MessageDetailActivity target);

  void inject(LogoutActivity target);

  // Expose to subcomponents
  Context context();

  Prefs prefs();

  NewsRepository newsRepository();

  UserRepository userRepository();

  ContactsRepository contactsRepository();

  PlannerRepository plannerRepository();

  MessagesRepository messagesRepository();

  CoursesRepository coursesRepository();

  ThreadExecutor threadExecutor();

  PostExecutionThread postExecutionThread();

}
