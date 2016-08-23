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

import android.text.TextUtils;

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
  @JsonProperty("user_id") public String userId;
  @JsonProperty("username") public String username;
  @JsonProperty("perms") public String perms;
  @JsonProperty("title_pre") public String titlePre;
  @JsonProperty("forename") public String forename;
  @JsonProperty("lastname") public String lastname;
  @JsonProperty("title_post") public String titlePost;
  @JsonProperty("email") public String email;
  @JsonProperty("avatar_normal") public String avatarNormal;
  @JsonProperty("phone") public String phone;
  @JsonProperty("homepage") public String homepage;
  @JsonProperty("privadr") public String privadr;
  @JsonProperty("role") public int role;
  @JsonProperty("skype") public String skype;
  @JsonProperty("skype_show") public boolean skypeShow;

  /**
   * Default constructor
   */
  public UserEntity() {}

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

  public String getFullName() {
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

  public String getName() {
    return this.forename + " " + this.lastname;
  }
}
