/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
/**
 * 
 */
package de.elanev.studip.android.app.data.datamodel;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author joern
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactGroups {
	public ArrayList<ContactGroup> groups;
	public ContactGroup group;

	public ContactGroups() {
		this.groups = new ArrayList<ContactGroup>();
	}

	public ContactGroups(ContactGroup groups) {
		this();
		this.groups.add(groups);
	}
}
