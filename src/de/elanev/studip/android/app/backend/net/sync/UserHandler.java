/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.net.sync;

import java.util.ArrayList;

import android.content.ContentProviderOperation;
import de.elanev.studip.android.app.backend.datamodel.User;
import de.elanev.studip.android.app.backend.datamodel.Users;
import de.elanev.studip.android.app.backend.db.UsersContract;

/**
 * @author joern
 * 
 */
public class UserHandler implements ResultHandler {

	private Users mUsers = null;

	/**
	 * 
	 */
	public UserHandler(Users users) {
		this.mUsers = users;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.elanev.studip.android.app.backend.net.sync.ResultHandler#parse()
	 */
	public ArrayList<ContentProviderOperation> parse() {
		ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

		for (User user : mUsers.users) {
			operations.add(parseUser(user));
		}

		return operations;
	}

	private ContentProviderOperation parseUser(User user) {
		ContentProviderOperation.Builder builder = ContentProviderOperation
				.newInsert(UsersContract.CONTENT_URI);
		builder.withValue(UsersContract.Columns.USER_ID, user.user_id);
		builder.withValue(UsersContract.Columns.USER_USERNAME, user.username);
		builder.withValue(UsersContract.Columns.USER_PERMS, user.perms);
		builder.withValue(UsersContract.Columns.USER_TITLE_PRE, user.title_pre);
		builder.withValue(UsersContract.Columns.USER_FORENAME, user.forename);
		builder.withValue(UsersContract.Columns.USER_LASTNAME, user.lastname);
		builder.withValue(UsersContract.Columns.USER_TITLE_POST,
				user.title_post);
		builder.withValue(UsersContract.Columns.USER_EMAIL, user.email);
		builder.withValue(UsersContract.Columns.USER_AVATAR_SMALL,
				user.avatar_small);
		builder.withValue(UsersContract.Columns.USER_AVATAR_MEDIUM,
				user.avatar_medium);
		builder.withValue(UsersContract.Columns.USER_AVATAR_NORMAL,
				user.avatar_normal);
		builder.withValue(UsersContract.Columns.USER_PHONE, user.phone);
		builder.withValue(UsersContract.Columns.USER_HOMEPAGE, user.homepage);
		builder.withValue(UsersContract.Columns.USER_PRIVADR, user.privadr);

		return builder.build();
	}
}
