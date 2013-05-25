/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.net.services.syncservice.activitys;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;

import de.elanev.studip.android.app.backend.datamodel.Document;
import de.elanev.studip.android.app.backend.datamodel.Documents;
import de.elanev.studip.android.app.backend.datamodel.DocumentFolder;
import de.elanev.studip.android.app.backend.datamodel.DocumentFolders;
import de.elanev.studip.android.app.backend.db.AbstractContract;
import de.elanev.studip.android.app.backend.db.CoursesContract;
import de.elanev.studip.android.app.backend.db.DocumentsContract;
import de.elanev.studip.android.app.backend.net.api.ApiEndpoints;
import de.elanev.studip.android.app.backend.net.services.syncservice.AbstractParserTask;
import de.elanev.studip.android.app.backend.net.services.syncservice.RestIPSyncService;
import de.elanev.studip.android.app.frontend.courses.CourseDocumentsFragment;
import de.elanev.studip.android.app.util.TextTools;

/**
 * @author joern
 * 
 */
public class DocumentsResponderFragment extends
		AbstractRestIPResultReceiver<Documents, CourseDocumentsFragment> {
	private DocumentFolders mFolders = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.elanev.studip.android.app.backend.net.services.syncservice.activitys.
	 * AbstractRestIPResultReceiver#loadData()
	 */
	@Override
	public void loadData() {
		String cid = null;
		if (getActivity() != null) {
			if (mContext != null) {
				cid = getArguments().getString(
						CoursesContract.Columns.Courses.COURSE_ID);

				if (mFolders == null) {

					Intent intent = new Intent(mContext,
							RestIPSyncService.class);
					intent.setData(Uri.parse(String.format(mServerApiUrl + "/"
							+ ApiEndpoints.COURSE_DOCUMENTS_FOLDERS_ENDPOINT,
							cid)));

					intent.putExtra(RestIPSyncService.RESTIP_RESULT_RECEIVER,
							getResultReceiver());
					mContext.startService(intent);

				} else if (mFolders != null && mReturnItem == null) {

					for (DocumentFolder f : mFolders.folders) {

						Intent intent = new Intent(mContext,
								RestIPSyncService.class);
						intent.setData(Uri.parse(String
								.format(mServerApiUrl
										+ "/"
										+ ApiEndpoints.COURSE_DOCUMENTS_FOLDERS_FILES_ENDPOINT,
										new Object[] { cid, f.folder_id })));

						intent.putExtra(
								RestIPSyncService.RESTIP_RESULT_RECEIVER,
								getResultReceiver());
						mContext.startService(intent);

					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.elanev.studip.android.app.backend.net.services.syncservice.activitys.
	 * AbstractRestIPResultReceiver#parse(java.lang.String)
	 */
	@Override
	protected void parse(String result) {
		if (mFolders == null) {
			FolderParserTask fTask = new FolderParserTask();
			fTask.execute(result);
		} else {
			DocumentsParserTask pTask = new DocumentsParserTask();
			List<String> uriSegment = mResponseUri.getPathSegments();
			String cid = uriSegment.get(uriSegment.size() - 3);
			pTask.execute(new String[] { result, cid });
		}
	}

	class FolderParserTask extends AbstractParserTask<DocumentFolders> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected DocumentFolders doInBackground(String... params) {
			JsonParser jp;
			DocumentFolders folders = null;
			try {
				jp = jsonFactory.createJsonParser(params[0]);
				folders = objectMapper.readValue(jp, DocumentFolders.class);
			} catch (JsonParseException e) {
				e.printStackTrace();
				cancel(true);
			} catch (IOException e) {
				e.printStackTrace();
			}

			return folders;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(DocumentFolders result) {
			super.onPostExecute(result);
			mFolders = result;
			loadData();
		}

	}

	class DocumentsParserTask extends AbstractParserTask<Documents> {
		ArrayList<ContentProviderOperation> mBatch = new ArrayList<ContentProviderOperation>();

		@Override
		protected Documents doInBackground(String... params) {
			Documents items = null;
			JsonParser jp;
			try {
				jp = jsonFactory.createJsonParser(params[0]);
				items = objectMapper.readValue(jp, Documents.class);
			} catch (JsonParseException e) {
				e.printStackTrace();
				cancel(true);

			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			ContentProviderOperation.Builder builder;
			for (Document d : items.documents) {

				builder = ContentProviderOperation
						.newInsert(DocumentsContract.CONTENT_URI)
						.withValue(DocumentsContract.Columns.DOCUMENT_ID,
								d.document_id)
						.withValue(DocumentsContract.Columns.DOCUMENT_FILENAME,
								d.filename)
						.withValue(
								DocumentsContract.Columns.DOCUMENT_DESCRIPTION,
								d.description)
						.withValue(DocumentsContract.Columns.DOCUMENT_FILESIZE,
								TextTools.readableFileSize(d.filesize))
						.withValue(DocumentsContract.Columns.DOCUMENT_CHDATE,
								d.chdate)
						.withValue(DocumentsContract.Columns.DOCUMENT_MKDATE,
								d.mkdate)
						.withValue(
								DocumentsContract.Columns.DOCUMENT_DOWNLOADS,
								d.downloads)
						.withValue(DocumentsContract.Columns.DOCUMENT_ICON,
								d.icon)
						.withValue(
								DocumentsContract.Columns.DOCUMENT_PROTECTED,
								d.file_protected)
						.withValue(DocumentsContract.Columns.DOCUMENT_USER_ID,
								d.user_id)
						.withValue(
								DocumentsContract.Columns.DOCUMENT_COURSE_ID,
								params[1])
						.withValue(
								DocumentsContract.Columns.DOCUMENT_MIME_TYPE,
								d.mime_type)
						.withValue(DocumentsContract.Columns.DOCUMENT_NAME,
								d.name);

				mBatch.add(builder.build());
			}
			try {
				mContext.getContentResolver().applyBatch(
						AbstractContract.CONTENT_AUTHORITY, mBatch);
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (OperationApplicationException e) {
				e.printStackTrace();
			}
			return items;
		}

		@Override
		protected void onPostExecute(Documents result) {
			mReturnItem = result;
			loadData();
		}

	}
}
