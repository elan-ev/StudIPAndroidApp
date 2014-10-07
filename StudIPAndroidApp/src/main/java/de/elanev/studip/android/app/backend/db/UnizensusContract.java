/*
 * Copyright (c) 2014 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */
package de.elanev.studip.android.app.backend.db;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * @author joern
 */
public class UnizensusContract extends AbstractContract {

  // table name
  public static final String TABLE = "unizensus";

  // table creation strings
  public static final String CREATE_TABLE_UNIZENSUS = String.format(
      "CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY, %s TEXT , %s TEXT, %s TEXT);",
      TABLE,
      Columns.Unizensus._ID,
      Columns.Unizensus.ZENSUS_TYPE,
      Columns.Unizensus.ZENSUS_URL,
      Columns.Unizensus.ZENSUS_COURSE_ID);

  // content uri
  public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
      .appendPath("unizensus")
      .build();
  public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.studip.unizensus";
  public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.studip.unizensus";


  public static final String DEFAULT_SORT_ORDER = Columns.Unizensus.ZENSUS_COURSE_ID + " ASC";

  // table columns
  public static final class Columns {
    private Columns() {
    }

    // unizensus table columns
    public static interface Unizensus extends BaseColumns {
      public static final String ZENSUS_TYPE = "zensus_type";
      public static final String ZENSUS_URL = "zensus_url";
      public static final String ZENSUS_COURSE_ID = "zensus_course_id";
    }
  }


}
