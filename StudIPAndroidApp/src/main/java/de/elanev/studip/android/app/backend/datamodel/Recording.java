/*
 * Copyright (c) 2014 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.backend.datamodel;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO that represents one episode of the oc_episodes API response.
 *
 * @author Jörn
 */
public class Recording {
  private String id;
  private String audioDownload;
  private String author;
  private String description;
  private long duration;
  private String externalPlayerUrl;
  private String presentationDownload;
  private String presenterDownload;
  private String preview;
  private String start;
  private String title;

  @JsonProperty("id")
  public String getId() {
    return id;
  }

  @JsonProperty("id")
  public void setId(String id) {
    this.id = id;
  }

  @JsonProperty("audio_download")
  public String getAudioDownload() {
    return audioDownload;
  }

  @JsonProperty("audio_download")
  public void setAudioDownload(String audioDownload) {
    this.audioDownload = audioDownload;
  }

  @JsonProperty("author")
  public String getAuthor() {
    return author;
  }

  @JsonProperty("author")
  public void setAuthor(String author) {
    this.author = author;
  }

  @JsonProperty("description")
  public String getDescription() {
    return description;
  }

  @JsonProperty("description")
  public void setDescription(String description) {
    this.description = description;
  }

  @JsonProperty("duration")
  public long getDuration() {
    return duration;
  }

  @JsonProperty("duration")
  public void setDuration(long duration) {
    this.duration = duration;
  }

  @JsonProperty("external_player_url")
  public String getExternalPlayerUrl() {
    return externalPlayerUrl;
  }

  @JsonProperty("external_player_url")
  public void setExternalPlayerUrl(String externalPlayerUrl) {
    this.externalPlayerUrl = externalPlayerUrl;
  }

  @JsonProperty("presentation_download")
  public String getPresentationDownload() {
    return presentationDownload;
  }

  @JsonProperty("presentation_download")
  public void setPresentationDownload(String presentationDownload) {
    this.presentationDownload = presentationDownload;
  }

  @JsonProperty("presenter_download")
  public String getPresenterDownload() {
    return presenterDownload;
  }

  @JsonProperty("presenter_download")
  public void setPresenterDownload(String presenterDownload) {
    this.presenterDownload = presenterDownload;
  }

  @JsonProperty("preview")
  public String getPreview() {
    return preview;
  }

  @JsonProperty("preview")
  public void setPreview(String preview) {
    this.preview = preview;
  }

  @JsonProperty("start")
  public String getStart() {
    return start;
  }

  @JsonProperty("start")
  public void setStart(String start) {
    this.start = start;
  }

  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  @JsonProperty("title")
  public void setTitle(String title) {
    this.title = title;
  }
}
