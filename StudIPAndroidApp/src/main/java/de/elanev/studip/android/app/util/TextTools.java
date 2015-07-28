/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.util;

import android.content.Context;
import android.content.res.Resources;
import android.text.Html;
import android.text.format.DateFormat;
import android.text.format.DateUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import de.elanev.studip.android.app.R;

/**
 * Utilities for working with files
 *
 * @author joern
 */
public class TextTools {

  private static final int SECOND_MILLIS = 1000;
  private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
  private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
  private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

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

  public static String getShortRelativeTime(long time, Context ctx) {
    if (time < 1000000000000L) {
      time *= 1000;
    }

    long now = System.currentTimeMillis();
    if (time > now || time <= 0) {
      return null;
    }

    final long diff = now - time;
    Resources res = ctx.getResources();
    if (diff < MINUTE_MILLIS) {
      return ctx.getString(R.string.just_now);
    } else if (diff < 60 * MINUTE_MILLIS) {
      long times = diff / MINUTE_MILLIS;
      return String.format("%d %s",
          times,
          res.getQuantityString(R.plurals.minutes_abbrev, (int) times));
    } else if (diff < 24 * HOUR_MILLIS) {
      long times = diff / HOUR_MILLIS;
      return String.format("%d %s",
          times,
          res.getQuantityString(R.plurals.hours_abbrev, (int) times));
    } else if (diff < 10 * DAY_MILLIS) {
      long times = diff / DAY_MILLIS;
      return String.format("%d %s",
          times,
          res.getQuantityString(R.plurals.days_abbrev, (int) times));
    } else if (diff < 365 * DAY_MILLIS) {
      return DateUtils.formatDateTime(ctx,
          time,
          DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_MONTH);
    } else {
      return DateUtils.formatDateTime(ctx,
          time,
          DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_MONTH
              | DateUtils.FORMAT_NO_MONTH_DAY);
    }
  }

  public static String getLocalizedAuthorAndDateString(String author, Long date, Context ctx) {
    return String.format("%s %s %s", getTimeAgo(date, ctx), ctx.getString(R.string.by), author);
  }

  public static String getTimeAgo(long time, Context ctx) {
    if (time < 1000000000000L) {
      time *= 1000;
    }

    long now = System.currentTimeMillis();
    if (time > now || time <= 0) {
      return null;
    }

    final long diff = now - time;
    if (diff < MINUTE_MILLIS) {
      return ctx.getString(R.string.just_now);
    } else if (diff < 2 * MINUTE_MILLIS) {
      return ctx.getString(R.string.a_minute_ago);
    } else if (diff < 50 * MINUTE_MILLIS) {
      return String.format(ctx.getString(R.string.minutes_ago), (diff / MINUTE_MILLIS));
    } else if (diff < 90 * MINUTE_MILLIS) {
      return ctx.getString(R.string.an_hour_ago);
    } else if (diff < 24 * HOUR_MILLIS) {
      return String.format(ctx.getString(R.string.hours_ago), (diff / HOUR_MILLIS));
    } else if (diff < 48 * HOUR_MILLIS) {
      return ctx.getString(R.string.yesterday);
    } else {
      Long diffDays = diff / DAY_MILLIS;
      if (diffDays < 100) {
        return String.format(ctx.getString(R.string.days_ago), diffDays);
      } else {
        return getLocalizedTime(time, ctx);
      }
    }
  }

  public static String getLocalizedTime(long time, Context ctx) {
    return DateUtils.formatDateTime(ctx,
        time,
        DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY);
  }

  /**
   * Creates a 24h formatted time string from a timestamp.
   *
   * @param time Timestamp to convert to 24h time String
   * @return 24h formatted time String
   */
  public static String get24hTime(long time) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:MM", Locale.getDefault());

    return simpleDateFormat.format(time);
  }

  public static String buildLocalizedTimeString(Long time, Context ctx) {
    return DateFormat.getTimeFormat(ctx).format(time);
  }

  public static String capitalizeFirstLetter(String s) {
    String str = s.substring(0, 1).toUpperCase(Locale.getDefault()) + s.substring(1)
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
    return Html.fromHtml(html).toString();
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

    return builder.toString().trim();
  }
}
