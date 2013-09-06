/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.util;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * @author joern
 * 
 */
public class VolleyHttp implements Cloneable {
	// private static final int MAX_IMAGE_CACHE_ENTIRES = 100;

	private static RequestQueue mRequestQueue;
	private static ImageLoader mImageLoader;
	private final static int maxMemory = (int) (Runtime.getRuntime()
			.maxMemory() / 1024);
	private final static int cacheSize = maxMemory / 8;
	private static VolleyHttp mInstance;

	/**
	 * Returns on instance of VolleHttp
	 * 
	 * @param context
	 *            the execution context
	 * @return the VolleyHttp instance
	 */
	public static VolleyHttp getVolleyHttp(Context context) {
		if (mInstance == null) {
			// Thread safety
			synchronized (VolleyHttp.class) {
				mInstance = new VolleyHttp();
				init(context);
			}

		}

		return mInstance;
	}

	/*
	 * Initializes the this Singleton
	 * 
	 * @param context the context
	 */
	private static void init(Context context) {
		mRequestQueue = Volley.newRequestQueue(context);
		mImageLoader = new ImageLoader(mRequestQueue, new BitmapLruCache(
				cacheSize));
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new CloneNotSupportedException("Singleton must not be cloned");
	}

	/*
	 * No access
	 */
	private VolleyHttp() {
	}

}
