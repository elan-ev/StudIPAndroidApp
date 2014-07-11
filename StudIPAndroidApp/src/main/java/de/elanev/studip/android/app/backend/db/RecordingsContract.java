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
 * Created by Jörn on 10.07.2014.
 */
public class RecordingsContract extends AbstractContract {
  public static final String TABLE_RECORDINGS = "recordings";

  public static final String CREATE_TABLE_RECORDINGS = String.format(
      "CREATE TABLE IF NOT EXISTS %s (%s INTEGER PRIMARY KEY, %s TEXT UNIQUE, " +
          "%s TEXT, %s TEXT, %s TEXT, %s INTEGER, %s TEXT, %s TEXT, %s TEXT, " +
          "%s TEXT, %s TEXT, %s TEXT, %s TEXT);",
      TABLE_RECORDINGS,
      Columns.Recordings._ID,
      Columns.Recordings.RECORDING_ID,
      Columns.Recordings.RECORDING_AUDIO_DOWNLOAD,
      Columns.Recordings.RECORDING_AUTHOR,
      Columns.Recordings.RECORDING_DESCRIPTION,
      Columns.Recordings.RECORDING_DURATION,
      Columns.Recordings.RECORDING_EXTERNAL_PLAYER_URL,
      Columns.Recordings.RECORDING_PRESENTATION_DOWNLOAD,
      Columns.Recordings.RECORDING_PRESENTER_DOWNLOAD,
      Columns.Recordings.RECORDING_PREVIEW,
      Columns.Recordings.RECORDING_START,
      Columns.Recordings.RECORDING_TITLE,
      Columns.Recordings.RECORDING_COURSE_ID
  );

  /*
   * ContentProvider stuff
   *
   */
  public static final String PATH_RECORDINGS = "recordings";
  public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
      .appendPath(PATH_RECORDINGS)
      .build();

  // content mime types
  public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.studip.recordings";
  public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.studip.recordings";

  // default sort orders
  public static final String DEFAULT_SORT_ORDER = Qualified.Recordings.RECORDINGS_RECORDING_START
      + " ASC";


  public static final class Columns {
    private Columns() {}

    public interface Recordings extends BaseColumns {
      String RECORDING_ID = "id";
      String RECORDING_AUDIO_DOWNLOAD = "audio_download";
      String RECORDING_AUTHOR = "author";
      String RECORDING_DESCRIPTION = "description";
      String RECORDING_DURATION = "duration";
      String RECORDING_EXTERNAL_PLAYER_URL = "external_player_url";
      String RECORDING_PRESENTATION_DOWNLOAD = "presentation_download";
      String RECORDING_PRESENTER_DOWNLOAD = "presenter_download";
      String RECORDING_PREVIEW = "preview";
      String RECORDING_START = "start";
      String RECORDING_TITLE = "title";
      String RECORDING_COURSE_ID = "course_id";
    }
  }

  public static final class Qualified {
    private Qualified() {}

    public interface Recordings {
      String RECORDINGS_ID = TABLE_RECORDINGS + "." + BaseColumns._ID;
      String RECORDINGS_RECORDING_ID = TABLE_RECORDINGS + "." + Columns.Recordings.RECORDING_ID;
      String RECORDINGS_RECORDING_AUDIO_DOWNLOAD = TABLE_RECORDINGS + "."
          + Columns.Recordings.RECORDING_AUDIO_DOWNLOAD;
      String RECORDINGS_RECORDING_AUTHOR = TABLE_RECORDINGS + "."
          + Columns.Recordings.RECORDING_AUTHOR;
      String RECORDINGS_RECORDING_DESCRIPTION = TABLE_RECORDINGS + "."
          + Columns.Recordings.RECORDING_DESCRIPTION;
      String RECORDINGS_RECORDING_DURATION = TABLE_RECORDINGS + "."
          + Columns.Recordings.RECORDING_DURATION;
      String RECORDINGS_RECORDING_EXTERNAL_PLAYER_URL = TABLE_RECORDINGS + "."
          + Columns.Recordings.RECORDING_EXTERNAL_PLAYER_URL;
      String RECORDINGS_RECORDING_PRESENTATION_DOWNLOAD = TABLE_RECORDINGS + "."
          + Columns.Recordings.RECORDING_PRESENTATION_DOWNLOAD;
      String RECORDINGS_RECORDING_PRESENTER_DOWNLOAD = TABLE_RECORDINGS + "."
          + Columns.Recordings.RECORDING_PRESENTER_DOWNLOAD;
      String RECORDINGS_RECORDING_PREVIEW = TABLE_RECORDINGS + "."
          + Columns.Recordings.RECORDING_PREVIEW;
      String RECORDINGS_RECORDING_START = TABLE_RECORDINGS + "."
          + Columns.Recordings.RECORDING_START;
      String RECORDINGS_RECORDING_TITLE = TABLE_RECORDINGS + "."
          + Columns.Recordings.RECORDING_TITLE;
      String RECORDINGS_RECORDING_COURSE_ID = TABLE_RECORDINGS + "."
          + Columns.Recordings.RECORDING_COURSE_ID;
    }
  }
}
