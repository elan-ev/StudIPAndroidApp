/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.data.net.sync;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.courses.domain.DomainSemester;
import de.elanev.studip.android.app.data.datamodel.RealmSemesterEntity;
import de.elanev.studip.android.app.data.datamodel.Semester;
import io.realm.RealmList;

/**
 * @author joern
 */

@Singleton
public class SemesterEntityDataMapper {

  @Inject SemesterEntityDataMapper() {
  }

  RealmList<RealmSemesterEntity> transformToRealm(List<Semester> semesters) {
    RealmList<RealmSemesterEntity> realmSemesterEntities = new RealmList<>();

    for (Semester semester : semesters) {
      realmSemesterEntities.add(transformToRealm(semester));
    }

    return realmSemesterEntities;
  }

  public RealmSemesterEntity transformToRealm(Semester semester) {
    RealmSemesterEntity realmSemesterEntity = new RealmSemesterEntity();
    realmSemesterEntity.setSemesterId(semester.getSemesterId());
    realmSemesterEntity.setTitle(semester.getTitle());
    realmSemesterEntity.setDescription(semester.getDescription());
    realmSemesterEntity.setBegin(semester.getBegin());
    realmSemesterEntity.setEnd(semester.getEnd());
    realmSemesterEntity.setSeminarsBegin(semester.getSeminarsBegin());
    realmSemesterEntity.setSeminarsEnd(semester.getSeminarsEnd());

    return realmSemesterEntity;
  }

  public Semester transformFromRealm(RealmSemesterEntity realmSemesterEntity) {
    Semester semester = new Semester();
    semester.setSemesterId(realmSemesterEntity.getSemesterId());
    semester.setTitle(realmSemesterEntity.getTitle());
    semester.setDescription(realmSemesterEntity.getDescription());
    semester.setBegin(realmSemesterEntity.getBegin());
    semester.setEnd(realmSemesterEntity.getEnd());
    semester.setSeminarsBegin(realmSemesterEntity.getSeminarsBegin());
    semester.setSeminarsEnd(realmSemesterEntity.getSeminarsEnd());

    return semester;
  }

  public DomainSemester transform(Semester semester) {
    DomainSemester domainSemester = new DomainSemester();
    domainSemester.setSemesterId(semester.getSemesterId());
    domainSemester.setTitle(semester.getTitle());
    domainSemester.setDescription(semester.getDescription());
    domainSemester.setBegin(semester.getBegin());
    domainSemester.setEnd(semester.getEnd());
    domainSemester.setSeminarsBegin(semester.getSeminarsBegin());
    domainSemester.setSeminarsEnd(semester.getSeminarsEnd());

    return domainSemester;
  }
}
