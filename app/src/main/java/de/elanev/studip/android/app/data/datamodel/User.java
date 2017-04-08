/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

/**
 *
 */
package de.elanev.studip.android.app.data.datamodel;

import android.text.TextUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.Serializable;

/**
 * @author joern
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName(value = "user")
public class User implements Serializable {
  public static final String NAME = User.class.getName() + ".name";
  public static final String AVATAR = User.class.getName() + ".avatar";
  @JsonProperty("user_id") public String userId;
  @JsonProperty("username") public String username;
  @JsonProperty("perms") public String perms;
  @JsonProperty("title_pre") public String titlePre;
  @JsonProperty("forename") public String forename;
  @JsonProperty("lastname") public String lastname;
  @JsonProperty("title_post") public String titlePost;
  @JsonProperty("email") public String email;
  @JsonProperty("avatar_small") public String avatarSmall;
  @JsonProperty("avatar_medium") public String avatarMedium;
  @JsonProperty("avatar_normal") public String avatarNormal;
  @JsonProperty("phonse") public String phone;
  @JsonProperty("homepage") public String homepage;
  @JsonProperty("privadr") public String privadr;
  @JsonProperty("role") public int role;
  @JsonProperty("skype") public String skype;
  @JsonProperty("skype_show") public boolean skypeShow;

  /**
   * Default constructor
   */
  public User() {}

  /**
   * @param userId
   * @param username
   * @param perms
   * @param titlePre
   * @param forename
   * @param lastname
   * @param titlePost
   * @param email
   * @param avatarSmall
   * @param avatarMedium
   * @param avatarNormal
   * @param phone
   * @param homepage
   * @param privadr
   * @param role
   */
  public User(String userId, String username, String perms, String titlePre, String forename,
      String lastname, String titlePost, String email, String avatarSmall, String avatarMedium,
      String avatarNormal, String phone, String homepage, String privadr, int role) {
    this.userId = userId;
    this.username = username;
    this.perms = perms;
    this.titlePre = titlePre;
    this.forename = forename;
    this.lastname = lastname;
    this.titlePost = titlePost;
    this.email = email;
    this.avatarSmall = avatarSmall;
    this.avatarMedium = avatarMedium;
    this.avatarNormal = avatarNormal;
    this.phone = phone;
    this.homepage = homepage;
    this.privadr = privadr;
    this.role = role;
  }

  @JsonIgnore public static User fromJson(String userJson) {

    if (TextUtils.isEmpty(userJson)) {
      return null;
    }

    ObjectMapper mapper = new ObjectMapper();
    User user = null;

    try {
      user = mapper.readValue(userJson, User.class);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    return user;
  }

  @JsonIgnore public static String toJson(User user) {

    if (user == null) {
      return null;
    }

    ObjectMapper mapper = new ObjectMapper();
    String json = "";

    try {
      json = mapper.writeValueAsString(user);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }

    return json;
  }

  public String getFullName() {
    StringBuilder builder = new StringBuilder();
    if (!TextUtils.isEmpty(this.titlePre)) {
      builder.append(this.titlePre)
          .append(" ");
    }
    if (!TextUtils.isEmpty(this.forename)) {
      builder.append(this.forename)
          .append(" ");
    }
    if (!TextUtils.isEmpty(this.lastname)) {
      builder.append(this.lastname)
          .append(" ");
    }
    if (!TextUtils.isEmpty(this.titlePost)) {
      builder.append(this.titlePost);
    }
    return builder.toString();
  }

  public String getName() {
    return this.forename + " " + this.lastname;
  }
}
