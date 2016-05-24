/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news;

import de.elanev.studip.android.app.BaseView;
import de.elanev.studip.android.app.news.repository.NewsModel;

/**
 * @author joern
 */
public interface NewsView extends BaseView<NewsModel> {
  void renderNews(NewsModel news);

  String getNewsId();
}
