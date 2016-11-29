/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.domain;

import java.util.List;

/**
 * @author joern
 */
public class DomainCourseAdditionalData {
  private List<DomainRecording> recordings;
  private DomainUnizensus unizensusItem;

  public DomainUnizensus getUnizensusItem() {
    return unizensusItem;
  }

  public void setUnizensusItem(DomainUnizensus unizensusItem) {
    this.unizensusItem = unizensusItem;
  }

  public List<DomainRecording> getRecordings() {
    return recordings;
  }

  public void setRecordings(List<DomainRecording> recordings) {
    this.recordings = recordings;
  }
}
