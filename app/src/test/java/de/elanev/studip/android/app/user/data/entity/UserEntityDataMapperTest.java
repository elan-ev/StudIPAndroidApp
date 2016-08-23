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

import de.elanev.studip.android.app.user.domain.User;

import static de.elanev.studip.android.app.user.data.entity.UserEntityUtil.createFakeUserEntity;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;

/**
 * @author joern
 */
public class UserEntityDataMapperTest {
  private UserEntityDataMapper dataMapper;
  private UserEntity fakeUserEntity;

  @Before public void setUp() throws Exception {
    dataMapper = new UserEntityDataMapper();
    fakeUserEntity = createFakeUserEntity();
  }


  @Test public void transform() throws Exception {
    User domainUser = dataMapper.transform(fakeUserEntity);

    assertThat(domainUser, is(instanceOf(User.class)));
    assertThat(domainUser.getUserId(), is(UserEntityUtil.FAKE_USER_ID));
    assertThat(domainUser.getFullname(), is(UserEntityUtil.FAKE_FULLNAME));
    assertThat(domainUser.getUsername(), is(UserEntityUtil.FAKE_USERNAME));
    assertThat(domainUser.getAvatarUrl(), is(UserEntityUtil.FAKE_AVATAR_URL));
    assertThat(domainUser.getEmail(), is(UserEntityUtil.FAKE_EMAIL));
    assertThat(domainUser.getHomepageUrl(), is(UserEntityUtil.FAKE_HOMEPAGE));
    assertThat(domainUser.getPhone(), is(UserEntityUtil.FAKE_PHONE));
    assertThat(domainUser.getSkypeAddress(), is(UserEntityUtil.FAKE_SKYPE_NAME));
    assertThat(domainUser.getPrivateAddress(), is(UserEntityUtil.FAKE_PRIVATE_ADDRESS));
  }

}