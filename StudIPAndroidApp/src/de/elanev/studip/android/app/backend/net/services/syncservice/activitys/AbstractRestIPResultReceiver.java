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
package de.elanev.studip.android.app.backend.net.services.syncservice.activitys;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockListFragment;

import de.elanev.studip.android.app.backend.net.services.syncservice.RestIPSyncService;

/**
 * @author joern
 * 
 */
public abstract class AbstractRestIPResultReceiver<T, A extends SherlockListFragment>
		extends SherlockFragment {

	private ResultReceiver mReceiver;
	public T mReturnItem;
	protected Context mContext;
	protected A mFragment;
	protected static String TAG = AbstractRestIPResultReceiver.class
			.getSimpleName();
	protected Uri mResponseUri;

	public AbstractRestIPResultReceiver() {
		mReceiver = new ResultReceiver(new Handler()) {

			@Override
			protected void onReceiveResult(int resultCode, Bundle resultData) {
				if (resultData != null
						&& resultData
								.containsKey(RestIPSyncService.RESTIP_RESULT)
						&& resultData
								.containsKey(RestIPSyncService.RESTIP_ACTION)) {
					String result = resultData
							.getString(RestIPSyncService.RESTIP_RESULT);
					mResponseUri = Uri.parse(resultData
							.getString(RestIPSyncService.RESTIP_ACTION));
					if (resultCode == 200 && result != null) {
						parse(result);

					} else {
						Log.d(TAG, "Result code: " + resultCode + "\n Result: "
								+ result);
					}
				}
			}

		};
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();
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

	public void setFragment(A frag) {
		this.mFragment = frag;
	}

	abstract public void loadData();

	abstract protected void parse(String result);
}
