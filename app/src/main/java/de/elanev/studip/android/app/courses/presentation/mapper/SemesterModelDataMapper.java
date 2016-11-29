/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.courses.presentation.mapper;

import javax.inject.Inject;

import de.elanev.studip.android.app.base.internal.di.PerActivity;
import de.elanev.studip.android.app.courses.domain.DomainSemester;
import de.elanev.studip.android.app.courses.presentation.model.SemesterModel;

/**
 * @author joern
 */
@PerActivity
public class SemesterModelDataMapper {
  @Inject public SemesterModelDataMapper() {
  }

  public SemesterModel transform(DomainSemester domainSemester) {
    SemesterModel semesterModel = new SemesterModel();
    semesterModel.setSemesterId(domainSemester.getSemesterId());
    semesterModel.setTitle(domainSemester.getTitle());
    semesterModel.setDescription(domainSemester.getDescription());
    semesterModel.setBegin(domainSemester.getBegin());
    semesterModel.setEnd(domainSemester.getEnd());
    semesterModel.setSeminarsBegin(domainSemester.getSeminarsBegin());
    semesterModel.setSeminarsEnd(domainSemester.getSeminarsEnd());

    return semesterModel;
  }
}
