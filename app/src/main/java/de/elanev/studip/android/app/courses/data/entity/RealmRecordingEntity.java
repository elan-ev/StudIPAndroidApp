/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.data.entity;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * POJO that represents one episode of the /courses/:course_id oc_recordings JSON property.
 *
 * @author Jörn
 */
public class RealmRecordingEntity extends RealmObject {

  @PrimaryKey private String id;
  private String title;
  private String start;
  private long duration;
  private String description;
  private String author;
  private String preview;
  private String externalPlayerUrl;
  private String presenterDownload;
  private String presentationDownload;
  private String audioDownload;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getStart() {
    return start;
  }

  public void setStart(String start) {
    this.start = start;
  }

  public long getDuration() {
    return duration;
  }

  public void setDuration(long duration) {
    this.duration = duration;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getPreview() {
    return preview;
  }

  public void setPreview(String preview) {
    this.preview = preview;
  }

  public String getExternalPlayerUrl() {
    return externalPlayerUrl;
  }

  public void setExternalPlayerUrl(String externalPlayerUrl) {
    this.externalPlayerUrl = externalPlayerUrl;
  }

  public String getPresenterDownload() {
    return presenterDownload;
  }

  public void setPresenterDownload(String presenterDownload) {
    this.presenterDownload = presenterDownload;
  }

  public String getPresentationDownload() {
    return presentationDownload;
  }

  public void setPresentationDownload(String presentationDownload) {
    this.presentationDownload = presentationDownload;
  }

  public String getAudioDownload() {
    return audioDownload;
  }

  public void setAudioDownload(String audioDownload) {
    this.audioDownload = audioDownload;
  }
}