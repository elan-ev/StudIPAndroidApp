/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.data.entity;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

/**
 * @author joern
 */
public class NewsEntityListTest {
  private NewsEntityList entityList;

  @Before public void setUp() throws Exception {
    entityList = new NewsEntityList();
  }

  @Test public void shouldCreateEmptyEntityList() {
    assertThat(entityList.getNewsEntities(), is(nullValue()));
    assertThat(entityList.getPagination(), is(nullValue()));
  }
}