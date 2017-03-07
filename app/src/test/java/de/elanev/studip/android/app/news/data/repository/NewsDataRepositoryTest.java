/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.data.repository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import de.elanev.studip.android.app.news.data.entity.NewsEntity;
import de.elanev.studip.android.app.news.data.repository.datastore.CloudNewsDataStore;
import de.elanev.studip.android.app.news.data.repository.datastore.RealmNewsDataStore;
import rx.Observable;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * @author joern
 */
public class NewsDataRepositoryTest {
  private static final String FAKE_NEWS_ID = "123";
  @Mock private NewsEntityDataMapper mockNewsEntityMapper;
  @Mock private CloudNewsDataStore mockCloudDataStore;
  @Mock private RealmNewsDataStore mockRealmDataStore;
  private NewsDataRepository newsDataRepository;

  @Before public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    newsDataRepository = new NewsDataRepository(mockNewsEntityMapper, mockRealmDataStore,
        mockCloudDataStore);
  }

  @Test public void newsItem() throws Exception {
    NewsEntity newsEntity = new NewsEntity();
    given(mockCloudDataStore.newsEntity(FAKE_NEWS_ID)).willReturn(Observable.just(newsEntity));
    given(mockRealmDataStore.newsEntity(FAKE_NEWS_ID)).willReturn(Observable.just(newsEntity));


    newsDataRepository.newsItem(FAKE_NEWS_ID, true);

    verify(mockRealmDataStore).newsEntity(FAKE_NEWS_ID);
    verify(mockCloudDataStore).newsEntity(FAKE_NEWS_ID);
  }

  @Test public void newsList() throws Exception {
    List<NewsEntity> newsEntities = new ArrayList<>(5);
    newsEntities.add(new NewsEntity());
    newsEntities.add(new NewsEntity());

    given(mockCloudDataStore.newsEntityList()).willReturn(Observable.just(newsEntities));
    given(mockRealmDataStore.newsEntityList()).willReturn(Observable.just(newsEntities));

    newsDataRepository.newsList(true);

    verify(mockCloudDataStore).newsEntityList();
    verify(mockRealmDataStore).newsEntityList();
  }

}