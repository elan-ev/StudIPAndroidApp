/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.data.repository.datastore;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.inject.Inject;
import javax.inject.Singleton;

import de.elanev.studip.android.app.authorization.data.entity.SettingsEntity;
import de.elanev.studip.android.app.util.Prefs;
import de.elanev.studip.android.app.util.TextTools;
import rx.Observable;
import timber.log.Timber;

/**
 * @author joern
 */
@Singleton
public class SettingsPrefsDataStore implements SettingsDataStore {
  private final ObjectMapper mapper;
  private final Prefs prefs;

  @Inject SettingsPrefsDataStore(ObjectMapper mapper, Prefs prefs) {
    this.mapper = mapper;
    this.prefs = prefs;
  }

  @Override public Observable<SettingsEntity> getSettings() {
    return Observable.fromCallable(() -> {
      String settingsJson = prefs.getApiSettings();
      if (!TextTools.isEmpty(settingsJson)) {
        SettingsEntity settings = null;
        try {
          settings = mapper.readValue(settingsJson, SettingsEntity.class);
        } catch (Exception e) {
          Timber.e(e.getLocalizedMessage(), e);
          throw e;
        }
        return settings;
      }

      return null;
    });
  }

  public void store(SettingsEntity settingsEntity) {
    try {
      String settingsJson = mapper.writeValueAsString(settingsEntity);
      prefs.setApiSettings(settingsJson);
    } catch (Exception e) {
      Timber.e(e.getLocalizedMessage(), e);
    }
  }
}
