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

import static de.elanev.studip.android.app.user.data.entity.UserEntityUtil.createFakeUserEntity;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;

/**
 * @author joern
 */
public class UserEntityTest {

  private UserEntity userEntity;

  @Before public void setUp() throws Exception {
    userEntity = createFakeUserEntity();
  }

  @Test public void shouldCreateEmptyUserEntity() {
    assertThat(userEntity, is(notNullValue()));
  }

  @Test public void fromJson() throws Exception {
    UserEntity jsonUserEntity = UserEntity.fromJson(UserEntityUtil.FAKE_JSON);

    assertThat(jsonUserEntity, is(notNullValue()));
    assertThat(jsonUserEntity != null ? jsonUserEntity.getFullName() : null, is(UserEntityUtil.FAKE_FULLNAME));
  }

  @Test public void toJson() throws Exception {
    String userEntityJson = UserEntity.toJson(userEntity);
    assertThat(userEntityJson, is(UserEntityUtil.FAKE_JSON));
  }

  @Test public void shouldReturnCorrectFullName() throws Exception {
    assertThat(userEntity.getFullName(), is(UserEntityUtil.FAKE_FULLNAME));
  }

  @Test public void getName() throws Exception {
    assertThat(userEntity.getName(),
        is(UserEntityUtil.FAKE_FORENAME + " " + UserEntityUtil.FAKE_LASTNAME));
  }

  @Test public void shouldReturnNullWithEmptyJson() {
    assertThat(UserEntity.fromJson(""), is(nullValue()));
  }

  @Test public void shouldReturnEmptyJsonWithNullUser() {
    assertThat(UserEntity.toJson(null), is(nullValue()));
  }
}