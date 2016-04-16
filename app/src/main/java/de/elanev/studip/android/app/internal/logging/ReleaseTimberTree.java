/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.internal.logging;

import android.util.Log;

import com.crashlytics.android.Crashlytics;

import timber.log.Timber;

/**
 * Custom Timber logging tree for use in release builds which only logs errors and warnings. It
 * also writes error exceptions to crashlytics.
 *
 * @author joern
 */
public class ReleaseTimberTree extends Timber.Tree {
  private static final int MAX_LOG_LENGTH = 4000;

  @Override protected boolean isLoggable(int priority) {

    if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
      return false;
    }

    return super.isLoggable(priority);
  }

  @Override protected void log(int priority, String tag, String message, Throwable t) {
    if (isLoggable(priority)) {

      // Log errors to crashlytics
      if (priority == Log.ERROR && t != null) {
        Crashlytics.logException(t);
      }

      // Log message short enough. No need to break it up.
      if (message.length() < MAX_LOG_LENGTH) {
        if (priority == Log.ASSERT) {
          Timber.e(message);
        } else {
          Log.println(priority, tag, message);
        }
        return;
      }

      // Break the message into chunks
      for (int i = 0, length = message.length(); i < length; i++) {
        int newline = message.indexOf('\n', i);
        newline = newline != -1 ? newline : length;
        do {
          int end = Math.min(newline, i + MAX_LOG_LENGTH);
          String part = message.substring(i, end);
          if (priority == Log.ASSERT) {
            Timber.e(part);
          } else {
            Log.println(priority, tag, part);
          }
          i = end;
        } while (i < newline);
      }
    }
  }
}
