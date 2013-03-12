/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.net.services.syncservice.activitys;

import java.io.IOException;

import android.content.Intent;
import android.net.Uri;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;

import de.elanev.studip.android.app.backend.datamodel.Documents;
import de.elanev.studip.android.app.backend.datamodel.Folder;
import de.elanev.studip.android.app.backend.datamodel.Folders;
import de.elanev.studip.android.app.backend.db.DocumentsRepository;
import de.elanev.studip.android.app.backend.net.api.ApiEndpoints;
import de.elanev.studip.android.app.backend.net.services.syncservice.AbstractParserTask;
import de.elanev.studip.android.app.backend.net.services.syncservice.RestIPSyncService;
import de.elanev.studip.android.app.frontend.courses.CourseDocumentsFragment;

/**
 * @author joern
 * 
 */
public class DocumentsResponderFragment extends
		AbstractRestIPResultReceiver<Documents, CourseDocumentsFragment> {
	private Folders mFolders = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.elanev.studip.android.app.backend.net.services.syncservice.activitys.
	 * AbstractRestIPResultReceiver#loadData()
	 */
	@Override
	protected void loadData() {
		String cid = null;
		if (mContext != null) {
			cid = getArguments().getString("cid");

			if (mFolders == null) {

				Intent intent = new Intent(mContext, RestIPSyncService.class);
				intent.setData(Uri.parse(String.format(mServerApiUrl + "/"
						+ ApiEndpoints.COURSE_DOCUMENTS_FOLDERS_ENDPOINT, cid)));

				intent.putExtra(RestIPSyncService.RESTIP_RESULT_RECEIVER,
						getResultReceiver());
				mContext.startService(intent);

			} else if (mFolders != null && mReturnItem == null) {

				for (Folder f : mFolders.folders) {

					Intent intent = new Intent(mContext,
							RestIPSyncService.class);
					intent.setData(Uri.parse(String
							.format(mServerApiUrl
									+ "/"
									+ ApiEndpoints.COURSE_DOCUMENTS_FOLDERS_FILES_ENDPOINT,
									new Object[] { cid, f.folder_id })));

					intent.putExtra(RestIPSyncService.RESTIP_RESULT_RECEIVER,
							getResultReceiver());
					mContext.startService(intent);

				}

			} else if (getActivity() != null) {

				mFragment.setListAdapter(mFragment.getNewListAdapter());

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
			pTask.execute(new String[] { result,
					getArguments().getString("cid") });
		}
	}

	class FolderParserTask extends AbstractParserTask<Folders> {

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Folders doInBackground(String... params) {
			JsonParser jp;
			Folders folders = null;
			try {
				jp = jsonFactory.createJsonParser(params[0]);
				folders = objectMapper.readValue(jp, Folders.class);
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
		protected void onPostExecute(Folders result) {
			super.onPostExecute(result);
			mFolders = result;
			loadData();
		}

	}

	class DocumentsParserTask extends AbstractParserTask<Documents> {

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
			DocumentsRepository.getInstance(getSherlockActivity())
					.addDocuments(items, params[1]);
			return items;
		}

		@Override
		protected void onPostExecute(Documents result) {
			mReturnItem = result;
			loadData();
		}

	}
}
