/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.internal.di.modules;

import dagger.Module;
import dagger.Provides;
import de.elanev.studip.android.app.internal.di.PerActivity;
import de.elanev.studip.android.app.news.NewsViewPresenter;
import de.elanev.studip.android.app.news.repository.NewsRepository;

/**
 * @author joern
 */
@Module
public class NewsModule {
  @Provides @PerActivity public NewsViewPresenter provideNewsViewPresenter(
      NewsRepository newsRepository) {
    return new NewsViewPresenter(newsRepository);
  }

}
