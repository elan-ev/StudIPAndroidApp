/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.base.data.executor;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.base.domain.executor.ThreadExecutor;
import rx.Scheduler;
import rx.schedulers.Schedulers;

/**
 * @author joern
 */
@Singleton
public class ThreadExecutorImpl implements ThreadExecutor {

  @Inject public ThreadExecutorImpl() {}

  @Override public Scheduler getScheduler() {
    return Schedulers.io();
  }
}
