/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package studip.app.backend.net.services.syncservice.activitys;

import java.io.IOException;

import studip.app.backend.datamodel.Events;
import studip.app.backend.db.EventsRepository;
import studip.app.backend.net.api.ApiEndpoints;
import studip.app.backend.net.services.syncservice.AbstractParserTask;
import studip.app.backend.net.services.syncservice.RestIPSyncService;
import studip.app.frontend.courses.CourseEventsFragment;
import android.content.Intent;
import android.net.Uri;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author joern
 * 
 */
public class EventsResponderFragment extends
	AbstractRestIPResultReceiver<Events, CourseEventsFragment> {

    /*
     * (non-Javadoc)
     * 
     * @see studip.app.backend.net.services.syncservice.activitys.
     * AbstractRestIPResultReceiver#loadData()
     */
    @Override
    protected void loadData() {
	String cid = getArguments().getString("cid");
	if (mReturnItem == null && mContext != null) {

	    Intent intent = new Intent(mContext, RestIPSyncService.class);
	    intent.setData(Uri.parse(String.format(mServerApiUrl + "/"
		    + ApiEndpoints.COURSE_EVENTS_ENDPOINT + ".json", cid)));

	    intent.putExtra(RestIPSyncService.RESTIP_RESULT_RECEIVER,
		    getResultReceiver());

	    mContext.startService(intent);
	} else if (getActivity() != null) {

	    mFragment.setListAdapter(mFragment.getNewListAdapter());

	}

    }

    /*
     * (non-Javadoc)
     * 
     * @see studip.app.backend.net.services.syncservice.activitys.
     * AbstractRestIPResultReceiver#parse(java.lang.String)
     */
    @Override
    protected void parse(String result) {
	EventsLoaderTask pTask = new EventsLoaderTask();
	pTask.execute(result);
    }

    class EventsLoaderTask extends AbstractParserTask<Events> {

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
	    EventsRepository.getInstance(getSherlockActivity())
		    .addEvents(items);
	    return items;
	}

	@Override
	protected void onPostExecute(Events result) {
	    mReturnItem = result;
	    loadData();
	}

    }
}
