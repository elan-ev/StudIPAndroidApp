/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.user.presentation.model;

import java.io.Serializable;

/**
 * @author joern
 */
public class UserModel implements Serializable {
  private String userId;
  private String fullName;
  private String avatarUrl;
  private String email;
  private String address;
  private String skype;
  private String homepage;
  private String phone;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getSkype() {
    return skype;
  }

  public void setSkype(String skype) {
    this.skype = skype;
  }

  public String getHomepage() {
    return homepage;
  }

  public void setHomepage(String homepage) {
    this.homepage = homepage;
  }

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

  public String getFullName() {
    return fullName;
  }

  public void setFullName(String fullName) {
    this.fullName = fullName;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  @Override public int hashCode() {
    int result = userId.hashCode();
    result = 31 * result + fullName.hashCode();
    result = 31 * result + avatarUrl.hashCode();
    result = 31 * result + email.hashCode();
    result = 31 * result + address.hashCode();
    result = 31 * result + skype.hashCode();
    result = 31 * result + homepage.hashCode();
    result = 31 * result + phone.hashCode();
    return result;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof UserModel)) return false;

    UserModel userModel = (UserModel) o;

    if (!userId.equals(userModel.userId)) return false;
    if (!fullName.equals(userModel.fullName)) return false;
    if (!avatarUrl.equals(userModel.avatarUrl)) return false;
    if (!email.equals(userModel.email)) return false;
    if (!address.equals(userModel.address)) return false;
    if (!skype.equals(userModel.skype)) return false;
    if (!homepage.equals(userModel.homepage)) return false;
    return phone.equals(userModel.phone);

  }
}
