/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.data.model;

import java.util.HashMap;

import de.elanev.studip.android.app.data.datamodel.Settings;

/**
 * @author joern
 */
public class MockSettings extends Settings {

  public MockSettings() {
    super(getSemTypes());
  }

  private static HashMap<Integer, SeminarTypeData> getSemTypes() {
    HashMap<Integer, SeminarTypeData> semTypes = new HashMap<>();
    semTypes.put(1, new SeminarTypeData("Vorlesung"));
    semTypes.put(2, new SeminarTypeData("Seminar"));
    semTypes.put(3, new SeminarTypeData("Übungen"));

    return semTypes;
  }
}
