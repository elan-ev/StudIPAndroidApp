package de.elanev.studip.android.app;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;

import de.elanev.studip.android.app.backend.net.oauth.OAuthConnector;

/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

/**
 * Application class
 *
 * @author joern
 *         <p/>
 *         Extends the Application class to enable crash reports through
 *         Crashlytics
 */
public class StudIPApplication extends Application {

    public static String TAG = StudIPApplication.class.getSimpleName();
    private static StudIPApplication mInstance;
    private RequestQueue mRequestQueue;

    public OAuthConnector getOAuthConnector() {
        return mOAuthConnector;
    }

    public void setOAuthConnector(OAuthConnector authConnector) {
        this.mOAuthConnector = authConnector;
    }

    private OAuthConnector mOAuthConnector;

    public static synchronized StudIPApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // create instance
        mInstance = this;

        // Trigger initialization of Crashlytics
        if (BuildConfig.USE_CRASHLYTICS)
            Crashlytics.start(this);

        /*
         * Clear shared prefs for debugging
		 */
//        Prefs.getInstance(getApplicationContext()).clearPrefs();
    }

    /**
     * Returns a RequestQueue using the ApplicationContext.
     *
     * @return RequestQueue
     */
    public RequestQueue getRequestQueue() {
        // Create new if no queue is null
        if (mRequestQueue == null)
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());

        return mRequestQueue;
    }

    /**
     * Adds a Volley Request<T> to the RequestQueue of the Application Context using the supplied
     * tag.
     *
     * @param request the Volley Request to add to the queue
     * @param tag     the tag for this request, empty string will force default tag
     */
    public <T> void addToRequestQueue(Request<T> request, String tag) {
        request.setTag(TextUtils.isEmpty(tag) ? TAG : tag);

        getRequestQueue().add(request);
    }

    /**
     * Adds a Volley Request<T> to the RequestQueue of the Application Context using the default
     * tag
     *
     * @param request the Volley Request to add to the queue
     */
    public <T> void addToRequestQueue(Request<T> request) {
        addToRequestQueue(request, "");
    }

    /**
     * Cancels all pending Volley Requests waiting in the queue and are tagged with the supplied
     * tag Object.
     *
     * @param tag the tag Object to identify the requests to cancel
     */
    public void cancelAllPendingRequests(Object tag) {
        if (mRequestQueue != null)
            mRequestQueue.cancelAll(tag == null ? TAG : tag);
    }
}
