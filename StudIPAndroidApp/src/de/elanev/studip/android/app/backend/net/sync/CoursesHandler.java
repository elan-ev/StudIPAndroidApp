/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.net.sync;

import java.util.ArrayList;

import android.content.ContentProviderOperation;
import de.elanev.studip.android.app.backend.datamodel.Course;
import de.elanev.studip.android.app.backend.datamodel.Courses;
import de.elanev.studip.android.app.backend.db.CoursesContract;

/**
 * @author joern
 * 
 */
public class CoursesHandler implements ResultHandler {

	Courses mCourses = null;

	/**
	 * 
	 */
	public CoursesHandler(Courses c) {
		mCourses = c;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.elanev.studip.android.app.backend.net.sync.ResultHandler#parse()
	 */
	public ArrayList<ContentProviderOperation> parse() {
		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

		for (Course c : mCourses.courses) {
			operations.add(parseCourse(c));

			// Load teacher and tutor information for course
			for (String userId : c.teachers) {
				operations.add(parseCourseUser(userId, c.course_id,
						CoursesContract.USER_ROLE_TEACHER));
			}

			for (String userId : c.tutors) {
				operations.add(parseCourseUser(userId, c.course_id,
						CoursesContract.USER_ROLE_TUTOR));
			}

		}

		return operations;
	}

	/**
	 * @param userId
	 * @param courseId
	 * @param userRoleTeacher
	 * @return
	 */
	private ContentProviderOperation parseCourseUser(String userId,
			String courseId, int role) {
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
		return courseUserBuilder.build();
	}

	/**
	 * @param c
	 * @return
	 */
	private ContentProviderOperation parseCourse(Course c) {
		ContentProviderOperation.Builder builder = ContentProviderOperation
				.newInsert(CoursesContract.CONTENT_URI)
				.withValue(CoursesContract.Columns.Courses.COURSE_ID,
						c.course_id)
				.withValue(CoursesContract.Columns.Courses.COURSE_TITLE,
						c.title)
				.withValue(CoursesContract.Columns.Courses.COURSE_DESCIPTION,
						c.description)
				.withValue(CoursesContract.Columns.Courses.COURSE_SUBTITLE,
						c.subtitle)
				.withValue(CoursesContract.Columns.Courses.COURSE_LOCATION,
						c.location)
				.withValue(CoursesContract.Columns.Courses.COURSE_SEMESERT_ID,
						c.semester_id)
				.withValue(
						CoursesContract.Columns.Courses.COURSE_DURATION_TIME,
						c.duration_time)
				.withValue(CoursesContract.Columns.Courses.COURSE_COLORS,
						c.colors)
				// .withValue(CoursesContract.Columns.Courses.COURSE_NUMBER,
				// c.number)
				.withValue(CoursesContract.Columns.Courses.COURSE_TYPE, c.type)
				// .withValue(CoursesContract.Columns.Courses.COURSE_MODULES,
				// JSONWriter.writeValueAsString(c.modules))
				.withValue(CoursesContract.Columns.Courses.COURSE_START_TIME,
						c.start_time);
		return builder.build();
	}

}
