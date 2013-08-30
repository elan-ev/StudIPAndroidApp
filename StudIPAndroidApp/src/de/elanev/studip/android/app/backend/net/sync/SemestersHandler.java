/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.net.sync;

import java.util.ArrayList;

import de.elanev.studip.android.app.backend.datamodel.Semester;
import de.elanev.studip.android.app.backend.datamodel.Semesters;
import de.elanev.studip.android.app.backend.db.SemestersContract;
import android.content.ContentProviderOperation;

/**
 * @author joern
 * 
 */
public class SemestersHandler implements ResultHandler {

	Semesters mSemesters = null;

	/**
	 * @param response
	 */
	public SemestersHandler(Semesters s) {
		mSemesters = s;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.elanev.studip.android.app.backend.net.sync.ResultHandler#parse()
	 */
	public ArrayList<ContentProviderOperation> parse() {
		ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
		for (Semester s : mSemesters.semesters) {
			ops.add(parseSemester(s));
		}
		return ops;
	}

	private ContentProviderOperation parseSemester(Semester semester) {
		final ContentProviderOperation.Builder semesterBuilder = ContentProviderOperation
				.newInsert(SemestersContract.CONTENT_URI)
				.withValue(SemestersContract.Columns.SEMESTER_ID,
						semester.semester_id)
				.withValue(SemestersContract.Columns.SEMESTER_TITLE,
						semester.title)
				.withValue(SemestersContract.Columns.SEMESTER_DESCRIPTION,
						semester.description)
				.withValue(SemestersContract.Columns.SEMESTER_BEGIN,
						semester.begin)
				.withValue(SemestersContract.Columns.SEMESTER_END, semester.end)
				.withValue(SemestersContract.Columns.SEMESTER_SEMINARS_BEGIN,
						semester.seminars_begin)
				.withValue(SemestersContract.Columns.SEMESTER_SEMINARS_END,
						semester.seminars_end);
		return semesterBuilder.build();
	}

}
