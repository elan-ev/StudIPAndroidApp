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
  static final String FAKE_JSON = "{\"fullName\":\"Dr. Peter Pan Msc.\",\"name\":\"Peter Pan\",\"user_id\":\"123\",\"username\":\"peterthepan\",\"perms\":null,\"title_pre\":\"Dr.\",\"forename\":\"Peter\",\"lastname\":\"Pan\",\"title_post\":\"Msc.\",\"email\":\"peter@pan.de\",\"avatar_normal\":\"http://google.de\",\"phone\":\"+49 1234 4567\",\"homepage\":\"http://www.google.de\",\"privadr\":\"Auf der Mauer 123, 12345 Auf der Lauer\",\"role\":0,\"skype\":\"peterpan\",\"skype_show\":false}";
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
    userEntity.userId = FAKE_USER_ID;
    userEntity.titlePre = FAKE_TITLE_PRE;
    userEntity.titlePost = FAKE_TITLE_POST;
    userEntity.forename = FAKE_FORENAME;
    userEntity.lastname = FAKE_LASTNAME;
    userEntity.avatarNormal = FAKE_AVATAR_URL;
    userEntity.email = FAKE_EMAIL;
    userEntity.homepage = FAKE_HOMEPAGE;
    userEntity.phone = FAKE_PHONE;
    userEntity.privadr = FAKE_PRIVATE_ADDRESS;
    userEntity.skype = FAKE_SKYPE_NAME;
    userEntity.username = FAKE_USERNAME;

    return userEntity;
  }

}
