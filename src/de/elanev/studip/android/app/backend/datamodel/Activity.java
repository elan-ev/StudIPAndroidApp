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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author joern
 * 
 */
public class Activity {

	public String id;
	public String title;
	public String author;
	public String author_id;
	public String link;
	public String summary;
	public String content;
	public String category;
	public Date updated;

	/**
	 * Default constructor
	 */
	public Activity() {
	}

	/**
	 * @param id
	 * @param title
	 * @param author
	 * @param author_id
	 * @param link
	 * @param summary
	 * @param content
	 * @param category
	 * @param updated
	 */
	public Activity(String id, String title, String author, String author_id,
			String link, String summary, String content, String category,
			Date updated) {
		this.id = id;
		this.title = title;
		this.author = author;
		this.author_id = author_id;
		this.link = link;
		this.summary = summary;
		this.content = content;
		this.category = category;
		this.updated = updated;
	}

	public String getDate() {
		return new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
				.format(updated);
	}

	public String getTime() {
		return new SimpleDateFormat("HH:mm", Locale.getDefault())
				.format(updated);
	}

}
