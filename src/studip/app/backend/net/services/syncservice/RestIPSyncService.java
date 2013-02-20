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
package studip.app.backend.net.services.syncservice;

import java.io.IOException;
import java.net.URI;

import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import studip.app.backend.net.oauth.OAuthConnector;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

/**
 * @author joern
 * 
 */
public class RestIPSyncService extends IntentService {
	public final static String TAG = RestIPSyncService.class.getSimpleName();
	public static final String RESTIP_CALL_PARAMS = "studip.app.backend.net.services.syncservice.RESTIP_CALL_PARAMS";
	public static final String RESTIP_RESULT_RECEIVER = "studip.app.backend.net.services.syncservice.RESTIP_RESULT_RECEIVER";
	public static final String RESTIP_RESULT = "studip.app.backend.net.services.syncservice.RESTIP_RESULT";

	private URI mAction;
	private ResultReceiver mReceiver;
	private DefaultHttpClient mClient;
	private HttpGet mRequest;
	private org.apache.http.HttpResponse mResponse;

	public RestIPSyncService() {
		super(TAG);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent(Intent intent) {
		mAction = URI.create(intent.getDataString());

		Bundle extras = intent.getExtras();
		if (mAction == null || extras == null
				|| !extras.containsKey(RESTIP_RESULT_RECEIVER)) {
			Log.e(TAG, "Result receiver fehlt.");
			return;
		}
		mRequest = new HttpGet(mAction);
		mClient = new DefaultHttpClient();

		// @SuppressWarnings("unchecked")
		// ArrayList<NameValuePair> arrayList = (ArrayList<NameValuePair>)
		// extras
		// .get(RESTIP_CALL_PARAMS);
		// ArrayList<NameValuePair> params = arrayList;
		//
		// HttpParams httpParams;
		// for (NameValuePair pair : params) {
		// httpParams.setParameter(pair.getName(), pair.getValue());
		// }
		// mRequest.setParams(httpParams);

		mReceiver = extras.getParcelable(RESTIP_RESULT_RECEIVER);

		try {
			OAuthConnector.getInstance().consumer.sign(mRequest);
			mResponse = mClient.execute(mRequest);
		} catch (OAuthMessageSignerException e) {
			e.printStackTrace();
		} catch (OAuthExpectationFailedException e) {
			e.printStackTrace();
		} catch (OAuthCommunicationException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		int resultCode = mResponse.getStatusLine().getStatusCode();
		String resultBody = null;
		Log.d(TAG, String.valueOf(resultCode));
		if (resultCode == 200) {

			try {
				HttpEntity entity = mResponse.getEntity();
				if (entity != null) {
					resultBody = EntityUtils.toString(entity);
				}
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Bundle resultData = new Bundle();
			resultData.putString(RESTIP_RESULT, resultBody);
			mReceiver.send(resultCode, resultData);

		}

	}

}
