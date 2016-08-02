/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.data.repository;

import java.util.List;

import de.elanev.studip.android.app.data.datamodel.NewsItem;
import rx.Observable;

/**
 * @author joern
 */
public interface NewsRepository {
  Observable<NewsItem> newsItem(String id);

  Observable<List<NewsItem>> newsList();
}
