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
public class VolleyHttp {
	private static final int MAX_IMAGE_CACHE_ENTIRES = 100;

	private static RequestQueue mRequestQueue;
	private static ImageLoader mImageLoader;

	final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
	final int cacheSize = maxMemory / 8;

	private VolleyHttp() {

		// no instances
	}

	public static void init(Context context) {
		mRequestQueue = Volley.newRequestQueue(context);
		mImageLoader = new ImageLoader(mRequestQueue, new BitmapLruCache(
				MAX_IMAGE_CACHE_ENTIRES));
	}

	public static RequestQueue getRequestQueue() {
		if (mRequestQueue != null) {
			return mRequestQueue;
		} else {
			throw new IllegalStateException("RequestQueue not initialized");
		}
	}

	/**
	 * Returns instance of ImageLoader initialized with {@see FakeImageCache}
	 * which effectively means that no memory caching is used. This is useful
	 * for images that you know that will be show only once.
	 * 
	 * @return
	 */
	public static ImageLoader getImageLoader() {
		if (mImageLoader != null) {
			return mImageLoader;
		} else {
			throw new IllegalStateException("ImageLoader not initialized");
		}
	}

}
