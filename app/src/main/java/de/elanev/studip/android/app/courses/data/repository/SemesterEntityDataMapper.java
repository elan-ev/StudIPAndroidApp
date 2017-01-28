/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.data.repository;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.courses.data.entity.RealmSemesterEntity;
import de.elanev.studip.android.app.courses.data.entity.SemesterEntity;
import de.elanev.studip.android.app.courses.domain.Semester;
import io.realm.RealmList;

/**
 * @author joern
 */

@Singleton
public class SemesterEntityDataMapper {

  @Inject SemesterEntityDataMapper() {
  }

  RealmList<RealmSemesterEntity> transformToRealm(List<SemesterEntity> semesterEntities) {
    RealmList<RealmSemesterEntity> realmSemesterEntities = new RealmList<>();

    for (SemesterEntity semesterEntity : semesterEntities) {
      realmSemesterEntities.add(transformToRealm(semesterEntity));
    }

    return realmSemesterEntities;
  }

  public RealmSemesterEntity transformToRealm(SemesterEntity semesterEntity) {
    RealmSemesterEntity realmSemesterEntity = new RealmSemesterEntity();
    realmSemesterEntity.setSemesterId(semesterEntity.getSemesterId());
    realmSemesterEntity.setTitle(semesterEntity.getTitle());
    realmSemesterEntity.setDescription(semesterEntity.getDescription());
    realmSemesterEntity.setBegin(semesterEntity.getBegin());
    realmSemesterEntity.setEnd(semesterEntity.getEnd());
    realmSemesterEntity.setSeminarsBegin(semesterEntity.getSeminarsBegin());
    realmSemesterEntity.setSeminarsEnd(semesterEntity.getSeminarsEnd());

    return realmSemesterEntity;
  }

  public SemesterEntity transformFromRealm(RealmSemesterEntity realmSemesterEntity) {
    SemesterEntity semesterEntity = new SemesterEntity();
    semesterEntity.setSemesterId(realmSemesterEntity.getSemesterId());
    semesterEntity.setTitle(realmSemesterEntity.getTitle());
    semesterEntity.setDescription(realmSemesterEntity.getDescription());
    semesterEntity.setBegin(realmSemesterEntity.getBegin());
    semesterEntity.setEnd(realmSemesterEntity.getEnd());
    semesterEntity.setSeminarsBegin(realmSemesterEntity.getSeminarsBegin());
    semesterEntity.setSeminarsEnd(realmSemesterEntity.getSeminarsEnd());

    return semesterEntity;
  }

  public Semester transform(SemesterEntity semesterEntity) {
    Semester semester = new Semester();
    semester.setSemesterId(semesterEntity.getSemesterId());
    semester.setTitle(semesterEntity.getTitle());
    semester.setDescription(semesterEntity.getDescription());
    semester.setBegin(semesterEntity.getBegin());
    semester.setEnd(semesterEntity.getEnd());
    semester.setSeminarsBegin(semesterEntity.getSeminarsBegin());
    semester.setSeminarsEnd(semesterEntity.getSeminarsEnd());

    return semester;
  }
}
