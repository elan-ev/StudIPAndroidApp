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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author joern
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactGroup {
	public String group_id;
	public String name;
	public ArrayList<String> members;

	/**
	 * Default constructor
	 */
	public ContactGroup() {
	}

	/**
	 * Constructor
	 * 
	 * @param group_id
	 *            The id string of the group
	 * @param name
	 *            The name string of the group
	 * @param members
	 *            An arrayList<String> with user_ids
	 */
	public ContactGroup(String group_id, String name, ArrayList<String> members) {
		this.group_id = group_id;
		this.name = name;
		this.members = members;
	}

}
