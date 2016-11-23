/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.data.repository.datastore;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import de.elanev.studip.android.app.data.net.services.StudIpLegacyApiService;
import de.elanev.studip.android.app.news.data.entity.NewsEntity;
import de.elanev.studip.android.app.util.Prefs;
import rx.Observable;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

/**
 * @author joern
 */
public class CloudNewsDataStoreTest {
  private static final String FAKE_NEWS_ID = "123";

  @Mock StudIpLegacyApiService mockApiService;
  private CloudNewsDataStore cloudNewsDataStore;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    cloudNewsDataStore = new CloudNewsDataStore(mockApiService);
  }

  @Test public void newsEntityList() throws Exception {
    List<NewsEntity> newsEntities = new ArrayList<>();
    NewsEntity newsEntity1 = new NewsEntity();
    NewsEntity newsEntity2 = new NewsEntity();
    newsEntity1.date = 1L;
    newsEntity2.date = 2L;

    newsEntities.add(newsEntity2);
    newsEntities.add(newsEntity1);

    given(mockApiService.getNews()).willReturn(Observable.just(newsEntities));
    cloudNewsDataStore.newsEntityList();

    verify(mockApiService).getNews();
  }

  @Test public void newsEntity() throws Exception {
    NewsEntity newsEntity = new NewsEntity();
    given(mockApiService.getNewsItem(FAKE_NEWS_ID)).willReturn(Observable.just(newsEntity));
    cloudNewsDataStore.newsEntity(FAKE_NEWS_ID);

    verify(mockApiService).getNewsItem(FAKE_NEWS_ID);
  }

}