/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.net.services.syncservice.activitys;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.actionbarsherlock.app.SherlockListFragment;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;

import de.elanev.studip.android.app.backend.datamodel.User;
import de.elanev.studip.android.app.backend.db.UsersContract;
import de.elanev.studip.android.app.backend.net.api.ApiEndpoints;
import de.elanev.studip.android.app.backend.net.services.syncservice.AbstractParserTask;
import de.elanev.studip.android.app.backend.net.services.syncservice.RestIPSyncService;

/**
 * @author joern
 * 
 */
public class UsersResponderFragment extends
		AbstractRestIPResultReceiver<ArrayList<User>, SherlockListFragment> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.elanev.studip.android.app.backend.net.services.syncservice.activitys.
	 * AbstractRestIPResultReceiver#loadData()
	 */
	@Override
	public void loadData() {
		if (getActivity() != null) {
			Bundle args = getArguments();
			if (args != null) {
				ArrayList<String> userList = args
						.getStringArrayList(UsersContract.Columns.USER_ID);
				Cursor c = null;

				for (String user : userList) {
					try {
						c = mContext.getContentResolver().query(
								UsersContract.CONTENT_URI,
								new String[] { UsersContract.Columns.USER_ID },
								UsersContract.Columns.USER_ID + " = ?",
								new String[] { user },
								UsersContract.Columns.USER_ID + " ASC");
						if (c.getCount() < 1) {
							Intent intent = new Intent(mContext,
									RestIPSyncService.class);
							intent.setData(Uri.parse(String.format(
									ApiEndpoints.USER_ENDPOINT, user)));
							intent.putExtra(
									RestIPSyncService.RESTIP_RESULT_RECEIVER,
									getResultReceiver());
							mContext.startService(intent);

						}
					} finally {
						c.close();
					}
				}
				args = null;
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * de.elanev.studip.android.app.backend.net.services.syncservice.activitys.
	 * AbstractRestIPResultReceiver#parse(java.lang.String)
	 */
	@Override
	protected void parse(String result) {
		if (getActivity() != null) {
			UserParserTask uTask = new UserParserTask();
			uTask.execute(result);
		}
	}

	class UserParserTask extends AbstractParserTask<User> {

		@Override
		protected User doInBackground(String... params) {
			User user = null;
			JsonParser jp;
			ContentValues values = new ContentValues();
			try {
				// Unwrap first element
				JSONObject jsono = (new JSONObject(params[0]))
						.getJSONObject("user");
				jp = jsonFactory.createJsonParser(jsono.toString());

				user = objectMapper.readValue(jp, User.class);
				if (user != null)
					values.put(UsersContract.Columns.USER_ID, user.user_id);
				values.put(UsersContract.Columns.USER_USERNAME, user.username);
				values.put(UsersContract.Columns.USER_PERMS, user.perms);
				values.put(UsersContract.Columns.USER_TITLE_PRE, user.title_pre);
				values.put(UsersContract.Columns.USER_FORENAME, user.forename);
				values.put(UsersContract.Columns.USER_LASTNAME, user.lastname);
				values.put(UsersContract.Columns.USER_TITLE_POST,
						user.title_post);
				values.put(UsersContract.Columns.USER_EMAIL, user.email);
				values.put(UsersContract.Columns.USER_AVATAR_SMALL,
						user.avatar_small);
				values.put(UsersContract.Columns.USER_AVATAR_MEDIUM,
						user.avatar_medium);
				values.put(UsersContract.Columns.USER_AVATAR_NORMAL,
						user.avatar_normal);
				values.put(UsersContract.Columns.USER_PHONE, user.phone);
				values.put(UsersContract.Columns.USER_HOMEPAGE, user.homepage);
				values.put(UsersContract.Columns.USER_PRIVADR, user.privadr);
				mContext.getContentResolver().insert(UsersContract.CONTENT_URI,
						values);
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return user;
		}

	}
}
