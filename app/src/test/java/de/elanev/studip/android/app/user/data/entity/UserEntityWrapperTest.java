/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.user.data.entity;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @author joern
 */
public class UserEntityWrapperTest {
  private UserEntityWrapper userEntityWrapper;

  @Before public void setUp() throws Exception {
    userEntityWrapper = new UserEntityWrapper();
  }

  @Test public void shouldReturnEmptyUserEntityWrapper() throws Exception {
    assertThat(userEntityWrapper.getUserEntity(), is(nullValue()));
  }

  @Test public void setUserEntity() throws Exception {
    userEntityWrapper.setUserEntity(new UserEntity());
    assertThat(userEntityWrapper.getUserEntity(), is(notNullValue()));
  }

}