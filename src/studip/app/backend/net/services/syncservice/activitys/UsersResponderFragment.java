/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package studip.app.backend.net.services.syncservice.activitys;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import studip.app.backend.datamodel.User;
import studip.app.backend.db.UsersRepository;
import studip.app.backend.net.api.ApiEndpoints;
import studip.app.backend.net.services.syncservice.AbstractParserTask;
import studip.app.backend.net.services.syncservice.RestIPSyncService;
import studip.app.frontend.util.AbstractBaseListFragment;
import android.content.Intent;
import android.net.Uri;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author joern
 * 
 */
public class UsersResponderFragment extends
		AbstractRestIPResultReceiver<ArrayList<User>, AbstractBaseListFragment> {
	public ArrayList<String> mUsers = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see studip.app.backend.net.services.syncservice.activitys.
	 * AbstractRestIPResultReceiver#loadData()
	 */
	@Override
	protected void loadData() {

		if (mUsers != null) {
			loadUsers();
		}

	}

	private void loadUsers() {
		UsersRepository userDb = UsersRepository.getInstance(mContext);
		for (String uid : mUsers) {
			if (!userDb.userExists(uid)) {
				Intent intent = new Intent(mContext, RestIPSyncService.class);
				intent.setData(Uri.parse(String.format(mServerApiUrl + "/"
						+ ApiEndpoints.USER_ENDPOINT + ".json", uid)));
				intent.putExtra(RestIPSyncService.RESTIP_RESULT_RECEIVER,
						getResultReceiver());
				mContext.startService(intent);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see studip.app.backend.net.services.syncservice.activitys.
	 * AbstractRestIPResultReceiver#parse(java.lang.String)
	 */
	@Override
	protected void parse(String result) {
		UserParserTask uTask = new UserParserTask();
		uTask.execute(result);
	}

	class UserParserTask extends AbstractParserTask<User> {

		@Override
		protected User doInBackground(String... params) {
			User item = null;
			JsonParser jp;
			try {
				// Unwrap first element
				JSONObject jsono = (new JSONObject(params[0]))
						.getJSONObject("user");
				jp = jsonFactory.createJsonParser(jsono.toString());

				item = objectMapper.readValue(jp, User.class);
				if (item != null)
					UsersRepository.getInstance(getSherlockActivity()).addUser(
							item);
			} catch (JsonParseException e) {
				e.printStackTrace();
			} catch (JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return item;
		}

	}
}
