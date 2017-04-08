/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.data.net.sync;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.data.datamodel.Institutes;
import de.elanev.studip.android.app.data.datamodel.RealmInstituteEntity;

/**
 * @author joern
 */
@Singleton
public class InstitutesEntityDataMapper {

  @Inject public InstitutesEntityDataMapper() {
  }

  List<RealmInstituteEntity> transformToRealm(List<Institutes.Institute> institutes) {
    ArrayList<RealmInstituteEntity> realmInstituteEntities = new ArrayList<>(institutes.size());

    for (Institutes.Institute institute : institutes) {
      if (institute != null) {
        realmInstituteEntities.add(transformToRealm(institute));
      }
    }

    return realmInstituteEntities;
  }

  private RealmInstituteEntity transformToRealm(Institutes.Institute institute) {
    RealmInstituteEntity realmInstituteEntity = new RealmInstituteEntity();
    realmInstituteEntity.setInstituteId(institute.getInstituteId());
    realmInstituteEntity.setName(institute.getName());
    realmInstituteEntity.setPerms(institute.getPerms());
    realmInstituteEntity.setConsultation(institute.getConsultation());
    realmInstituteEntity.setRoom(institute.getRoom());
    realmInstituteEntity.setPhone(institute.getPhone());
    realmInstituteEntity.setFax(institute.getFax());
    realmInstituteEntity.setStreet(institute.getStreet());
    realmInstituteEntity.setCity(institute.getCity());
    realmInstituteEntity.setFacultyName(institute.getFacultyName());
    realmInstituteEntity.setFacultyStreet(institute.getFacultyStreet());
    realmInstituteEntity.setFacultyCity(institute.getFacultyCity());

    return realmInstituteEntity;
  }
}
