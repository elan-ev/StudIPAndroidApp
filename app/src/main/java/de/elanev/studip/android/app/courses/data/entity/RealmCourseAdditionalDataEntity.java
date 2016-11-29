/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.data.entity;

import io.realm.RealmList;
import io.realm.RealmObject;

/**
 * @author joern
 */
public class RealmCourseAdditionalDataEntity extends RealmObject {
  private RealmList<RealmRecordingEntity> recordings = new RealmList<>();
  private RealmUnizensusEntity unizensusItem;

  public RealmList<RealmRecordingEntity> getRecordings() {
    return recordings;
  }

  public void setRecordings(RealmList<RealmRecordingEntity> recordings) {
    this.recordings = recordings;
  }

  public RealmUnizensusEntity getUnizensusItem() {
    return unizensusItem;
  }

  public void setUnizensusItem(RealmUnizensusEntity unizensusItem) {
    this.unizensusItem = unizensusItem;
  }
}
