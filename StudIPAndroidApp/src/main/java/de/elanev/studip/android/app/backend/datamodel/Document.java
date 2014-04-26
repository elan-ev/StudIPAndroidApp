/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.datamodel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author joern
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Document {
	public String document_id;
	public String user_id;
	public String name;
	public String description;
	public Long mkdate;
	public Long chdate;
	public String filename;
	public Long filesize;
	public Integer downloads;
	@JsonProperty("protected")
	public Boolean file_protected;
	public String mime_type;
	public String icon;

	public Document() {
	}

	/**
	 * @param document_id
	 * @param user_id
	 * @param name
	 * @param description
	 * @param mkdate
	 * @param chdate
	 * @param filename
	 * @param filesize
	 * @param downloads
	 * @param file_protected
	 * @param mime_type
	 * @param icon
	 */
	public Document(String document_id, String user_id, String name,
			String description, Long mkdate, Long chdate, String filename,
			Long filesize, Integer downloads, Boolean file_protected,
			String mime_type, String icon) {
		this.document_id = document_id;
		this.user_id = user_id;
		this.name = name;
		this.description = description;
		this.mkdate = mkdate;
		this.chdate = chdate;
		this.filename = filename;
		this.filesize = filesize;
		this.downloads = downloads;
		this.file_protected = file_protected;
		this.mime_type = mime_type;
		this.icon = icon;
	}
}
