/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.internal.di;

import dagger.Module;
import dagger.Provides;
import de.elanev.studip.android.app.base.internal.di.PerActivity;
import de.elanev.studip.android.app.news.data.repository.NewsRepository;
import de.elanev.studip.android.app.news.domain.GetNewsDetails;
import de.elanev.studip.android.app.news.domain.GetNewsList;

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

  @Provides @PerActivity public GetNewsDetails providesGetNewsDetailsUseCase(
      NewsRepository newsRepository) {
    return new GetNewsDetails(newsId, newsRepository);
  }

  @Provides @PerActivity public GetNewsList provideGetNewsListUseCase(
      NewsRepository newsRepository) {
    return new GetNewsList(newsRepository);
  }
}
