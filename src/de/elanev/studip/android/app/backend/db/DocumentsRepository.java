/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.db;

import de.elanev.studip.android.app.backend.datamodel.Document;
import de.elanev.studip.android.app.backend.datamodel.Documents;
import de.elanev.studip.android.app.util.FileUtils;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DocumentsRepository {
	private static DocumentsRepository instance;
	private Context mContext;

	public static synchronized DocumentsRepository getInstance(Context context) {
		if (instance == null)
			instance = new DocumentsRepository(context);

		return instance;
	}

	private DocumentsRepository(Context context) {
		this.mContext = context;
	}

	public void addDocuments(Documents documents, String cid) {
		SQLiteDatabase db = null;
		try {
			for (Document document : documents.documents) {

				ContentValues values = new ContentValues();

				values.put(DocumentsContract.Columns.DOCUMENT_ID,
						document.document_id);
				values.put(DocumentsContract.Columns.DOCUMENT_USER_ID,
						document.user_id);
				values.put(DocumentsContract.Columns.DOCUMENT_NAME,
						document.name);
				values.put(DocumentsContract.Columns.DOCUMENT_DESCRIPTION,
						document.description);
				values.put(DocumentsContract.Columns.DOCUMENT_MKDATE,
						document.mkdate);
				values.put(DocumentsContract.Columns.DOCUMENT_CHDATE,
						document.chdate);
				values.put(DocumentsContract.Columns.DOCUMENT_FILENAME,
						document.filename);
				String fsize = FileUtils.readableFileSize(document.filesize
						.longValue());
				values.put(DocumentsContract.Columns.DOCUMENT_FILESIZE, fsize);
				values.put(DocumentsContract.Columns.DOCUMENT_DOWNLOADS,
						document.downloads);
				values.put(DocumentsContract.Columns.DOCUMENT_MIME_TYPE,
						document.mime_type);
				values.put(DocumentsContract.Columns.DOCUMENT_ICON,
						document.icon);
				values.put(DocumentsContract.Columns.DOCUMENT_PROTECTED,
						document.file_protected);
				values.put(DocumentsContract.Columns.DOCUMENT_COURSE_ID, cid);
				db = DatabaseHandler.getInstance(mContext)
						.getWritableDatabase();
				db.beginTransaction();
				try {
					db.insertWithOnConflict(DocumentsContract.TABLE, null,
							values, SQLiteDatabase.CONFLICT_IGNORE);
					db.setTransactionSuccessful();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					db.endTransaction();
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public Cursor getDocumentsCursorForCourse(String cid) {
		SQLiteDatabase db = DatabaseHandler.getInstance(mContext)
				.getReadableDatabase();
		Cursor cursor = null;
		cursor = db.query(DocumentsContract.TABLE, null,
				DocumentsContract.Columns.DOCUMENT_COURSE_ID + "=? ",
				new String[] { cid }, null, null,
				DocumentsContract.Columns.DOCUMENT_NAME + " ASC");
		return cursor;
	}
}
