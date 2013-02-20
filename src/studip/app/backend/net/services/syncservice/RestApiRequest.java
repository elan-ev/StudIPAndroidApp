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
import android.util.Log;

/**
 * @author joern
 * 
 */
public class RestApiRequest {

    public final static String TAG = RestApiRequest.class.getSimpleName();
    private URI mAction;
    private OAuthConnector mConnector;
    private DefaultHttpClient mClient;
    private HttpGet mRequest;
    private org.apache.http.HttpResponse mResponse;

    public RestApiRequest() {
	mRequest = new HttpGet(mAction);
	mClient = new DefaultHttpClient();
	mConnector = OAuthConnector.getInstance();
    }

    public String get(String endpoint, String id) {
	try {
	    String reqString = String.format(mConnector.server.API_URL + "/"
		    + endpoint + ".json", id);
	    Log.i(TAG, reqString);
	    mRequest = new HttpGet(URI.create(reqString));
	    mConnector.consumer.sign(mRequest);
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
		    return resultBody;
		}
	    } catch (IllegalStateException e) {
		e.printStackTrace();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}
	return null;

    }
}
