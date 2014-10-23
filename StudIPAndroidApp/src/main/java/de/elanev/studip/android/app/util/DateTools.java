package de.elanev.studip.android.app.util;

import java.text.SimpleDateFormat;
import java.util.Date;

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
    SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");

    String fmtDate1 = fmt.format(new Date(dateMillies1));
    String fmtDate2 = fmt.format(new Date(dateMillies2));

    return fmtDate1.equals(fmtDate2);
  }
}
