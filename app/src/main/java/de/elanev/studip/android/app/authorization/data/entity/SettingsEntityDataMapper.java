/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.data.entity;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.authorization.domain.model.Settings;

/**
 * @author joern
 */
@Singleton
public class SettingsEntityDataMapper {
  @Inject public SettingsEntityDataMapper() {}

  public Settings transform(SettingsEntity settingsEntity) {
    Settings settings = new Settings();
    settings.setSemTypes(transform(settingsEntity.getSemTypes()));

    return settings;
  }

  private HashMap<Integer, Settings.SeminarTypeData> transform(
      HashMap<Integer, SettingsEntity.SeminarTypeData> semTypes) {
    HashMap<Integer, Settings.SeminarTypeData> domainSemTypes = new HashMap<>(semTypes.size());

    for (Map.Entry<Integer, SettingsEntity.SeminarTypeData> seminarTypeDataEntry : semTypes.entrySet()) {
      domainSemTypes.put(seminarTypeDataEntry.getKey(), transform(seminarTypeDataEntry.getValue()));
    }

    return domainSemTypes;
  }

  private Settings.SeminarTypeData transform(SettingsEntity.SeminarTypeData value) {
    Settings.SeminarTypeData seminarTypeData = new Settings.SeminarTypeData();
    seminarTypeData.setName(value.getName());

    return seminarTypeData;
  }
}
