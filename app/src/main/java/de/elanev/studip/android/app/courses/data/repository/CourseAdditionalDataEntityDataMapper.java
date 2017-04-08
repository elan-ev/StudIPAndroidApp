/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.data.repository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.courses.data.entity.CourseAdditionalData;
import de.elanev.studip.android.app.courses.data.entity.RealmCourseAdditionalDataEntity;
import de.elanev.studip.android.app.courses.data.entity.RealmRecordingEntity;
import de.elanev.studip.android.app.courses.data.entity.RealmUnizensusEntity;
import de.elanev.studip.android.app.courses.data.entity.Recording;
import de.elanev.studip.android.app.courses.data.entity.UnizensusItem;
import de.elanev.studip.android.app.courses.domain.DomainCourseAdditionalData;
import de.elanev.studip.android.app.courses.domain.DomainRecording;
import de.elanev.studip.android.app.courses.domain.DomainUnizensus;
import io.realm.RealmList;

/**
 * @author joern
 */
@Singleton
public class CourseAdditionalDataEntityDataMapper {

  @Inject public CourseAdditionalDataEntityDataMapper() {
  }

  DomainCourseAdditionalData transform(CourseAdditionalData courseAdditionalData) {
    if (courseAdditionalData == null) return null;

    DomainCourseAdditionalData domainCourseAddData = new DomainCourseAdditionalData();
    if (courseAdditionalData.getRecordings() != null) {
      domainCourseAddData.setRecordings(transform(courseAdditionalData.getRecordings()));
    }

    if (courseAdditionalData.getUnizensusItem() != null) {
      domainCourseAddData.setUnizensusItem(transform(courseAdditionalData.getUnizensusItem()));
    }

    return domainCourseAddData;
  }

  private List<DomainRecording> transform(List<Recording> recordings) {
    ArrayList<DomainRecording> domainRecordings = new ArrayList<>(recordings.size());

    for (Recording recording : recordings) {
      domainRecordings.add(transform(recording));
    }

    return domainRecordings;
  }

  private DomainUnizensus transform(UnizensusItem unizensusItem) {
    DomainUnizensus domainUnizensus = new DomainUnizensus();
    domainUnizensus.setType(unizensusItem.type);
    domainUnizensus.setUrl(unizensusItem.url);

    return domainUnizensus;
  }

  private DomainRecording transform(Recording recording) {
    DomainRecording domainRecording = new DomainRecording();
    domainRecording.setId(recording.getId());
    domainRecording.setTitle(recording.getTitle());
    domainRecording.setStart(recording.getStart());
    domainRecording.setDuration(recording.getDuration());
    domainRecording.setDescription(recording.getDescription());
    domainRecording.setAuthor(recording.getAuthor());
    domainRecording.setPreview(recording.getPreview());
    domainRecording.setExternalPlayerUrl(recording.getExternalPlayerUrl());
    domainRecording.setPresentationDownload(recording.getPresentationDownload());
    domainRecording.setPresenterDownload(recording.getPresenterDownload());
    domainRecording.setAudioDownload(recording.getAudioDownload());

    return domainRecording;
  }

  CourseAdditionalData transformFromRealm(
      RealmCourseAdditionalDataEntity realmCourseAdditionalDataEntity) {
    if (realmCourseAdditionalDataEntity == null) return null;

    CourseAdditionalData courseAdditionalData = new CourseAdditionalData();
    if (realmCourseAdditionalDataEntity.getRecordings() != null) {
      courseAdditionalData.setRecordings(
          transformFromRealm(realmCourseAdditionalDataEntity.getRecordings()));
    }

    if (realmCourseAdditionalDataEntity.getUnizensusItem() != null) {
      courseAdditionalData.setUnizensusItem(
          transformFromRealm(realmCourseAdditionalDataEntity.getUnizensusItem()));
    }

    return courseAdditionalData;
  }

  private List<Recording> transformFromRealm(
      RealmList<RealmRecordingEntity> realmRecordingEntities) {
    ArrayList<Recording> recordings = new ArrayList<>(realmRecordingEntities.size());

    for (RealmRecordingEntity realmRecordingEntity : realmRecordingEntities) {
      recordings.add(transformFromRealm(realmRecordingEntity));
    }

    return recordings;
  }

  private UnizensusItem transformFromRealm(RealmUnizensusEntity realmUnizensusEntity) {
    UnizensusItem unizensusItem = new UnizensusItem();
    unizensusItem.type = realmUnizensusEntity.getType();
    unizensusItem.url = realmUnizensusEntity.getUrl();

    return unizensusItem;
  }

  private Recording transformFromRealm(RealmRecordingEntity realmRecordingEntity) {
    Recording recording = new Recording();
    recording.setId(realmRecordingEntity.getId());
    recording.setTitle(realmRecordingEntity.getTitle());
    recording.setStart(realmRecordingEntity.getStart());
    recording.setDuration(realmRecordingEntity.getDuration());
    recording.setDescription(realmRecordingEntity.getDescription());
    recording.setAuthor(realmRecordingEntity.getAuthor());
    recording.setPreview(realmRecordingEntity.getPreview());
    recording.setExternalPlayerUrl(realmRecordingEntity.getExternalPlayerUrl());
    recording.setPresentationDownload(realmRecordingEntity.getPresentationDownload());
    recording.setPresenterDownload(realmRecordingEntity.getPresenterDownload());
    recording.setAudioDownload(realmRecordingEntity.getAudioDownload());

    return recording;
  }

  RealmCourseAdditionalDataEntity transformToRealm(CourseAdditionalData courseAdditionalData) {
    if (courseAdditionalData == null) return null;

    RealmCourseAdditionalDataEntity realmCourseAddData = new RealmCourseAdditionalDataEntity();
    if (courseAdditionalData.getRecordings() != null) {
      realmCourseAddData.setRecordings(transformToRealm(courseAdditionalData.getRecordings()));
    }

    if (courseAdditionalData.getUnizensusItem() != null) {
      realmCourseAddData.setUnizensusItem(
          transformToRealm(courseAdditionalData.getUnizensusItem()));
    }

    return realmCourseAddData;
  }

  private RealmList<RealmRecordingEntity> transformToRealm(List<Recording> recordings) {
    RealmList<RealmRecordingEntity> realmRecordingEntities = new RealmList<>();

    for (Recording recording : recordings) {
      realmRecordingEntities.add(transformToRealm(recording));
    }

    return realmRecordingEntities;
  }

  private RealmUnizensusEntity transformToRealm(UnizensusItem unizensusItem) {
    RealmUnizensusEntity realmUnizensusEntity = new RealmUnizensusEntity();
    realmUnizensusEntity.setType(unizensusItem.type);
    realmUnizensusEntity.setUrl(unizensusItem.url);

    return realmUnizensusEntity;
  }

  private RealmRecordingEntity transformToRealm(Recording recording) {
    RealmRecordingEntity realmRecording = new RealmRecordingEntity();
    realmRecording.setId(recording.getId());
    realmRecording.setTitle(recording.getTitle());
    realmRecording.setStart(recording.getStart());
    realmRecording.setDuration(recording.getDuration());
    realmRecording.setDescription(recording.getDescription());
    realmRecording.setAuthor(recording.getAuthor());
    realmRecording.setPreview(recording.getPreview());
    realmRecording.setExternalPlayerUrl(recording.getExternalPlayerUrl());
    realmRecording.setPresentationDownload(recording.getPresentationDownload());
    realmRecording.setPresenterDownload(recording.getPresenterDownload());
    realmRecording.setAudioDownload(recording.getAudioDownload());

    return realmRecording;
  }
}
