/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.util;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.squareup.okhttp.OkHttpClient;

import java.net.CookieHandler;
import java.net.CookieManager;

import de.elanev.studip.android.app.backend.net.util.OkHttpStack;

/**
 * Singleton class for easier accessing the Volley HTTP Stack
 *
 * @author joern
 */
public class VolleyHttp implements Cloneable {

    private final static int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    private final static int cacheSize = maxMemory / 8;
    private static RequestQueue mRequestQueue;
    private static ImageLoader mImageLoader;
    private static VolleyHttp mInstance;
    private static CookieManager mCookiemanager;
    private static OkHttpClient mOkHttpClient;

    /*
     * No access
     */
    private VolleyHttp() {
    }

    /**
     * Returns on instance of VolleHttp
     *
     * @param context the execution context
     * @return the VolleyHttp instance
     */
    public static VolleyHttp getVolleyHttp(Context context) {
        if (mInstance == null) {
            // Thread safety
            synchronized (VolleyHttp.class) {
                mInstance = new VolleyHttp();
                mCookiemanager = new CookieManager();
                CookieHandler.setDefault(mCookiemanager);
//                mOkHttpClient = new OkHttpClient();
//                mOkHttpClient.setCookieHandler(CookieHandler.getDefault());
//                mRequestQueue = Volley.newRequestQueue(context, new OkHttpStack(mOkHttpClient));
                mRequestQueue = Volley.newRequestQueue(context);
                mImageLoader = new ImageLoader(mRequestQueue, new BitmapLruCache(cacheSize));
            }

        }

        return mInstance;
    }

    /**
     * Returns the RequestQueue if properly initialized
     *
     * @return Volley RequestQueue
     */
    public RequestQueue getRequestQueue() {
        if (mRequestQueue != null) {
            return mRequestQueue;
        } else {
            throw new IllegalStateException("RequestQueue not initialized");
        }
    }

    /**
     * Returns instance of ImageLoader
     *
     * @return Volley ImageLoader
     */
    public ImageLoader getImageLoader() {
        if (mImageLoader != null) {
            return mImageLoader;
        } else {
            throw new IllegalStateException("ImageLoader not initialized");
        }
    }

    /**
     * Cancels all pending requests in the queue
     */
    public void cancelAll() {
        mRequestQueue.cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#clone()
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return new CloneNotSupportedException("Singleton must not be cloned");
    }

}
