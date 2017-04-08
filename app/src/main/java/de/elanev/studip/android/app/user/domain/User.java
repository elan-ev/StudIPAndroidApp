/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.user.domain;

/**
 * @author joern
 */
public class User {

  // Required property user id
  private final String userId;

  // Other properties
  private String fullname;
  private String username;
  private String avatarUrl;
  private String email;
  private String phone;
  private String homepageUrl;
  private String privateAddress;
  private String skypeAddress;
  private boolean showSkypeOnline;

  public User(String userId, String fullname, String username, String avatarUrl, String email,
      String phone, String homepageUrl, String privateAddress, String skypeAddress,
      boolean showSkypeOnline) {
    this.userId = userId;
    this.fullname = fullname;
    this.username = username;
    this.avatarUrl = avatarUrl;
    this.email = email;
    this.phone = phone;
    this.homepageUrl = homepageUrl;
    this.privateAddress = privateAddress;
    this.skypeAddress = skypeAddress;
    this.showSkypeOnline = showSkypeOnline;
  }

  public User(String userId) {this.userId = userId;}

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getUserId() {
    return userId;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getFullname() {
    return fullname;
  }

  public void setFullname(String fullname) {
    this.fullname = fullname;
  }

  public String getAvatarUrl() {
    return avatarUrl;
  }

  public void setAvatarUrl(String avatarUrl) {
    this.avatarUrl = avatarUrl;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPrivateAddress() {
    return privateAddress;
  }

  public void setPrivateAddress(String privateAddress) {
    this.privateAddress = privateAddress;
  }

  public String getHomepageUrl() {
    return homepageUrl;
  }

  public void setHomepageUrl(String homepageUrl) {
    this.homepageUrl = homepageUrl;
  }

  public String getSkypeAddress() {
    return skypeAddress;
  }

  public void setSkypeAddress(String skypeAddress) {
    this.skypeAddress = skypeAddress;
  }

  public boolean showSkypeOnline() {
    return showSkypeOnline;
  }

  public void setShowSkypeOnline(boolean showSkypeOnline) {
    this.showSkypeOnline = showSkypeOnline;
  }

  @Override public String toString() {
    return "User{" + "userId='" + userId + '\'' + ", fullname='" + fullname + '\'' + ", username='"
        + username + '\'' + ", avatarUrl='" + avatarUrl + '\'' + ", email='" + email + '\''
        + ", phone='" + phone + '\'' + ", homepageUrl='" + homepageUrl + '\'' + ", privateAddress='"
        + privateAddress + '\'' + ", skypeAddress='" + skypeAddress + '\'' + ", showSkypeOnline="
        + showSkypeOnline + '}';
  }
}
