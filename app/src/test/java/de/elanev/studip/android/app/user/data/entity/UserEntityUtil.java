/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.user.data.entity;

/**
 * @author joern
 */

public class UserEntityUtil {
  public static final String FAKE_USER_ID = "123";
  static final String FAKE_JSON = "{\"user_id\":\"123\",\"username\":\"peterthepan\","
    + "\"title_pre\":\"Dr.\",\"forename\":\"Peter\",\"lastname\":\"Pan\",\"title_post\":\"Msc.\",\"email\":\"peter@pan.de\",\"avatar_normal\":\"http://google.de\",\"phone\":\"+49 1234 4567\",\"homepage\":\"http://www.google.de\",\"privadr\":\"Auf der Mauer 123, 12345 Auf der Lauer\",\"skype\":\"peterpan\"}";
  static final String FAKE_FORENAME = "Peter";
  static final String FAKE_LASTNAME = "Pan";
  static final String FAKE_TITLE_PRE = "Dr.";
  static final String FAKE_TITLE_POST = "Msc.";
  static final String FAKE_FULLNAME = FAKE_TITLE_PRE + " " + FAKE_FORENAME + " " + FAKE_LASTNAME
      + " " + "" + FAKE_TITLE_POST;
  static final String FAKE_AVATAR_URL = "http://google.de";
  static final String FAKE_EMAIL = "peter@pan.de";
  static final String FAKE_HOMEPAGE = "http://www.google.de";
  static final String FAKE_PHONE = "+49 1234 4567";
  static final String FAKE_PRIVATE_ADDRESS = "Auf der Mauer 123, 12345 Auf der Lauer";
  static final String FAKE_SKYPE_NAME = "peterpan";
  static final String FAKE_USERNAME = "peterthepan";


  public static UserEntity createFakeUserEntity() {
    UserEntity userEntity = new UserEntity();
    userEntity.setUserId(FAKE_USER_ID);
    userEntity.setTitlePre(FAKE_TITLE_PRE);
    userEntity.setTitlePost(FAKE_TITLE_POST);
    userEntity.setForename(FAKE_FORENAME);
    userEntity.setLastname(FAKE_LASTNAME);
    userEntity.setAvatarNormal(FAKE_AVATAR_URL);
    userEntity.setEmail(FAKE_EMAIL);
    userEntity.setHomepage(FAKE_HOMEPAGE);
    userEntity.setPhone(FAKE_PHONE);
    userEntity.setPrivadr(FAKE_PRIVATE_ADDRESS);
    userEntity.setSkype(FAKE_SKYPE_NAME);
    userEntity.setUsername(FAKE_USERNAME);

    return userEntity;
  }

}
