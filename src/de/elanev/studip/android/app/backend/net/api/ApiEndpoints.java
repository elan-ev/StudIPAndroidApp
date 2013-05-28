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
package de.elanev.studip.android.app.backend.net.api;

import de.elanev.studip.android.app.R;

/**
 * @author joern
 * 
 */
public interface ApiEndpoints {
	/*
	 * Users
	 */
	public static final String USER_ENDPOINT = "user/%s";
	/*
	 * Semesters
	 */
	public static final String SEMESTERS_ENDPOINT = "semesters/%s";
	/*
	 * News
	 */
	public static final String NEWS_ENDPOINT = "news/range/%s";
	public static final String NEWS_GLOBAL_RANGE_IDENITFIER = "studip";
	public static final String NEWS_GLOBAL_ENDPOINT = "news/range/"
			+ NEWS_GLOBAL_RANGE_IDENITFIER;
	/*
	 * Courses
	 */
	public static final String COURSES_ENDPOINT = "courses";
	public static final String COURSE_EVENTS_ENDPOINT = COURSES_ENDPOINT
			+ "/%s/events";
	/*
	 * Documents
	 */
	public static final String DOCUMENTS_ENDPOINT = "documents";
	public static final String COURSE_DOCUMENTS_ENDPOINT = DOCUMENTS_ENDPOINT
			+ "/%s";
	public static final String COURSE_DOCUMENTS_FOLDERS_ENDPOINT = DOCUMENTS_ENDPOINT
			+ "/%s/folder"; // access the folders
	public static final String COURSE_DOCUMENTS_FOLDERS_FILES_ENDPOINT = COURSE_DOCUMENTS_FOLDERS_ENDPOINT
			+ "/%s"; // access the folders
	/*
	 * Messages
	 */
	public static final String MESSAGES_ENDPOINT = "messages";
	public static final String MESSAGES_FOLDERS_ENDPOINT = MESSAGES_ENDPOINT
			+ "/%s";
	public static final String MESSAGES_MESSAGE_ENDPOINT = MESSAGES_ENDPOINT
			+ "/%s";
	public static final String MESSAGES_FOLDER_MESSAGES_ENDPOINT = MESSAGES_FOLDERS_ENDPOINT
			+ "/%s";
	public static final String MESSAGE_MARK_AS_READ_ENDPOINT = MESSAGES_ENDPOINT
			+ "/%s/read";
	public static final String MESSAGE_DELETE_ENDPOINT = MESSAGES_ENDPOINT
			+ "/%s";
}
