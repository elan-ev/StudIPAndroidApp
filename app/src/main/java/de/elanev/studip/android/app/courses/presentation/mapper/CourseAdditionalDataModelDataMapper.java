/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.presentation.mapper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.elanev.studip.android.app.base.internal.di.PerActivity;
import de.elanev.studip.android.app.courses.domain.DomainCourseAdditionalData;
import de.elanev.studip.android.app.courses.domain.DomainRecording;
import de.elanev.studip.android.app.courses.domain.DomainUnizensus;
import de.elanev.studip.android.app.courses.presentation.model.CourseAdditionalDataModel;
import de.elanev.studip.android.app.courses.presentation.model.RecordingModel;
import de.elanev.studip.android.app.courses.presentation.model.UnizensusModel;

/**
 * @author joern
 */
@PerActivity
public class CourseAdditionalDataModelDataMapper {

  @Inject public CourseAdditionalDataModelDataMapper() {}

  public CourseAdditionalDataModel transform(
      DomainCourseAdditionalData domainCourseAdditionalData) {
    if (domainCourseAdditionalData == null) return null;

    CourseAdditionalDataModel courseAddDataModel = new CourseAdditionalDataModel();
    if (courseAddDataModel.getRecordings() != null) {
      courseAddDataModel.setRecordings(transform(domainCourseAdditionalData.getRecordings()));
    }

    if (courseAddDataModel.getUnizensusItem() != null) {
      courseAddDataModel.setUnizensusItem(transform(domainCourseAdditionalData.getUnizensusItem()));
    }

    return courseAddDataModel;
  }

  private List<RecordingModel> transform(List<DomainRecording> recordings) {
    ArrayList<RecordingModel> recordingModels = new ArrayList<>(recordings.size());

    for (DomainRecording domainRecording : recordings) {
      recordingModels.add(transform(domainRecording));
    }

    return recordingModels;
  }

  private UnizensusModel transform(DomainUnizensus domainUnizensus) {
    UnizensusModel unizensusModel = new UnizensusModel();
    unizensusModel.setType(domainUnizensus.getType());
    unizensusModel.setUrl(domainUnizensus.getUrl());

    return unizensusModel;
  }

  private RecordingModel transform(DomainRecording domainRecording) {
    RecordingModel recordingModel = new RecordingModel();
    recordingModel.setId(domainRecording.getId());
    recordingModel.setTitle(domainRecording.getTitle());
    recordingModel.setStart(domainRecording.getStart());
    recordingModel.setDuration(domainRecording.getDuration());
    recordingModel.setDescription(domainRecording.getDescription());
    recordingModel.setAuthor(domainRecording.getAuthor());
    recordingModel.setPreview(domainRecording.getPreview());
    recordingModel.setExternalPlayerUrl(domainRecording.getExternalPlayerUrl());
    recordingModel.setPresentationDownload(domainRecording.getPresentationDownload());
    recordingModel.setPresenterDownload(domainRecording.getPresenterDownload());
    recordingModel.setAudioDownload(domainRecording.getAudioDownload());

    return recordingModel;
  }
}
