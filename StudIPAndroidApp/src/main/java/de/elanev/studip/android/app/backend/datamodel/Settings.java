/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.backend.datamodel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author joern
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Settings {

  @JsonProperty("SEM_TYPE") public HashMap<Integer, SeminarTypeData> semTypes;

  // Default Constructor
  public Settings() {
  }

  public static Settings fromJson(String jsonString) {
    ObjectMapper mapper = new ObjectMapper();
    Settings settings = null;
    try {
      settings = mapper.readValue(jsonString, Settings.class);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return settings;
  }

  public String toJson() {
    ObjectMapper mapper = new ObjectMapper();
    String json = "";
    try {
      json = mapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return json;
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class SeminarTypeData {
    @JsonProperty("name") public String name;
    //    @JsonProperty("class") public int class;
  }

  /*
   * Unused /studip/settings properties
   */
  //  @JsonProperty("API_VERSION") public String apiVersion;
  //  @JsonProperty("ALLOW_CHANGE_USERNAME") public boolean isAllowChangeUsername;
  //  @JsonProperty("ALLOW_CHANGE_EMAIL") public boolean isAllowChangeEmail;
  //  @JsonProperty("ALLOW_CHANGE_NAME") public boolean isAllowChangeName;
  //  @JsonProperty("ALLOW_CHANGE_TITLE") public boolean isAllowChangeTitle;
  //
  //  @JsonProperty("INST_TYPE") public HashMap<Integer, InstituteType> instituteTypes;
  //
  //  public static class InstituteType {
  //    @JsonProperty("name") String name;
  //  }
  //
  //  @JsonProperty("SEM_CLASS") public HashMap<Integer, SeminarClass> seminarClasses;
  //
  //  public static class SeminarClass {
  //    @JsonProperty("name") public String name;
  //    @JsonProperty("compact_mode") public int compactMode;
  //    @JsonProperty("workgroup_mode") public int workgroupMode;
  //    @JsonProperty("only_inst_user") public int onlyInstUser;
  //    @JsonProperty("turnus_default") public int turnusDefault;
  //    @JsonProperty("default_read_level") public int defaultReadLevel;
  //    @JsonProperty("default_write_level") public int defaultWriteLevel;
  //    @JsonProperty("bereiche") public int bereiche; //TODO: Shouldn't this be areas, domains, realms?
  //    @JsonProperty("show_browse") public int showBrowse;
  //    @JsonProperty("write_access_nobody") public int writeAccessNobody;
  //    @JsonProperty("topic_create_autor") public int topicCreateAutor;
  //    @JsonProperty("visible") public int visible; //TODO: Maybe boolean would be better
  //    @JsonProperty("course_creation_forbidden") public int courseCreationForbidden; //TODO: Maybe bool?
  //    @JsonProperty("overview") public String overview;
  //    @JsonProperty("forum") public String forum;
  //    @JsonProperty("admin") public String admin;
  //    @JsonProperty("documents") public String documents;
  //    @JsonProperty("schedule") public String schedule;
  //    @JsonProperty("participants") public String participants;
  //    @JsonProperty("literature") public String literature;
  //    @JsonProperty("scm") public String scm;
  //    @JsonProperty("wiki") public String wiki;
  //    @JsonProperty("resources") public String resources;
  //    @JsonProperty("calendar") public String calender;
  //    @JsonProperty("elearning_interface") public String elearningInterface;
  //    @JsonProperty("modules") public HashMap<String, Module> modules;
  //    @JsonProperty("description") public String description;
  //    @JsonProperty("create_description") public String createDescription;
  //    @JsonProperty("studygroup_mode") public int studygroupMode;
  //    @JsonProperty("admission_prelim_default") public int adminssionPrelimDefault;
  //    @JsonProperty("admission_type_default") public int admissionTypeDefault;
  //    @JsonProperty("title_dozent") public String titleDozent;
  //    @JsonProperty("title_dozent_plural") public String titleDozentPlural;
  //    @JsonProperty("title_tutor") public String titleTutor;
  //    @JsonProperty("title_tutor_plural") public String titleTutorPlural;
  //    @JsonProperty("title_autor") public String titleAutor;
  //    @JsonProperty("title_autor_plural") public String titleAutorPlural;
  //    @JsonProperty("mkdate") public long mkdate;
  //    @JsonProperty("chdate") public long chdate;
  //  }
  //
  //  public static class Module {
  //    @JsonProperty("activated") public int activated; //TODO: Maybe bool?
  //    @JsonProperty("sticky") public int sticky; //TODO bool?
  //  }
  //
  //  @JsonProperty("TERMIN_TYP") public HashMap<Integer, TerminType> terminTypes;
  //
  //  public static class TerminType {
  //    @JsonProperty("name") public String name;
  //    @JsonProperty("sitzung") public int sitzung;
  //    @JsonProperty("color") public String color;
  //  }
  //
  //  @JsonProperty("PERS_TERMIN_KAT") public ArrayList<PersonalTerminKatergorie> personalTerminKatergories;
  //
  //  public static class PersonalTerminKatergorie {
  //    @JsonProperty("id") public int id;
  //    @JsonProperty("name") public String name;
  //    @JsonProperty("color") public String color;
  //  }
  //
  //  @JsonProperty("SUPPORT_EMAIL") public String supportEmail;
  //
  //  @JsonProperty("TITLES") public HashMap<String, Title> titles;
  //
  //  public static class Title {
  //    @JsonProperty("singular") public String singular;
  //    @JsonProperty("plural") public String plural;
  //  }
  //
  //  @JsonProperty("UNI_NAME_CLEAN") public String uniNameClean;
}
