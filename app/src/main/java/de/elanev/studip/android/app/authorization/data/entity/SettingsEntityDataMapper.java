/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.data.entity;

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
    try {
      throw new Exception("TODO");
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
