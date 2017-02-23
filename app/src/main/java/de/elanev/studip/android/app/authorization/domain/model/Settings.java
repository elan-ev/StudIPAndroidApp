/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.authorization.domain.model;

import java.util.HashMap;

/**
 * @author joern
 */

public class Settings {
  private HashMap<Integer, SeminarTypeData> semTypes;

  public HashMap<Integer, SeminarTypeData> getSemTypes() {
    return semTypes;
  }

  public void setSemTypes(HashMap<Integer, SeminarTypeData> semTypes) {
    this.semTypes = semTypes;
  }

  public static class SeminarTypeData {
    private String name;

    public SeminarTypeData() {
    }

    public SeminarTypeData(String name) {
      this.name = name;
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
  }
}
