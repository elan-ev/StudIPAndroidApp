/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.internal.di;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
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
public class NewsModule {

  private String newsId = "";

  public NewsModule() {}

  public NewsModule(String id) {
    this.newsId = id;
  }

  @Provides @PerActivity @Named("newsDetails") UseCase providesGetNewsDetailsUseCase(
      NewsRepository newsRepository, ThreadExecutor threadExecutor,
      PostExecutionThread postExecutionThread) {

    return new GetNewsDetails(newsId, newsRepository, threadExecutor, postExecutionThread);
  }

  @Provides @PerActivity @Named("newsList") UseCase provideGetNewsListUseCase(
      GetNewsList getNewsList) {

    return getNewsList;
  }
}
