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
package studip.app.frontend.courses;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import studip.app.backend.datamodel.Course;
import studip.app.backend.datamodel.Event;
import studip.app.backend.datamodel.Events;
import studip.app.backend.db.EventsRepository;
import studip.app.backend.net.api.ApiEndpoints;
import studip.app.backend.net.services.syncservice.RestApiRequest;
import StudIPApp.app.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author joern
 * 
 */
//TODO prototype, dont touch
public class CourseDetailFragment extends Fragment {
    public static Course mCourse = null;
    ArrayAdapter<Event> mAdapter;
    ListView mListView;

    public static CourseDetailFragment newInstance(Course course) {
	CourseDetailFragment frag = new CourseDetailFragment();

	mCourse = course;
	return frag;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);
	new EventsLoaderTask().execute(mCourse.course_id);
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy",
		Locale.getDefault());
	// Setting TextLabels
	TextView courseTitleTextview = (TextView) getActivity().findViewById(
		R.id.course_title);
	TextView courseSubtitleTextView = (TextView) getView().findViewById(
		R.id.course_subtitle);
	// TextView courseDescriptionTextView = (TextView)
	// getView().findViewById(
	// R.id.course_description);
	mListView = (ListView) getView().findViewById(R.id.upcoming_events);

	// Create a progress bar to display while the list loads
	ProgressBar progressBar = new ProgressBar(getActivity());
	progressBar.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
		LayoutParams.WRAP_CONTENT));
	progressBar.setIndeterminate(true);
	mListView.setEmptyView(progressBar);
	courseTitleTextview.setText(mCourse.title);
	// if (!mCourse.subtitle.equals("")) {
	// courseSubtitleTextView.setText(mCourse.subtitle);
	// courseDescriptionTextView.setVisibility(View.VISIBLE);
	// }
	// courseDescriptionTextView.setText(mCourse.description);

	// Create an empty adapter we will use to display the loaded data.
	EventsRepository eventsDb = EventsRepository.getInstance(getActivity());
	mListView.setAdapter(new ArrayAdapter<Event>(getActivity(),
		android.R.layout.simple_list_item_1, (eventsDb
			.getEventsForCourse(mCourse.course_id)).events));

	// setListAdapter(mAdapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
	    Bundle savedInstanceState) {
	if (container == null) {
	    return null;
	}

	View detailView = inflater.inflate(R.layout.activity_course_view,
		container, false);

	return detailView;
    }

    class EventsLoaderTask extends AsyncTask<String, Integer, Events> {

	@Override
	protected Events doInBackground(String... params) {
	    RestApiRequest eventsLoader = new RestApiRequest();
	    String response = eventsLoader.get(
		    ApiEndpoints.COURSE_EVENTS_ENDPOINT, mCourse.course_id);
	    Events items = null;
	    JsonParser jp;
	    try {
		jp = jsonFactory.createJsonParser(response);
		items = objectMapper.readValue(jp, Events.class);
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
	protected void onPostExecute(Events result) {
	    EventsRepository eventsDB = EventsRepository
		    .getInstance(getActivity());
	    eventsDB.addEvents(result);
	    // Create an empty adapter we will use to display the loaded data.
	    EventsRepository eventsDb = EventsRepository
		    .getInstance(getActivity());
	    mListView.setAdapter(new ArrayAdapter<Event>(getActivity(),
		    android.R.layout.simple_list_item_1, (eventsDb
			    .getEventsForCourse(mCourse.course_id)).events));
	    if (mListView.getAdapter().isEmpty()) {
		TextView tv = new TextView(getActivity());
		tv.setText("Keine Termine");
		mListView.setEmptyView(tv);

	    }
	    super.onPostExecute(result);
	}

	protected ObjectMapper objectMapper;
	protected JsonFactory jsonFactory;

	public EventsLoaderTask() {
	    objectMapper = rootMapper();
	    jsonFactory = new JsonFactory();
	}

	protected ObjectMapper rootMapper() {
	    ObjectMapper mapper = new ObjectMapper();
	    // mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
	    // mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
	    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
		    false);
	    return mapper;
	}
    }

}
