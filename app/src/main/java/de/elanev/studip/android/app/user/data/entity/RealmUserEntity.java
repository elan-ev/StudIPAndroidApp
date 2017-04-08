/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.user.data.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * @author joern
 */
public class RealmUserEntity extends RealmObject {
  @PrimaryKey private String userId;
  private String username;
  private String titlePre;
  private String forename;
  private String lastname;
  private String titlePost;
  private String email;
  private String avatarNormal;
  private String phone;
  private String homepage;
  private String privadr;
  private String skype;

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getTitlePre() {
    return titlePre;
  }

  public void setTitlePre(String titlePre) {
    this.titlePre = titlePre;
  }

  public String getForename() {
    return forename;
  }

  public void setForename(String forename) {
    this.forename = forename;
  }

  public String getLastname() {
    return lastname;
  }

  public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  public String getTitlePost() {
    return titlePost;
  }

  public void setTitlePost(String titlePost) {
    this.titlePost = titlePost;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getAvatarNormal() {
    return avatarNormal;
  }

  public void setAvatarNormal(String avatarNormal) {
    this.avatarNormal = avatarNormal;
  }

  public String getHomepage() {
    return homepage;
  }

  public void setHomepage(String homepage) {
    this.homepage = homepage;
  }

  public String getPrivadr() {
    return privadr;
  }

  public void setPrivadr(String privadr) {
    this.privadr = privadr;
  }

  public String getSkype() {
    return skype;
  }

  public void setSkype(String skype) {
    this.skype = skype;
  }
}
