/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.internal.di.components;

import dagger.Component;
import de.elanev.studip.android.app.internal.di.PerActivity;
import de.elanev.studip.android.app.internal.di.modules.NewsModule;
import de.elanev.studip.android.app.news.NewsListFragment;
import de.elanev.studip.android.app.news.NewsViewFragment;

/**
 * @author joern
 */
@PerActivity
@Component(dependencies = ApplicationComponent.class, modules = {
    NewsModule.class
})
public interface NewsComponent {
  void inject(NewsViewFragment target);

  void inject(NewsListFragment target);
}
