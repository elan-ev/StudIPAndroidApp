/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.provider;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import net.sqlcipher.DatabaseUtils;
import net.sqlcipher.SQLException;
import net.sqlcipher.database.SQLiteConstraintException;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteDoneException;
import net.sqlcipher.database.SQLiteStatement;

import java.util.ArrayList;
import java.util.Map;

import de.elanev.studip.android.app.backend.db.AbstractContract;
import de.elanev.studip.android.app.backend.db.AuthenticationContract;
import de.elanev.studip.android.app.backend.db.ContactsContract;
import de.elanev.studip.android.app.backend.db.CoursesContract;
import de.elanev.studip.android.app.backend.db.DatabaseHandler;
import de.elanev.studip.android.app.backend.db.DocumentsContract;
import de.elanev.studip.android.app.backend.db.EventsContract;
import de.elanev.studip.android.app.backend.db.InstitutesContract;
import de.elanev.studip.android.app.backend.db.MessagesContract;
import de.elanev.studip.android.app.backend.db.NewsContract;
import de.elanev.studip.android.app.backend.db.RecordingsContract;
import de.elanev.studip.android.app.backend.db.SemestersContract;
import de.elanev.studip.android.app.backend.db.UsersContract;
import de.elanev.studip.android.app.backend.net.SyncHelper;
import de.elanev.studip.android.app.util.Config;

/**
 * @author joern
 */
public class RestIpProvider extends ContentProvider {
  public static final String TAG = RestIpProvider.class.getSimpleName();
  // Time to wait for the next sync (in ms)
  private static final long SYNC_THRESHOLD = 10000;
  private static final UriMatcher sUriMatcher = buildUriMatcher();
  private static final int NEWS = 100;
  private static final int NEWS_ID = 101;
  private static final int NEWS_RANGE_ID = 102;
  private static final int NEWS_COURSES = 103;
  private static final int NEWS_INSTITUTES = 104;
  private static final int NEWS_GLOBAL = 105;
  private static final int COURSES = 200;
  private static final int COURSES_ID = 201;
  private static final int COURSES_ID_EVENTS = 202;
  private static final int COURSES_USERS = 203;
  private static final int COURSE_USER_ID = 204;
  private static final int COURSES_IDS = 205;
  private static final int COURSE_ID = 206;
  private static final int COURSES_ID_RECORDINGS = 207;
  private static final int USERS = 300;
  private static final int USERS_ID = 301;
  private static final int USERS_COURSE_ID = 302;
  private static final int EVENTS = 400;
  private static final int EVENTS_ID = 401;
  private static final int SEMESTERS = 500;
  private static final int SEMESTERS_ID = 501;
  private static final int DOCUMENTS = 600;
  private static final int DOCUMENTS_ID = 601;
  private static final int DOCUMENTS_FOLDER = 602;
  private static final int DOCUMENTS_COURSE_ID = 603;
  private static final int MESSAGES = 700;
  private static final int MESSAGES_IN = 701;
  private static final int MESSAGES_OUT = 702;
  private static final int MESSAGES_FOLDERS = 703;
  private static final int MESSAGES_IN_FOLDERS = 704;
  private static final int MESSAGES_OUT_FOLDERS = 705;
  private static final int MESSAGES_FOLDER_MESSAGES = 706;
  private static final int MESSAGES_IN_FOLDER_MESSAGES = 707;
  private static final int MESSAGES_OUT_FOLDER_MESSAGES = 708;
  private static final int MESSAGES_IN_ID = 709;
  private static final int MESSAGES_OUT_ID = 710;
  private static final int MESSAGES_ID = 711;
  private static final int MESSAGES_STRING_ID = 712;
  private static final int MESSAGES_READ_ID = 713;
  private static final int MESSAGES_FOLDER_NAME_MESSAGES = 714;
  private static final int CONTACTS = 800;
  private static final int CONTACTS_ID = 801;
  private static final int CONTACTS_GROUPS = 802;
  private static final int CONTACTS_GROUPS_ID = 803;
  private static final int CONTACTS_GROUP_MEMBERS = 804;
  private static final int CONTACTS_GROUP_MEMBERS_GROUPID = 805;
  private static final int AUTHENTICATION = 900;
  private static final int INSTITUTES = 1000;
  private static final int RECORDINGS = 1100;
  private long mLastDocumentsSync = -1;
  private long mLastCoursesSync = -1;
  private long mLastNewsSync = -1;
  private long mLastEventsSync = -1;
  private long mLastMessagesSync = -1;
  private long mLastContactsSync = -1;
  private long mLastContactGroupsSync = -1;

  /*
   * Uri Matcher
   */
  private static UriMatcher buildUriMatcher() {
    final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    final String authority = AbstractContract.CONTENT_AUTHORITY;

    // matches for news
    matcher.addURI(authority, "news", NEWS);
    matcher.addURI(authority, "news/courses", NEWS_COURSES);
    matcher.addURI(authority, "news/institutes", NEWS_INSTITUTES);
    matcher.addURI(authority, "news/global", NEWS_GLOBAL);
    matcher.addURI(authority, "news/*", NEWS_RANGE_ID);
    matcher.addURI(authority, "news/#", NEWS_ID);

    // matches for courses
    matcher.addURI(authority, "courses", COURSES);
    matcher.addURI(authority, "courses/users", COURSES_USERS);
    matcher.addURI(authority, "courses/ids", COURSES_IDS);
    matcher.addURI(authority, "courses/events/*", COURSES_ID_EVENTS);
    matcher.addURI(authority, "courses/userids/*", COURSE_USER_ID);
    matcher.addURI(authority, "courses/recordings/*", COURSES_ID_RECORDINGS);
    matcher.addURI(authority, "courses/#", COURSES_ID);
    matcher.addURI(authority, "course/#", COURSE_ID);

    // matches for users
    matcher.addURI(authority, "users", USERS);
    matcher.addURI(authority, "users/course/*", USERS_COURSE_ID);
    matcher.addURI(authority, "users/*", USERS_ID);

    // matches for events
    matcher.addURI(authority, "events", EVENTS);
    matcher.addURI(authority, "events/*", EVENTS_ID);

    // matches for semesters
    matcher.addURI(authority, "semesters", SEMESTERS);
    matcher.addURI(authority, "semesters/#", SEMESTERS_ID);

    // matches for documents
    matcher.addURI(authority, "documents", DOCUMENTS);
    matcher.addURI(authority, "documents/folder", DOCUMENTS_FOLDER);
    matcher.addURI(authority, "documents/#", DOCUMENTS_ID);
    matcher.addURI(authority, "documents/*", DOCUMENTS_COURSE_ID);

    // matches for messages
    matcher.addURI(authority, "messages", MESSAGES);
    matcher.addURI(authority, "messages/in", MESSAGES_IN);
    matcher.addURI(authority, "messages/out", MESSAGES_OUT);
    matcher.addURI(authority, "messages/folders", MESSAGES_FOLDERS);
    matcher.addURI(authority, "messages/folders/#", MESSAGES_FOLDER_MESSAGES);
    matcher.addURI(authority, "messages/folders/name/*", MESSAGES_FOLDER_NAME_MESSAGES);
    matcher.addURI(authority, "messages/in/folders", MESSAGES_IN_FOLDERS);
    matcher.addURI(authority, "messages/out/folders", MESSAGES_OUT_FOLDERS);
    matcher.addURI(authority, "messages/in/folders/#", MESSAGES_IN_FOLDER_MESSAGES);
    matcher.addURI(authority, "messages/out/folders/#", MESSAGES_OUT_FOLDER_MESSAGES);
    matcher.addURI(authority, "messages/in/#", MESSAGES_IN_ID);
    matcher.addURI(authority, "messages/out/#", MESSAGES_OUT_ID);
    matcher.addURI(authority, "messages/read/*", MESSAGES_READ_ID);
    matcher.addURI(authority, "messages/#", MESSAGES_ID);
    matcher.addURI(authority, "messages/*", MESSAGES_STRING_ID);

    // matches for contacts
    matcher.addURI(authority, "contacts", CONTACTS);
    matcher.addURI(authority, "contacts/groups", CONTACTS_GROUPS);
    matcher.addURI(authority, "contacts/groups/members", CONTACTS_GROUP_MEMBERS);
    matcher.addURI(authority, "contacts/groups/members/*", CONTACTS_GROUP_MEMBERS_GROUPID);
    matcher.addURI(authority, "contacts/groups/*", CONTACTS_GROUPS_ID);
    matcher.addURI(authority, "contacts/*", CONTACTS_ID);

    // matchers for authentication
    matcher.addURI(authority, "authentication", AUTHENTICATION);

    // matchers for institutes
    matcher.addURI(authority, "institutes", INSTITUTES);

    // match for recordings
    matcher.addURI(authority, "recordings", RECORDINGS);

    return matcher;
  }

  @Override public boolean onCreate() {
    return true;
  }

  @Override public Cursor query(Uri uri,
      String[] projection,
      String selection,
      String[] selectionArgs,
      String sortOrder) {
    String key = Config.PRIVATE_KEY;
    SQLiteDatabase db = DatabaseHandler.getInstance(getContext())
        .getReadableDatabase(Config.PRIVATE_KEY);
    String orderBy;
    final int match = sUriMatcher.match(uri);
    Cursor c = null;

    switch (match) {
      case NEWS:
//        SyncHelper.getInstance(getContext()).performNewsSync(null);

        if (TextUtils.isEmpty(sortOrder)) {
          orderBy = NewsContract.DEFAULT_SORT_ORDER;
        } else {
          orderBy = sortOrder;
        }

        c = db.query(NewsContract.NEWS_JOIN_USER_COURSES,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            orderBy);

        c.setNotificationUri(getContext().getContentResolver(), NewsContract.CONTENT_URI);

        break;

      case NEWS_COURSES:
//        SyncHelper.getInstance(getContext()).performNewsSync(null);

        if (TextUtils.isEmpty(sortOrder)) {
          orderBy = NewsContract.DEFAULT_SORT_ORDER;
        } else {
          orderBy = sortOrder;
        }

        c = db.query(NewsContract.NEWS_JOIN_USER_COURSES,
            projection,
            NewsContract.Columns.NEWS_RANGE_ID + " != 'studip'" +
                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
            selectionArgs,
            null,
            null,
            orderBy
        );

        c.setNotificationUri(getContext().getContentResolver(), NewsContract.CONTENT_URI);

        break;

      case NEWS_INSTITUTES:
//        SyncHelper.getInstance(getContext()).performNewsSync(null);

        if (TextUtils.isEmpty(sortOrder)) {
          orderBy = NewsContract.DEFAULT_SORT_ORDER;
        } else {
          orderBy = sortOrder;
        }

        c = db.query(NewsContract.NEWS_JOIN_USER_INSTITUTES,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            orderBy);

        c.setNotificationUri(getContext().getContentResolver(), NewsContract.CONTENT_URI);

        break;

      case NEWS_GLOBAL:
//        SyncHelper.getInstance(getContext()).performNewsSync(null);

        if (TextUtils.isEmpty(sortOrder)) {
          orderBy = NewsContract.DEFAULT_SORT_ORDER;
        } else {
          orderBy = sortOrder;
        }

        c = db.query(NewsContract.NEWS_JOIN_USER_COURSES,
            projection,
            NewsContract.Columns.NEWS_RANGE_ID + " = ?",
            new String[]{"studip"},
            null,
            null,
            orderBy);

        c.setNotificationUri(getContext().getContentResolver(), NewsContract.CONTENT_URI);

        break;

      case NEWS_RANGE_ID: {
        String rangeId = uri.getLastPathSegment();

        if (TextUtils.isEmpty(sortOrder)) {
          orderBy = NewsContract.DEFAULT_SORT_ORDER;
        } else {
          orderBy = sortOrder;
        }
        c = db.query(NewsContract.NEWS_JOIN_USER,
            projection,
            NewsContract.Columns.NEWS_RANGE_ID + " = " + "'" + rangeId + "'" +
                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
            selectionArgs,
            null,
            null,
            orderBy
        );
        c.setNotificationUri(getContext().getContentResolver(), NewsContract.CONTENT_URI);
        break;
      }
      case NEWS_ID:
        if (TextUtils.isEmpty(sortOrder)) {
          orderBy = NewsContract.DEFAULT_SORT_ORDER;
        } else {
          orderBy = sortOrder;
        }
        long newsId = ContentUris.parseId(uri);
        c = db.query(NewsContract.NEWS_JOIN_USER,
            projection,
            NewsContract.Columns._ID + " = " + newsId +
                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
            selectionArgs,
            null,
            null,
            orderBy
        );
      case COURSES: {
        if (TextUtils.isEmpty(sortOrder)) {
          orderBy = CoursesContract.DEFAULT_SORT_ORDER;
        } else {
          orderBy = sortOrder;
        }
        c = db.query(CoursesContract.COURSES_JOIN_SEMESTERS,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            orderBy);
        c.setNotificationUri(getContext().getContentResolver(), CoursesContract.CONTENT_URI);
        break;
      }
      case COURSE_ID: {
        if (TextUtils.isEmpty(sortOrder)) {
          orderBy = CoursesContract.DEFAULT_SORT_ORDER;
        } else {
          orderBy = sortOrder;
        }
        long courseId = ContentUris.parseId(uri);
        c = db.query(CoursesContract.TABLE_COURSES,
            projection,
            CoursesContract.Qualified.Courses.COURSES_ID + " = " + courseId +
                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
            selectionArgs,
            null,
            null,
            orderBy
        );
        c.setNotificationUri(getContext().getContentResolver(), CoursesContract.CONTENT_URI);
        break;
      }
      case COURSES_ID: {
        if (TextUtils.isEmpty(sortOrder)) {
          orderBy = CoursesContract.DEFAULT_SORT_ORDER;
        } else {
          orderBy = sortOrder;
        }
        long courseId = ContentUris.parseId(uri);
        c = db.query(CoursesContract.COURSES_JOIN_USERS_SEMESTERS,
            projection,
            CoursesContract.Qualified.Courses.COURSES_ID + " = " + courseId +
                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
            selectionArgs,
            null,
            null,
            orderBy
        );
        c.setNotificationUri(getContext().getContentResolver(), CoursesContract.CONTENT_URI);
        break;
      }
      case COURSES_IDS:
        if (TextUtils.isEmpty(sortOrder)) {
          orderBy = CoursesContract.DEFAULT_SORT_ORDER;
        } else {
          orderBy = sortOrder;
        }
        c = db.query(CoursesContract.TABLE_COURSES,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            orderBy);
        c.setNotificationUri(getContext().getContentResolver(), CoursesContract.CONTENT_URI);
        break;
      case COURSE_USER_ID: {
        if (TextUtils.isEmpty(sortOrder)) {
          orderBy = CoursesContract.COURSE_USERS_DEFAULT_SORT;
        } else {
          orderBy = sortOrder;
        }
        String cid = uri.getLastPathSegment();
        c = db.query(CoursesContract.TABLE_COURSE_USER,
            projection,
            CoursesContract.Columns.CourseUsers.COURSE_USER_COURSE_ID + " = " + "'" + cid + "'" +
                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
            selectionArgs,
            null,
            null,
            orderBy
        );
        c.setNotificationUri(getContext().getContentResolver(), CoursesContract.CONTENT_URI);
        break;
      }
      case COURSES_ID_EVENTS: {
        String cid = uri.getLastPathSegment();
        long currTime = System.currentTimeMillis();
        if ((currTime - mLastEventsSync) > SYNC_THRESHOLD) {
          SyncHelper.getInstance(getContext()).performEventsSyncForCourseId(cid);
          mLastEventsSync = currTime;
        }
        if (TextUtils.isEmpty(sortOrder)) {
          orderBy = EventsContract.DEFAULT_SORT_ORDER;
        } else {
          orderBy = sortOrder;
        }

        String whereStr = "(" + EventsContract.Columns.EVENT_COURSE_ID + " = " + "'" + cid + "')" +
            (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
        c = db.query(EventsContract.TABLE,
            projection,
            whereStr,
            selectionArgs,
            null,
            null,
            orderBy);

        c.setNotificationUri(getContext().getContentResolver(), EventsContract.CONTENT_URI);
        break;
      }
      case COURSES_ID_RECORDINGS: {
        String cid = uri.getLastPathSegment();

        if (TextUtils.isEmpty(sortOrder)) {
          orderBy = RecordingsContract.DEFAULT_SORT_ORDER;
        } else {
          orderBy = sortOrder;
        }

        String whereStr = "(" + RecordingsContract.Columns.Recordings.RECORDING_COURSE_ID + " = "
            + "'" + cid + "')" +
            (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : "");
        c = db.query(RecordingsContract.TABLE_RECORDINGS,
            projection,
            whereStr,
            selectionArgs,
            null,
            null,
            orderBy);

        c.setNotificationUri(getContext().getContentResolver(), RecordingsContract.CONTENT_URI);
        break;
      }
      case USERS:
        if (TextUtils.isEmpty(sortOrder)) {
          orderBy = UsersContract.DEFAULT_SORT_ORDER;
        } else {
          orderBy = sortOrder;
        }
        c = db.query(UsersContract.TABLE,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            orderBy);
        c.setNotificationUri(getContext().getContentResolver(), UsersContract.CONTENT_URI);
        break;
      case USERS_ID:
        String userId = uri.getLastPathSegment();
        if (TextUtils.isEmpty(sortOrder)) {
          orderBy = UsersContract.DEFAULT_SORT_ORDER;
        } else {
          orderBy = sortOrder;
        }
        c = db.query(UsersContract.TABLE,
            projection,
            UsersContract.Columns.USER_ID + " = " + '"' + userId + '"' +
                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
            selectionArgs,
            null,
            null,
            orderBy
        );
        c.setNotificationUri(getContext().getContentResolver(), UsersContract.CONTENT_URI);
        break;
      case USERS_COURSE_ID:
        String usersCourseId = uri.getLastPathSegment();

        if (TextUtils.isEmpty(sortOrder)) {
          orderBy = UsersContract.DEFAULT_SORT_ORDER;
        } else {
          orderBy = sortOrder;
        }
        c = db.query(UsersContract.TABLE + " " + UsersContract.USERS_JOIN_COURSES,
            projection,
            CoursesContract.Qualified.CourseUsers.COURSES_USERS_TABLE_COURSE_USER_COURSE_ID +
                " = " + '"' + usersCourseId + '"' +
                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
            selectionArgs,
            UsersContract.Qualified.USERS_USER_ID,
            null,
            orderBy
        );

        c.setNotificationUri(getContext().getContentResolver(), UsersContract.CONTENT_URI);
        break;
      case DOCUMENTS_COURSE_ID: {
        String cid = uri.getLastPathSegment();
        long currTime = System.currentTimeMillis();
        if ((currTime - mLastDocumentsSync) > SYNC_THRESHOLD) {
          SyncHelper.getInstance(getContext()).performDocumentsSyncForCourse(cid);
          mLastDocumentsSync = currTime;
        }

        if (TextUtils.isEmpty(sortOrder)) {
          orderBy = DocumentsContract.DEFAULT_SORT_ORDER;
        } else {
          orderBy = sortOrder;
        }
        //                        DocumentsContract.DOCUMENTS_JOIN_USERS_JOIN_COURSES_JOIN_FOLDERS,
        c = db.query(DocumentsContract.DOCUMENTS_JOIN_COURSES_JOIN_FOLDERS,
            projection,
            DocumentsContract.Qualified.Documents.DOCUMENTS_DOCUMENT_COURSE_ID + " = " + '"' + cid +
                '"' + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
            selectionArgs,
            null,
            null,
            orderBy
        );
        c.setNotificationUri(getContext().getContentResolver(), DocumentsContract.CONTENT_URI);
        break;
      }
      case MESSAGES_ID: {
        long messageId = ContentUris.parseId(uri);
        if (TextUtils.isEmpty(sortOrder)) {
          orderBy = MessagesContract.DEFAULT_SORT_ORDER_MESSAGES;
        } else {
          orderBy = sortOrder;
        }
        c = db.query(MessagesContract.MESSAGES_JOIN_USERS,
            projection,
            MessagesContract.Qualified.Messages.MESSAGES_ID + " = " + messageId +
                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
            selectionArgs,
            null,
            null,
            orderBy
        );
        c.setNotificationUri(getContext().getContentResolver(),
            MessagesContract.CONTENT_URI_MESSAGES);
        break;
      }
      case MESSAGES_STRING_ID: {
        String messageId = uri.getLastPathSegment();
        if (TextUtils.isEmpty(sortOrder)) {
          orderBy = MessagesContract.DEFAULT_SORT_ORDER_MESSAGES;
        } else {
          orderBy = sortOrder;
        }
        c = db.query(MessagesContract.MESSAGES_JOIN_USERS,
            projection,
            MessagesContract.Qualified.Messages.MESSAGES_MESSAGE_ID + " = " + "'" + messageId +
                "'" + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
            selectionArgs,
            null,
            null,
            orderBy
        );
        c.setNotificationUri(getContext().getContentResolver(),
            MessagesContract.CONTENT_URI_MESSAGES);
        break;
      }
      case MESSAGES_FOLDERS:
        if (TextUtils.isEmpty(sortOrder)) {
          orderBy = MessagesContract.DEFAULT_SORT_ORDER_FOLDERS;
        } else {
          orderBy = sortOrder;
        }

        c = db.query(MessagesContract.TABLE_MESSAGE_FOLDERS,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            orderBy);

        c.setNotificationUri(getContext().getContentResolver(),
            MessagesContract.CONTENT_URI_MESSAGE_FOLDERS);
        break;
      case MESSAGES_FOLDER_MESSAGES: {
        long currTime = System.currentTimeMillis();
        //			if ((currTime - mLastMessagesSync) > SYNC_THRESHOLD) {
        //				SyncHelper.getInstance(getContext()).performMessagesSync(null);
        //				mLastMessagesSync = currTime;
        //			}
        if (TextUtils.isEmpty(sortOrder)) {
          orderBy = MessagesContract.DEFAULT_SORT_ORDER_MESSAGES;
        } else {
          orderBy = sortOrder;
        }

        long folderId = ContentUris.parseId(uri);

        c = db.query(MessagesContract.MESSAGES_JOIN_MESSAGE_FOLDERS_JOIN_USERS,
            projection,
            MessagesContract.Qualified.Messages.MESSAGES_MESSAGE_FOLDER_ID + " = " + folderId +
                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
            selectionArgs,
            null,
            null,
            orderBy
        );
        c.setNotificationUri(getContext().getContentResolver(),
            MessagesContract.CONTENT_URI_MESSAGES);
        break;
      }
      case MESSAGES_FOLDER_NAME_MESSAGES: {
        long currTime = System.currentTimeMillis();
        //			if ((currTime - mLastMessagesSync) > SYNC_THRESHOLD) {
        //				SyncHelper.getInstance(getContext()).performMessagesSync(null);
        //				mLastMessagesSync = currTime;
        //			}
        if (TextUtils.isEmpty(sortOrder)) {
          orderBy = MessagesContract.DEFAULT_SORT_ORDER_MESSAGES;
        } else {
          orderBy = sortOrder;
        }

        String folderName = uri.getLastPathSegment();

        c = db.query(MessagesContract.MESSAGES_JOIN_MESSAGE_FOLDERS_JOIN_USERS,
            projection,
            MessagesContract.Qualified.MessageFolders.MESSAGES_FOLDERS_MESSAGE_FOLDER_NAME + " = " +
                "'" + folderName + "'" +
                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
            selectionArgs,
            null,
            null,
            orderBy
        );
        c.setNotificationUri(getContext().getContentResolver(),
            MessagesContract.CONTENT_URI_MESSAGES);
        break;
      }
      case CONTACTS: {
        SyncHelper.getInstance(getContext()).performContactsSync(null, null);

        if (TextUtils.isEmpty(sortOrder)) {
          orderBy = ContactsContract.DEFAULT_SORT_ORDER_CONTACTS;
        } else {
          orderBy = sortOrder;
        }

        c = db.query(ContactsContract.TABLE_CONTACTS + " " + ContactsContract.CONTACTS_JOIN_USERS,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            orderBy);
        c.setNotificationUri(getContext().getContentResolver(),
            ContactsContract.CONTENT_URI_CONTACTS);
        break;
      }
      case CONTACTS_ID:
        String contactId = uri.getLastPathSegment();
        if (TextUtils.isEmpty(sortOrder)) {
          orderBy = ContactsContract.DEFAULT_SORT_ORDER_CONTACT_GROUPS;
        } else {
          orderBy = sortOrder;
        }

        c = db.query(ContactsContract.TABLE_CONTACTS + " " + ContactsContract.CONTATCS_JOIN_GROUPS,
            projection,
            ContactsContract.Qualified.Contacts.CONTACTS_USER_ID + " = " + "'" + contactId + "'" +
                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
            selectionArgs,
            null,
            null,
            orderBy
        );
        c.setNotificationUri(getContext().getContentResolver(),
            ContactsContract.CONTENT_URI_CONTACT_GROUP_MEMBERS);
        break;
      case CONTACTS_GROUPS:

        if (TextUtils.isEmpty(sortOrder)) {
          orderBy = ContactsContract.DEFAULT_SORT_ORDER_CONTACT_GROUPS;
        } else {
          orderBy = sortOrder;
        }

        c = db.query(ContactsContract.TABLE_CONTACT_GROUPS,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            orderBy);
        c.setNotificationUri(getContext().getContentResolver(),
            ContactsContract.CONTENT_URI_CONTACT_GROUPS);
        break;
      case CONTACTS_GROUP_MEMBERS: {
        long currTime = System.currentTimeMillis();
        if ((currTime - mLastContactGroupsSync) > SYNC_THRESHOLD) {
          SyncHelper.getInstance(getContext()).performContactsSync(null, null);
          mLastContactGroupsSync = currTime;
        }

        if (TextUtils.isEmpty(sortOrder)) {
          orderBy = ContactsContract.DEFAULT_SORT_ORDER_CONTACT_GROUPS;
        } else {
          orderBy = sortOrder;
        }

        c = db.query(ContactsContract.TABLE_CONTACTS + " " +
                ContactsContract.CONTATCS_JOIN_USERS_JOIN_GROUPS,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            orderBy
        );
        c.setNotificationUri(getContext().getContentResolver(),
            ContactsContract.CONTENT_URI_CONTACT_GROUP_MEMBERS);
        break;
      }
      case AUTHENTICATION: {

        c = db.query(AuthenticationContract.TABLE_AUTHENTICATION,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null);

        c.setNotificationUri(getContext().getContentResolver(), AuthenticationContract.CONTENT_URI);

        break;
      }
      case INSTITUTES: {

        c = db.query(InstitutesContract.TABLE,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null);

        c.setNotificationUri(getContext().getContentResolver(), AuthenticationContract.CONTENT_URI);

        break;
      }
      default:
        throw new IllegalArgumentException("Unsupported uri: " + uri);
    }

    return c;
  }

  @Override public String getType(Uri uri) {
    final int match = sUriMatcher.match(uri);
    switch (match) {
      case NEWS:
        return NewsContract.CONTENT_TYPE;
      case NEWS_RANGE_ID:
        return NewsContract.CONTENT_TYPE;
      case NEWS_ID:
        return NewsContract.CONTENT_ITEM_TYPE;
      case COURSES:
        return CoursesContract.CONTENT_TYPE;
      case COURSES_IDS:
        return CoursesContract.CONTENT_TYPE;
      case COURSES_ID:
        return CoursesContract.CONTENT_ITEM_TYPE;
      case COURSE_USER_ID:
        return CoursesContract.CONTENT_TYPE;
      case USERS:
        return UsersContract.CONTENT_TYPE;
      case USERS_COURSE_ID:
        return UsersContract.CONTENT_TYPE;
      case USERS_ID:
        return UsersContract.CONTENT_ITEM_TYPE;
      case EVENTS:
        return EventsContract.CONTENT_TYPE;
      case EVENTS_ID:
        return EventsContract.CONTENT_ITEM_TYPE;
      case SEMESTERS:
        return SemestersContract.CONTENT_TYPE;
      case SEMESTERS_ID:
        return SemestersContract.CONTENT_ITEM_TYPE;
      case DOCUMENTS_COURSE_ID:
        return DocumentsContract.CONTENT_TYPE;
      case DOCUMENTS_ID:
        return DocumentsContract.CONTENT_ITEM_TYPE;
      case MESSAGES:
        return MessagesContract.CONTENT_TYPE;
      case MESSAGES_IN:
        return MessagesContract.CONTENT_TYPE;
      case MESSAGES_OUT:
        return MessagesContract.CONTENT_TYPE;
      case MESSAGES_FOLDERS:
        return MessagesContract.FOLDER_CONTENT_TYPE;
      case MESSAGES_IN_FOLDERS:
        return MessagesContract.FOLDER_CONTENT_TYPE;
      case MESSAGES_OUT_FOLDERS:
        return MessagesContract.FOLDER_CONTENT_TYPE;
      case MESSAGES_IN_FOLDER_MESSAGES:
        return MessagesContract.CONTENT_TYPE;
      case MESSAGES_FOLDER_NAME_MESSAGES:
        return MessagesContract.CONTENT_TYPE;
      case MESSAGES_OUT_FOLDER_MESSAGES:
        return MessagesContract.CONTENT_TYPE;
      case MESSAGES_IN_ID:
        return MessagesContract.CONTENT_ITEM_TYPE;
      case MESSAGES_OUT_ID:
        return MessagesContract.CONTENT_ITEM_TYPE;
      case MESSAGES_ID:
        return MessagesContract.CONTENT_ITEM_TYPE;
      case MESSAGES_STRING_ID:
        return MessagesContract.CONTENT_ITEM_TYPE;
      case CONTACTS:
        return ContactsContract.CONTENT_TYPE_CONTACTS;
      case CONTACTS_ID:
        return ContactsContract.CONTENT_ITEM_TYPE_CONTACTS;
      case CONTACTS_GROUPS:
        return ContactsContract.CONTENT_TYPE_CONTACT_GROUPS;
      case CONTACTS_GROUPS_ID:
        return ContactsContract.CONTENT_ITEM_TYPE_CONTACT_GROUPS;
      case AUTHENTICATION:
        return AuthenticationContract.CONTENT_ITEM_TYPE;
      default:
        throw new UnsupportedOperationException("Unknown mime type: " + uri);
    }
  }

  @Override public Uri insert(Uri uri, ContentValues values) {
    SQLiteDatabase db = DatabaseHandler.getInstance(getContext())
        .getWritableDatabase(Config.PRIVATE_KEY);
    final int match = sUriMatcher.match(uri);
    switch (match) {
      case NEWS: {
        long rowId = db.insertWithOnConflict(NewsContract.TABLE,
            null,
            values,
            SQLiteDatabase.CONFLICT_IGNORE);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(NewsContract.CONTENT_URI, rowId);
      }
      case USERS: {
        long rowId = db.insertWithOnConflict(UsersContract.TABLE,
            null,
            values,
            SQLiteDatabase.CONFLICT_IGNORE);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(UsersContract.CONTENT_URI, rowId);
      }
      case COURSES_USERS: {
        long rowId = db.insertWithOnConflict(CoursesContract.TABLE_COURSE_USER,
            null,
            values,
            SQLiteDatabase.CONFLICT_IGNORE);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(CoursesContract.COURSES_USERS_CONTENT_URI, rowId);
      }
      case EVENTS: {
        long rowId = db.insertWithOnConflict(EventsContract.TABLE,
            null,
            values,
            SQLiteDatabase.CONFLICT_IGNORE);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(EventsContract.CONTENT_URI, rowId);
      }
      case COURSES: {
        long rowId = db.insertWithOnConflict(CoursesContract.TABLE_COURSES,
            null,
            values,
            SQLiteDatabase.CONFLICT_IGNORE);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(CoursesContract.CONTENT_URI, rowId);
      }
      case SEMESTERS: {
        long rowId = db.insertWithOnConflict(SemestersContract.TABLE,
            null,
            values,
            SQLiteDatabase.CONFLICT_IGNORE);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(SemestersContract.CONTENT_URI, rowId);
      }
      case DOCUMENTS: {
        long rowId = insertIgnoringConflict(db,
            DocumentsContract.TABLE_DOCUMENTS,
            DocumentsContract.Columns.Documents._ID,
            values,
            false);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(DocumentsContract.CONTENT_URI, rowId);
      }
      case DOCUMENTS_FOLDER: {
        long rowId = insertIgnoringConflict(db,
            DocumentsContract.TABLE_DOCUMENT_FOLDERS,
            DocumentsContract.Columns.DocumentFolders._ID,
            values,
            false);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(DocumentsContract.CONTENT_URI, rowId);
      }
      case MESSAGES_FOLDERS: {
        long rowId = insertIgnoringConflict(db,
            MessagesContract.TABLE_MESSAGE_FOLDERS,
            MessagesContract.Columns.MessageFolders._ID,
            values,
            false);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(MessagesContract.CONTENT_URI_MESSAGE_FOLDERS, rowId);
      }
      case MESSAGES: {
        long rowId = insertIgnoringConflict(db,
            MessagesContract.TABLE_MESSAGES,
            MessagesContract.Columns.Messages._ID,
            values,
            false);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(MessagesContract.CONTENT_URI_MESSAGES, rowId);
      }

      case CONTACTS: {
        long rowId = insertIgnoringConflict(db,
            ContactsContract.TABLE_CONTACTS,
            ContactsContract.Columns.Contacts._ID,
            values,
            false);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(ContactsContract.CONTENT_URI_CONTACTS, rowId);
      }
      case CONTACTS_GROUPS: {
        long rowId = insertIgnoringConflict(db,
            ContactsContract.TABLE_CONTACT_GROUPS,
            ContactsContract.Columns.ContactGroups._ID,
            values,
            false);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(ContactsContract.CONTENT_URI_CONTACT_GROUPS, rowId);
      }
      case CONTACTS_GROUP_MEMBERS: {
        long rowId = db.insertWithOnConflict(ContactsContract.TABLE_CONTACT_GROUP_MEMBERS,
            null,
            values,
            SQLiteDatabase.CONFLICT_IGNORE);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(ContactsContract.CONTENT_URI_CONTACT_GROUPS, rowId);
      }
      case AUTHENTICATION: {
        delete(uri, null, null);
        long rowId = db.insertWithOnConflict(AuthenticationContract.TABLE_AUTHENTICATION,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(AuthenticationContract.CONTENT_URI, rowId);
      }
      case INSTITUTES: {
        delete(uri, null, null);
        long rowId = db.insertWithOnConflict(InstitutesContract.TABLE,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(InstitutesContract.CONTENT_URI, rowId);
      }
      case RECORDINGS: {
        long rowId = db.insertWithOnConflict(RecordingsContract.TABLE_RECORDINGS,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(RecordingsContract.CONTENT_URI, rowId);
      }
      default: {
        throw new UnsupportedOperationException("Unsupported insert uri: " + uri);
      }
    }
  }

  public static long insertIgnoringConflict(SQLiteDatabase db,
      String table,
      String idColumn,
      ContentValues values,
      boolean updateFlag) {
    try {
      return db.insertOrThrow(table, null, values);
    } catch (SQLiteConstraintException e) {
      StringBuilder sql = new StringBuilder();
      sql.append("SELECT ").append(idColumn).append(" FROM ").append(table).append(" WHERE ");

      Object[] bindArgs = new Object[values.size()];
      int i = 0;
      for (Map.Entry<String, Object> entry : values.valueSet()) {
        sql.append((i > 0) ? " AND " : "").append(entry.getKey()).append(" = ?");
        bindArgs[i++] = entry.getValue();
      }

      SQLiteStatement stmt = db.compileStatement(sql.toString());
      for (i = 0; i < bindArgs.length; i++) {
        DatabaseUtils.bindObjectToProgram(stmt, i + 1, bindArgs[i]);
      }

      long id = -1;
      try {
        id = stmt.simpleQueryForLong();
      } catch (SQLiteDoneException e2) {
      } finally {
        stmt.close();
      }

      if (updateFlag) {
        try {
          int affected = db.update(table,
              values,
              idColumn + " = ?",
              new String[]{String.format("%d", id)});
          Log.d(TAG, affected + " rows updated!");
        } catch (SQLException ex) {
          ex.printStackTrace();
        }

      }

      return id;
    }
  }

  private void deleteDatabase() {
    Context context = getContext();
    DatabaseHandler.getInstance(context).close();
    DatabaseHandler.getInstance(context).deleteDatabase();
  }

  @Override public int bulkInsert(Uri uri, ContentValues[] values) {
    SQLiteDatabase db = DatabaseHandler.getInstance(getContext())
        .getWritableDatabase(Config.PRIVATE_KEY);
    final int match = sUriMatcher.match(uri);
    int rowsInserted = 0;
    switch (match) {

      case MESSAGES_FOLDER_MESSAGES:
        long folderId = ContentUris.parseId(uri);
        db.beginTransaction();
        try {

          // first clear the messages table
          StringBuilder deleteSql = new StringBuilder();
          deleteSql.append("DELETE FROM ")
              .append(MessagesContract.TABLE_MESSAGES)
              .append(" WHERE ")
              .append(MessagesContract.Columns.Messages.MESSAGE_FOLDER_ID)
              .append(" = ?");

          // build standard insert string
          StringBuilder sqlMessage = new StringBuilder();
          sqlMessage.append("INSERT OR IGNORE INTO ")
              .append(MessagesContract.TABLE_MESSAGES)
              .append(" ( ")
              .append(MessagesContract.Columns.Messages.MESSAGE_SENDER_ID)
              .append(", ")
              .append(MessagesContract.Columns.Messages.MESSAGE_RECEIVER_ID)
              .append(", ")
              .append(MessagesContract.Columns.Messages.MESSAGE_SUBJECT)
              .append(", ")
              .append(MessagesContract.Columns.Messages.MESSAGE)
              .append(", ")
              .append(MessagesContract.Columns.Messages.MESSAGE_MKDATE)
              .append(", ")
              .append(MessagesContract.Columns.Messages.MESSAGE_PRIORITY)
              .append(", ")
              .append(MessagesContract.Columns.Messages.MESSAGE_UNREAD)
              .append(", ")
              .append(MessagesContract.Columns.Messages.MESSAGE_ID)
              .append(", ")
              .append(MessagesContract.Columns.Messages.MESSAGE_FOLDER_ID)
              .append(") VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ? ) ");

          // compile to reusable sql statement
          SQLiteStatement deleteMessages = db.compileStatement(deleteSql.toString());
          deleteMessages.bindLong(1, folderId);
          deleteMessages.execute();

          SQLiteStatement insertMessage = db.compileStatement(sqlMessage.toString());

          // set values and execute
          for (ContentValues value : values) {
            insertMessage.bindString(1,
                value.getAsString(MessagesContract.Columns.Messages.MESSAGE_SENDER_ID));
            insertMessage.bindString(2,
                value.getAsString(MessagesContract.Columns.Messages.MESSAGE_RECEIVER_ID));
            insertMessage.bindString(3,
                value.getAsString(MessagesContract.Columns.Messages.MESSAGE_SUBJECT));
            insertMessage.bindString(4,
                value.getAsString(MessagesContract.Columns.Messages.MESSAGE));
            insertMessage.bindLong(5,
                value.getAsLong(MessagesContract.Columns.Messages.MESSAGE_MKDATE));
            insertMessage.bindString(6,
                value.getAsString(MessagesContract.Columns.Messages.MESSAGE_PRIORITY));
            insertMessage.bindLong(7,
                value.getAsInteger(MessagesContract.Columns.Messages.MESSAGE_UNREAD));
            insertMessage.bindString(8,
                value.getAsString(MessagesContract.Columns.Messages.MESSAGE_ID));
            insertMessage.bindLong(9, folderId);
            insertMessage.execute();
          }

          db.setTransactionSuccessful();
          rowsInserted = values.length;

        } finally {
          db.endTransaction();
        }
        break;

      case COURSES_USERS: {
        StringBuilder insertSql = new StringBuilder();
        insertSql.append("INSERT OR IGNORE INTO ")
            .append(CoursesContract.TABLE_COURSE_USER)
            .append(" ( ")
            .append(CoursesContract.Columns.CourseUsers.COURSE_USER_COURSE_ID)
            .append(", ")
            .append(CoursesContract.Columns.CourseUsers.COURSE_USER_USER_ID)
            .append(", ")
            .append(CoursesContract.Columns.CourseUsers.COURSE_USER_USER_ROLE)
            .append(") VALUES ( ?, ?, ?) ");
        SQLiteStatement insertMessage = db.compileStatement(insertSql.toString());
        try {
          db.beginTransaction();

          String courseIdCol = CoursesContract.Columns.CourseUsers.COURSE_USER_COURSE_ID;
          String userIdCol = CoursesContract.Columns.CourseUsers.COURSE_USER_USER_ID;
          String userRoleCol = CoursesContract.Columns.CourseUsers.COURSE_USER_USER_ROLE;

          for (ContentValues value : values) {
            insertMessage.bindString(1, value.getAsString(courseIdCol));
            insertMessage.bindString(2, value.getAsString(userIdCol));
            insertMessage.bindLong(3, value.getAsLong(userRoleCol));
            insertMessage.execute();
          }

          db.setTransactionSuccessful();
          rowsInserted = values.length;

        } finally {
          insertMessage.close();
          db.endTransaction();
        }
        break;
      }
      case EVENTS_ID:
        try {
          // save column references locally to prevent lookup
          final String eventIdCol = EventsContract.Columns.EVENT_ID;
          final String eventCourseIdCol = EventsContract.Columns.EVENT_COURSE_ID;
          final String eventTitleCol = EventsContract.Columns.EVENT_TITLE;
          final String eventStartCol = EventsContract.Columns.EVENT_START;
          final String eventEndCol = EventsContract.Columns.EVENT_END;
          final String eventDescriptionCol = EventsContract.Columns.EVENT_DESCRIPTION;
          final String eventRoomCol = EventsContract.Columns.EVENT_ROOM;
          final String eventCategoriesCol = EventsContract.Columns.EVENT_CATEGORIES;

          // build delete string
          StringBuilder deleteSql = new StringBuilder();
          deleteSql.append("DELETE FROM ")
              .append(EventsContract.TABLE)
              .append(" WHERE ")
              .append(eventCourseIdCol)
              .append(" = ?");

          // build insert string
          StringBuilder insertSql = new StringBuilder();
          insertSql.append("INSERT OR IGNORE INTO ")
              .append(EventsContract.TABLE)
              .append(" ( ")
              .append(eventIdCol)
              .append(", ")
              .append(eventCourseIdCol)
              .append(", ")
              .append(eventTitleCol)
              .append(", ")
              .append(eventStartCol)
              .append(", ")
              .append(eventEndCol)
              .append(", ")
              .append(eventDescriptionCol)
              .append(", ")
              .append(eventRoomCol)
              .append(", ")
              .append(eventCategoriesCol)
              .append(") VALUES ( ?, ?, ?, ?, ?, ?, ?, ?) ");

          db.beginTransaction();
          // compile to reusable sql statement
          SQLiteStatement deleteEvents = db.compileStatement(deleteSql.toString());
          String courseId = uri.getLastPathSegment();
          deleteEvents.bindString(1, courseId);
          deleteEvents.execute();
          deleteEvents.close();

          SQLiteStatement insertEvent = db.compileStatement(insertSql.toString());
          // set values and execute
          for (ContentValues value : values) {
            insertEvent.bindString(1, value.getAsString(eventIdCol));
            insertEvent.bindString(2, value.getAsString(eventCourseIdCol));
            String val = value.getAsString(eventTitleCol);
            if (val != null) { insertEvent.bindString(3, val); }
            insertEvent.bindLong(4, value.getAsLong(eventStartCol));
            insertEvent.bindLong(5, value.getAsLong(eventEndCol));
            insertEvent.bindString(6, value.getAsString(eventDescriptionCol));
            insertEvent.bindString(7, value.getAsString(eventRoomCol));
            insertEvent.bindString(8, value.getAsString(eventCategoriesCol));
            insertEvent.execute();
          }
          insertEvent.close();

          db.setTransactionSuccessful();
          rowsInserted = values.length;

        } finally {
          db.endTransaction();
        }
        break;
      default:
        throw new UnsupportedOperationException("Unsupported bulk insert uri: " + uri);

    }

    getContext().getContentResolver().notifyChange(uri, null);
    return rowsInserted;

  }

  @Override public int delete(Uri uri, String selection, String[] selectionArgs) {
    SQLiteDatabase db = DatabaseHandler.getInstance(getContext())
        .getWritableDatabase(Config.PRIVATE_KEY);
    int retVal = -1;

    // Deleting the whole db
    if (uri == AbstractContract.BASE_CONTENT_URI) {
      deleteDatabase();
      return 1;
    }

    final int match = sUriMatcher.match(uri);
    switch (match) {
      case NEWS: {
        return db.delete(NewsContract.TABLE, selection, selectionArgs);
      }
      case USERS: {
        break;
      }
      case EVENTS: {
        break;
      }
      case COURSES: {
        retVal = db.delete(CoursesContract.TABLE_COURSES, null, null);
        db.execSQL("INSERT INTO courses (course_id, title) VALUES ('studip', 'Global')");
        break;
      }
      case SEMESTERS: {
        break;
      }
      case DOCUMENTS_COURSE_ID: {
        break;
      }
      case MESSAGES_ID: {
        long messageId = ContentUris.parseId(uri);
        retVal = db.delete(MessagesContract.TABLE_MESSAGES,
            MessagesContract.Columns.Messages._ID + " = " + messageId +
                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
            selectionArgs
        );
        break;
      }
      case MESSAGES_STRING_ID: {
        String messageId = uri.getLastPathSegment();
        retVal = db.delete(MessagesContract.TABLE_MESSAGES,
            MessagesContract.Columns.Messages.MESSAGE_ID + " = " + '"' + messageId + '"' +
                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
            selectionArgs
        );
        break;
      }
      case CONTACTS_ID: {
        String userId = uri.getLastPathSegment();
        retVal = db.delete(ContactsContract.TABLE_CONTACTS,
            ContactsContract.Columns.Contacts.USER_ID + " = " + '"' + userId + '"' +
                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
            selectionArgs
        );
        break;
      }

      case CONTACTS_GROUP_MEMBERS_GROUPID: {
        String groupId = uri.getLastPathSegment();
        retVal = db.delete(ContactsContract.TABLE_CONTACT_GROUP_MEMBERS,
            ContactsContract.Columns.ContactGroupMembers.GROUP_ID + " = " + '"' + groupId + '"' +
                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
            selectionArgs
        );
        break;
      }
      case CONTACTS: {
        retVal = db.delete(ContactsContract.TABLE_CONTACTS,
            (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
            selectionArgs);
        return retVal;
      }
      case CONTACTS_GROUPS: {
        retVal = db.delete(ContactsContract.TABLE_CONTACT_GROUPS,
            (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
            selectionArgs);
        return retVal;
      }
      case CONTACTS_GROUP_MEMBERS: {
        retVal = db.delete(ContactsContract.TABLE_CONTACT_GROUP_MEMBERS, selection, selectionArgs);
        return retVal;
      }
      case CONTACTS_GROUPS_ID: {
        String groupId = uri.getLastPathSegment();
        retVal = db.delete(ContactsContract.TABLE_CONTACT_GROUPS,
            ContactsContract.Columns.ContactGroups.GROUP_ID + " = " + '"' + groupId + '"' +
                (!TextUtils.isEmpty(selection) ? " AND (" + selection + ")" : ""),
            selectionArgs
        );
        return retVal;
      }
      case AUTHENTICATION: {
        return db.delete(AuthenticationContract.TABLE_AUTHENTICATION, selection, selectionArgs);
      }
      case INSTITUTES: {
        return db.delete(InstitutesContract.TABLE, selection, selectionArgs);
      }

      case RECORDINGS: {
        return db.delete(RecordingsContract.TABLE_RECORDINGS, selection, selectionArgs);
      }

      default: {
        throw new UnsupportedOperationException("Unsupported delete uri: " + uri);
      }
    }

    getContext().getContentResolver().notifyChange(uri, null);
    return retVal;
  }

  @Override public int update(Uri uri,
      ContentValues values,
      String selection,
      String[] selectionArgs) {
    SQLiteDatabase db = DatabaseHandler.getInstance(getContext())
        .getReadableDatabase(Config.PRIVATE_KEY);
    int affectedRows = 0;
    final int match = sUriMatcher.match(uri);

    switch (match) {
      case CONTACTS:
        affectedRows = db.update(ContactsContract.TABLE_CONTACTS, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        break;
      case MESSAGES_READ_ID:
        long messageId = ContentUris.parseId(uri);
        ContentValues val = new ContentValues();
        val.put(MessagesContract.Columns.Messages.MESSAGE_UNREAD, "0");
        affectedRows = db.update(MessagesContract.TABLE_MESSAGES,
            val,
            MessagesContract.Columns.Messages._ID + " = ?",
            new String[]{String.valueOf(messageId)});
        getContext().getContentResolver().notifyChange(uri, null);
        break;

      default:
        throw new IllegalArgumentException("Unsupported uri: " + uri);
    }
    return affectedRows;
  }

  @Override
  public ContentProviderResult[] applyBatch(ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
    final SQLiteDatabase db = DatabaseHandler.getInstance(getContext())
        .getWritableDatabase(Config.PRIVATE_KEY);
    db.beginTransaction();
    try {
      final int numOperations = operations.size();
      final ContentProviderResult[] results = new ContentProviderResult[numOperations];
      for (int i = 0; i < numOperations; i++) {
        results[i] = operations.get(i).apply(this, results, i);
      }
      db.setTransactionSuccessful();
      return results;
    } finally {
      db.endTransaction();
    }
  }

}