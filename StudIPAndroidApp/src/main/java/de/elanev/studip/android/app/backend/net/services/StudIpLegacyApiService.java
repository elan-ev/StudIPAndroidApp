package de.elanev.studip.android.app.backend.net.services;

import android.content.Context;
import android.database.Cursor;
import android.util.Pair;

import org.apache.http.HttpStatus;

import java.util.ArrayList;

import de.elanev.studip.android.app.BuildConfig;
import de.elanev.studip.android.app.backend.datamodel.Course;
import de.elanev.studip.android.app.backend.datamodel.CourseItem;
import de.elanev.studip.android.app.backend.datamodel.DocumentFolders;
import de.elanev.studip.android.app.backend.datamodel.Event;
import de.elanev.studip.android.app.backend.datamodel.Events;
import de.elanev.studip.android.app.backend.datamodel.ForumArea;
import de.elanev.studip.android.app.backend.datamodel.ForumAreas;
import de.elanev.studip.android.app.backend.datamodel.ForumCategories;
import de.elanev.studip.android.app.backend.datamodel.ForumCategory;
import de.elanev.studip.android.app.backend.datamodel.ForumEntries;
import de.elanev.studip.android.app.backend.datamodel.ForumEntry;
import de.elanev.studip.android.app.backend.datamodel.Recording;
import de.elanev.studip.android.app.backend.datamodel.Server;
import de.elanev.studip.android.app.backend.datamodel.Settings;
import de.elanev.studip.android.app.backend.datamodel.User;
import de.elanev.studip.android.app.backend.datamodel.UserItem;
import de.elanev.studip.android.app.backend.db.UsersContract;
import retrofit.Callback;
import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.JacksonConverter;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;
import se.akerfeldt.signpost.retrofit.RetrofitHttpOAuthConsumer;
import se.akerfeldt.signpost.retrofit.SigningOkClient;

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
    RetrofitHttpOAuthConsumer oAuthConsumer = new RetrofitHttpOAuthConsumer(server.getConsumerKey(),
        server.getConsumerSecret());
    oAuthConsumer.setTokenWithSecret(server.getAccessToken(), server.getAccessTokenSecret());

    // Set log request log level based on BuildConfig
    RestAdapter.LogLevel logLevel = (BuildConfig.DEBUG)
        ? RestAdapter.LogLevel.FULL
        : RestAdapter.LogLevel.BASIC;

    // Set up the RestAdapter with the appropriate configuration options
    RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(server.getApiUrl())
        .setLogLevel(logLevel)
        .setConverter(new JacksonConverter())
        .setClient(new SigningOkClient(oAuthConsumer))
        .setErrorHandler(new ErrorHandler() {
          @Override public Throwable handleError(RetrofitError cause) {
            if (cause != null) {
              Response response = cause.getResponse();
              if (response != null && response.getUrl()
                  .contains("user") && cause.getResponse()
                  .getStatus() == HttpStatus.SC_NOT_FOUND) {
                return new UserNotFoundException(cause);
              }
            }
            return cause;
          }
        })
        .build();

    mService = restAdapter.create(RestIPLegacyService.class);
    mContext = context.getApplicationContext();
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
            return Observable.just(course.course.getAdditionalData()
                .getRecordings());
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
  //endregion --------------------------------------------------------------------------------------

  //region INTERFACES ------------------------------------------------------------------------------
  public interface RestIPLegacyService {
    /*
     * Forums
     */
    @PUT("/courses/{course_id}/set_forum_read") void setForumRead(
        @Path("course_id") String courseId, Callback<ForumCategory> cb);

    @GET("/courses/{course_id}/forum_categories") Observable<ForumCategories> getForumCategories(
        @Path("course_id") String courseId);

    @GET("/forum_category/{category_id}/areas") Observable<ForumAreas> getForumAreas(@Path(
        "category_id") String categoryId, @Query("offset") int offset, @Query("limit") int limit);

    @GET("/forum_entry/{topic_id}/children") Observable<ForumEntries> getForumTopicEntries(@Path(
        "topic_id") String topicId, @Query("offset") int offset, @Query("limit") int limit);

    @FormUrlEncoded @POST("/forum_entry/{topic_id}") Observable<ForumArea> createForumEntry(@Path(
        "topic_id") String topicId, @Field("subject") String entrySubject,
        @Field("content") String entryContent);

    /*
     * User specific
     */
    @GET("/user/{user_id}") Observable<UserItem> getUser(@Path("user_id") String userId);

    @GET("/user") Observable<UserItem> getCurrentUserInfo();

    @GET("/events") Observable<Events> getEvents();

    /*
     * Generally Stud.IP specifix
     */
    @GET("/studip/settings") Observable<Settings> getSettings();

    /*
     * Course specific
     */
    @GET("/courses/{course_id}") Observable<CourseItem> getCourse(
        @Path("course_id") String courseId);

    @GET("/documents/{course_id}/folder") Observable<DocumentFolders> getCourseDocuments(
        @Path("course_id") String courseId);

    @GET("/documents/{course_id}/folder/{folder_id}") Observable<DocumentFolders> getCourseDocumentsFolders(
        @Path("course_id") String courseId, @Path("folder_id") String folderId);
  }
  //endregion --------------------------------------------------------------------------------------

  //region INNER CLASSES ---------------------------------------------------------------------------
  public static class UserNotFoundException extends RuntimeException {
    RetrofitError cause;

    UserNotFoundException(RetrofitError cause) {
      this.cause = cause;
    }
  }
  //endregion --------------------------------------------------------------------------------------
}