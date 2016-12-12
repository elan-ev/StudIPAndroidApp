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
package de.elanev.studip.android.app.user.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import de.elanev.studip.android.app.util.TextTools;

/**
 * @author joern
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName(value = "user")
public class UserEntity {
  private String userId;
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


  @JsonIgnore public static UserEntity fromJson(String userJson) {

    if (TextTools.isEmpty(userJson)) {
      return null;
    }

    ObjectMapper mapper = new ObjectMapper();
    UserEntity user = null;

    try {
      user = mapper.readValue(userJson, UserEntity.class);
    } catch (IOException e) {
      e.printStackTrace();
    }

    return user;
  }

  @JsonIgnore public static String toJson(UserEntity user) {

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

  @JsonProperty("user_id") public String getUserId() {
    return userId;
  }

  @JsonProperty("user_id") public void setUserId(String userId) {
    this.userId = userId;
  }

  @JsonProperty("username") public String getUsername() {
    return username;
  }

  @JsonProperty("username") public void setUsername(String username) {
    this.username = username;
  }

  @JsonProperty("title_pre") public String getTitlePre() {
    return titlePre;
  }

  @JsonProperty("title_pre") public void setTitlePre(String titlePre) {
    this.titlePre = titlePre;
  }

  @JsonProperty("forename") public String getForename() {
    return forename;
  }

  @JsonProperty("forename") public void setForename(String forename) {
    this.forename = forename;
  }

  @JsonProperty("lastname") public String getLastname() {
    return lastname;
  }

  @JsonProperty("lastname") public void setLastname(String lastname) {
    this.lastname = lastname;
  }

  @JsonProperty("title_post") public String getTitlePost() {
    return titlePost;
  }

  @JsonProperty("title_post") public void setTitlePost(String titlePost) {
    this.titlePost = titlePost;
  }

  @JsonProperty("email") public String getEmail() {
    return email;
  }

  @JsonProperty("email") public void setEmail(String email) {
    this.email = email;
  }

  @JsonProperty("avatar_normal") public String getAvatarNormal() {
    return avatarNormal;
  }

  @JsonProperty("avatar_normal") public void setAvatarNormal(String avatarNormal) {
    this.avatarNormal = avatarNormal;
  }

  @JsonProperty("phone") public String getPhone() {
    return phone;
  }

  @JsonProperty("phone") public void setPhone(String phone) {
    this.phone = phone;
  }

  @JsonProperty("homepage") public String getHomepage() {
    return homepage;
  }

  @JsonProperty("homepage") public void setHomepage(String homepage) {
    this.homepage = homepage;
  }

  @JsonProperty("privadr") public String getPrivadr() {
    return privadr;
  }

  @JsonProperty("privadr") public void setPrivadr(String privadr) {
    this.privadr = privadr;
  }

  @JsonProperty("skype") public String getSkype() {
    return skype;
  }

  @JsonProperty("skype") public void setSkype(String skype) {
    this.skype = skype;
  }

  @JsonIgnore public String getFullName() {
    StringBuilder builder = new StringBuilder();
    if (!TextTools.isEmpty(this.titlePre)) {
      builder.append(this.titlePre)
          .append(" ");
    }
    if (!TextTools.isEmpty(this.forename)) {
      builder.append(this.forename)
          .append(" ");
    }
    if (!TextTools.isEmpty(this.lastname)) {
      builder.append(this.lastname)
          .append(" ");
    }
    if (!TextTools.isEmpty(this.titlePost)) {
      builder.append(this.titlePost);
    }

    return builder.toString();
  }

  @JsonIgnore public String getName() {
    return this.forename + " " + this.lastname;
  }
}
