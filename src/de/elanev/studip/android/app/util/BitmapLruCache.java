/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.util;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader.ImageCache;

/**
 * @author joern
 * 
 */
public class BitmapLruCache extends LruCache<String, Bitmap> implements
		ImageCache {
	public BitmapLruCache(int maxSize) {
		super(maxSize);
	}

	@Override
	protected int sizeOf(String key, Bitmap value) {
		return value.getRowBytes() * value.getHeight();
	}

	public Bitmap getBitmap(String url) {
		return get(url);
	}

	public void putBitmap(String url, Bitmap bitmap) {
		put(url, bitmap);
	}
}
