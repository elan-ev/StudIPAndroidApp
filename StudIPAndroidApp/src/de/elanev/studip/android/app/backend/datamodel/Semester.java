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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author joern
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Semester {

	public String semester_id;
	public String title;
	public String description;
	public String begin;
	public String end;
	public String seminars_begin;
	public String seminars_end;

	public Semester() {
	}

	/**
	 * @param semester_id
	 * @param title
	 * @param description
	 * @param begin
	 * @param end
	 * @param seminars_begin
	 * @param seminars_end
	 */
	public Semester(String semester_id, String title, String description,
			String begin, String end, String seminars_begin, String seminars_end) {
		this.semester_id = semester_id;
		this.title = title;
		this.description = description;
		this.begin = begin;
		this.end = end;
		this.seminars_begin = seminars_begin;
		this.seminars_end = seminars_end;
	}

}
