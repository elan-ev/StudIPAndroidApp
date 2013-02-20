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

import studip.app.backend.net.oauth.OAuthConnector;
import studip.app.backend.net.services.syncservice.RestIPSyncService;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * @author joern
 * 
 */
public abstract class AbstractRestIPResultReceiver<T, A> extends Fragment {

	private ResultReceiver mReceiver;
	public T mReturnItem;
	protected A mActivity;
	public static Class<?> fragmentReceiverClass;
	protected static String TAG = AbstractRestIPResultReceiver.class
			.getSimpleName();

	protected Uri mServerApiUrl = Uri
			.parse(OAuthConnector.getInstance().server.API_URL);

	public AbstractRestIPResultReceiver(Class<?> fragmentClass) {
		fragmentReceiverClass = fragmentClass;
		mReceiver = new ResultReceiver(new Handler()) {

			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				if (resultData != null
						&& resultData
								.containsKey(RestIPSyncService.RESTIP_RESULT)) {
					onRestIPResult(resultCode,
							resultData
									.getString(RestIPSyncService.RESTIP_RESULT));
				} else {
					onRestIPResult(resultCode, null);
				}
			}

		};
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mActivity = (A) getActivity();
		loadData();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);

		setRetainInstance(true);
	}

	public ResultReceiver getResultReceiver() {
		return mReceiver;
	}

	public void onRestIPResult(int resultCode, String result) {
		if (resultCode == 200 && result != null) {
			parse(result);
		} else {
			Log.d(TAG, "Result code: " + resultCode);
		}

	}

	abstract protected void loadData();

	abstract protected void parse(String result);
}
