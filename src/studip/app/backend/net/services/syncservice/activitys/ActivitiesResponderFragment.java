/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
/**
 * 
 */
package studip.app.backend.net.services.syncservice.activitys;

import java.io.IOException;

import studip.app.backend.datamodel.Activities;
import studip.app.backend.net.services.syncservice.AbstractParserTask;
import studip.app.backend.net.services.syncservice.RestIPSyncService;
import studip.app.frontend.activities.ActivitiesActivity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author joern
 * 
 */
public class ActivitiesResponderFragment extends
		AbstractRestIPResultReceiver<Activities, ActivitiesActivity> {

	public ActivitiesResponderFragment() {
		super(ActivitiesResponderFragment.class);
	}

	@Override
	protected void loadData() {
		if (mReturnItem == null && mActivity != null) {
			mActivity.mRefreshButton.setVisibility(View.GONE);
			mActivity.mProgressBar.setVisibility(ProgressBar.VISIBLE);
			Intent intent = new Intent(mActivity, RestIPSyncService.class);
			intent.setData(Uri.parse(mServerApiUrl + "/" + "activities.json"));

			intent.putExtra(RestIPSyncService.RESTIP_RESULT_RECEIVER,
					getResultReceiver());

			mActivity.startService(intent);
		} else if (mActivity != null) {

			mActivity.mProgressBar.setVisibility(ProgressBar.GONE);
			mActivity.mRefreshButton.setVisibility(View.VISIBLE);

//			ArrayAdapter<String> adapter = mActivity.getArrayAdapter();
//			adapter.clear();
//
//			if (mReturnItem.activities.size() == 0) {
//				adapter.add("No Activities :(");
//			} else {
//				for (Activity activity : mReturnItem.activities) {
//					adapter.add(activity.title);
//				}
//			}
			mActivity.refreshArrayList();
		}

	}

	class ActivitiesParserTask extends AbstractParserTask<Activities> {

		@Override
		protected Activities doInBackground(String... params) {
			Log.i(TAG, "Parsing started");
			Activities items = new Activities();
			try {
				JsonParser jp = jsonFactory.createJsonParser(params[0]);
				items = objectMapper.readValue(jp, Activities.class);
			} catch (JsonParseException e) {
				e.printStackTrace();
				cancel(true);

			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return items;
		}

		@Override
		protected void onPostExecute(Activities result) {
			super.onPostExecute(result);

			mReturnItem = result;
			loadData();
		}

	}

	@Override
	protected void parse(String result) {
//		ActivitiesParserTask pTask = new ActivitiesParserTask();
//		pTask.execute(result);
		// TODO Plugin Fixen
	}


}
