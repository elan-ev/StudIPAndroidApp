/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.presentation.mapper;

import javax.inject.Inject;

import de.elanev.studip.android.app.base.internal.di.PerFragment;
import de.elanev.studip.android.app.courses.domain.Semester;
import de.elanev.studip.android.app.courses.presentation.model.SemesterModel;

/**
 * @author joern
 */
@PerFragment
public class SemesterModelDataMapper {
  @Inject public SemesterModelDataMapper() {
  }

  public SemesterModel transform(Semester semester) {
    SemesterModel semesterModel = new SemesterModel();
    semesterModel.setSemesterId(semester.getSemesterId());
    semesterModel.setTitle(semester.getTitle());
    semesterModel.setDescription(semester.getDescription());
    semesterModel.setBegin(semester.getBegin());
    semesterModel.setEnd(semester.getEnd());
    semesterModel.setSeminarsBegin(semester.getSeminarsBegin());
    semesterModel.setSeminarsEnd(semester.getSeminarsEnd());

    return semesterModel;
  }
}
