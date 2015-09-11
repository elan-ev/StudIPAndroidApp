package de.elanev.studip.android.app.util;

import android.content.Context;
import android.text.format.DateUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.elanev.studip.android.app.R;

/**
 * Class encapsulation some utility methods for easier working with dates.
 *
 * @author joern
 */
public class DateTools {

  /**
   * Static method which compares two timestamps in milliseconds and checks whether they are in
   * the same day or not.
   *
   * @param dateMillies1 First timestamp in milliseconds
   * @param dateMillies2 Second timestamp in milliseconds
   * @return True if both timestamps point to the same day, false if the days are different.
   */
  public static boolean isSameDay(long dateMillies1, long dateMillies2) {
    SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd", Locale.getDefault());

    String fmtDate1 = fmt.format(new Date(dateMillies1));
    String fmtDate2 = fmt.format(new Date(dateMillies2));

    return fmtDate1.equals(fmtDate2);
  }

  /**
   * Returns a String containing the relative time like @link{getLocalizedRelativeTimeString} amd the
   * passed author.
   *
   * @param author The author for the String
   * @param date   Timestamp pointing to the starting point for the relative time string
   * @param ctx    Context for getting resources
   * @return String containing the author and relative time from timestamp
   */
  public static String getLocalizedAuthorAndDateString(String author,
      final long date,
      Context ctx) {

    return String.format("%s %s %s", getLocalizedRelativeTimeString(date),
        ctx.getString(R.string.by), author);
  }

  /**
   * Creates a string stating the time span relative to the passed time. Like 5 days ago, 2 hours
   * ago, etc.
   *
   * @param time Timestamp pointing to the starting point form where the relative time should be
   *             measured
   * @return String describing the relative time span form the passed timestamp
   */
  public static String getLocalizedRelativeTimeString(final long time) {
    long timeMillis = normalizeTimestamp(time);
    long now = System.currentTimeMillis();
    int formatter = DateUtils.FORMAT_ABBREV_RELATIVE | DateUtils.FORMAT_SHOW_YEAR;

    return DateUtils.getRelativeTimeSpanString(timeMillis, now, DateUtils.SECOND_IN_MILLIS,
        formatter)
        .toString();
  }

  private static long normalizeTimestamp(final long time) {

    if (time <= 9999999999L) {
      return time * 1000L;
    } else {
      return time;
    }

  }

  /**
   * Creates and returns a localized, abbreviated String representation of the passed time.
   *
   * @param time Time to generate the String representation from
   * @param ctx  The context to run this method in
   * @return A localized and abbreviated String representation of the passed timestamp
   */
  public static String getShortLocalizedTime(final long time, Context ctx) {
    long timeMillis = normalizeTimestamp(time);

    int formatter = DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_SHOW_YEAR;
    return DateUtils.formatDateTime(ctx.getApplicationContext(), timeMillis, formatter);
  }

  /**
   * Creates a 24h formatted time string from a timestamp.
   *
   * @param time Timestamp to convert to 24h time String
   * @return 24h formatted time String'###'''''''''''''''
   */
  public static String get24hTime(final long time) {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:MM", Locale.getDefault());

    return simpleDateFormat.format(time);
  }
}


