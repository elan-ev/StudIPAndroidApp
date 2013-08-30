/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.net.sync;

import java.util.ArrayList;

import android.content.ContentProviderOperation;
import de.elanev.studip.android.app.backend.datamodel.Document;
import de.elanev.studip.android.app.backend.datamodel.Documents;
import de.elanev.studip.android.app.backend.db.DocumentsContract;
import de.elanev.studip.android.app.util.TextTools;

/**
 * @author joern
 * 
 */
public class DocumentsHandler implements ResultHandler {

	private Documents mDocuments;
	private String mCourseId;
	private String mFolderId;

	public DocumentsHandler(Documents documents, String courseId,
			String folderId) {
		this.mDocuments = documents;
		this.mCourseId = courseId;
		this.mFolderId = folderId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.elanev.studip.android.app.backend.net.sync.ResultHandler#parse()
	 */
	public ArrayList<ContentProviderOperation> parse() {
		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

		for (Document document : mDocuments.documents) {
			operations.add(parseDocument(document));
		}

		return operations;
	}

	private ContentProviderOperation parseDocument(Document d) {
		ContentProviderOperation.Builder builder = ContentProviderOperation
				.newInsert(DocumentsContract.CONTENT_URI)
				.withValue(DocumentsContract.Columns.DOCUMENT_ID, d.document_id)
				.withValue(DocumentsContract.Columns.DOCUMENT_FILENAME,
						d.filename)
				.withValue(DocumentsContract.Columns.DOCUMENT_DESCRIPTION,
						d.description)
				.withValue(DocumentsContract.Columns.DOCUMENT_FILESIZE,
						TextTools.readableFileSize(d.filesize))
				.withValue(DocumentsContract.Columns.DOCUMENT_CHDATE, d.chdate)
				.withValue(DocumentsContract.Columns.DOCUMENT_MKDATE, d.mkdate)
				.withValue(DocumentsContract.Columns.DOCUMENT_DOWNLOADS,
						d.downloads)
				.withValue(DocumentsContract.Columns.DOCUMENT_ICON, d.icon)
				.withValue(DocumentsContract.Columns.DOCUMENT_PROTECTED,
						d.file_protected)
				.withValue(DocumentsContract.Columns.DOCUMENT_USER_ID,
						d.user_id)
				.withValue(DocumentsContract.Columns.DOCUMENT_COURSE_ID,
						mCourseId)
				.withValue(DocumentsContract.Columns.DOCUMENT_MIME_TYPE,
						d.mime_type)
				.withValue(DocumentsContract.Columns.DOCUMENT_NAME, d.name);
//				.withValue(DocumentsContract.Columns.DOCUMENT_FOLDER, mFolderId);
		return builder.build();
	}
}
