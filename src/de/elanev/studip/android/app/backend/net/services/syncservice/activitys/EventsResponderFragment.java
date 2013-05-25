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

import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;

import de.elanev.studip.android.app.backend.datamodel.Event;
import de.elanev.studip.android.app.backend.datamodel.Events;
import de.elanev.studip.android.app.backend.db.AbstractContract;
import de.elanev.studip.android.app.backend.db.EventsContract;
import de.elanev.studip.android.app.backend.net.api.ApiEndpoints;
import de.elanev.studip.android.app.backend.net.services.syncservice.AbstractParserTask;
import de.elanev.studip.android.app.backend.net.services.syncservice.RestIPSyncService;
import de.elanev.studip.android.app.frontend.courses.CourseEventsFragment;

/**
 * @author joern
 * 
 */
public class EventsResponderFragment extends
		AbstractRestIPResultReceiver<Events, CourseEventsFragment> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.elanev.studip.android.app.backend.net.services.syncservice.activitys.
	 * AbstractRestIPResultReceiver#loadData()
	 */
	@Override
	public void loadData() {
		if (getActivity() != null) {
			String cid = getArguments().getString("cid");
			if (mReturnItem == null && mContext != null) {

				Intent intent = new Intent(mContext, RestIPSyncService.class);
				intent.setData(Uri.parse(String.format(mServerApiUrl + "/"
						+ ApiEndpoints.COURSE_EVENTS_ENDPOINT, cid)));

				intent.putExtra(RestIPSyncService.RESTIP_RESULT_RECEIVER,
						getResultReceiver());

				mContext.startService(intent);
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
		EventsLoaderTask pTask = new EventsLoaderTask();
		pTask.execute(result);
	}

	class EventsLoaderTask extends AbstractParserTask<Events> {
		ArrayList<ContentProviderOperation> mBatch = new ArrayList<ContentProviderOperation>();

		@Override
		protected Events doInBackground(String... params) {
			Events items = null;
			JsonParser jp;
			try {
				jp = jsonFactory.createJsonParser(params[0]);
				items = objectMapper.readValue(jp, Events.class);
			} catch (JsonParseException e) {
				e.printStackTrace();
				cancel(true);

			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			for (Event e : items.events) {
				ContentProviderOperation.Builder builder;
				builder = ContentProviderOperation
						.newInsert(EventsContract.CONTENT_URI)
						.withValue(EventsContract.Columns.EVENT_ID, e.event_id)
						.withValue(EventsContract.Columns.EVENT_TITLE, e.title)
						.withValue(EventsContract.Columns.EVENT_DESCRIPTION,
								e.description)
						.withValue(EventsContract.Columns.EVENT_ROOM, e.room)
						.withValue(EventsContract.Columns.EVENT_START,
								e.start * 1000L)
						.withValue(EventsContract.Columns.EVENT_END,
								e.end * 1000L)
						.withValue(EventsContract.Columns.EVENT_CATEGORIES,
								e.categories)
						.withValue(EventsContract.Columns.EVENT_COURSE_ID,
								e.course_id);

				mBatch.add(builder.build());
			}

			if (!mBatch.isEmpty()) {
				try {
					mContext.getContentResolver().applyBatch(
							AbstractContract.CONTENT_AUTHORITY, mBatch);
				} catch (RemoteException e) {
					throw new RuntimeException(
							"Problem applying batch operation", e);
				} catch (OperationApplicationException e) {
					throw new RuntimeException(
							"Problem applying batch operation", e);
				}
			}

			return items;
		}

		@Override
		protected void onPostExecute(Events result) {
			mReturnItem = result;
			loadData();
		}

	}
}
