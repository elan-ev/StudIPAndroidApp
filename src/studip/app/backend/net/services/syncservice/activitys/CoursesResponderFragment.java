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

import org.json.JSONException;
import org.json.JSONObject;

import studip.app.backend.datamodel.Course;
import studip.app.backend.datamodel.Courses;
import studip.app.backend.datamodel.Semester;
import studip.app.backend.datamodel.Semesters;
import studip.app.backend.db.CoursesRepository;
import studip.app.backend.db.SemestersRepository;
import studip.app.backend.net.api.ApiEndpoints;
import studip.app.backend.net.services.syncservice.AbstractParserTask;
import studip.app.backend.net.services.syncservice.RestApiRequest;
import studip.app.backend.net.services.syncservice.RestIPSyncService;
import studip.app.frontend.courses.CoursesFragment;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author joern
 * 
 */
public class CoursesResponderFragment extends
		AbstractRestIPResultReceiver<Courses, CoursesFragment> {
	private static final String TAG = CoursesResponderFragment.class
			.getSimpleName();

	protected void loadData() {
		if (mReturnItem == null && mContext != null) {

			Intent intent = new Intent(mContext, RestIPSyncService.class);
			intent.setData(Uri.parse(mServerApiUrl + "/" + "courses.json"));

			intent.putExtra(RestIPSyncService.RESTIP_RESULT_RECEIVER,
					getResultReceiver());

			mContext.startService(intent);
		} else if (getActivity() != null) {

			mFragment.setListAdapter(mFragment.getNewListAdapter());

		}
	}

	@Override
	protected void parse(String result) {
		CoursesParserTask pTask = new CoursesParserTask();
		pTask.execute(result);
	}

	class CoursesParserTask extends AbstractParserTask<Courses> {

		@Override
		protected Courses doInBackground(String... params) {
			Log.i(TAG, "Parsing started");
			Courses items = new Courses();
			JsonParser jp;
			try {
				jp = jsonFactory.createJsonParser(params[0]);
				items = objectMapper.readValue(jp, Courses.class);
			} catch (JsonParseException e) {
				e.printStackTrace();
				cancel(true);

			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (items != null && !items.courses.isEmpty()) {
				RestApiRequest semesterLoader = new RestApiRequest();
				Semesters semesterList = new Semesters();
				SemestersRepository semesterDB = SemestersRepository
						.getInstance(mContext);
				for (Course c : items.courses) {
					if (!semesterDB.semesterExists(c.semester_id)) {
						String semeserResponse = semesterLoader.get(
								ApiEndpoints.SEMESTERS_ENDPOINT, c.semester_id);
						Semester semester = null;
						try {
							// unwrap element
							JSONObject jsono = (new JSONObject(semeserResponse))
									.getJSONObject("semester");
							jp = jsonFactory.createJsonParser(jsono.toString());
							semester = objectMapper.readValue(jp,
									Semester.class);
						} catch (JsonParseException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (JSONException e) {
							e.printStackTrace();
						}
						if (semester != null) {
							semesterList.semesters.add(semester);

						}
					}
				}
				if (!semesterList.semesters.isEmpty()) {
					SemestersRepository.getInstance(mContext).addSemesters(
							semesterList);
				}
			}
			if (!items.courses.isEmpty()) {
				CoursesRepository.getInstance(mContext).addCourses(items);
			}

			return items;
		}

		@Override
		protected void onPostExecute(Courses result) {
			super.onPostExecute(result);

			mReturnItem = result;
			loadData();
		}

	}

}
