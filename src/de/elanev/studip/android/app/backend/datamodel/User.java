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
import com.fasterxml.jackson.annotation.JsonRootName;

/**
 * @author joern
 * 
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonRootName(value = "user")
public class User {
	public String user_id;
	public String username;
	public String perms;
	public String title_pre;
	public String forename;
	public String lastname;
	public String title_post;
	public String email;
	public String avatar_small;
	public String avatar_medium;
	public String avatar_normal;
	public String phone;
	public String homepage;
	public String privadr;
	public int role;

	/**
	 * Default constructor
	 */
	public User() {
	}

	/**
	 * @param user_id
	 * @param username
	 * @param perms
	 * @param title_pre
	 * @param forename
	 * @param lastname
	 * @param title_post
	 * @param email
	 * @param avatar_small
	 * @param avatar_medium
	 * @param avatar_normal
	 * @param phone
	 * @param homepage
	 * @param privadr
	 * @param role
	 */
	public User(String user_id, String username, String perms,
			String title_pre, String forename, String lastname,
			String title_post, String email, String avatar_small,
			String avatar_medium, String avatar_normal, String phone,
			String homepage, String privadr, int role) {
		this.user_id = user_id;
		this.username = username;
		this.perms = perms;
		this.title_pre = title_pre;
		this.forename = forename;
		this.lastname = lastname;
		this.title_post = title_post;
		this.email = email;
		this.avatar_small = avatar_small;
		this.avatar_medium = avatar_medium;
		this.avatar_normal = avatar_normal;
		this.phone = phone;
		this.homepage = homepage;
		this.privadr = privadr;
		this.role = role;
	}

	public String getFullName() {
		return this.title_pre + " " + this.forename + " " + this.lastname + " "
				+ this.title_post;
	}

	public String getName() {
		return this.forename + " " + this.lastname;
	}

}
