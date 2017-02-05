/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.internal.di;

import org.mockito.Mockito;

import javax.inject.Named;

import dagger.Module;
import de.elanev.studip.android.app.base.UseCase;
import de.elanev.studip.android.app.base.domain.executor.PostExecutionThread;
import de.elanev.studip.android.app.base.domain.executor.ThreadExecutor;
import de.elanev.studip.android.app.base.internal.di.PerActivity;
import de.elanev.studip.android.app.news.domain.GetNewsDetails;
import de.elanev.studip.android.app.news.domain.GetNewsList;
import de.elanev.studip.android.app.news.domain.NewsRepository;

/**
 * @author joern
 */
@Module
public class NewsTestModule {
  @PerActivity @Named("newsDetails") public UseCase providesGetNewsDetailsUseCase(
      NewsRepository newsRepository, ThreadExecutor threadExecutor,
      PostExecutionThread postExecutionThread) {
    return Mockito.mock(GetNewsDetails.class);
  }

  @PerActivity @Named("newsList") public UseCase provideGetNewsListUseCase(
      GetNewsList getNewsList) {
    return Mockito.mock(GetNewsList.class);
  }
}
