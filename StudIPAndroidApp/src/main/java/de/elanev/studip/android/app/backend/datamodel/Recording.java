/*
 * Copyright (c) 2014 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.backend.datamodel;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.util.HashMap;
import java.util.Map;

/**
 * POJO that represents one episode of the /courses/:course_id oc_recordings JSON property.
 *
 * @author Jörn
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "id",
    "title",
    "start",
    "duration",
    "description",
    "author",
    "preview",
    "external_player_url",
    "presenter_download",
    "presentation_download",
    "audio_download"
})
public class Recording {

  @JsonProperty("id")
  private String id;
  @JsonProperty("title")
  private String title;
  @JsonProperty("start")
  private String start;
  @JsonProperty("duration")
  private String duration;
  @JsonProperty("description")
  private Object description;
  @JsonProperty("author")
  private String author;
  @JsonProperty("preview")
  private String preview;
  @JsonProperty("external_player_url")
  private String externalPlayerUrl;
  @JsonProperty("presenter_download")
  private String presenterDownload;
  @JsonProperty("presentation_download")
  private String presentationDownload;
  @JsonProperty("audio_download")
  private String audioDownload;
  @JsonIgnore
  private Map<String, Object> additionalProperties = new HashMap<String, Object>();

  @JsonProperty("id")
  public String getId() {
    return id;
  }

  @JsonProperty("id")
  public void setId(String id) {
    this.id = id;
  }

  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  @JsonProperty("title")
  public void setTitle(String title) {
    this.title = title;
  }

  @JsonProperty("start")
  public String getStart() {
    return start;
  }

  @JsonProperty("start")
  public void setStart(String start) {
    this.start = start;
  }

  @JsonProperty("duration")
  public String getDuration() {
    return duration;
  }

  @JsonProperty("duration")
  public void setDuration(String duration) {
    this.duration = duration;
  }

  @JsonProperty("description")
  public Object getDescription() {
    return description;
  }

  @JsonProperty("description")
  public void setDescription(Object description) {
    this.description = description;
  }

  @JsonProperty("author")
  public String getAuthor() {
    return author;
  }

  @JsonProperty("author")
  public void setAuthor(String author) {
    this.author = author;
  }

  @JsonProperty("preview")
  public String getPreview() {
    return preview;
  }

  @JsonProperty("preview")
  public void setPreview(String preview) {
    this.preview = preview;
  }

  @JsonProperty("external_player_url")
  public String getExternalPlayerUrl() {
    return externalPlayerUrl;
  }

  @JsonProperty("external_player_url")
  public void setExternalPlayerUrl(String externalPlayerUrl) {
    this.externalPlayerUrl = externalPlayerUrl;
  }

  @JsonProperty("presenter_download")
  public String getPresenterDownload() {
    return presenterDownload;
  }

  @JsonProperty("presenter_download")
  public void setPresenterDownload(String presenterDownload) {
    this.presenterDownload = presenterDownload;
  }

  @JsonProperty("presentation_download")
  public String getPresentationDownload() {
    return presentationDownload;
  }

  @JsonProperty("presentation_download")
  public void setPresentationDownload(String presentationDownload) {
    this.presentationDownload = presentationDownload;
  }

  @JsonProperty("audio_download")
  public String getAudioDownload() {
    return audioDownload;
  }

  @JsonProperty("audio_download")
  public void setAudioDownload(String audioDownload) {
    this.audioDownload = audioDownload;
  }

  @JsonAnyGetter
  public Map<String, Object> getAdditionalProperties() {
    return this.additionalProperties;
  }

  @JsonAnySetter
  public void setAdditionalProperty(String name, Object value) {
    this.additionalProperties.put(name, value);
  }
}