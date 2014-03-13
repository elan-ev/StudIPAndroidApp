/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.datamodel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author joern
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentFolder {
	public String folder_id;
	public String user_id;
	public String name;
    //TODO Back to Long, when the "Themenbezogener Dateiordner"-Bug in the
    // API is gone
	public String mkdate;
	public Long chdate;
	public String description;
	public Permissions permissions;

	public DocumentFolder() {
	}

	/**
	 * @param folder_id
	 * @param user_id
	 * @param name
	 * @param mkdate
	 * @param chdate
	 * @param description
	 * @param permissions
	 */
	public DocumentFolder(String folder_id, String user_id, String name,
			String mkdate, Long chdate, String description,
			Permissions permissions) {
		this.folder_id = folder_id;
		this.user_id = user_id;
		this.name = name;
		this.mkdate = mkdate;
		this.chdate = chdate;
		this.description = description;
		this.permissions = permissions;
	}

	public static class Permissions {
		public Boolean visible;
		public Boolean writable;
		public Boolean readable;
		public Boolean extendable;
	}
}
