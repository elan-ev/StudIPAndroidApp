/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.user.presentation.mapper;

import org.junit.Before;
import org.junit.Test;

import de.elanev.studip.android.app.user.domain.User;
import de.elanev.studip.android.app.user.presentation.model.UserModel;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsInstanceOf.instanceOf;


/**
 * @author joern
 */
public class UserModelDataMapperTest {
  private static final String FAKE_USER_ID = "123";
  private static final String FAKE_FULL_NAME = "Dr. Peter Pan";
  private static final String FAKE_AVATAR_URL = "http;//google.de";
  private static final String FAKE_PHONE = "+49 123 456789";
  private static final String FAKE_PRIVATE_ADDRESS = "Baum 3, 12345 Lummerland";
  private static final String FAKE_EMAIL = "peter@pan.de";
  private static final String FAKE_HOMEPAGE = "http://google.de";
  private static final String FAKE_SKYPE_ADDRESS = "peterThePan";
  private UserModelDataMapper userModelDataMapper;

  @Before public void setUp() throws Exception {
    userModelDataMapper = new UserModelDataMapper();
  }

  @Test public void transform() throws Exception {
    User fakeDomainUser = createFakeDomainUser();

    UserModel userModel = userModelDataMapper.transform(fakeDomainUser);

    assertThat(userModel, is(instanceOf(UserModel.class)));
    assertThat(userModel.getFullName(), is(FAKE_FULL_NAME));
    assertThat(userModel.getAvatarUrl(), is(FAKE_AVATAR_URL));
    assertThat(userModel.getPhone(), is(FAKE_PHONE));
    assertThat(userModel.getAddress(), is(FAKE_PRIVATE_ADDRESS));
    assertThat(userModel.getEmail(), is(FAKE_EMAIL));
    assertThat(userModel.getHomepage(), is(FAKE_HOMEPAGE));
    assertThat(userModel.getSkype(), is(FAKE_SKYPE_ADDRESS));
  }

  private User createFakeDomainUser() {
    User fakeUser = new User(FAKE_USER_ID);

    fakeUser.setFullname(FAKE_FULL_NAME);
    fakeUser.setAvatarUrl(FAKE_AVATAR_URL);
    fakeUser.setPhone(FAKE_PHONE);
    fakeUser.setPrivateAddress(FAKE_PRIVATE_ADDRESS);
    fakeUser.setEmail(FAKE_EMAIL);
    fakeUser.setHomepageUrl(FAKE_HOMEPAGE);
    fakeUser.setSkypeAddress(FAKE_SKYPE_ADDRESS);

    return fakeUser;
  }

}