/*******************************************************************************
 * Copyright (c) 2013 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/
package de.elanev.studip.android.app.backend.net;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.backend.datamodel.ContactGroups;
import de.elanev.studip.android.app.backend.datamodel.Contacts;
import de.elanev.studip.android.app.backend.datamodel.Course;
import de.elanev.studip.android.app.backend.datamodel.Courses;
import de.elanev.studip.android.app.backend.datamodel.DocumentFolder;
import de.elanev.studip.android.app.backend.datamodel.DocumentFolders;
import de.elanev.studip.android.app.backend.datamodel.Events;
import de.elanev.studip.android.app.backend.datamodel.Message;
import de.elanev.studip.android.app.backend.datamodel.MessageFolders;
import de.elanev.studip.android.app.backend.datamodel.Messages;
import de.elanev.studip.android.app.backend.datamodel.News;
import de.elanev.studip.android.app.backend.datamodel.NewsItem;
import de.elanev.studip.android.app.backend.datamodel.Semester;
import de.elanev.studip.android.app.backend.datamodel.Semesters;
import de.elanev.studip.android.app.backend.datamodel.Server;
import de.elanev.studip.android.app.backend.datamodel.User;
import de.elanev.studip.android.app.backend.db.AbstractContract;
import de.elanev.studip.android.app.backend.db.ContactsContract;
import de.elanev.studip.android.app.backend.db.CoursesContract;
import de.elanev.studip.android.app.backend.db.NewsContract;
import de.elanev.studip.android.app.backend.db.SemestersContract;
import de.elanev.studip.android.app.backend.db.UsersContract;
import de.elanev.studip.android.app.backend.net.oauth.VolleyOAuthConsumer;
import de.elanev.studip.android.app.backend.net.sync.ContactGroupsHandler;
import de.elanev.studip.android.app.backend.net.sync.DocumentsHandler;
import de.elanev.studip.android.app.backend.net.sync.EventsHandler;
import de.elanev.studip.android.app.backend.net.sync.MessagesHandler;
import de.elanev.studip.android.app.backend.net.util.JacksonRequest;
import de.elanev.studip.android.app.util.Prefs;
import de.elanev.studip.android.app.util.StuffUtil;
import de.elanev.studip.android.app.util.VolleyHttp;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

/**
 * A convenience class for interacting with the rest.IP endpoints
 *
 * @author joern
 */
public class SyncHelper {
    protected static final String TAG = SyncHelper.class.getSimpleName();
    private static final long COURSES_SYNC_THRESHOLD = 3600000; // 1h
    private static final long NEWS_SYNC_THRESHOLD = 60000; // 1min
    private static final long CONTACTS_SYNC_THRESHOLD = 60000; // 1min
    private static long mLastNewsSync = 0;
    private static long mLastContactsSync = 0;
    private static long mLastCoursesSync = 0;
    private static SyncHelper mInstance;
    private static VolleyOAuthConsumer mConsumer;
    private static Server mServer;
    private static Context mContext;
    private static Set<String> mUserSyncQueue = Collections.synchronizedSet(new HashSet<String>());
    private static ArrayList<ContentProviderOperation> mUserDbOp = new
            ArrayList<ContentProviderOperation>();
    // TODO Make dependent on device connection type
    DefaultRetryPolicy mRetryPolicy = new DefaultRetryPolicy(30000,
            DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);

    private SyncHelper() {
    }

    /**
     * Returns an instance of the SyncHelper class
     *
     * @param context the execution context
     * @return an instance of the SyncHelper
     */
    public static SyncHelper getInstance(Context context) {
        if (mInstance == null)
            mInstance = new SyncHelper();

        Prefs prefs = Prefs.getInstance(context);
        if (prefs.isAppAuthorized()) {
            mServer = prefs.getServer();
            mConsumer = new VolleyOAuthConsumer(mServer.getConsumerKey(), mServer.getConsumerSecret());
            mConsumer.setTokenWithSecret(prefs.getAccessToken(), prefs.getAccessTokenSecret());
        } else {
//            throw new IllegalStateException("App must be authorized");
            StuffUtil.startSignInActivity(mContext);
        }

        mContext = context;

        return mInstance;
    }

    private static ContentProviderOperation parseNewsItem(NewsItem news, String mCourseId) {
        ContentProviderOperation.Builder builder = ContentProviderOperation
                .newInsert(NewsContract.CONTENT_URI);
        builder.withValue(NewsContract.Columns.NEWS_ID, news.news_id);
        builder.withValue(NewsContract.Columns.NEWS_TOPIC, news.topic);
        builder.withValue(NewsContract.Columns.NEWS_BODY, news.body);
        builder.withValue(NewsContract.Columns.NEWS_DATE, news.date * 1000L);
        builder.withValue(NewsContract.Columns.NEWS_USER_ID, news.user_id);
        builder.withValue(NewsContract.Columns.NEWS_CHDATE, news.chdate * 1000L);
        builder.withValue(NewsContract.Columns.NEWS_MKDATE, news.mkdate * 1000L);
        builder.withValue(NewsContract.Columns.NEWS_EXPIRE, news.expire * 1000L);
        builder.withValue(NewsContract.Columns.NEWS_ALLOW_COMMENTS,
                news.allow_comments);
        builder.withValue(NewsContract.Columns.NEWS_CHDATE_UID, news.chdate_uid);
        builder.withValue(NewsContract.Columns.NEWS_BODY_ORIGINAL,
                news.body_original);
        builder.withValue(NewsContract.Columns.NEWS_COURSE_ID, mCourseId);

        return builder.build();
    }

    private static ContentProviderOperation parseUser(User user) {
        ContentProviderOperation.Builder builder = ContentProviderOperation
                .newInsert(UsersContract.CONTENT_URI);
        builder.withValue(UsersContract.Columns.USER_ID, user.user_id);
        builder.withValue(UsersContract.Columns.USER_USERNAME, user.username);
        builder.withValue(UsersContract.Columns.USER_PERMS, user.perms);
        builder.withValue(UsersContract.Columns.USER_TITLE_PRE, user.title_pre);
        builder.withValue(UsersContract.Columns.USER_FORENAME, user.forename);
        builder.withValue(UsersContract.Columns.USER_LASTNAME, user.lastname);
        builder.withValue(UsersContract.Columns.USER_TITLE_POST,
                user.title_post);
        builder.withValue(UsersContract.Columns.USER_EMAIL, user.email);
        builder.withValue(UsersContract.Columns.USER_AVATAR_SMALL,
                user.avatar_small);
        builder.withValue(UsersContract.Columns.USER_AVATAR_MEDIUM,
                user.avatar_medium);
        builder.withValue(UsersContract.Columns.USER_AVATAR_NORMAL,
                user.avatar_normal);
        builder.withValue(UsersContract.Columns.USER_PHONE, user.phone);
        builder.withValue(UsersContract.Columns.USER_HOMEPAGE, user.homepage);
        builder.withValue(UsersContract.Columns.USER_PRIVADR, user.privadr);

        return builder.build();
    }

    private static ArrayList<ContentProviderOperation> parseSemesters(Semesters semesterList) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        for (Semester semester : semesterList.semesters) {

            ContentProviderOperation.Builder semesterBuilder = ContentProviderOperation
                    .newInsert(SemestersContract.CONTENT_URI)
                    .withValue(SemestersContract.Columns.SEMESTER_ID,
                            semester.semester_id)
                    .withValue(SemestersContract.Columns.SEMESTER_TITLE,
                            semester.title)
                    .withValue(SemestersContract.Columns.SEMESTER_DESCRIPTION,
                            semester.description)
                    .withValue(SemestersContract.Columns.SEMESTER_BEGIN,
                            semester.begin)
                    .withValue(SemestersContract.Columns.SEMESTER_END, semester.end)
                    .withValue(SemestersContract.Columns.SEMESTER_SEMINARS_BEGIN,
                            semester.seminars_begin)
                    .withValue(SemestersContract.Columns.SEMESTER_SEMINARS_END,
                            semester.seminars_end);
            ops.add(semesterBuilder.build());
        }

        return ops;
    }

    private static JacksonRequest<User> createUserRequest(String id, final SyncHelperCallbacks
            callbacks)
            throws OAuthCommunicationException, OAuthExpectationFailedException, OAuthMessageSignerException {
        final String usersUrl = String.format(
                mContext.getString(R.string.restip_users) + ".json",
                mServer.getApiUrl(),
                id);

        JacksonRequest<User> userJacksonRequest = new JacksonRequest<User>(usersUrl,
                User.class,
                null,
                new Listener<User>() {

                    public void onResponse(User response) {
                        try {
                            if (response != null &&
                                    !TextUtils.equals("____%system%____", response.user_id)) {

                                // FIXME meh modus on...
                                mUserDbOp.add(parseUser(response));
                                mContext.getContentResolver()
                                        .applyBatch(AbstractContract.CONTENT_AUTHORITY,
                                                mUserDbOp);
                                mUserDbOp.clear();
                                // meh modus off....

                                if (callbacks != null)
                                    callbacks.onSyncFinished(SyncHelperCallbacks.FINISHED_USER_SYNC);
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        } catch (OperationApplicationException e) {
                            e.printStackTrace();
                        }
                    }

                },
                new ErrorListener() {

                    public void onErrorResponse(VolleyError error) {
                        if (callbacks != null)
                            callbacks.onSyncError(SyncHelperCallbacks.ERROR_USER_SYNC, error);

                        if (error.getMessage() != null)
                            Log.wtf(TAG, error.getMessage());
                    }

                },
                Method.GET
        );

        mConsumer.sign(userJacksonRequest);

        return userJacksonRequest;
    }

    /*
     * @param courses
     * @return
     */
    private static ArrayList<ContentProviderOperation> parseCourses(Courses courses) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

        // FIXME meh^2 on.....
        for (Course c : courses.courses) {
            ContentProviderOperation.Builder builder = ContentProviderOperation
                    .newInsert(CoursesContract.CONTENT_URI)
                    .withValue(CoursesContract.Columns.Courses.COURSE_ID,
                            c.course_id)
                    .withValue(CoursesContract.Columns.Courses.COURSE_TITLE,
                            c.title)
                    .withValue(CoursesContract.Columns.Courses.COURSE_DESCIPTION,
                            c.description)
                    .withValue(CoursesContract.Columns.Courses.COURSE_SUBTITLE,
                            c.subtitle)
                    .withValue(CoursesContract.Columns.Courses.COURSE_LOCATION,
                            c.location)
                    .withValue(CoursesContract.Columns.Courses.COURSE_SEMESERT_ID,
                            c.semester_id)
                    .withValue(
                            CoursesContract.Columns.Courses.COURSE_DURATION_TIME,
                            c.duration_time)
                    .withValue(CoursesContract.Columns.Courses.COURSE_COLOR,
                            c.color)
                            // .withValue(CoursesContract.Columns.Courses.COURSE_NUMBER,
                            // c.number)
                    .withValue(CoursesContract.Columns.Courses.COURSE_TYPE, c.type)
                            // .withValue(CoursesContract.Columns.Courses.COURSE_MODULES,
                            // JSONWriter.writeValueAsString(c.modules))
                    .withValue(CoursesContract.Columns.Courses.COURSE_START_TIME,
                            c.start_time);
            operations.add(builder.build());

            for (String userId : c.teachers) {
                final ContentProviderOperation.Builder courseUserBuilder = ContentProviderOperation
                        .newInsert(CoursesContract.COURSES_USERS_CONTENT_URI)
                        .withValue(
                                CoursesContract.Columns.CourseUsers.COURSE_USER_COURSE_ID,
                                c.course_id)
                        .withValue(
                                CoursesContract.Columns.CourseUsers.COURSE_USER_USER_ID,
                                userId)
                        .withValue(
                                CoursesContract.Columns.CourseUsers.COURSE_USER_USER_ROLE,
                                CoursesContract.USER_ROLE_TEACHER);
                operations.add(courseUserBuilder.build());
            }

            for (String userId : c.tutors) {
                final ContentProviderOperation.Builder courseUserBuilder = ContentProviderOperation
                        .newInsert(CoursesContract.COURSES_USERS_CONTENT_URI)
                        .withValue(
                                CoursesContract.Columns.CourseUsers.COURSE_USER_COURSE_ID,
                                c.course_id)
                        .withValue(
                                CoursesContract.Columns.CourseUsers.COURSE_USER_USER_ID,
                                userId)
                        .withValue(
                                CoursesContract.Columns.CourseUsers.COURSE_USER_USER_ROLE,
                                CoursesContract.USER_ROLE_TUTOR);
                operations.add(courseUserBuilder.build());
            }
            for (String userId : c.students) {
                final ContentProviderOperation.Builder courseUserBuilder = ContentProviderOperation
                        .newInsert(CoursesContract.COURSES_USERS_CONTENT_URI)
                        .withValue(
                                CoursesContract.Columns.CourseUsers.COURSE_USER_COURSE_ID,
                                c.course_id)
                        .withValue(
                                CoursesContract.Columns.CourseUsers.COURSE_USER_USER_ID,
                                userId)
                        .withValue(
                                CoursesContract.Columns.CourseUsers.COURSE_USER_USER_ROLE,
                                CoursesContract.USER_ROLE_STUDENT);
                operations.add(courseUserBuilder.build());
            }

        }
        // meh^2 off...

        return operations;

    }

    private static ArrayList<ContentProviderOperation> parseContacts(Contacts contacts) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

        for (String contact : contacts.contacts) {
            ContentProviderOperation.Builder builder = ContentProviderOperation
                    .newInsert(ContactsContract.CONTENT_URI_CONTACTS);
            builder.withValue(ContactsContract.Columns.Contacts.USER_ID, contact);
            operations.add(builder.build());
        }

        return operations;
    }

    /**
     * Resets the internal SyncHelper state
     */
    public static void resetSyncHelper() {
        mLastCoursesSync = 0;
        mLastNewsSync = 0;
        mLastContactsSync = 0;
        mUserDbOp.clear();
        mUserSyncQueue.clear();
    }

    public void forcePerformContactsSync(SyncHelperCallbacks callbacks) {
        mLastContactsSync = 0;
        performContactsSync(callbacks);
    }

    /**
     * Requests new contacts and contact groups data from the API and refreshes the DB values
     *
     * @param callbacks SyncHelperCallbacks for calling back, can be null
     */
    public void performContactsSync(final SyncHelperCallbacks callbacks) {
        long currTime = System.currentTimeMillis();
        if ((currTime - mLastContactsSync) > CONTACTS_SYNC_THRESHOLD) {
            mLastContactsSync = currTime;
            Log.i(TAG, "SYNCING CONTACTS");

            final ContentResolver resolver = mContext.getContentResolver();
            final String contactsURL = String.format(mContext.getString(R.string.restip_contacts)
                    + ".json", mServer.getApiUrl());
            final String contactGroupsURL = String.format(mContext.getString(R.string.restip_contacts_groups)
                    + ".json", mServer.getApiUrl());

            // Request Contacts
            final JacksonRequest<Contacts> contactsRequest = new JacksonRequest<Contacts>(
                    contactsURL,
                    Contacts.class,
                    null,
                    new Listener<Contacts>() {
                        public void onResponse(Contacts response) {
                            try {
                                resolver.applyBatch(
                                        AbstractContract.CONTENT_AUTHORITY,
                                        parseContacts(response));

                                mUserSyncQueue.addAll(response.contacts);

                                if (callbacks != null)
                                    callbacks.onSyncFinished(SyncHelperCallbacks.FINISHED_CONTACTS_SYNC);

                                Log.i(TAG, "FINISHED SYNCING CONTACTS");
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            } catch (OperationApplicationException e) {
                                e.printStackTrace();
                            }
                        }

                    },
                    new ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            if (callbacks != null)
                                callbacks.onSyncError(SyncHelperCallbacks
                                        .ERROR_CONTACTS_SYNC, error);

                            if (error.getMessage() != null)
                                Log.wtf(TAG, error.getMessage());
                        }
                    },
                    Method.GET
            );

            // Request ContactGroups
            final JacksonRequest<ContactGroups> contactGroupsRequest = new JacksonRequest<ContactGroups>(
                    contactGroupsURL,
                    ContactGroups.class,
                    null,
                    new Listener<ContactGroups>() {
                        public void onResponse(ContactGroups response) {

                            try {
                                resolver.applyBatch(
                                        AbstractContract.CONTENT_AUTHORITY,
                                        new ContactGroupsHandler(response)
                                                .parse());


                                VolleyHttp.getVolleyHttp(mContext)
                                        .getRequestQueue()
                                        .add(contactsRequest);

                            } catch (RemoteException e) {
                                e.printStackTrace();
                            } catch (OperationApplicationException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            if (callbacks != null)
                                callbacks.onSyncError(SyncHelperCallbacks
                                        .ERROR_CONTACTS_SYNC, error);

                            if (error.getMessage() != null)
                                Log.wtf(TAG, error.getMessage());
                        }
                    },
                    Method.GET
            );

            contactsRequest.setRetryPolicy(mRetryPolicy);
            contactGroupsRequest.setRetryPolicy(mRetryPolicy);

            try {

                mConsumer.sign(contactsRequest);
                mConsumer.sign(contactGroupsRequest);
                VolleyHttp.getVolleyHttp(mContext)
                        .getRequestQueue()
                        .add(contactGroupsRequest);

                if (callbacks != null)
                    callbacks.onSyncStateChange(SyncHelperCallbacks.STARTED_CONTACTS_SYNC);

            } catch (OAuthMessageSignerException e) {
                e.printStackTrace();
            } catch (OAuthExpectationFailedException e) {
                e.printStackTrace();
            } catch (OAuthCommunicationException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Requests new courses groups data from the API and refreshes the DB values
     *
     * @param callbacks SyncHelperCallbacks for calling back, can be null
     */
    public void performCoursesSync(final SyncHelperCallbacks callbacks) {
        long currTime = System.currentTimeMillis();
        if ((currTime - mLastCoursesSync) > COURSES_SYNC_THRESHOLD) {
            mLastCoursesSync = currTime;
            Log.i(TAG, "SYNCING COURSES");
            final String coursesUrl = String.format(
                    mContext.getString(R.string.restip_courses) + ".json",
                    mServer.getApiUrl());

            JacksonRequest<Courses> coursesRequest;
            coursesRequest = new JacksonRequest<Courses>(coursesUrl,
                    Courses.class,
                    null,
                    new Listener<Courses>() {
                        public void onResponse(Courses response) {

                            try {
                                mContext.getContentResolver().applyBatch(
                                        AbstractContract.CONTENT_AUTHORITY,
                                        parseCourses(response));

                            } catch (RemoteException e) {
                                e.printStackTrace();
                            } catch (OperationApplicationException e) {
                                e.printStackTrace();
                            }

                            HashSet<String> courseIdSet = new HashSet<String>();
                            for (Course c : response.courses) {
                                mUserSyncQueue.addAll(c.teachers);
                                mUserSyncQueue.addAll(c.tutors);
                                courseIdSet.add(c.course_id);
                            }

                            // add global news to sync queue
                            courseIdSet.add(mContext.getString(R.string
                                    .restip_news_global_identifier));
                            performNewsSyncForIds(courseIdSet, callbacks);
                            if (callbacks != null)
                                callbacks.onSyncFinished(SyncHelperCallbacks.FINISHED_COURSES_SYNC);
                            Log.i(TAG, "FINISHED SYNCING COURSES");

                        }

                    },
                    new ErrorListener() {
                        public void onErrorResponse(VolleyError error) {
                            if (callbacks != null)
                                callbacks.onSyncError(SyncHelperCallbacks.ERROR_COURSES_SYNC, error);

                            if (error.getMessage() != null)
                                Log.wtf(TAG, error.getMessage());
                        }
                    },
                    Method.GET
            );

            coursesRequest.setRetryPolicy(mRetryPolicy);

            try {
                mConsumer.sign(coursesRequest);
                VolleyHttp.getVolleyHttp(mContext).getRequestQueue()
                        .add(coursesRequest);

                // Tell the listener that the course sync started
                if (callbacks != null)
                    callbacks.onSyncStateChange(SyncHelperCallbacks.STARTED_COURSES_SYNC);

            } catch (OAuthMessageSignerException e) {
                e.printStackTrace();
            } catch (OAuthExpectationFailedException e) {
                e.printStackTrace();
            } catch (OAuthCommunicationException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Requests news data for all courses from the API and refreshes the DB values
     *
     * @param callbacks SyncHelperCallbacks for calling back, can be null
     */
    public void performNewsSync(final SyncHelperCallbacks callbacks) {
        final ContentResolver resolver = mContext.getContentResolver();
        Cursor c = resolver.query(CoursesContract.CONTENT_URI,
                new String[]{CoursesContract.Columns.Courses.COURSE_ID},
                null,
                null,
                null);
        HashSet<String> courseIds = new HashSet<String>();
        c.moveToFirst();
        while (!c.isAfterLast()) {
            courseIds.add(c.getString(c.getColumnIndex(CoursesContract.Columns.Courses.COURSE_ID)
            ));

            c.moveToNext();
        }
        c.close();

        // Adding the global news range
        courseIds.add(mContext.getString(R.string.restip_news_global_identifier));
        if (!courseIds.isEmpty())
            performNewsSyncForIds(courseIds, callbacks);
    }

    /**
     * Requests new news data specified in the HashSet data from the API and refreshes the DB
     * values
     *
     * @param newsRangeIds HashSet with news range ids
     * @param callbacks    SyncHelperCallbacks for calling back, can be null
     */
    public void performNewsSyncForIds(final HashSet<String> newsRangeIds,
                                      final SyncHelperCallbacks callbacks) {
        long currTime = System.currentTimeMillis();
        if ((currTime - mLastNewsSync) > NEWS_SYNC_THRESHOLD) {
            mLastNewsSync = currTime;

            Log.i(TAG, "SYNCING NEWS");

            if (callbacks != null)
                callbacks.onSyncStateChange(SyncHelperCallbacks.STARTED_NEWS_SYNC);

            int i = 0;
            for (final String id : newsRangeIds) {
                final int finalI = i;

                requestNewsForRange(id,
                        new Listener<News>() {
                            public void onResponse(News response) {
                                try {
                                    ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
                                    for (NewsItem n : response.news) {
                                        mUserSyncQueue.add(n.user_id);
                                        operations.add(parseNewsItem(n, id));
                                    }
                                    mContext.getContentResolver().applyBatch(
                                            AbstractContract.CONTENT_AUTHORITY, operations);

                                    if (finalI == (newsRangeIds.size() - 1)) {
                                        if (callbacks != null)
                                            callbacks.onSyncFinished(SyncHelperCallbacks.FINISHED_NEWS_SYNC);
                                        Log.i(TAG, "FINISHED SYNCING NEWS");
                                    }
                                } catch (RemoteException e) {
                                    e.printStackTrace();
                                } catch (OperationApplicationException e) {
                                    e.printStackTrace();
                                }

                            }
                        },
                        null);
                i++;
            }
        }
    }

    /**
     * Performs a request for the internal stored user IDs and refreshes the DB values
     *
     * @param callbacks SyncHelperCallbacks for calling back, can be null
     */
    public void performPendingUserSync(final SyncHelperCallbacks callbacks) {
        if (!mUserSyncQueue.isEmpty()) {
            if (callbacks != null)
                callbacks.onSyncStateChange(SyncHelperCallbacks.STARTED_USER_SYNC);

            Log.i(TAG, "SYNCING PENDING USERS");

            int i = 1;
            for (String id : mUserSyncQueue) {
                final int finalI = i;
                requestUser(id,
                        new Listener<User>() {
                            @Override
                            public void onResponse(User response) {
                                mUserDbOp.add(parseUser(response));
                                if (finalI == mUserSyncQueue.size()) {

                                    Log.i(TAG, "FINISHED SYNCING PENDING USERS");
                                    try {
                                        mContext.getContentResolver().applyBatch
                                                (AbstractContract.CONTENT_AUTHORITY, mUserDbOp);
                                    } catch (RemoteException e) {
                                        e.printStackTrace();
                                    } catch (OperationApplicationException e) {
                                        e.printStackTrace();
                                    } finally {
                                        mUserDbOp.clear();
                                        mUserSyncQueue.clear();
                                        if (callbacks != null)
                                            callbacks.onSyncFinished(SyncHelperCallbacks.FINISHED_USER_SYNC);
                                    }

                                }
                            }
                        },
                        null);
                i++;
            }
        }
    }

    /**
     * Requests all users from a specfic course
     *
     * @param courseId  the ID of the course to request the users for
     * @param callbacks SyncHelperCallbacks for calling back, can be null
     */
    public void loadUsersForCourse(String courseId, SyncHelperCallbacks callbacks) {
        Cursor c = mContext.getContentResolver()
                .query(CoursesContract.CONTENT_URI.buildUpon()
                        .appendPath("userids")
                        .appendPath(courseId)
                        .build(),
                        new String[]{CoursesContract.
                                Columns.
                                CourseUsers.
                                COURSE_USER_USER_ID},
                        null,
                        null,
                        null);
        c.moveToFirst();
        while (!c.isAfterLast()) {
            String userId = c.getString(c.getColumnIndex(CoursesContract.
                    Columns.
                    CourseUsers.
                    COURSE_USER_USER_ID));

            requestUser(userId, callbacks);

            c.moveToNext();
        }

    }

    /**
     * Requests a specific user from the API if no in DB
     *
     * @param userId    the ID of the user to request from the API
     * @param callbacks SyncHelperCallbacks for calling back, can be null
     */
    public void requestUser(String userId, SyncHelperCallbacks callbacks) {
        Log.i(TAG, "SYNCING USER: " + userId);

        if (!TextUtils.equals("", userId)
                && !TextUtils.equals("____%system%____", userId)) {

            final ContentResolver resolver = mContext.getContentResolver();
            Cursor c = resolver.query(UsersContract.CONTENT_URI.buildUpon().appendPath(userId).build(),
                    new String[]{UsersContract.Columns.USER_ID},
                    null,
                    null,
                    UsersContract.DEFAULT_SORT_ORDER);
            int count = c.getCount();
            c.close();

            if (count < 1) {
                try {
                    JacksonRequest<User> userJacksonRequest = createUserRequest(userId, callbacks);
                    userJacksonRequest.setRetryPolicy(mRetryPolicy);
                    VolleyHttp
                            .getVolleyHttp(mContext)
                            .getRequestQueue()
                            .add(createUserRequest(userId, callbacks));

                    if (callbacks != null)
                        callbacks.onSyncStateChange(SyncHelperCallbacks.STARTED_USER_SYNC);

                } catch (OAuthCommunicationException e) {
                    e.printStackTrace();
                } catch (OAuthExpectationFailedException e) {
                    e.printStackTrace();
                } catch (OAuthMessageSignerException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Requests a specific user from the API if no in DB
     *
     * @param userId    the ID of the user to request from the API
     * @param listener  the VolleyListener to call when the request is finished
     * @param callbacks SyncHelperCallbacks for calling back, can be null
     */
    public void requestUser(String userId,
                            final Listener<User> listener,
                            final SyncHelperCallbacks callbacks) {

        if (!TextUtils.equals("", userId) && !TextUtils.equals("____%system%____", userId)) {
            final ContentResolver resolver = mContext.getContentResolver();
            Cursor c = resolver.query(UsersContract
                    .CONTENT_URI
                    .buildUpon()
                    .appendPath(userId)
                    .build(),
                    new String[]{UsersContract.Columns.USER_ID},
                    null,
                    null,
                    UsersContract.DEFAULT_SORT_ORDER);
            int count = c.getCount();
            c.close();

            if (count < 1) {
                final String usersUrl = String.format(
                        mContext.getString(R.string.restip_users) + ".json",
                        mServer.getApiUrl(),
                        userId);

                JacksonRequest<User> userJacksonRequest = new JacksonRequest<User>(usersUrl,
                        User.class,
                        null,
                        listener,
                        new ErrorListener() {
                            public void onErrorResponse(
                                    VolleyError error) {
                                Log.wtf(TAG, error.getMessage());
                            }
                        },
                        Method.GET
                );
                userJacksonRequest.setRetryPolicy(mRetryPolicy);

                try {
                    mConsumer.sign(userJacksonRequest);
                    VolleyHttp
                            .getVolleyHttp(mContext)
                            .getRequestQueue()
                            .add(userJacksonRequest);

                    if (callbacks != null)
                        callbacks.onSyncStateChange(SyncHelperCallbacks.STARTED_USER_SYNC);

                } catch (OAuthCommunicationException e) {
                    e.printStackTrace();
                } catch (OAuthExpectationFailedException e) {
                    e.printStackTrace();
                } catch (OAuthMessageSignerException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(TAG, "USER ALREADY EXISTS");
            }
        }
    }

    /**
     * Requests the users Semesters from the API and updates the DB values
     *
     * @param callbacks SyncHelperCallbacks for calling back, can be null
     */
    public void performSemestersSync(final SyncHelperCallbacks callbacks) {

        Log.i(TAG, "SYNCING SEMESTERS");
        final String semestersUrl = String.format(
                mContext.getString(R.string.restip_semesters) + ".json",
                mServer.getApiUrl());

        JacksonRequest<Semesters> semestersRequest = new JacksonRequest<Semesters>(
                semestersUrl,
                Semesters.class,
                null,
                new Listener<Semesters>() {
                    public void onResponse(Semesters response) {

                        try {
                            mContext.getContentResolver().applyBatch(
                                    AbstractContract.CONTENT_AUTHORITY,
                                    parseSemesters(response));

                            if (callbacks != null)
                                callbacks.onSyncFinished(SyncHelperCallbacks.FINISHED_SEMESTER_SYNC);
                            Log.i(TAG, "FINISHED SYNCING SEMESTERS");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        } catch (OperationApplicationException e) {
                            e.printStackTrace();
                        }
                    }

                },
                new ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        if (callbacks != null)
                            callbacks.onSyncError(SyncHelperCallbacks.ERROR_SEMESTER_SYNC, error);

                        if (error.getMessage() != null)
                            Log.wtf(TAG, error.getMessage());
                    }
                },
                Method.GET
        );
        semestersRequest.setRetryPolicy(mRetryPolicy);

        try {
            mConsumer.sign(semestersRequest);
            VolleyHttp.getVolleyHttp(mContext).getRequestQueue()
                    .add(semestersRequest);

            if (callbacks != null)
                callbacks.onSyncStateChange(SyncHelperCallbacks.STARTED_SEMESTER_SYNC);

        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
        }

    }

    /**
     * Requests news for a specified range and executes the passed listener with
     * the response
     *
     * @param range     the range to request
     * @param callbacks SyncHelperCallbacks for calling back, can be null
     */
    public void requestNewsForRange(final String range,
                                    final Listener<News> listener,
                                    final SyncHelperCallbacks callbacks) {

        final String newsUrl = String.format(
                mContext.getString(R.string.restip_news_rangeid) + ".json",
                mServer.getApiUrl(), range);

        JacksonRequest<News> newsRequest;

        newsRequest = new JacksonRequest<News>(newsUrl,
                News.class,
                null,
                listener,
                new ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        if (callbacks != null)
                            callbacks.onSyncError(SyncHelperCallbacks.ERROR_NEWS_SYNC, error);

                        if (error.getMessage() != null)
                            Log.wtf(TAG, error.getMessage());
                    }
                },
                Method.GET
        );
        newsRequest.setRetryPolicy(mRetryPolicy);

        try {
            mConsumer.sign(newsRequest);
            VolleyHttp.getVolleyHttp(mContext).getRequestQueue()
                    .add(newsRequest);

            if (callbacks != null)
                callbacks.onSyncStateChange(SyncHelperCallbacks.STARTED_NEWS_SYNC);

        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
        }


    }

    /**
     * Requests the events for the passed course id
     *
     * @param courseId the course id for which the events should be requested
     */
    public void performEventsSyncForCourseId(String courseId) {
        Log.i(TAG, "SYNCING COURSE EVENTS: " + courseId);
        final String eventsUrl = String.format(
                mContext.getString(R.string.restip_courses_courseid_events)
                        + ".json", mServer.getApiUrl(), courseId);
        JacksonRequest<Events> eventsRequest;
        eventsRequest = new JacksonRequest<Events>(
                eventsUrl, Events.class, null,
                new Listener<Events>() {
                    public void onResponse(Events response) {
                        try {
                            mContext.getContentResolver().applyBatch(
                                    AbstractContract.CONTENT_AUTHORITY,
                                    new EventsHandler(response).parse());
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        } catch (OperationApplicationException e) {
                            e.printStackTrace();
                        }
                    }

                }, new ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                Log.wtf(TAG, error.getMessage());
            }
        }, Method.GET
        );
        eventsRequest.setRetryPolicy(mRetryPolicy);

        try {
            mConsumer.sign(eventsRequest);
            VolleyHttp.getVolleyHttp(mContext)
                    .getRequestQueue()
                    .add(eventsRequest);
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
        }
    }

    /**
     * Requests the message folders for the passed box
     *
     * @param callbacks SyncHelperCallbacks for calling back, can be null
     */
    public void performMessagesSync(final SyncHelperCallbacks callbacks) {
        Log.i(TAG, "SYNCING MESSAGES");

        final String[] boxes = mContext.getResources().getStringArray(
                R.array.restip_messages_box_identifiers);

        if (callbacks != null)
            callbacks.onSyncStateChange(SyncHelperCallbacks.STARTED_MESSAGES_SYNC);
// TODO: Sync in- and outbox when the messages system is complete
//        for (final String box : boxes) {
        final String box = boxes[0];
        Log.i(TAG, "PERFORMING MESSAGES SYNC FOR BOX " + box);

        String boxUrl = String.format(
                mContext.getString(R.string.restip_messages_box),
                mServer.getApiUrl(),
                box);

        JacksonRequest<MessageFolders> messageFoldersRequest = new JacksonRequest<MessageFolders>(
                boxUrl,
                MessageFolders.class,
                null,
                new Listener<MessageFolders>() {
                    public void onResponse(final MessageFolders foldersResponse) {

                        for (int i = 0; i < foldersResponse.folders.size(); i++) {

                            final int finalI = i;
                            requestMessagesForFolder(i,
                                    box,
                                    callbacks,
                                    new Listener<Messages>() {
                                        public void onResponse(Messages response) {
                                            try {

                                                for (Message m : response.messages) {
                                                    mUserSyncQueue.add(m.sender_id);
                                                    mUserSyncQueue.add(m.receiver_id);
                                                }

                                                mContext.getContentResolver().applyBatch(
                                                        AbstractContract.CONTENT_AUTHORITY,
                                                        new MessagesHandler(response,
                                                                foldersResponse.folders.get(finalI),
                                                                box).parse());

                                                if (callbacks != null
                                                        && finalI == foldersResponse.folders.size() - 1) {
                                                    callbacks.onSyncFinished(SyncHelperCallbacks.FINISHED_MESSAGES_SYNC);
                                                    Log.i(TAG, "FINISHED SYNCING MESSAGES");
                                                    return;
                                                }

                                            } catch (RemoteException e) {
                                                e.printStackTrace();
                                            } catch (OperationApplicationException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                        }
                    }
                },
                new ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        if (callbacks != null)
                            callbacks.onSyncError(SyncHelperCallbacks.ERROR_MESSAGES_SYNC, error);

                        if (error.getMessage() != null)
                            Log.wtf(TAG, error.getMessage());
                    }
                },
                Method.GET
        );

        messageFoldersRequest.setRetryPolicy(mRetryPolicy);

        try {
            mConsumer.sign(messageFoldersRequest);
            VolleyHttp.getVolleyHttp(mContext)
                    .getRequestQueue()
                    .add(messageFoldersRequest);

        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
        }
//        }
    }

    /*
     * Requests the messages for the passed folder and saves them to the content
     * provider
     */
    private void requestMessagesForFolder(final int folder,
                                          final String box,
                                          final SyncHelperCallbacks callbacks,
                                          Listener<Messages> listener) {

        Log.i(TAG, "SYNCING MESSAGES FOR FOLDER " + folder);
        String folderUrl = String.format(
                mContext.getString(R.string.restip_messages_box_folderid),
                mServer.getApiUrl(),
                box,
                folder);

        JacksonRequest<Messages> messagesRequest = new JacksonRequest<Messages>(
                folderUrl,
                Messages.class,
                null,
                listener,
                new ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
                        if (callbacks != null)
                            callbacks.onSyncError(SyncHelperCallbacks
                                    .ERROR_MESSAGES_SYNC, error);

                        if (error.getMessage() != null)
                            Log.wtf(TAG, error.getMessage());
                    }
                }, Method.GET
        );

        try {
            messagesRequest.setRetryPolicy(mRetryPolicy);

            mConsumer.sign(messagesRequest);
            VolleyHttp.getVolleyHttp(mContext)
                    .getRequestQueue()
                    .add(messagesRequest);

        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        }

    }

    /**
     * Performs a sync of the folders for the passed course
     *
     * @param courseId course id to perform the sync for
     */
    public void performDocumentsSyncForCourse(final String courseId) {
        Log.i(TAG, "PERFORMING DOCUMENTS SYNC FOR COURSE " + courseId);

        String foldersUrl = String.format(mContext
                .getString(R.string.restip_documents_rangeid_folder),
                mServer.getApiUrl(), courseId)
                + ".json";

        JacksonRequest<DocumentFolders> messagesRequest = new JacksonRequest<DocumentFolders>(
                foldersUrl, DocumentFolders.class, null,
                new Listener<DocumentFolders>() {
                    public void onResponse(DocumentFolders response) {
                        if (!response.documents.isEmpty())
                            try {
                                mContext.getContentResolver().applyBatch(
                                        AbstractContract.CONTENT_AUTHORITY,
                                        new DocumentsHandler(
                                                response.documents,
                                                courseId).parse());
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            } catch (OperationApplicationException e) {
                                e.printStackTrace();
                            }

                        for (DocumentFolder folder : response.folders) {
                            requestDocumentsForFolder(folder, courseId);
                        }

                    }

                }, new ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() != null)
                    Log.wtf(TAG, error.getMessage());
            }
        }, Method.GET
        );

        messagesRequest.setRetryPolicy(mRetryPolicy);

        try {
            mConsumer.sign(messagesRequest);
            VolleyHttp.getVolleyHttp(mContext)
                    .getRequestQueue()
                    .add(messagesRequest);
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        }
    }

    /*
     * Requests a folder and recursive the subfolders
     */
    private void requestDocumentsForFolder(final DocumentFolder folder,
                                           final String courseId) {
        Log.i(TAG, "PERFORMING DOCUMENTS SYNC FOR COURSE " + courseId
                + " Folder: " + folder.folder_id);

        String foldersUrl = String.format(
                mContext.getString(R.string.restip_documents_rangeid_folder_folderid),
                mServer.getApiUrl(), courseId, folder.folder_id)
                + ".json";
        JacksonRequest<DocumentFolders> messagesRequest = new JacksonRequest<DocumentFolders>(
                foldersUrl, DocumentFolders.class, null,
                new Listener<DocumentFolders>() {
                    public void onResponse(DocumentFolders response) {
                        if (!response.documents.isEmpty())
                            try {
                                mContext.getContentResolver().applyBatch(
                                        AbstractContract.CONTENT_AUTHORITY,
                                        new DocumentsHandler(
                                                response.documents,
                                                courseId, folder).parse());
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            } catch (OperationApplicationException e) {
                                e.printStackTrace();
                            }

                        for (DocumentFolder folder : response.folders) {
                            // Recursive request the subfolders
                            requestDocumentsForFolder(folder, courseId);
                        }

                    }

                }, new ErrorListener() {
            public void onErrorResponse(VolleyError error) {
                if (error.getMessage() != null)
                    Log.wtf(TAG, error.getMessage());
            }
        }, Method.GET
        );

        messagesRequest.setRetryPolicy(mRetryPolicy);

        try {
            mConsumer.sign(messagesRequest);
            VolleyHttp.getVolleyHttp(mContext)
                    .getRequestQueue()
                    .add(messagesRequest);

        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        }
    }

    /**
     * Callback interface for clients to interact with the SyncHelper
     */
    public interface SyncHelperCallbacks {
        public static final int STARTED_COURSES_SYNC = 101;
        public static final int STARTED_NEWS_SYNC = 102;
        public static final int STARTED_SEMESTER_SYNC = 103;
        public static final int STARTED_CONTACTS_SYNC = 104;
        public static final int STARTED_MESSAGES_SYNC = 105;
        public static final int STARTED_USER_SYNC = 106;
        public static final int FINISHED_COURSES_SYNC = 201;
        public static final int FINISHED_NEWS_SYNC = 202;
        public static final int FINISHED_SEMESTER_SYNC = 203;
        public static final int FINISHED_CONTACTS_SYNC = 204;
        public static final int FINISHED_MESSAGES_SYNC = 205;
        public static final int FINISHED_USER_SYNC = 206;
        public static final int ERROR_COURSES_SYNC = 301;
        public static final int ERROR_NEWS_SYNC = 302;
        public static final int ERROR_SEMESTER_SYNC = 303;
        public static final int ERROR_CONTACTS_SYNC = 304;
        public static final int ERROR_MESSAGES_SYNC = 305;
        public static final int ERROR_USER_SYNC = 306;

        public void onSyncStarted();

        public void onSyncStateChange(int status);

        public void onSyncFinished(int status);

        public void onSyncError(int status, VolleyError error);

    }

}


/*
 * The favorite group feature needs some more refinement
 */
//    private boolean favoritesGroupExisting(ContactGroups groups) {
//        String favGroupName = mContext
//                .getString(R.string.studip_app_contacts_favorites);
//        for (ContactGroup group : groups.groups) {
//            if (TextUtils.equals(group.name, favGroupName))
//                return true;
//        }
//        return false;
//    }
//
//    private void createFavoritesGroup() {
//        final String contactGroupsURL = String.format(
//                mContext.getString(R.string.restip_contacts_groups) + ".json",
//                mServer.API_URL);
//        // Create Jackson HTTP post request
//        JacksonRequest<ContactGroups> request = new JacksonRequest<ContactGroups>(
//                contactGroupsURL, ContactGroups.class, null,
//                new Listener<ContactGroups>() {
//
//                    public void onResponse(ContactGroups response) {
//                        try {
//                            mContext.getContentResolver().applyBatch(
//                                    AbstractContract.CONTENT_AUTHORITY,
//                                    new ContactGroupsHandler(response).parse());
//                        } catch (RemoteException e) {
//                            e.printStackTrace();
//                        } catch (OperationApplicationException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new ErrorListener() {
//            /*
//             * (non-Javadoc)
//             *
//             * @see com.android.volley.Response. ErrorListener
//             * #onErrorResponse(com .android.volley. VolleyError)
//             */
//            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(mContext,
//                        "Fehler: " + error.getMessage(),
//                        Toast.LENGTH_SHORT).show();
//            }
//        }, Method.POST
//        );
//
//        // Set parameters
//        request.addParam("name",
//                mContext.getString(R.string.studip_app_contacts_favorites));
//
//        // Sign request
//        try {
//            mConsumer.sign(request);
//        } catch (OAuthMessageSignerException e) {
//            e.printStackTrace();
//        } catch (OAuthExpectationFailedException e) {
//            e.printStackTrace();
//        } catch (OAuthCommunicationException e) {
//            e.printStackTrace();
//        }
//
//        // Add request to HTTP request queue
//        VolleyHttp.getVolleyHttp(mContext).getRequestQueue().add(request);
//    }