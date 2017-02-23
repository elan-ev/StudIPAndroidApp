/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.data.repository;

import java.util.HashMap;

import de.elanev.studip.android.app.authorization.domain.SettingsRepository;
import de.elanev.studip.android.app.authorization.domain.model.Settings;
import rx.Observable;

/**
 * @author joern
 */

public class MockSettingsRepository implements SettingsRepository {
  public static final HashMap<Integer, Settings.SeminarTypeData> semTypes = new HashMap<>();

  static {
    semTypes.put(1, new Settings.SeminarTypeData("Vorlesung"));
    semTypes.put(2, new Settings.SeminarTypeData("Übungen"));
    semTypes.put(3, new Settings.SeminarTypeData("Gruppe"));
  }

  @Override public Observable<Settings> studipSettings(boolean forceUpdate) {
    Settings settings = new Settings();
    settings.setSemTypes(semTypes);

    return Observable.just(settings);
  }
}
