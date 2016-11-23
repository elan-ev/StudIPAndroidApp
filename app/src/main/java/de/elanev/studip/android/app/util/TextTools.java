/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package de.elanev.studip.android.app.util;

import android.support.annotation.Nullable;
import android.text.Html;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Utilities for working with files
 *
 * @author joern
 */
public class TextTools {

  /**
   * Prints human readable file size
   *
   * @param size unreadable size in long
   * @return readable size string
   */
  public static String readableFileSize(long size) {
    if (size <= 0) {
      return "0";
    }

    final String[] units = new String[]{
        "B", "KB", "MB", "GB", "TB"
    };

    int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
    return new DecimalFormat("#,##0.00").format(size / Math.pow(1024, digitGroups)) + " "
        + units[digitGroups];
  }

  public static String capitalizeFirstLetter(String s) {
    String str = s.substring(0, 1)
        .toUpperCase(Locale.getDefault()) + s.substring(1)
        .toLowerCase(Locale.getDefault());
    return str;
  }

  /**
   * Removes all <img /> HTML tags from a String.
   *
   * @param content the String the <img /> tags should be removed from.
   * @return a String without any <img /> tags
   */
  public static String stripImages(String content) {
    return content.replaceAll("\\<img.*?>", "");
  }

  /**
   * Removes all HTML tags from the passed String.
   *
   * @param html the String the HTML tags should be removed from
   * @return a HTML tag free String
   */
  public static String stripHtml(String html) {
    return Html.fromHtml(html)
        .toString();
  }

  /**
   * Takes an arbitrary number of name parts and created a full name String with all parts
   * concatenated. The parts must be passed in the correct order.
   *
   * @param parts Parts of a name to concatenate to a full name String
   * @return Full name string
   */
  public static String createNameSting(String... parts) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < parts.length; i++) {
      if (i != 0) {
        builder.append(" ");
      }
      builder.append(parts[i]);
    }

    return builder.toString()
        .trim();
  }

  /**
   * Checks whether the passed string is null or empty (length = 0)
   *
   * @param str the String to check for emptiness
   * @return true if the passed String is null or empty, otherwise false
   */
  public static boolean isEmpty(@Nullable String str) {
    return str == null || str.length() == 0;
  }
}
