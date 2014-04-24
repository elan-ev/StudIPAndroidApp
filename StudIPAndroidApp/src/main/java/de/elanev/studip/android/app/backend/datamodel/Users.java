/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.datamodel;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author joern
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Users {

	public ArrayList<User> users;

	public Users() {
		this.users = new ArrayList<User>();
	}

	/**
	 * @param user
	 */
	public Users(User user) {
		this.users = new ArrayList<User>();
		this.users.add(user);
	}
}
