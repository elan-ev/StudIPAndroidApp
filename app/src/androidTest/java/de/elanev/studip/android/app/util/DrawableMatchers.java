/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.support.test.espresso.matcher.BoundedMatcher;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

/**
 * @author joern
 */

public class DrawableMatchers {
  public static Matcher<View> withDrawable(final int resourceId) {
    return new BoundedMatcher<View, ImageView>(ImageView.class) {
      @Override public void describeTo(Description description) {
        description.appendText("has image drawable resource " + resourceId);
      }

      @Override public boolean matchesSafely(ImageView imageView) {
        return sameBitmap(imageView.getContext(), imageView.getDrawable(), resourceId);
      }
    };
  }

  private static boolean sameBitmap(Context context, Drawable drawable, int resourceId) {
    Drawable otherDrawable = ContextCompat.getDrawable(context, resourceId);

    if (drawable == null || otherDrawable == null) {
      return false;
    }

    if (drawable instanceof StateListDrawable && otherDrawable instanceof StateListDrawable) {
      drawable = drawable.getCurrent();
      otherDrawable = otherDrawable.getCurrent();
    }

    if (drawable instanceof BitmapDrawable) {
      Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
      Bitmap otherBitmap = ((BitmapDrawable) otherDrawable).getBitmap();
      return bitmap.sameAs(otherBitmap);
    }

    return false;
  }
}