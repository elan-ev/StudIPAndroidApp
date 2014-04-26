/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.net.sync;

import android.content.ContentProviderOperation;

import de.elanev.studip.android.app.backend.datamodel.Document;
import de.elanev.studip.android.app.backend.datamodel.DocumentFolder;
import de.elanev.studip.android.app.backend.db.DocumentsContract;
import de.elanev.studip.android.app.util.TextTools;

import java.util.ArrayList;

/**
 * @author joern
 * 
 */
public class DocumentsHandler implements ResultHandler {

	private ArrayList<Document> mDocuments;
	private String mCourseId;
	private DocumentFolder mFolder;

	/**
	 * Creates a new DocumentsHandler with documents, course reference and
	 * folder reference
	 * 
	 * @param documents
	 *            the documents to insert
	 * @param courseId
	 *            the referencing course
	 * @param folder
	 *            the referencing folder
	 */
	public DocumentsHandler(ArrayList<Document> documents, String courseId,
			DocumentFolder folder) {
		this.mDocuments = documents;
		this.mCourseId = courseId;
		this.mFolder = folder;
	}

	/**
	 * @param documents
	 * @param courseId
	 */
	public DocumentsHandler(ArrayList<Document> documents, String courseId) {
		this.mDocuments = documents;
		this.mCourseId = courseId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.elanev.studip.android.app.backend.net.sync.ResultHandler#parse()
	 */
	public ArrayList<ContentProviderOperation> parse() {
		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

		if (mFolder != null) {
			operations.add(parseFolder(mFolder));
			for (Document document : mDocuments) {
				operations.add(parseDocumentWithBackRef(document, 0));
			}
		} else {
			for (Document document : mDocuments) {
				operations.add(parseDocument(document).build());
			}
		}

		return operations;
	}

	/*
	 * Parses the passed document object and builds an ContentProvider insert
	 */
	private ContentProviderOperation.Builder parseDocument(Document d) {
		ContentProviderOperation.Builder builder = ContentProviderOperation
				.newInsert(DocumentsContract.CONTENT_URI)
				.withValue(DocumentsContract.Columns.Documents.DOCUMENT_ID,
						d.document_id)
				.withValue(
						DocumentsContract.Columns.Documents.DOCUMENT_FILENAME,
						d.filename)
				.withValue(
						DocumentsContract.Columns.Documents.DOCUMENT_DESCRIPTION,
						d.description)
				.withValue(
						DocumentsContract.Columns.Documents.DOCUMENT_FILESIZE,
						TextTools.readableFileSize(d.filesize))
				.withValue(DocumentsContract.Columns.Documents.DOCUMENT_CHDATE,
						d.chdate)
				.withValue(DocumentsContract.Columns.Documents.DOCUMENT_MKDATE,
						d.mkdate)
				.withValue(
						DocumentsContract.Columns.Documents.DOCUMENT_DOWNLOADS,
						d.downloads)
				.withValue(DocumentsContract.Columns.Documents.DOCUMENT_ICON,
						d.icon)
				.withValue(
						DocumentsContract.Columns.Documents.DOCUMENT_PROTECTED,
						d.file_protected)
				.withValue(
						DocumentsContract.Columns.Documents.DOCUMENT_USER_ID,
						d.user_id)
				.withValue(
						DocumentsContract.Columns.Documents.DOCUMENT_COURSE_ID,
						mCourseId)
				.withValue(
						DocumentsContract.Columns.Documents.DOCUMENT_MIME_TYPE,
						d.mime_type)
				.withValue(DocumentsContract.Columns.Documents.DOCUMENT_NAME,
						d.name);

		return builder;
	}

	/*
	 * Parse the passed document and build an ContentProvider insert with the
	 * passed back reference to the previously inserted folder
	 */
	private ContentProviderOperation parseDocumentWithBackRef(Document d,
			int folderBackRef) {
		ContentProviderOperation.Builder builder = parseDocument(d);
		builder.withValueBackReference(
				DocumentsContract.Columns.Documents.DOCUMENT_FOLDER_ID,
				folderBackRef);
		return builder.build();
	}

	/*
	 * Parsing the passed folder and build an ContentProvider insert
	 */
	private ContentProviderOperation parseFolder(DocumentFolder folder) {

		ContentProviderOperation.Builder builder = ContentProviderOperation
				.newInsert(
						DocumentsContract.CONTENT_URI.buildUpon()
								.appendPath("folder").build())
				.withValue(DocumentsContract.Columns.DocumentFolders.FOLDER_ID,
						folder.folder_id)
				.withValue(
						DocumentsContract.Columns.DocumentFolders.FOLDER_NAME,
						folder.name)
				.withValue(
						DocumentsContract.Columns.DocumentFolders.FOLDER_DESCRIPTION,
						folder.description)
				.withValue(
						DocumentsContract.Columns.DocumentFolders.FOLDER_MKDATE,
						folder.mkdate)
				.withValue(
						DocumentsContract.Columns.DocumentFolders.FOLDER_CHDATE,
						folder.chdate)
				// .withValue(
				// DocumentsContract.Columns.DocumentFolders.FOLDER_PERMISSIONS,
				// folder.permissions)
				.withValue(
						DocumentsContract.Columns.DocumentFolders.FOLDER_USER_ID,
						folder.user_id);

		return builder.build();
	}

}
