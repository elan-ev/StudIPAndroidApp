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
package de.elanev.studip.android.app.backend.datamodel;

import java.util.ArrayList;

import android.database.Cursor;
import de.elanev.studip.android.app.backend.db.CoursesContract;

/**
 * @author joern
 * 
 */

public class Course {
	public String course_id;
	public Long start_time;
	public Long duration_time;
	public Double number;
	public String title;
	public String subtitle;
	public String type;
	public Modules modules;
	public String description;
	public String location;
	public String semester_id;
	public ArrayList<String> teachers;
	public ArrayList<String> tutors;
	public ArrayList<String> students;
	public String colors;

	public Course() {
	}

	/**
	 * @param course_id
	 * @param start_time
	 * @param duration_time
	 * @param number
	 * @param title
	 * @param subtitle
	 * @param type
	 * @param modules
	 * @param description
	 * @param location
	 * @param semester_id
	 * @param teachers
	 * @param tutors
	 * @param students
	 * @param colors
	 */
	public Course(String course_id, Long start_time, Long duration_time,
			Double number, String title, String subtitle, String type,
			Modules modules, String description, String location,
			String semester_id, ArrayList<String> teachers,
			ArrayList<String> tutors, ArrayList<String> students, String colors) {
		this.course_id = course_id;
		this.start_time = start_time;
		this.duration_time = duration_time;
		this.number = number;
		this.title = title;
		this.subtitle = subtitle;
		this.type = type;
		this.modules = modules;
		this.description = description;
		this.location = location;
		this.semester_id = semester_id;
		this.teachers = teachers;
		this.tutors = tutors;
		this.students = students;
		this.colors = colors;
	}

	public Course(Cursor cursor) {
		ArrayList<String> students = null;
		ArrayList<String> tutors = null;
		ArrayList<String> teachers = null;
		Modules modules = null;

		this.course_id = cursor.getString(cursor
				.getColumnIndex(CoursesContract.Columns.Courses.COURSE_ID));
		this.start_time = cursor
				.getLong(cursor
						.getColumnIndex(CoursesContract.Columns.Courses.COURSE_START_TIME));
		this.duration_time = cursor
				.getLong(cursor
						.getColumnIndex(CoursesContract.Columns.Courses.COURSE_DURATION_TIME));
		this.number = cursor.getDouble(cursor
				.getColumnIndex(CoursesContract.Columns.Courses.COURSE_NUMBER));
		this.title = cursor.getString(cursor
				.getColumnIndex(CoursesContract.Columns.Courses.COURSE_TITLE));
		this.subtitle = cursor
				.getString(cursor
						.getColumnIndex(CoursesContract.Columns.Courses.COURSE_SUBTITLE));
		this.type = cursor.getString(cursor
				.getColumnIndex(CoursesContract.Columns.Courses.COURSE_TYPE));
		this.modules = modules;
		this.description = cursor
				.getString(cursor
						.getColumnIndex(CoursesContract.Columns.Courses.COURSE_DESCIPTION));
		this.location = cursor
				.getString(cursor
						.getColumnIndex(CoursesContract.Columns.Courses.COURSE_LOCATION));
		this.semester_id = cursor
				.getString(cursor
						.getColumnIndex(CoursesContract.Columns.Courses.COURSE_SEMESERT_ID));
		this.teachers = teachers;
		this.tutors = tutors;
		this.students = students;
		this.colors = cursor.getString(cursor
				.getColumnIndex(CoursesContract.Columns.Courses.COURSE_COLORS));

	}

	public static class Modules {
		public Boolean calendar = false;
		public Boolean chat = false;
		public Boolean documents = false;
		public Boolean documents_folder_permissions = false;
		public Boolean elearning_interface = false;
		public Boolean forum = false;
		public Boolean literature = false;
		public Boolean participants = false;
		public Boolean personal = false;
		public Boolean schedule = false;
		public Boolean scm = false;
		public Boolean wiki = false;
	}

}
