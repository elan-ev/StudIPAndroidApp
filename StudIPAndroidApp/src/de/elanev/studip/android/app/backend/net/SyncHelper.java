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
import android.os.AsyncTask;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import de.elanev.studip.android.app.BuildConfig;
import de.elanev.studip.android.app.R;
import de.elanev.studip.android.app.StudIPApplication;
import de.elanev.studip.android.app.backend.datamodel.ContactGroups;
import de.elanev.studip.android.app.backend.datamodel.Contacts;
import de.elanev.studip.android.app.backend.datamodel.Course;
import de.elanev.studip.android.app.backend.datamodel.Courses;
import de.elanev.studip.android.app.backend.datamodel.DocumentFolder;
import de.elanev.studip.android.app.backend.datamodel.DocumentFolders;
import de.elanev.studip.android.app.backend.datamodel.Events;
import de.elanev.studip.android.app.backend.datamodel.Institutes;
import de.elanev.studip.android.app.backend.datamodel.InstitutesContainer;
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
import de.elanev.studip.android.app.backend.db.InstitutesContract;
import de.elanev.studip.android.app.backend.db.NewsContract;
import de.elanev.studip.android.app.backend.db.SemestersContract;
import de.elanev.studip.android.app.backend.db.UsersContract;
import de.elanev.studip.android.app.backend.net.oauth.OAuthConnector;
import de.elanev.studip.android.app.backend.net.sync.ContactGroupsHandler;
import de.elanev.studip.android.app.backend.net.sync.DocumentsHandler;
import de.elanev.studip.android.app.backend.net.sync.EventsHandler;
import de.elanev.studip.android.app.backend.net.sync.MessagesHandler;
import de.elanev.studip.android.app.backend.net.util.JacksonRequest;
import de.elanev.studip.android.app.util.Prefs;
import de.elanev.studip.android.app.util.StuffUtil;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;
import oauth.signpost.exception.OAuthNotAuthorizedException;

/**
 * A convenience class for interacting with the rest.IP endpoints
 *
 * @author joern
 */
public class SyncHelper {
    public static final String TAG = SyncHelper.class.getSimpleName();
    private static long mLastNewsSync = 0;
    private static long mLastContactsSync = 0;
    private static long mLastCoursesSync = 0;
    private static SyncHelper mInstance;
    private static Server mServer;
    private static Context mContext;

    private static LoadingCache<String, User> sUsersCache;

    private static Set<String> mUserSyncQueue = Collections
            .synchronizedSet(new HashSet<String>());
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
        if (mInstance == null) {
            mInstance = new SyncHelper();

            mServer = Prefs.getInstance(context).getServer();
            sUsersCache = CacheBuilder.newBuilder()
                    .maximumSize(1000)
                    .expireAfterWrite(5, TimeUnit.MINUTES)
                    .build(new CacheLoader<String, User>() {
                        @Override
                        public User load(String key) throws Exception {
                            return dbQueryForUser(key);
                        }
                    });
        }
        mContext = context;

        return mInstance;
    }

    private static User dbQueryForUser(String userId) {
        Cursor c = mContext.getContentResolver()
                .query(UsersContract.CONTENT_URI,
                        new String[]{UsersContract.Columns.USER_ID},
                        UsersContract.Columns.USER_ID + " = ?",
                        new String[]{userId},
                        UsersContract.Columns.USER_ID
                );

        if (c.getCount() > 0) {
            User user = new User();

            c.moveToFirst();
            user.user_id = c.getString(0);
            c.close();
            return user;
        } else {
            c.close();
            return null;
        }


    }

    private static ContentProviderOperation parseNewsItem(NewsItem news, String mCourseId) {
        ContentProviderOperation.Builder builder = ContentProviderOperation
                .newInsert(NewsContract.CONTENT_URI);
        builder.withValue(NewsContract.Columns.NEWS_ID, news.news_id);
        builder.withValue(NewsContract.Columns.NEWS_TOPIC, news.topic);
        builder.withValue(NewsContract.Columns.NEWS_BODY, news.body);
        builder.withValue(NewsContract.Columns.NEWS_DATE, news.date);
        builder.withValue(NewsContract.Columns.NEWS_USER_ID, news.user_id);
        builder.withValue(NewsContract.Columns.NEWS_CHDATE, news.chdate);
        builder.withValue(NewsContract.Columns.NEWS_MKDATE, news.mkdate);
        builder.withValue(NewsContract.Columns.NEWS_EXPIRE, news.expire);
        builder.withValue(NewsContract.Columns.NEWS_ALLOW_COMMENTS,
                news.allow_comments);
        builder.withValue(NewsContract.Columns.NEWS_CHDATE_UID, news.chdate_uid);
        builder.withValue(NewsContract.Columns.NEWS_BODY_ORIGINAL,
                news.body_original);
        builder.withValue(NewsContract.Columns.NEWS_RANGE_ID, mCourseId);

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

    private static ArrayList<ContentProviderOperation> parseInstitutes
            (Institutes institutes) {
        ArrayList<ContentProviderOperation> ops = new
                ArrayList<ContentProviderOperation>();
        for (Institutes.Institute i : institutes.getStudy()) {
            ContentProviderOperation.Builder instituteBuilder =
                    ContentProviderOperation.newInsert(InstitutesContract
                            .CONTENT_URI)
                            .withValue(InstitutesContract.Columns.INSTITUTE_ID,
                                    i.getInstituteId())
                            .withValue(InstitutesContract.Columns.INSTITUTE_NAME,
                                    i.getName())
                            .withValue(InstitutesContract.Columns.INSTITUTE_PERMS,
                                    i.getPerms())
                            .withValue(InstitutesContract.Columns.INSTITUTE_CONSULTATION,
                                    i.getConsultation())
                            .withValue(InstitutesContract.Columns.INSTITUTE_ROOM,
                                    i.getRoom())
                            .withValue(InstitutesContract.Columns.INSTITUTE_PHONE,
                                    i.getPhone())
                            .withValue(InstitutesContract.Columns.INSTITUTE_FAX,
                                    i.getFax())
                            .withValue(InstitutesContract.Columns.INSTITUTE_STREET,
                                    i.getStreet())
                            .withValue(InstitutesContract.Columns.INSTITUTE_CITY,
                                    i.getCity())
                            .withValue(InstitutesContract.Columns.INSTITUTE_FACULTY_NAME,
                                    i.getFacultyName())
                            .withValue(InstitutesContract.Columns.INSTITUTE_FACULTY_STREET,
                                    i.getFacultyStreet())
                            .withValue(InstitutesContract.Columns
                                            .INSTITUTE_FACULTY_CITY,
                                    i.getFacultyCity()
                            );
            ops.add(instituteBuilder.build());
        }

        for (Institutes.Institute i : institutes.getWork()) {
            ContentProviderOperation.Builder instituteBuilder =
                    ContentProviderOperation.newInsert(InstitutesContract
                            .CONTENT_URI)
                            .withValue(InstitutesContract.Columns.INSTITUTE_ID,
                                    i.getInstituteId())
                            .withValue(InstitutesContract.Columns.INSTITUTE_NAME,
                                    i.getName())
                            .withValue(InstitutesContract.Columns.INSTITUTE_PERMS,
                                    i.getPerms())
                            .withValue(InstitutesContract.Columns.INSTITUTE_CONSULTATION,
                                    i.getConsultation())
                            .withValue(InstitutesContract.Columns.INSTITUTE_ROOM,
                                    i.getRoom())
                            .withValue(InstitutesContract.Columns.INSTITUTE_PHONE,
                                    i.getPhone())
                            .withValue(InstitutesContract.Columns.INSTITUTE_FAX,
                                    i.getFax())
                            .withValue(InstitutesContract.Columns.INSTITUTE_STREET,
                                    i.getStreet())
                            .withValue(InstitutesContract.Columns.INSTITUTE_CITY,
                                    i.getCity())
                            .withValue(InstitutesContract.Columns.INSTITUTE_FACULTY_NAME,
                                    i.getFacultyName())
                            .withValue(InstitutesContract.Columns.INSTITUTE_FACULTY_STREET,
                                    i.getFacultyStreet())
                            .withValue(InstitutesContract.Columns
                                            .INSTITUTE_FACULTY_CITY,
                                    i.getFacultyCity()
                            );
            ops.add(instituteBuilder.build());
        }

        return ops;
    }

    private static JacksonRequest<User> createUserRequest(String id, final SyncHelperCallbacks
            callbacks)
            throws OAuthCommunicationException, OAuthExpectationFailedException, OAuthMessageSignerException, OAuthNotAuthorizedException {
        final String usersUrl = String.format(
                mContext.getString(R.string.restip_user_id) + ".json",
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
                                sUsersCache.put(response.user_id, response);

                                //FIXME: Add to userDbOp cache and execute
                                // the whole bunch at once
                                mUserDbOp.add(parseUser(response));
                                mContext.getContentResolver()
                                        .applyBatch(AbstractContract.CONTENT_AUTHORITY,
                                                mUserDbOp);
                                mUserDbOp.clear();


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

        OAuthConnector.with(mServer).sign(userJacksonRequest);

        return userJacksonRequest;
    }

    private static ArrayList<ContentProviderOperation> parseCourses(Courses courses) {
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();

        operations.add(ContentProviderOperation
                .newDelete(CoursesContract.CONTENT_URI)
                .build());

        // FIXME meh^2 on....
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
        sUsersCache.invalidateAll();
    }

    public void requestInstitutesForUserID(String userId, final SyncHelperCallbacks
            callbacks) {
        String institutesUrl = String.format(
                mContext.getString(R.string.restip_user_institutes),
                mServer.getApiUrl(),
                userId
        );

        JacksonRequest<InstitutesContainer> institutesRequest = new JacksonRequest<InstitutesContainer>(
                institutesUrl,
                InstitutesContainer.class,
                null,
                new Listener<InstitutesContainer>() {
                    @Override
                    public void onResponse(InstitutesContainer response) {
                        try {
                            mContext.getContentResolver()
                                    .applyBatch(
                                            AbstractContract.CONTENT_AUTHORITY,
                                            parseInstitutes(response.getInstitutes())
                                    );

                            if (callbacks != null) {
                                callbacks.onSyncFinished(
                                        SyncHelperCallbacks.FINISHED_INSTITUTES_SYNC
                                );
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        } catch (OperationApplicationException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (callbacks != null) {
                            callbacks.onSyncError(
                                    SyncHelperCallbacks.ERROR_INSTITUTES_SYNC,
                                    error
                            );
                        }
                    }
                },
                Method.GET
        );

        institutesRequest.setRetryPolicy(mRetryPolicy);
        institutesRequest.setPriority(Request.Priority.NORMAL);

        try {

            OAuthConnector.with(mServer)
                    .sign(institutesRequest);
            StudIPApplication.getInstance()
                    .addToRequestQueue(institutesRequest, TAG);

            if (callbacks != null) {
                callbacks.onSyncStateChange(SyncHelperCallbacks.STARTED_INSTITUTES_SYNC);
            }

        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        } catch (OAuthNotAuthorizedException e) {
            StuffUtil.startSignInActivity(mContext);
        }

    }

    public void forcePerformContactsSync(SyncHelperCallbacks callbacks) {
        mLastContactsSync = 0;
        performContactsSync(callbacks, Request.Priority.IMMEDIATE);
    }

    /**
     * Requests new contacts and contact groups data from the API and refreshes the DB values
     *
     * @param callbacks SyncHelperCallbacks for calling back, can be null
     */
    public void performContactsSync(final SyncHelperCallbacks callbacks, Request.Priority priority) {
        long currTime = System.currentTimeMillis();
        if ((currTime - mLastContactsSync) > BuildConfig
                .CONTACTS_SYNC_THRESHOLD) {
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

                                if (callbacks != null) {
                                    callbacks.onSyncFinished(SyncHelperCallbacks.FINISHED_CONTACTS_SYNC);
                                }

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
                                                .parse()
                                );


                                StudIPApplication.getInstance()
                                        .addToRequestQueue(contactsRequest, TAG);

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
            if (priority != null) {
                contactGroupsRequest.setPriority(priority);
                contactsRequest.setPriority(priority);
            }

            try {

                OAuthConnector.with(mServer).sign(contactsRequest);
                OAuthConnector.with(mServer).sign(contactGroupsRequest);
                StudIPApplication.getInstance().addToRequestQueue(contactGroupsRequest, TAG);

                if (callbacks != null)
                    callbacks.onSyncStateChange(SyncHelperCallbacks.STARTED_CONTACTS_SYNC);

            } catch (OAuthMessageSignerException e) {
                e.printStackTrace();
            } catch (OAuthExpectationFailedException e) {
                e.printStackTrace();
            } catch (OAuthCommunicationException e) {
                e.printStackTrace();
            } catch (OAuthNotAuthorizedException e) {
                StuffUtil.startSignInActivity(mContext);
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
        if ((currTime - mLastCoursesSync) > BuildConfig
                .COURSES_SYNC_THRESHOLD) {
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

                            for (Course c : response.courses) {
                                mUserSyncQueue.addAll(c.teachers);
                            }

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
                OAuthConnector.with(mServer).sign(coursesRequest);
                StudIPApplication.getInstance().addToRequestQueue(coursesRequest, TAG);

                // Tell the listener that the course sync started
                if (callbacks != null)
                    callbacks.onSyncStateChange(SyncHelperCallbacks.STARTED_COURSES_SYNC);

            } catch (OAuthMessageSignerException e) {
                e.printStackTrace();
            } catch (OAuthExpectationFailedException e) {
                e.printStackTrace();
            } catch (OAuthCommunicationException e) {
                e.printStackTrace();
            } catch (OAuthNotAuthorizedException e) {
                StuffUtil.startSignInActivity(mContext);
            }
        }

    }

    /**
     * Requests news data for all courses from the API and refreshes the DB values
     *
     * @param callbacks SyncHelperCallbacks for calling back, can be null
     */
    public void performNewsSync(final SyncHelperCallbacks callbacks) {
        long currTime = System.currentTimeMillis();
        if ((currTime - mLastNewsSync) > BuildConfig.NEWS_SYNC_THRESHOLD) {
            mLastNewsSync = currTime;
            final ContentResolver resolver = mContext.getContentResolver();

            Cursor c = resolver.query(
                    CoursesContract.CONTENT_URI,
                    new String[]{CoursesContract.Columns.Courses.COURSE_ID},
                    null,
                    null,
                    null
            );

            HashSet<String> rangeIds = new HashSet<String>();
            c.moveToFirst();

            while (!c.isAfterLast()) {
                rangeIds.add(c.getString(0));

                c.moveToNext();
            }
            c.close();

            c = resolver.query(
                    InstitutesContract.CONTENT_URI,
                    new String[]{InstitutesContract.Columns.INSTITUTE_ID},
                    null,
                    null,
                    null
            );

            c.moveToFirst();
            while (!c.isAfterLast()) {
                rangeIds.add(c.getString(0));

                c.moveToNext();
            }
            c.close();

            rangeIds.add(mContext.getString(R.string.restip_news_global_identifier));
            performNewsSyncForIds(rangeIds, callbacks);

            //TODO: Delete old news from database
        }
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
                                    //TODO: TEST
                                    mUserSyncQueue.add(n.user_id);
                                    requestUser(n.user_id, null);
                                    operations.add(parseNewsItem(n, id));
                                }
                                if (!operations.isEmpty()) {
                                    mContext.getContentResolver().applyBatch(
                                            AbstractContract.CONTENT_AUTHORITY, operations);
                                }

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
                    callbacks
            );
            i++;
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
                        null
                );
                i++;
            }
        } else {
            if (callbacks != null) {
                callbacks.onSyncFinished(SyncHelperCallbacks.FINISHED_USER_SYNC);
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

        new UserLoadTask().execute(new Object[]{courseId, callbacks});

    }

    /**
     * Requests a specific user from the API if no in DB
     *
     * @param userId    the ID of the user to request from the API
     * @param callbacks SyncHelperCallbacks for calling back, can be null
     */
    public void requestUser(String userId, SyncHelperCallbacks callbacks) {
//        Log.i(TAG, "SYNCING USER: " + userId);

        if (!TextUtils.equals("", userId)
                && !TextUtils.equals("____%system%____", userId)) {
            try {
                sUsersCache.get(userId);
//                    Log.i(TAG, "!!!!!USER CACHE HIT!!!!!");
            } catch (CacheLoader.InvalidCacheLoadException exception) {

                try {
                    JacksonRequest<User> userJacksonRequest = createUserRequest(userId, callbacks);
                    userJacksonRequest.setRetryPolicy(mRetryPolicy);
                    StudIPApplication.getInstance()
                            .addToRequestQueue(createUserRequest(userId, callbacks), TAG);

                    if (callbacks != null)
                        callbacks.onSyncStateChange(SyncHelperCallbacks.STARTED_USER_SYNC);
                } catch (OAuthCommunicationException e) {
                    e.printStackTrace();
                } catch (OAuthExpectationFailedException e) {
                    e.printStackTrace();
                } catch (OAuthMessageSignerException e) {
                    e.printStackTrace();
                } catch (OAuthNotAuthorizedException e) {
                    StuffUtil.startSignInActivity(mContext);
                }

            } catch (ExecutionException exception) {
                exception.printStackTrace();
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
            try {
                sUsersCache.get(userId);
//                    Log.i(TAG, "!!!!!USER CACHE HIT!!!!!");
            } catch (CacheLoader.InvalidCacheLoadException exception) {

                try {
                    final String usersUrl = String.format(
                            mContext.getString(R.string.restip_user_id) + ".json",
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
                                    if (callbacks != null) {
                                        callbacks.onSyncError(SyncHelperCallbacks
                                                .ERROR_USER_SYNC, error);
                                    }
                                }
                            },
                            Method.GET
                    );
                    userJacksonRequest.setRetryPolicy(mRetryPolicy);
                    userJacksonRequest.setPriority(Request.Priority.LOW);


                    OAuthConnector.with(mServer).sign(userJacksonRequest);
                    StudIPApplication.getInstance().addToRequestQueue(userJacksonRequest, TAG);

                    if (callbacks != null)
                        callbacks.onSyncStateChange(SyncHelperCallbacks.STARTED_USER_SYNC);

                } catch (OAuthCommunicationException e) {
                    e.printStackTrace();
                } catch (OAuthExpectationFailedException e) {
                    e.printStackTrace();
                } catch (OAuthMessageSignerException e) {
                    e.printStackTrace();
                } catch (OAuthNotAuthorizedException e) {
                    StuffUtil.startSignInActivity(mContext);
                }

            } catch (ExecutionException exception) {
                exception.printStackTrace();
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
            OAuthConnector.with(mServer).sign(semestersRequest);
            StudIPApplication.getInstance().addToRequestQueue(semestersRequest, TAG);

            if (callbacks != null)
                callbacks.onSyncStateChange(SyncHelperCallbacks.STARTED_SEMESTER_SYNC);

        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
        } catch (OAuthNotAuthorizedException e) {
            StuffUtil.startSignInActivity(mContext);
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
        Log.i(TAG, "Performing Sync for range: " + range);
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
            OAuthConnector.with(mServer).sign(newsRequest);
            StudIPApplication.getInstance().addToRequestQueue(newsRequest, TAG);

            if (callbacks != null)
                callbacks.onSyncStateChange(SyncHelperCallbacks.STARTED_NEWS_SYNC);

        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
        } catch (OAuthNotAuthorizedException e) {
            StuffUtil.startSignInActivity(mContext);
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
                        + ".json", mServer.getApiUrl(), courseId
        );
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
        eventsRequest.setPriority(Request.Priority.IMMEDIATE);
        try {
            OAuthConnector.with(mServer).sign(eventsRequest);
            StudIPApplication.getInstance().addToRequestQueue(eventsRequest, TAG);
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
        } catch (OAuthNotAuthorizedException e) {
            StuffUtil.startSignInActivity(mContext);
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
                                                    if (callbacks != null) {
                                                        mUserSyncQueue.add(m.sender_id);
                                                        mUserSyncQueue.add(m.receiver_id);
                                                    } else {
                                                        requestUser(m
                                                                        .sender_id,
                                                                null
                                                        );
                                                        requestUser(m
                                                                        .receiver_id,
                                                                null
                                                        );
                                                    }
                                                }

                                                mContext.getContentResolver().applyBatch(
                                                        AbstractContract.CONTENT_AUTHORITY,
                                                        new MessagesHandler(response,
                                                                foldersResponse.folders.get(finalI),
                                                                box).parse()
                                                );

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
                                    }
                            );
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
        messageFoldersRequest.setPriority(Request.Priority.IMMEDIATE);
        try {
            OAuthConnector.with(mServer).sign(messageFoldersRequest);
            StudIPApplication.getInstance().addToRequestQueue(messageFoldersRequest, TAG);

        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
        } catch (OAuthNotAuthorizedException e) {
            StuffUtil.startSignInActivity(mContext);
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
            messagesRequest.setPriority(Request.Priority.IMMEDIATE);
            OAuthConnector.with(mServer).sign(messagesRequest);
            StudIPApplication.getInstance().addToRequestQueue(messagesRequest, TAG);

        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        } catch (OAuthNotAuthorizedException e) {
            StuffUtil.startSignInActivity(mContext);
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
                mServer.getApiUrl(), courseId
        )
                + ".json";

        JacksonRequest<DocumentFolders> documentFoldersRequest = new JacksonRequest<DocumentFolders>(
                foldersUrl, DocumentFolders.class, null,
                new Listener<DocumentFolders>() {
                    public void onResponse(DocumentFolders response) {
                        if (!response.documents.isEmpty())
                            try {
                                mContext.getContentResolver().applyBatch(
                                        AbstractContract.CONTENT_AUTHORITY,
                                        new DocumentsHandler(
                                                response.documents,
                                                courseId).parse()
                                );
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

        documentFoldersRequest.setRetryPolicy(mRetryPolicy);
        documentFoldersRequest.setPriority(Request.Priority.IMMEDIATE);

        try {
            OAuthConnector.with(mServer).sign(documentFoldersRequest);
            StudIPApplication.getInstance().addToRequestQueue(documentFoldersRequest, TAG);
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        } catch (OAuthNotAuthorizedException e) {
            StuffUtil.startSignInActivity(mContext);
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
        JacksonRequest<DocumentFolders> documentRequest = new JacksonRequest<DocumentFolders>(
                foldersUrl, DocumentFolders.class, null,
                new Listener<DocumentFolders>() {
                    public void onResponse(DocumentFolders response) {
                        if (!response.documents.isEmpty())
                            try {
                                mContext.getContentResolver().applyBatch(
                                        AbstractContract.CONTENT_AUTHORITY,
                                        new DocumentsHandler(
                                                response.documents,
                                                courseId, folder).parse()
                                );
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

        documentRequest.setRetryPolicy(mRetryPolicy);
        documentRequest.setPriority(Request.Priority.IMMEDIATE);

        try {
            OAuthConnector.with(mServer).sign(documentRequest);
            StudIPApplication.getInstance().addToRequestQueue(documentRequest, TAG);
        } catch (OAuthExpectationFailedException e) {
            e.printStackTrace();
        } catch (OAuthCommunicationException e) {
            e.printStackTrace();
        } catch (OAuthMessageSignerException e) {
            e.printStackTrace();
        } catch (OAuthNotAuthorizedException e) {
            StuffUtil.startSignInActivity(mContext);
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
        public static final int STARTED_INSTITUTES_SYNC = 107;
        public static final int FINISHED_COURSES_SYNC = 201;
        public static final int FINISHED_NEWS_SYNC = 202;
        public static final int FINISHED_SEMESTER_SYNC = 203;
        public static final int FINISHED_CONTACTS_SYNC = 204;
        public static final int FINISHED_MESSAGES_SYNC = 205;
        public static final int FINISHED_USER_SYNC = 206;
        public static final int FINISHED_INSTITUTES_SYNC = 207;
        public static final int ERROR_COURSES_SYNC = 301;
        public static final int ERROR_NEWS_SYNC = 302;
        public static final int ERROR_SEMESTER_SYNC = 303;
        public static final int ERROR_CONTACTS_SYNC = 304;
        public static final int ERROR_MESSAGES_SYNC = 305;
        public static final int ERROR_USER_SYNC = 306;
        public static final int ERROR_INSTITUTES_SYNC = 307;

        public void onSyncStarted();

        public void onSyncStateChange(int status);

        public void onSyncFinished(int status);

        public void onSyncError(int status, VolleyError error);

    }

    private class UserLoadTask extends AsyncTask<Object, Void, Void> {

        @Override
        protected Void doInBackground(Object... params) {

            String courseId = (String) params[0];
            SyncHelperCallbacks callbacks = (SyncHelperCallbacks) params[1];

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
                            null
                    );
            c.moveToFirst();

            while (!c.isAfterLast()) {
                String userId = c.getString(c.getColumnIndex(CoursesContract.
                        Columns.
                        CourseUsers.
                        COURSE_USER_USER_ID));

                requestUser(userId, callbacks);

                c.moveToNext();
            }
            c.close();

            return null;
        }
    }

}