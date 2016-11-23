/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.messages.data.entity;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

/**
 * @author joern
 */
public class MessageEntitiesTest {
  private MessageEntities messageEntities;

  @Before public void setUp() throws Exception {
    messageEntities = new MessageEntities();
  }

  @Test public void shouldCreateEmptyMessageEntityList() throws Exception {
    assertThat(messageEntities.getMessages(), is(nullValue()));
    assertThat(messageEntities.getPagination(), is(nullValue()));
  }
}