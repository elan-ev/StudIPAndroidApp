/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.util;

import android.content.Context;
import android.text.format.DateFormat;
import android.text.format.DateUtils;

import java.text.DecimalFormat;
import java.util.Calendar;
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

        final String[] units = new String[]
                {
                        "B", "KB", "MB", "GB", "TB"
                };

        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.00")
                .format(size / Math.pow(1024, digitGroups))
                + " "
                + units[digitGroups];
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
            return String.format(ctx.getString(R.string.minutes_ago),
                    (diff / MINUTE_MILLIS));
        } else if (diff < 90 * MINUTE_MILLIS) {
            return ctx.getString(R.string.an_hour_ago);
        } else if (diff < 24 * HOUR_MILLIS) {
            return String.format(ctx.getString(R.string.hours_ago),
                    (diff / HOUR_MILLIS));
        } else if (diff < 48 * HOUR_MILLIS) {
            return ctx.getString(R.string.yesterday);
        } else {
            Long diffDays = diff / DAY_MILLIS;
            if (diffDays < 100) {
                return String
                        .format(ctx.getString(R.string.days_ago),
                                diffDays);
            } else {
                return getLocalizedTime(time, ctx);
            }
        }
    }

    public static String getLocalizedAuthorAndDateString(String author,
                                                         Long date, Context ctx) {
        return String.format("%s %s %s", getTimeAgo(date, ctx),
                ctx.getString(R.string.by), author);
    }

    public static boolean isSameDay(long time1, long time2) {

        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTimeInMillis(time1);
        cal2.setTimeInMillis(time2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2
                .get(Calendar.DAY_OF_YEAR);
    }

    public static String getLocalizedTime(long time, Context ctx) {
        return DateUtils.formatDateTime(ctx, time,
                DateUtils.FORMAT_ABBREV_MONTH | DateUtils.FORMAT_SHOW_DATE
                        | DateUtils.FORMAT_SHOW_WEEKDAY);
    }

    public static String buildLocalizedTimeString(Long time, Context ctx) {
        return DateFormat.getTimeFormat(ctx).format(time);
    }

    public static String capitalizeFirstLetter(String s) {
        String str = s.substring(0, 1).toUpperCase(Locale.getDefault())
                + s.substring(1).toLowerCase(Locale.getDefault());
        return str;
    }
}
