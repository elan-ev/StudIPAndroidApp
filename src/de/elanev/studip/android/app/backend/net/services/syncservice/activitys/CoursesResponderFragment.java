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
package de.elanev.studip.android.app.backend.net.services.syncservice.activitys;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

import de.elanev.studip.android.app.backend.datamodel.Course;
import de.elanev.studip.android.app.backend.datamodel.Courses;
import de.elanev.studip.android.app.backend.datamodel.Semester;
import de.elanev.studip.android.app.backend.datamodel.User;
import de.elanev.studip.android.app.backend.db.AbstractContract;
import de.elanev.studip.android.app.backend.db.CoursesContract;
import de.elanev.studip.android.app.backend.db.SemestersContract;
import de.elanev.studip.android.app.backend.db.SemestersRepository;
import de.elanev.studip.android.app.backend.db.UsersContract;
import de.elanev.studip.android.app.backend.db.UsersRepository;
import de.elanev.studip.android.app.backend.net.api.ApiEndpoints;
import de.elanev.studip.android.app.backend.net.services.syncservice.AbstractParserTask;
import de.elanev.studip.android.app.backend.net.services.syncservice.RestApiRequest;
import de.elanev.studip.android.app.backend.net.services.syncservice.RestIPSyncService;
import de.elanev.studip.android.app.frontend.courses.CoursesFragment;

/**
 * @author joern
 * 
 */
public class CoursesResponderFragment extends
		AbstractRestIPResultReceiver<Courses, CoursesFragment> {
	private static final String TAG = CoursesResponderFragment.class
			.getSimpleName();

	public void loadData() {
		if (getActivity() != null) {
			Intent intent = new Intent(mContext, RestIPSyncService.class);
			Uri request = Uri.parse(ApiEndpoints.COURSES_ENDPOINT);
			Log.v(TAG, request.toString());
			intent.setData(request);

			intent.putExtra(RestIPSyncService.RESTIP_RESULT_RECEIVER,
					getResultReceiver());
			mContext.startService(intent);
		}
	}

	@Override
	protected void parse(String result) {
		CoursesParserTask pTask = new CoursesParserTask();
		pTask.execute(result);
	}

	class CoursesParserTask extends AbstractParserTask<Courses> {
		JsonParser mJsonParser;
		ArrayList<ContentProviderOperation> mBatch = new ArrayList<ContentProviderOperation>();

		@Override
		protected Courses doInBackground(String... params) {
			Log.v(TAG, "Parsing started");
			Courses items = new Courses();

			try {
				mJsonParser = jsonFactory.createJsonParser(params[0]);
				items = objectMapper.readValue(mJsonParser, Courses.class);
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

				SemestersRepository semesterDB = SemestersRepository
						.getInstance(mContext);
				for (Course c : items.courses) {
					if (c != null) {
						ContentProviderOperation.Builder builder;
						try {
							builder = ContentProviderOperation
									.newInsert(CoursesContract.CONTENT_URI)
									.withValue(
											CoursesContract.Columns.Courses.COURSE_ID,
											c.course_id)
									.withValue(
											CoursesContract.Columns.Courses.COURSE_TITLE,
											c.title)
									.withValue(
											CoursesContract.Columns.Courses.COURSE_DESCIPTION,
											c.description)
									.withValue(
											CoursesContract.Columns.Courses.COURSE_SUBTITLE,
											c.subtitle)
									.withValue(
											CoursesContract.Columns.Courses.COURSE_LOCATION,
											c.location)
									.withValue(
											CoursesContract.Columns.Courses.COURSE_SEMESERT_ID,
											c.semester_id)
									.withValue(
											CoursesContract.Columns.Courses.COURSE_DURATION_TIME,
											c.duration_time)
									.withValue(
											CoursesContract.Columns.Courses.COURSE_COLORS,
											c.colors)
									.withValue(
											CoursesContract.Columns.Courses.COURSE_NUMBER,
											c.number)
									.withValue(
											CoursesContract.Columns.Courses.COURSE_TYPE,
											c.type)
									.withValue(
											CoursesContract.Columns.Courses.COURSE_MODULES,
											objectMapper
													.writeValueAsString(c.modules))
									.withValue(
											CoursesContract.Columns.Courses.COURSE_START_TIME,
											c.start_time);
							mBatch.add(builder.build());
						} catch (JsonProcessingException e) {
							e.printStackTrace();
						}

					}
					// Load semester information for course
					if (!semesterDB.semesterExists(c.semester_id)) {
						String semeserResponse = semesterLoader.get(
								ApiEndpoints.SEMESTERS_ENDPOINT, c.semester_id);
						Semester semester = null;
						try {
							// unwrap element
							JSONObject jsono = (new JSONObject(semeserResponse))
									.getJSONObject("semester");
							mJsonParser = jsonFactory.createJsonParser(jsono
									.toString());
							semester = objectMapper.readValue(mJsonParser,
									Semester.class);
						} catch (JsonParseException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						} catch (JSONException e) {
							e.printStackTrace();
						}
						if (semester != null) {
							final ContentProviderOperation.Builder semesterBuilder = ContentProviderOperation
									.newInsert(SemestersContract.CONTENT_URI)
									.withValue(
											SemestersContract.Columns.SEMESTER_ID,
											semester.semester_id)
									.withValue(
											SemestersContract.Columns.SEMESTER_TITLE,
											semester.title)
									.withValue(
											SemestersContract.Columns.SEMESTER_DESCRIPTION,
											semester.description)
									.withValue(
											SemestersContract.Columns.SEMESTER_BEGIN,
											semester.begin)
									.withValue(
											SemestersContract.Columns.SEMESTER_END,
											semester.end)
									.withValue(
											SemestersContract.Columns.SEMESTER_SEMINARS_BEGIN,
											semester.seminars_begin)
									.withValue(
											SemestersContract.Columns.SEMESTER_SEMINARS_END,
											semester.seminars_end);
							mBatch.add(semesterBuilder.build());
						}
					}

					// Load teacher and tutor information for course
					for (String userId : c.teachers) {
						User usr = loadUser(userId);
						if (usr != null) {
							usr.role = CoursesContract.USER_ROLE_TEACHER;
							addUserToBatch(usr);
						}
					}
					for (String userId : c.tutors) {
						User usr = loadUser(userId);
						if (usr != null) {
							usr.role = CoursesContract.USER_ROLE_TUTOR;
							addUserToBatch(usr);
						}
					}

					// Save user to course relations
					addUserIdToBatch(c.teachers, c.course_id,
							CoursesContract.USER_ROLE_TEACHER);
					addUserIdToBatch(c.tutors, c.course_id,
							CoursesContract.USER_ROLE_TUTOR);
					addUserIdToBatch(c.students, c.course_id,
							CoursesContract.USER_ROLE_STUDENT);
				}
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

		private User loadUser(String userId) {
			RestApiRequest userLoader = new RestApiRequest();
			UsersRepository userDb = UsersRepository.getInstance(mContext);
			User usr = null;
			if (!userDb.userExists(userId)) {
				String userResponse = userLoader.get(
						ApiEndpoints.USER_ENDPOINT, userId);

				try {
					// unwrap element
					JSONObject jsono = (new JSONObject(userResponse))
							.getJSONObject("user");
					mJsonParser = jsonFactory
							.createJsonParser(jsono.toString());
					usr = objectMapper.readValue(mJsonParser, User.class);
				} catch (JsonParseException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				usr = userDb.getUser(userId);
			}
			return usr;
		}

		private void addUserIdToBatch(ArrayList<String> userList,
				String courseId, int role) {

			for (String userId : userList) {
				final ContentProviderOperation.Builder courseUserBuilder = ContentProviderOperation
						.newInsert(CoursesContract.COURSES_USERS_CONTENT_URI)
						.withValue(
								CoursesContract.Columns.CourseUsers.COURSE_USER_COURSE_ID,
								courseId)
						.withValue(
								CoursesContract.Columns.CourseUsers.COURSE_USER_USER_ID,
								userId)
						.withValue(
								CoursesContract.Columns.CourseUsers.COURSE_USER_USER_ROLE,
								role);
				mBatch.add(courseUserBuilder.build());
			}
		}

		private void addUserToBatch(User usr) {
			final ContentProviderOperation.Builder builder = ContentProviderOperation
					.newInsert(UsersContract.CONTENT_URI)
					.withValue(UsersContract.Columns.USER_ID, usr.user_id)
					.withValue(UsersContract.Columns.USER_USERNAME,
							usr.username)
					.withValue(UsersContract.Columns.USER_TITLE_PRE,
							usr.title_pre)
					.withValue(UsersContract.Columns.USER_FORENAME,
							usr.forename)
					.withValue(UsersContract.Columns.USER_LASTNAME,
							usr.lastname)
					.withValue(UsersContract.Columns.USER_TITLE_POST,
							usr.title_post)
					.withValue(UsersContract.Columns.USER_EMAIL, usr.email)
					.withValue(UsersContract.Columns.USER_HOMEPAGE,
							usr.homepage)
					.withValue(UsersContract.Columns.USER_PHONE, usr.phone)
					.withValue(UsersContract.Columns.USER_PRIVADR, usr.privadr)
					.withValue(UsersContract.Columns.USER_PERMS, usr.perms)
					.withValue(UsersContract.Columns.USER_AVATAR_SMALL,
							usr.avatar_small)
					.withValue(UsersContract.Columns.USER_AVATAR_MEDIUM,
							usr.avatar_medium)
					.withValue(UsersContract.Columns.USER_AVATAR_NORMAL,
							usr.avatar_normal);
			mBatch.add(builder.build());
		}

	}

}
