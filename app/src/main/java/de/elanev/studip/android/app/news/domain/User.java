/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.news.domain;

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
  private boolean showSkypeAddress;

  public User(String userId) {this.userId = userId;}

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  @Override public String toString() {
    String userStr = "*********** User ***********\n";
    userStr += "id=" + this.getUserId() + "\n";
    userStr += "username=" + this.getUsername() + "\n";
    userStr += "fullname=" + this.getFullname() + "\n";
    userStr += "avatar url=" + this.getAvatarUrl() + "\n";
    userStr += "email=" + this.getEmail() + "\n";
    userStr += "private address=" + this.getPrivateAddress() + "\n";
    userStr += "homepage url=" + this.getHomepageUrl() + "\n";
    userStr += "skype address=" + this.getSkypeAddress() + "\n";
    userStr += "show skype=" + this.showSkypeAddress() + "\n";
    userStr += "********************************";

    return userStr;
  }

  public String getUserId() {
    return userId;
  }

  public String getUsername() {
    return username;
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

  public boolean showSkypeAddress() {
    return showSkypeAddress;
  }

  public void setPrivateAddress(String privateAddress) {
    this.privateAddress = privateAddress;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public void setShowSkypeAddress(boolean showSkypeAddress) {
    this.showSkypeAddress = showSkypeAddress;
  }
}
