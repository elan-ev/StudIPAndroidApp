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

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.actionbarsherlock.app.SherlockListFragment;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;

import de.elanev.studip.android.app.backend.datamodel.Message;
import de.elanev.studip.android.app.backend.datamodel.MessageFolders;
import de.elanev.studip.android.app.backend.datamodel.Messages;
import de.elanev.studip.android.app.backend.db.MessagesContract;
import de.elanev.studip.android.app.backend.db.UsersContract;
import de.elanev.studip.android.app.backend.net.api.ApiEndpoints;
import de.elanev.studip.android.app.backend.net.services.syncservice.AbstractParserTask;
import de.elanev.studip.android.app.backend.net.services.syncservice.RestIPSyncService;

/**
 * @author joern
 * 
 */
public class MessagesResponderFragment extends
		AbstractRestIPResultReceiver<Messages, SherlockListFragment> {
	private static final String SYNC_LOCK = "sync_block";
	private static boolean mSyncStarted = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.elanev.studip.android.app.backend.net.services.syncservice.activitys.
	 * AbstractRestIPResultReceiver#loadData()
	 */
	@Override
	public void loadData() {
		if (getActivity() != null && mSyncStarted == false) {
			synchronized (SYNC_LOCK) {
				mSyncStarted = true;
			}

			createAndStartIntent(Uri.parse(mServerApiUrl
					+ "/"
					+ String.format(ApiEndpoints.MESSAGES_FOLDERS_ENDPOINT,
							"in")));

			createAndStartIntent(Uri.parse(mServerApiUrl
					+ "/"
					+ String.format(ApiEndpoints.MESSAGES_FOLDERS_ENDPOINT,
							"out")));
		}
	}

	private void createAndStartIntent(Uri action) {
		Intent intent = new Intent(mContext, RestIPSyncService.class);
		intent.setData(action);

		intent.putExtra(RestIPSyncService.RESTIP_RESULT_RECEIVER,
				getResultReceiver());
		mContext.startService(intent);
	}

	private void loadFolderMessages(String box, MessageFolders folders) {
		if (getActivity() != null) {
			for (String f : folders.folders) {
				createAndStartIntent(Uri.parse(String.format(mServerApiUrl
						+ "/"
						+ String.format(
								ApiEndpoints.MESSAGES_FOLDER_MESSAGES_ENDPOINT,
								box, f))));
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
		if (mResponseUri != null) {
			String lastPathSegment = mResponseUri.getLastPathSegment();

			if (lastPathSegment.equals("in") || lastPathSegment.equals("out")) {
				FolderParserTask fTask = new FolderParserTask(lastPathSegment);
				fTask.execute(result, lastPathSegment);
			} else {
				MessageParserTask pTask = new MessageParserTask();
				pTask.execute(result, mResponseUri.toString());
			}
		}
	}

	private class FolderParserTask extends AbstractParserTask<MessageFolders> {
		private final String mBox;

		/**
		 * 
		 */
		public FolderParserTask(String box) {
			this.mBox = box;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected MessageFolders doInBackground(String... params) {
			JsonParser jp;
			MessageFolders folders = null;

			try {
				jp = jsonFactory.createJsonParser(params[0]);
				folders = objectMapper.readValue(jp, MessageFolders.class);
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
		protected void onPostExecute(MessageFolders result) {
			loadFolderMessages(mBox, result);
		}
	}

	private class MessageParserTask extends
			AbstractParserTask<ArrayList<String>> {

		@Override
		protected ArrayList<String> doInBackground(String... params) {
			JsonParser jp;
			Messages items = null;
			ArrayList<String> userList = new ArrayList<String>();
			Uri responseUri = Uri.parse(params[1]);

			try {
				jp = jsonFactory.createJsonParser(params[0]);
				items = objectMapper.readValue(jp, Messages.class);
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			List<String> uriPathSegments = responseUri.getPathSegments();
			String box = uriPathSegments.get(uriPathSegments.size() - 2);

			// insert folder
			ContentValues values = new ContentValues();
			values.put(
					MessagesContract.Columns.MessageFolders.MESSAGE_FOLDER_NAME,
					responseUri.getLastPathSegment());
			values.put(
					MessagesContract.Columns.MessageFolders.MESSAGE_FOLDER_BOX,
					box);
			Uri insertResult = mContext.getContentResolver().insert(
					MessagesContract.CONTENT_URI_MESSAGE_FOLDERS, values);

			// insert messages
			if (insertResult != null) {
				long lastInsertedId = ContentUris.parseId(insertResult);
				if (lastInsertedId > -1) {
					// ContentProviderOperation.Builder builder;

					ContentValues[] messageValues = new ContentValues[items.messages
							.size()];

					for (int i = 0; i < items.messages.size(); i++) {
						Message m = items.messages.get(i);

						// adding message content values
						ContentValues messageValueSet = new ContentValues();
						messageValueSet.put(
								MessagesContract.Columns.Messages.MESSAGE_ID,
								m.message_id);
						messageValueSet
								.put(MessagesContract.Columns.Messages.MESSAGE_MKDATE,
										m.mkdate);
						messageValueSet.put(
								MessagesContract.Columns.Messages.MESSAGE,
								m.message);
						messageValueSet
								.put(MessagesContract.Columns.Messages.MESSAGE_PRIORITY,
										m.priority);
						messageValueSet
								.put(MessagesContract.Columns.Messages.MESSAGE_RECEIVER_ID,
										m.receiver_id);
						messageValueSet
								.put(MessagesContract.Columns.Messages.MESSAGE_SENDER_ID,
										m.sender_id);
						messageValueSet
								.put(MessagesContract.Columns.Messages.MESSAGE_SUBJECT,
										m.subject);
						messageValueSet
								.put(MessagesContract.Columns.Messages.MESSAGE_UNREAD,
										m.unread);
						// messageValueSet
						// .put(MessagesContract.Columns.Messages.MESSAGE_FOLDER_ID,
						// lastInsertedId);
						messageValues[i] = messageValueSet;

						// adding user ids for later retrival
						userList.add(m.receiver_id);
						userList.add(m.sender_id);

					}
					// insert the values
					mContext.getContentResolver().bulkInsert(
							MessagesContract.CONTENT_URI_MESSAGES.buildUpon()
									.appendPath("folders")
									.appendPath(String.valueOf(lastInsertedId))
									.build(), messageValues);
				}
			}

			return userList;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(ArrayList<String> userList) {
			synchronized (SYNC_LOCK) {
				mSyncStarted = false;
			}

			if (userList != null) {
				FragmentManager fm = getFragmentManager();

				UsersResponderFragment responderFragment = (UsersResponderFragment) fm
						.findFragmentByTag("userResponder");
				if (responderFragment == null) {
					responderFragment = (UsersResponderFragment) UsersResponderFragment
							.instantiate(mContext,
									UsersResponderFragment.class.getName());

					Bundle args = new Bundle();
					args.putStringArrayList(UsersContract.Columns.USER_ID,
							userList);
					responderFragment.setArguments(args);
					FragmentTransaction ft = fm.beginTransaction();
					ft.add(responderFragment, "userResponder").commit();
				}
			}

		}

	}
}
