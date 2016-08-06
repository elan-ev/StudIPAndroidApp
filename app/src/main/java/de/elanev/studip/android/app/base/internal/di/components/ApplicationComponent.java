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
import de.elanev.studip.android.app.auth.SignInFragment;
import de.elanev.studip.android.app.base.internal.di.modules.ApplicationModule;
import de.elanev.studip.android.app.base.internal.di.modules.NetworkModule;
import de.elanev.studip.android.app.data.net.sync.SyncHelper;
import de.elanev.studip.android.app.messages.MessageComposeActivity;
import de.elanev.studip.android.app.messages.MessageDetailActivity;
import de.elanev.studip.android.app.news.domain.NewsRepository;
import de.elanev.studip.android.app.planner.PlannerTimetableFragment;
import de.elanev.studip.android.app.widget.BaseFragment;
import de.elanev.studip.android.app.widget.ReactiveListFragment;
import de.elanev.studip.android.app.widget.UserListFragment;

/**
 * @author joern
 */

@Singleton
@Component(modules = {ApplicationModule.class, NetworkModule.class})
public interface ApplicationComponent {
  void inject(AbstractStudIPApplication target);

  void inject(MainActivity target);

  void inject(SyncHelper target);

  void inject(BaseFragment target);

  //TODO: Make it extend BaseFragment
  void inject(SignInFragment target);

  void inject(UserListFragment target);

  //FIXME: Just workaround
  void inject(MessageComposeActivity target);

  void inject(PlannerTimetableFragment target);

  void inject(ReactiveListFragment target);

  void inject(MessageDetailActivity target);

  // Expose to subcomponents
  Context context();

  NewsRepository newsRepository();

  //FIxme
  SyncHelper syncHelper();
}
