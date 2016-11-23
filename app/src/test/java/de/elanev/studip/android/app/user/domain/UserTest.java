/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.user.domain;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * @author joern
 */
public class UserTest {
  private static final String FAKE_USER_ID = "123";
  private static final String FAKE_USER_NAME = "peterpan";
  private static final String FAKE_FULL_NAME = "Dr. Peter Pan";
  private static final String FAKE_AVATAR_URL = "http://gooogle.de";
  private static final String FAKE_EMAIL = "peter@pan.de";
  private static final String FAKE_HOMEPAGE = "http://www.google.de";
  private static final String FAKE_PHONE = "+49 123 456789";
  private static final String FAKE_PRIVATE_ADDRESS = "Baum 3, 00000 Lummerland";
  private static final String FAKE_SKYPE_ADDRESS = "PeterThePan";
  private static final Object FAKE_USER_STRING = "*********** User ***********\n" + "id=123\n"
      + "username=peterpan\n" + "fullname=Dr. Peter Pan\n" + "avatar url=http://gooogle.de\n"
      + "email=peter@pan.de\n" + "private address=Baum 3, 00000 Lummerland\n"
      + "homepage url=http://www.google.de\n" + "skype address=PeterThePan\n" + "show skype=false\n"
      + "********************************";

  private User user;

  @Before public void setUp() throws Exception {
    user = createFakeUser();
  }

  private User createFakeUser() {
    User fakeUser = new User(FAKE_USER_ID);
    fakeUser.setUsername(FAKE_USER_NAME);
    fakeUser.setFullname(FAKE_FULL_NAME);
    fakeUser.setAvatarUrl(FAKE_AVATAR_URL);
    fakeUser.setEmail(FAKE_EMAIL);
    fakeUser.setHomepageUrl(FAKE_HOMEPAGE);
    fakeUser.setPhone(FAKE_PHONE);
    fakeUser.setPrivateAddress(FAKE_PRIVATE_ADDRESS);
    fakeUser.setSkypeAddress(FAKE_SKYPE_ADDRESS);

    return fakeUser;
  }

  @Test public void shouldReturnCorrectStringRepresentation() throws Exception {
    assertThat(user.toString(), is(FAKE_USER_STRING));
  }

}