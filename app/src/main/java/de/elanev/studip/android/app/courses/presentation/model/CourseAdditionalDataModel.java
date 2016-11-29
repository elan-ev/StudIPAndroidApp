/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.presentation.model;

import java.util.List;

/**
 * @author joern
 */
public class CourseAdditionalDataModel {
  private List<RecordingModel> recordings;
  private UnizensusModel unizensusItem;

  public List<RecordingModel> getRecordings() {
    return recordings;
  }

  public void setRecordings(List<RecordingModel> recordings) {
    this.recordings = recordings;
  }

  public UnizensusModel getUnizensusItem() {
    return unizensusItem;
  }

  public void setUnizensusItem(UnizensusModel unizensusItem) {
    this.unizensusItem = unizensusItem;
  }
}
