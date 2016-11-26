/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.base.internal.di.modules;

import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.elanev.studip.android.app.base.data.executor.ThreadExecutorImpl;
import de.elanev.studip.android.app.base.domain.executor.PostExecutionThread;
import de.elanev.studip.android.app.base.domain.executor.ThreadExecutor;
import de.elanev.studip.android.app.base.presentation.executor.PostExecutionThreadImpl;
import de.elanev.studip.android.app.contacts.data.repository.ContactsDataRepository;
import de.elanev.studip.android.app.contacts.domain.ContactsRepository;
import de.elanev.studip.android.app.messages.data.repository.MessagesDataRepository;
import de.elanev.studip.android.app.messages.domain.MessagesRepository;
import de.elanev.studip.android.app.news.data.repository.NewsDataRepository;
import de.elanev.studip.android.app.news.domain.NewsRepository;
import de.elanev.studip.android.app.planner.data.repository.PlannerDataRepository;
import de.elanev.studip.android.app.planner.domain.PlannerRepository;
import de.elanev.studip.android.app.user.data.repository.UserDataRepository;
import de.elanev.studip.android.app.user.domain.UserRepository;
import de.elanev.studip.android.app.util.Prefs;
import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * @author joern
 */
@Module
public class ApplicationModule {

  private Application mApplication;

  public ApplicationModule(Application application) {
    this.mApplication = application;
  }

  // Android
  @Provides @Singleton public Context provideContext() {
    return mApplication;
  }

  @Provides @Singleton public ContentResolver provideContentResolver(Context context) {
    return context.getContentResolver();
  }

  // Prefs
  @Provides @Singleton public Prefs providePrefs(Context context) {
    return Prefs.getInstance(context);
  }

  // Repositories
  @Provides @Singleton public NewsRepository provideNewsRepository(
      NewsDataRepository newsDataRepository) {
    return newsDataRepository;
  }

  @Provides @Singleton public UserRepository provideUserRepository(
      UserDataRepository userDataRepository) {
    return userDataRepository;
  }

  @Provides @Singleton public ContactsRepository provideContactsRepository(
      ContactsDataRepository contactsDataRepository) {
    return contactsDataRepository;
  }

  @Provides @Singleton public PlannerRepository providesPlannerRepository(
      PlannerDataRepository plannerDataRepository) {
    return plannerDataRepository;
  }

  @Provides @Singleton public MessagesRepository providesMessagesRepository
      (MessagesDataRepository messagesDataRepository) {
    return messagesDataRepository;
  }

  // Scheduling
  @Provides @Singleton PostExecutionThread providePostExecutionExecutor(
      PostExecutionThreadImpl postExecutionExecutor) {
    return postExecutionExecutor;
  }

  @Provides @Singleton ThreadExecutor provideThreadExecutor(ThreadExecutorImpl threadExecutor) {
    return threadExecutor;
  }

  // Database
  @Provides @Singleton RealmConfiguration provideRealmConfiguration(Context context) {
    Realm.init(context);
    RealmConfiguration.Builder builder = new RealmConfiguration.Builder();
    builder.deleteRealmIfMigrationNeeded();
    builder.name("studip.realm");
    return builder.build();
  }

}
