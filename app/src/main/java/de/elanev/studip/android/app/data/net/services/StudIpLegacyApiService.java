/*
 * Copyright (c) 2016 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.data.net.services;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.util.Pair;

import java.util.ArrayList;

import de.elanev.studip.android.app.BuildConfig;
import de.elanev.studip.android.app.StudIPConstants;
import de.elanev.studip.android.app.data.datamodel.ContactGroups;
import de.elanev.studip.android.app.data.datamodel.Contacts;
import de.elanev.studip.android.app.data.datamodel.Course;
import de.elanev.studip.android.app.data.datamodel.CourseItem;
import de.elanev.studip.android.app.data.datamodel.Courses;
import de.elanev.studip.android.app.data.datamodel.DocumentFolders;
import de.elanev.studip.android.app.data.datamodel.Event;
import de.elanev.studip.android.app.data.datamodel.Events;
import de.elanev.studip.android.app.data.datamodel.ForumArea;
import de.elanev.studip.android.app.data.datamodel.ForumAreas;
import de.elanev.studip.android.app.data.datamodel.ForumCategories;
import de.elanev.studip.android.app.data.datamodel.ForumCategory;
import de.elanev.studip.android.app.data.datamodel.ForumEntries;
import de.elanev.studip.android.app.data.datamodel.ForumEntry;
import de.elanev.studip.android.app.data.datamodel.Institutes;
import de.elanev.studip.android.app.data.datamodel.InstitutesContainer;
import de.elanev.studip.android.app.data.datamodel.Message;
import de.elanev.studip.android.app.data.datamodel.MessageFolder;
import de.elanev.studip.android.app.data.datamodel.MessageFolders;
import de.elanev.studip.android.app.data.datamodel.MessageItem;
import de.elanev.studip.android.app.data.datamodel.Messages;
import de.elanev.studip.android.app.data.datamodel.News;
import de.elanev.studip.android.app.data.datamodel.Postbox;
import de.elanev.studip.android.app.data.datamodel.Recording;
import de.elanev.studip.android.app.data.datamodel.Semesters;
import de.elanev.studip.android.app.data.datamodel.Server;
import de.elanev.studip.android.app.data.datamodel.Settings;
import de.elanev.studip.android.app.data.datamodel.User;
import de.elanev.studip.android.app.data.datamodel.UserItem;
import de.elanev.studip.android.app.data.db.UsersContract;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;
import se.akerfeldt.okhttp.signpost.SigningInterceptor;


/**
 * API service providing methodes to interact with the legacy implementation of the Stud.IP REST
 * API aka. Rest.IP
 *
 * @author joern
 */
public class StudIpLegacyApiService {

  //region LOCAL CONSTANTS -------------------------------------------------------------------------
  public static final String TAG = StudIpLegacyApiService.class.getSimpleName();
  //endregion --------------------------------------------------------------------------------------

  //region INSTANCE VARIABLES ----------------------------------------------------------------------
  private RestIPLegacyService mService;
  private Context mContext;
  //endregion --------------------------------------------------------------------------------------

  //region CONSTRUCTOR
  // -----------------------------------------------------------------------------------------------
  public StudIpLegacyApiService(Server server, Context context) {

    Retrofit retrofit = getRetrofit(server);

    // Create an instance of our RestIPLegacyService API interface.
    mService = retrofit.create(RestIPLegacyService.class);

    // Other initializations
    mContext = context.getApplicationContext();
  }

  public static Retrofit getRetrofit(Server server) {
    // Begin building the OkHttp3 client
    OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();

    // Create OkHttp3 SignPost interceptor and add it to the OkHttp3 client
    OkHttpOAuthConsumer oAuthConsumer = new OkHttpOAuthConsumer(server.getConsumerKey(),
        server.getConsumerSecret());
    oAuthConsumer.setTokenWithSecret(server.getAccessToken(), server.getAccessTokenSecret());
    clientBuilder.addInterceptor(new SigningInterceptor(oAuthConsumer));

    // Set log request log level based on BuildConfig
    HttpLoggingInterceptor.Level logLevel = (BuildConfig.DEBUG)
        ? HttpLoggingInterceptor.Level.BODY
        : HttpLoggingInterceptor.Level.BASIC;

    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
    logging.setLevel(logLevel);
    clientBuilder.addInterceptor(logging);

    // Add the necessary RestIpApiErrorInterceptor
    clientBuilder.addInterceptor(new RestIpErrorInterceptor());

    // Begin creating the Retrofit2 client
    Retrofit.Builder retrofitBuilder = new Retrofit.Builder();

    // Add API URL, JacksonConverter and the previously created OkHttp3 client
    retrofitBuilder.baseUrl(server.getApiUrl())
        .addConverterFactory(JacksonConverterFactory.create())
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .client(clientBuilder.build());

    // Build Retrofit
    return retrofitBuilder.build();
  }
  //endregion --------------------------------------------------------------------------------------

  //region PUBLIC API ------------------------------------------------------------------------------

  /**
   * Takes a course id as parameter and returns an {@link Observable} containing all {@link
   * ForumCategory} for the course identified by the passed course id.
   *
   * @param courseId String of the course id to load the {@link ForumCategory} for.
   * @return An {@link Observable} for all {@link ForumCategory} corresponding to the course id.
   */
  public Observable<ForumCategory> getForumCategories(final String courseId) {
    return mService.getForumCategories(courseId)
        .flatMap(new Func1<ForumCategories, Observable<? extends ForumCategory>>() {
          @Override public Observable<? extends ForumCategory> call(
              ForumCategories forumCategories) {
            return Observable.from(forumCategories.forumCategories);
          }
        });
  }

  /**
   * Takes an id of a forum category and a offset. It returns an {@link Observable} for every
   * {@link ForumArea} corresponding to the forum category id and is within the range selected by
   * the offset. By default 10 items from the offset on are returned.
   *
   * @param forumCategoryId The String id of the forum category to load the forum areas for.
   * @param offset          An int which indicates from where to load the next 10 items from.
   * @return An {@link Observable} for all {@link ForumArea} corresponding to the forum category
   * id and is within the range of the offset.
   */
  public Observable<ForumAreas> getForumAreas(final String forumCategoryId, final int offset) {
    return mService.getForumAreas(forumCategoryId, offset, 10);
  }

  /**
   * Takes a topic id and an offset as argument. The offset indicates the starting point from
   * where to load the next 10 entries form.
   *
   * @param topicId String topic id identifing the topic the load the entries from.
   * @param offset  int value indicating the offset from where the next 10 entries are loaded
   * @return An {@link Observable} for every {@link ForumEntry} corresponding to the passed
   * parameters.
   */
  public Observable<ForumEntry> getForumTopicEntries(final String topicId, final int offset) {
    return mService.getForumTopicEntries(topicId, offset, 10)
        .flatMap(new Func1<ForumEntries, Observable<? extends ForumEntry>>() {
          @Override public Observable<? extends ForumEntry> call(ForumEntries forumEntries) {

            return Observable.from(forumEntries.entries);
          }
        })
        .flatMap(new Func1<ForumEntry, Observable<ForumEntry>>() {
          @Override public Observable<ForumEntry> call(ForumEntry entry) {
            return Observable.zip(Observable.just(entry), getUser(entry.userId),
                new Func2<ForumEntry, User, ForumEntry>() {
                  @Override public ForumEntry call(ForumEntry entry, User user) {
                    entry.user = user;
                    return entry;
                  }
                });
          }
        });
  }

  /**
   * Takes a user id as argument and returns an {@link Observable} wrapping the user correspondig
   * to the {@link User}.
   *
   * @param userId String id identifying the user to load the info for.
   * @return An {@link Observable} wrapping an {@link User} object corresponding to the passed
   * user id.
   */
  public Observable<User> getUser(final String userId) {
    User u = getUserFromContentProvider(userId);
    if (u != null) {
      return Observable.just(u);
    }

    return mService.getUser(userId)
        .flatMap(new Func1<UserItem, Observable<? extends User>>() {
          @Override public Observable<? extends User> call(UserItem userItem) {
            return Observable.just(userItem.user);
          }
        })
        .onErrorReturn(new Func1<Throwable, User>() {
          @Override public User call(Throwable throwable) {
            return new User(null, null, null, null, "Deleted", "User", null, null, null, null, null,
                null, null, null, 0);
          }
        });
  }

  private User getUserFromContentProvider(final String userId) {

    String[] projection = {
        UsersContract.Columns.USER_TITLE_PRE,
        UsersContract.Columns.USER_FORENAME,
        UsersContract.Columns.USER_LASTNAME,
        UsersContract.Columns.USER_TITLE_POST,
        UsersContract.Columns.USER_AVATAR_NORMAL
    };
    String selection = UsersContract.Columns.USER_ID + " = ?";

    Cursor cursor = mContext.getContentResolver()
        .query(UsersContract.CONTENT_URI, projection, selection, new String[]{userId},
            UsersContract.DEFAULT_SORT_ORDER);

    String userTitlePre = "";
    String userTitlePost = "";
    String userForename = "";
    String userLastname = "";
    String userAvatarUrl = "";

    if (cursor != null) {

      cursor.moveToFirst();
      if (cursor.isAfterLast()) {
        cursor.close();
        return null;
      }

      userTitlePre = cursor.getString(cursor.getColumnIndex(UsersContract.Columns.USER_TITLE_PRE));
      userTitlePost = cursor.getString(
          cursor.getColumnIndex(UsersContract.Columns.USER_TITLE_POST));
      userForename = cursor.getString(cursor.getColumnIndex(UsersContract.Columns.USER_FORENAME));
      userLastname = cursor.getString(cursor.getColumnIndex(UsersContract.Columns.USER_LASTNAME));
      userAvatarUrl = cursor.getString(
          cursor.getColumnIndex(UsersContract.Columns.USER_AVATAR_NORMAL));

      cursor.close();
    }
    return new User(userId, null, null, userTitlePre, userForename, userLastname, userTitlePost,
        null, null, null, userAvatarUrl, null, null, null, 0);
  }

  /**
   * Creates a new forum entry for a specific forum topic and returns the new {@link ForumEntry}
   * wrapped in an {@link Observable}.
   *
   * @param topicId      String with the id of the topic under which the new entry should be created.
   * @param entrySubject The subject of the new entry as a String.
   * @param entryContent A String with the content of the new entry.
   * @return An {@link Observable} wrapping the newly created {@link ForumEntry}.
   */
  public Observable<ForumArea> createForumEntry(final String topicId, final String entrySubject,
      final String entryContent) {
    return mService.createForumEntry(topicId, entrySubject, entryContent);
  }

  /**
   * Marks the whole forum of a course as read. With the legacy API, this is currently the only way
   * to mark anything in the forum as read. It takes a String containing the course id and a
   * callback, which is called when the action was completed.
   *
   * @param courseId String containing the course id to mark the forum for as read.
   * @param callback A {@link Callback} which is executed upon completion of the action.
   */
  public void setForumRead(final String courseId, final Callback<ForumCategory> callback) {
    mService.setForumRead(courseId, callback);
  }

  /**
   * Gets various settings of the API and the underlying Stud.IP installation.
   *
   * @return An {@link Observable} wrapping a {@link Settings} object containing various setting
   * information.
   */
  public Observable<Settings> getSettings() {
    return mService.getSettings();
  }

  /**
   * Get the OpenCast Matterhorn recordings for a specific course. This is a Stud.IP plugin and
   * is not available everywhere.
   *
   * @param courseId String with the course id to load the OC recordings for.
   * @return An {@link Observable} wrapping an ArrayList containing a list of {@link Recording}
   * objects corresponding to the passed course id.
   */
  public Observable<ArrayList<Recording>> getRecordings(String courseId) {
    Observable<CourseItem> courseObservable = mService.getCourse(courseId);
    Observable<ArrayList<Recording>> recordingsObservable = courseObservable.flatMap(
        new Func1<CourseItem, Observable<ArrayList<Recording>>>() {
          @Override public Observable<ArrayList<Recording>> call(CourseItem course) {
            if (course != null && course.course != null && course.course.getAdditionalData() !=
                null) {
              return Observable.just(course.course.getAdditionalData()
                  .getRecordings());
            } else {
              return Observable.empty();
            }
          }
        });

    return recordingsObservable;
  }

  /**
   * Gets information of the currently signed in user.
   *
   * @return An {@link User} wrapped in qn {@link Observable} containing information about the
   * currently signed in user.
   */
  public Observable<User> getCurrentUserInfo() {
    return mService.getCurrentUserInfo()
        .flatMap(new Func1<UserItem, Observable<? extends User>>() {
          @Override public Observable<? extends User> call(UserItem userItem) {
            return Observable.just(userItem.user);
          }
        });
  }

  /**
   * Get all {@link DocumentFolders} for the course id passed as an argument. The {@link
   * DocumentFolders} object contains more folders as well es the {@link de.elanev.studip.android
   * .app.backend.datamodel.Documents} of the current folder.
   *
   * @param courseId A course id String identifying the course for which the folders should be
   *                 requested.
   * @return An {@link Observable} containing the {@link DocumentFolders} of the requested course.
   */
  public Observable<DocumentFolders> getCourseDocuments(String courseId) {
    return mService.getCourseDocuments(courseId);
  }

  /**
   * Get the subfolder of a give folder and course id. After getting the first course folders
   * with {@link #getCourseDocuments(String)} the folder hierarchy can be traversed deeper with
   * this method.
   *
   * @param courseId A course id String identifying the course for which the folders should be
   *                 requested.
   * @param folderId The id of the current folder from which the subfolder are requested.
   * @return An {@link Observable} containing the {@link DocumentFolders} of the requested course
   * and folder.
   */
  public Observable<DocumentFolders> getCourseDocumentsFolders(String courseId, String folderId) {
    return mService.getCourseDocumentsFolders(courseId, folderId);
  }

  /**
   * Requests the events planned for the user. The time span is set by the API to two weeks.
   *
   * @return An {@link Observable} containing the user's {@link Events} for the next two weeks.
   */
  public Observable<Pair<Event, Course>> getEvents() {
    // First get the events
    return mService.getEvents()
        // Then unwrap the events
        .flatMap(new Func1<Events, Observable<Event>>() {
          @Override public Observable<Event> call(Events events) {
            return Observable.from(events.events);
          }
        })
        // Then for every event get the course and emit it as Pair
        .flatMap(new Func1<Event, Observable<Pair<Event, Course>>>() {
          @Override public Observable<Pair<Event, Course>> call(Event event) {

            // Create Observable for course
            final Observable<Course> courseObservable = mService.getCourse(event.course_id)
                .flatMap(new Func1<CourseItem, Observable<Course>>() {
                  @Override public Observable<Course> call(CourseItem courseItem) {
                    return Observable.just(courseItem.course);
                  }
                });

            // Zip courseObservable and the event to emit the Pair of them.
            return Observable.zip(Observable.just(event), courseObservable,
                new Func2<Event, Course, Pair<Event, Course>>() {
                  @Override public Pair<Event, Course> call(Event event, Course course) {
                    return new Pair<>(event, course);
                  }
                });
          }
        });
  }

  public Observable<Events> getEvents(final String courseId) {
    return mService.getEvents(courseId);
  }

  /**
   * Gets the message folders from Stud.IP.
   *
   * @param offset The pagination offset
   * @param limit  The limit of entries until it paginates.
   * @return An {@link Observable} containing a list of the users {@link MessageFolders}.
   */
  public Observable<Postbox> getMessageFolders(int offset, int limit) {
    Observable<MessageFolders> inboxObservable = mService.getMessageInbox(offset, limit)
        .flatMap(new Func1<MessageFolders, Observable<MessageFolders>>() {
          @Override public Observable<MessageFolders> call(MessageFolders messageFolders) {
            messageFolders.boxType = StudIPConstants.STUDIP_MESSAGES_INBOX_IDENTIFIER;
            return Observable.just(messageFolders);
          }
        });

    Observable<MessageFolders> outboxObservable = mService.getMessageOutbox(offset, limit)
        .flatMap(new Func1<MessageFolders, Observable<MessageFolders>>() {
          @Override public Observable<MessageFolders> call(MessageFolders messageFolders) {
            messageFolders.boxType = StudIPConstants.STUDIP_MESSAGES_OUTBOX_IDENTIFIER;
            return Observable.just(messageFolders);
          }
        });

    return Observable.zip(inboxObservable, outboxObservable,
        new Func2<MessageFolders, MessageFolders, Postbox>() {
          @Override public Postbox call(MessageFolders inbox, MessageFolders outbox) {
            return new Postbox(inbox, outbox);
          }
        });
  }

  /**
   * Get the users {@link Messages} in the specified Stud.IP messages inbox folder.
   *
   * @param folder The ID of the inbox {@link MessageFolder}
   * @param offset Offset number of the message pagination.
   * @param limit  The limit of entries until it paginates.
   * @return An {@link Observable} containing {@link Messages} from the specified inbox folder.
   */
  public Observable<Pair<Message, User>> getInboxMessages(String folder, int offset, int limit) {
    return mService.getMessagesInboxFolder(folder, offset, limit)
        // Unwrap message
        .flatMap(new Func1<Messages, Observable<Message>>() {
          @Override public Observable<Message> call(Messages messages) {
            return Observable.from(messages.messages);
          }
        })

        // For each message, load the user for senderId
        .flatMap(new Func1<Message, Observable<Pair<Message, User>>>() {
          @Override public Observable<Pair<Message, User>> call(Message message) {

            // Zip the message and user observables together
            return Observable.zip(Observable.just(message), getUser(message.senderId),
                new Func2<Message, User, Pair<Message, User>>() {

                  // Function to zip them together
                  @Override public Pair<Message, User> call(Message message, User user) {
                    return new Pair<>(message, user);
                  }
                });
          }
        });
  }

  /**
   * Get the users {@link Messages} in the specified Stud.IP messages outbox folder.
   *
   * @param folder The ID of the outbox {@link MessageFolder}
   * @param offset Offset number of the message pagination.
   * @param limit  The limit of entries until it paginates.
   * @return An {@link Observable} containing {@link Messages} from the specified outbox folder.
   */
  public Observable<Pair<Message, User>> getOutboxMessages(String folder, int offset, int limit) {
    return mService.getMessagesOutboxFolder(folder, offset, limit)
        // Unwrap message
        .flatMap(new Func1<Messages, Observable<Message>>() {
          @Override public Observable<Message> call(Messages messages) {
            return Observable.from(messages.messages);
          }
        })

        // For each message, load the user for senderId
        .flatMap(new Func1<Message, Observable<Pair<Message, User>>>() {
          @Override public Observable<Pair<Message, User>> call(Message message) {

            // Zip the message and user observables together
            return Observable.zip(Observable.just(message), getUser(message.senderId),
                new Func2<Message, User, Pair<Message, User>>() {

                  // Function to zip them together
                  @Override public Pair<Message, User> call(Message message, User user) {
                    return new Pair<>(message, user);
                  }
                });
          }
        });
  }

  /**
   * Marks the specified message as read.
   *
   * @param messageId The ID of the message to be marked as read.
   * @return Nothing
   */
  public Observable<Void> setMessageRead(final String messageId) {
    return mService.setMessageRead(messageId);
  }

  /**
   * Sends a message to the user specified by the receiverId.
   *
   * @param receiverId The ID of the user to send the message to.
   * @param subject    A String as subject for the message.
   * @param message    The message String.
   * @return The newly created {@link Message}
   */
  public Observable<MessageItem> sendMessage(final String receiverId, final String subject,
      final String message) {
    return mService.sendMessage(receiverId, subject, message);
  }

  /**
   * Deletes the specified message from the users message box.
   *
   * @param messageId The id of the message to delete.
   * @return Nothing
   */
  public Observable<Void> deleteMessage(final String messageId) {
    return mService.deleteMessage(messageId);
  }

  /**
   * Gets the {@link Institutes} the user specified by the users ID is registered in.
   *
   * @param userId The users ID for whom to load the {@link Institutes} for.
   * @return A List {@link Institutes}
   */
  public Observable<InstitutesContainer> getInstitutes(final String userId) {
    return mService.getInstitutes(userId);
  }

  /**
   * Gets the {@link Contacts} of the user.
   *
   * @return A list of {@link Contacts}
   */
  public Observable<Contacts> getContacts() {
    return mService.getContacts();
  }

  /**
   * Gets the {@link ContactGroups} of the user.
   *
   * @return A list of {@link ContactGroups}
   */
  public Observable<ContactGroups> getContactGroups() {
    return mService.getContactGroups();
  }

  /**
   * Adds a specified user to the group specified by the group id.
   *
   * @param groupId The id of the group the user shall be added to.
   * @param userId  The id of the user to add to the group.
   * @return The refreshed list of contacts groups.
   */
  public Observable<ContactGroups> addUserToContactsGroup(final String groupId,
      final String userId) {
    return mService.addUserToContactsGroup(groupId, userId);
  }

  /**
   * Deletes a specified user from the group specified by the passed group id.
   *
   * @param groupId The id of the group the user shall be deleted from.
   * @param userId  The id of the user which shall be deleted from the group.
   * @return Nothing
   */
  public Observable<Void> deleteUserFromContactsGroup(final String groupId, final String userId) {
    return mService.deleteUserFormContactsGroup(groupId, userId);
  }

  /**
   * Adds a specified user to the contacts.
   *
   * @param userId The id of the user which shall be added to the contacts.
   * @return The new contacts list.
   */
  public Observable<Contacts> addUserToContacts(final String userId) {
    return mService.addUserToContatcs(userId);
  }

  public Observable<Void> deleteUserFromContacts(final String userId) {
    return mService.deleteUserFromContacts(userId);
  }

  /**
   * Gets a list of the users {@link Courses}.
   *
   * @return A list of the users {@link Courses}.
   */
  public Observable<Courses> getCourses() {
    return mService.getCourses();
  }

  public Observable<News> getNews(final String range) {
    return mService.getNews(range);
  }

  public Observable<Semesters> getSemesters() {
    return mService.getSemesters();
  }
  //endregion --------------------------------------------------------------------------------------

  //region INTERFACES ------------------------------------------------------------------------------
  public interface RestIPLegacyService {
    /* Forums */
    @PUT("courses/{course_id}/set_forum_read") void setForumRead(@Path("course_id") String courseId,
        Callback<ForumCategory> cb);

    @GET("courses/{course_id}/forum_categories") Observable<ForumCategories> getForumCategories(
        @Path("course_id") String courseId);

    @GET("forum_category/{category_id}/areas") Observable<ForumAreas> getForumAreas(@Path(
        "category_id") String categoryId, @Query("offset") int offset, @Query("limit") int limit);

    @GET("forum_entry/{topic_id}/children") Observable<ForumEntries> getForumTopicEntries(@Path(
        "topic_id") String topicId, @Query("offset") int offset, @Query("limit") int limit);

    @FormUrlEncoded @POST("forum_entry/{topic_id}") Observable<ForumArea> createForumEntry(@Path(
        "topic_id") String topicId, @Field("subject") String entrySubject,
        @Field("content") String entryContent);

    /* User */
    @GET("user/{user_id}") Observable<UserItem> getUser(@Path("user_id") String userId);

    @GET("user") Observable<UserItem> getCurrentUserInfo();

    /* Events */
    @GET("events") Observable<Events> getEvents();

    @GET("events/{course_id}") Observable<Events> getEvents(@Path("course_id") String courseId);

    /* General */
    @GET("studip/settings") Observable<Settings> getSettings();

    /* Courses */
    @GET("courses/{course_id}") Observable<CourseItem> getCourse(
        @Path("course_id") String courseId);

    @GET("documents/{course_id}/folder") Observable<DocumentFolders> getCourseDocuments(
        @Path("course_id") String courseId);

    @GET("documents/{course_id}/folder/{folder_id}") Observable<DocumentFolders> getCourseDocumentsFolders(
        @Path("course_id") String courseId, @Path("folder_id") String folderId);

    /* Messages */
    @GET("messages/in") Observable<MessageFolders> getMessageInbox(@Query("offset") int offset,
        @Query("limit") int limit);

    @GET("messages/out") Observable<MessageFolders> getMessageOutbox(@Query("offset") int offset,
        @Query("limit") int limit);

    @GET("messages/in/{folder_id}") Observable<Messages> getMessagesInboxFolder(
        @Path("folder_id") String folderId, @Query("offset") int offset, @Query("limit") int limit);

    @GET("messages/out/{folder_id}") Observable<Messages> getMessagesOutboxFolder(
        @Path("folder_id") String folderId, @Query("offset") int offset, @Query("limit") int limit);

    @PUT("messages/{message_id}/read") Observable<Void> setMessageRead(
        @Path("message_id") String messageId);

    @FormUrlEncoded @POST("messages") Observable<MessageItem> sendMessage(
        @Field("user_id") String receiverId, @Field("subject") String subject,
        @Field("message") String message);

    @DELETE("messages/{message_id}") Observable<Void> deleteMessage(
        @Path("message_id") String messageId);

    //TODO: Add unread counter to messages
    //@GET("messages") Observable<MessagesStats> getMessagesStats();

    /* Institutes */
    @GET("user/{user_id}/institutes") Observable<InstitutesContainer> getInstitutes(
        @Path("user_id") String userId);

    /* Contacts */
    @GET("contacts") Observable<Contacts> getContacts();

    @GET("contacts/groups") Observable<ContactGroups> getContactGroups();

    @PUT("contacts/groups/{group_id}/{user_id}") Observable<ContactGroups> addUserToContactsGroup(
        @Path("group_id") String groupId, @Path("user_id") String userId);

    @DELETE("contacts/groups/{group_id}/{user_id}") Observable<Void> deleteUserFormContactsGroup(
        @Path("group_id") String groupId, @Path("user_id") String userId);

    @DELETE("contacts/{user_id}") Observable<Void> deleteUserFromContacts(
        @Path("user_id") String userId);

    @PUT("contacts/{user_id}") Observable<Contacts> addUserToContatcs(
        @Path("user_id") String userId);

    /* Courses */
    @GET("courses") Observable<Courses> getCourses();

    /* News */
    @GET("news/range/{range}") Observable<News> getNews(@Path("range") String range);

    /* Semesters */
    @GET("semesters") Observable<Semesters> getSemesters();
  }
  //endregion --------------------------------------------------------------------------------------

  //region INNER CLASSES ---------------------------------------------------------------------------

  //endregion --------------------------------------------------------------------------------------
}