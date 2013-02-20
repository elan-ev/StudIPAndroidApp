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
package studip.app.backend.net.services.syncservice.activitys;

import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import studip.app.backend.datamodel.News;
import studip.app.backend.datamodel.NewsItem;
import studip.app.backend.datamodel.User;
import studip.app.backend.db.NewsRepository;
import studip.app.backend.db.UsersRepository;
import studip.app.backend.net.api.ApiEndpoints;
import studip.app.backend.net.services.syncservice.AbstractParserTask;
import studip.app.backend.net.services.syncservice.RestApiRequest;
import studip.app.backend.net.services.syncservice.RestIPSyncService;
import studip.app.frontend.news.NewsActivity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * @author joern
 * 
 */
public class NewsResponderFragment extends
	AbstractRestIPResultReceiver<News, NewsActivity> {

    private Context mContext = this.getActivity();
    private static final String TAG = NewsResponderFragment.class
	    .getSimpleName();
    private NewsActivity mActivity;

    public NewsResponderFragment() {
	super(NewsResponderFragment.class);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);
	mActivity = (NewsActivity) getActivity();
	loadData();
    }

    protected void loadData() {
	if (mReturnItem == null && mActivity != null) {
	    mActivity.mRefreshButton.setVisibility(View.GONE);
	    mActivity.mProgressBar.setVisibility(ProgressBar.VISIBLE);

	    Intent intent = new Intent(mActivity, RestIPSyncService.class);
	    intent.setData(Uri.parse(mServerApiUrl + "/news/range/"
		    + "studip.json"));

	    intent.putExtra(RestIPSyncService.RESTIP_RESULT_RECEIVER,
		    getResultReceiver());

	    mActivity.startService(intent);
	} else if (mActivity != null) {
	    mActivity.mProgressBar.setVisibility(ProgressBar.GONE);
	    mActivity.mRefreshButton.setVisibility(View.VISIBLE);

	    mActivity.refreshArrayList();
	}
    }

    class NewsParserTask extends AbstractParserTask<News> {

	@Override
	protected News doInBackground(String... params) {
	    Log.i(TAG, "Parsing started");
	    News items = new News();
	    JsonParser jp;
	    try {
		jp = jsonFactory.createJsonParser(params[0]);
		items = objectMapper.readValue(jp, News.class);
	    } catch (JsonParseException e) {
		e.printStackTrace();
		cancel(true);

	    } catch (JsonMappingException e) {
		e.printStackTrace();
	    } catch (IOException e) {
		e.printStackTrace();
	    }

	    if (items != null && items.news.size() != 0) {
		RestApiRequest userLoader = new RestApiRequest();
		ArrayList<User> userList = new ArrayList<User>();
		UsersRepository userDB = UsersRepository.getInstance(mContext);
		for (NewsItem newsItem : items.news) {
		    if (!userDB.userExists(newsItem.user_id)) {
			String userResponse = userLoader.get(
				ApiEndpoints.USER_ENDPOINT, newsItem.user_id);
			User user = null;
			try {
			    // unwrap element
			    JSONObject jsono = (new JSONObject(userResponse))
				    .getJSONObject("user");
			    jp = jsonFactory.createJsonParser(jsono.toString());
			    user = objectMapper.readValue(jp, User.class);
			} catch (JsonParseException e) {
			    e.printStackTrace();
			} catch (IOException e) {
			    e.printStackTrace();
			} catch (JSONException e) {
			    e.printStackTrace();
			}
			if (user != null) {
			    userList.add(user);
			}

		    }
		}
		if (!userList.isEmpty()) {
		    userDB.addUsers(userList);
		}
	    }
	    if (!items.news.isEmpty()) {
		NewsRepository.getInstance(mContext).addNews(items);
	    }
	    return items;
	}

	@Override
	protected void onPostExecute(News result) {
	    super.onPostExecute(result);

	    mReturnItem = result;
	    loadData();
	}

    }

    @Override
    protected void parse(String result) {
	NewsParserTask pTask = new NewsParserTask();
	pTask.execute(result);
    }

}
