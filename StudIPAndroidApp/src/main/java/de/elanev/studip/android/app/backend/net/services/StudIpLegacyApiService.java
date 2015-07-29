package de.elanev.studip.android.app.backend.net.services;

import android.content.Context;
import android.database.Cursor;

import com.fasterxml.jackson.databind.JsonNode;

import org.apache.http.HttpStatus;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.elanev.studip.android.app.backend.datamodel.ForumArea;
import de.elanev.studip.android.app.backend.datamodel.ForumAreas;
import de.elanev.studip.android.app.backend.datamodel.ForumCategories;
import de.elanev.studip.android.app.backend.datamodel.ForumCategory;
import de.elanev.studip.android.app.backend.datamodel.ForumEntries;
import de.elanev.studip.android.app.backend.datamodel.ForumEntry;
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
 * @author joern
 */
public class StudIpLegacyApiService {

  private RestIPLegacyService mService;
  private Context mContext;

  public StudIpLegacyApiService(Server server, Context context) {
    //        RequestInterceptor requestInterceptor = new RequestInterceptor() {
    //          @Override public void intercept(RequestFacade request) {
    //            request.
    //          }
    //        };
    //    // TODO: unwrap root elements for messages, users and courses
    //    // TODO: Auth
    //        ObjectMapper mapper = new ObjectMapper();
    //        if (clazz.equals(User.class) || clazz.equals(Message.class) || clazz.equals(Course
    //            .class)) {
    //          mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
    //        }

    RetrofitHttpOAuthConsumer oAuthConsumer = new RetrofitHttpOAuthConsumer(server.getConsumerKey(),
        server.getConsumerSecret());
    oAuthConsumer.setTokenWithSecret(server.getAccessToken(), server.getAccessTokenSecret());

    RestAdapter restAdapter = new RestAdapter.Builder().setEndpoint(server.getApiUrl())
        //        .setRequestInterceptor(requestInterceptor)
        .setLogLevel(RestAdapter.LogLevel.FULL)
        .setConverter(new JacksonConverter())
        .setClient(new SigningOkClient(oAuthConsumer))
        .setErrorHandler(new ErrorHandler() {
          @Override public Throwable handleError(RetrofitError cause) {
            Response response = cause.getResponse();
            if (response.getUrl().contains("user")
                && cause.getResponse().getStatus() == HttpStatus.SC_NOT_FOUND) {
              return new UserNotFoundException(cause);
            }
            return cause;
          }
        })
        .build();

    mService = restAdapter.create(RestIPLegacyService.class);
    mContext = context.getApplicationContext();
  }

  public Observable<ForumCategory> getForumCategories(final String courseId) {
    return mService.getForumCategories(courseId)
        .flatMap(new Func1<ForumCategories, Observable<? extends ForumCategory>>() {
          @Override public Observable<? extends ForumCategory> call(ForumCategories forumCategories) {
            return Observable.from(forumCategories.forumCategories);
          }
        });
  }

  public Observable<ForumAreas> getForumAreas(final String categoryId, int offset) {
    return mService.getForumAreas(categoryId, offset, 10);
  }

  public Observable<ForumEntry> getForumEntry(final String entryId) {
    return mService.getForumEntry(entryId);
  }

  public Observable<ForumEntries> getAreaTopics(final String areaId) {
    return mService.getForumAreaTopics(areaId);
  }

  public Observable<ForumEntry> getForumTopicEntries(final String topicId, final int offset) {
    return mService.getForumTopicEntries(topicId, offset, 10)
        .flatMap(new Func1<ForumEntries, Observable<? extends ForumEntry>>() {
          @Override public Observable<? extends ForumEntry> call(ForumEntries forumEntries) {

            return Observable.from(forumEntries.entries);
          }
        })
        .flatMap(new Func1<ForumEntry, Observable<ForumEntry>>() {
          @Override public Observable<ForumEntry> call(ForumEntry entry) {
            return Observable.zip(Observable.just(entry),
                getUser(entry.userId),
                new Func2<ForumEntry, User, ForumEntry>() {
                  @Override public ForumEntry call(ForumEntry entry, User user) {
                    entry.user = user;
                    return entry;
                  }
                });
            //            if (!TextUtils.isEmpty(entry.userId)) {
            //              User user = getUser(entry.userId);
            //              entry.user = user;
            //            }
            //
          }
        });
  }

  public Observable<User> getUser(final String userId) {
    User u = getUserFromContentProvider(userId);
    if (u != null) {
      return Observable.just(u);
    }

    return mService.getUser(userId).flatMap(new Func1<UserItem, Observable<? extends User>>() {
      @Override public Observable<? extends User> call(UserItem userItem) {
        return Observable.just(userItem.user);
      }
    }).onErrorReturn(new Func1<Throwable, User>() {
      @Override public User call(Throwable throwable) {
        return new User(null,
            null,
            null,
            null,
            "Deleted",
            "User",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            0);
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
        .query(UsersContract.CONTENT_URI,
            projection,
            selection,
            new String[]{userId},
            UsersContract.DEFAULT_SORT_ORDER);

    cursor.moveToFirst();
    if (cursor.isAfterLast()) {
      cursor.close();
      return null;
    }

    String userTitlePre = cursor.getString(cursor.getColumnIndex(UsersContract.Columns.USER_TITLE_PRE));
    String userTitlePost = cursor.getString(cursor.getColumnIndex(UsersContract.Columns.USER_TITLE_POST));
    String userForename = cursor.getString(cursor.getColumnIndex(UsersContract.Columns.USER_FORENAME));
    String userLastname = cursor.getString(cursor.getColumnIndex(UsersContract.Columns.USER_LASTNAME));
    String userAvatar = cursor.getString(cursor.getColumnIndex(UsersContract.Columns.USER_AVATAR_NORMAL));
    cursor.close();

    return new User(userId,
        null,
        null,
        userTitlePre,
        userForename,
        userLastname,
        userTitlePost,
        null,
        null,
        null,
        userAvatar,
        null,
        null,
        null,
        0);
  }

  public Observable<ForumEntry> getForumEntryChildren(final String entryId) {
    return mService.getForumAreaTopics(entryId)
        .flatMap(new Func1<ForumEntries, Observable<? extends ForumEntry>>() {
          @Override public Observable<? extends ForumEntry> call(ForumEntries forumEntries) {
            return Observable.from(forumEntries.entries);
          }
        });
  }

  public Observable<ForumArea> createForumEntry(final String topicId,
      final String entrySubject,
      final String entryContent) {
    return mService.createForumEntry(topicId, entrySubject, entryContent);
  }

  public void setForumRead(final String courseId, final Callback callback) {
    mService.setForumRead(courseId, callback);
  }

  public Observable<Settings> getSettings() {
    return mService.getSettings();
  }

  public interface RestIPLegacyService {
    @PUT("/courses/{course_id}/set_forum_read") void setForumRead(@Path("course_id") String courseId,
        Callback<ForumCategory> cb);

    @GET("/courses/{course_id}/forum_categories") Observable<ForumCategories> getForumCategories(@Path(
        "course_id") String courseId);

    @GET("/forum_category/{category_id}/areas") Observable<ForumAreas> getForumAreas(@Path(
        "category_id") String categoryId, @Query("offset") int offset, @Query("limit") int limit);

    @GET("/forum_entry/area_id}/children") Observable<ForumEntries> getForumAreaTopics(@Path(
        "area_id") String areaId);

    @GET("/forum_entry/{topic_id}/children") Observable<ForumEntries> getForumTopicEntries(@Path(
        "topic_id") String topicId, @Query("offset") int offset, @Query("limit") int limit);

    @GET("/forum_entry/{entry_id}") Observable<ForumEntry> getForumEntry(@Path("entry_id") String entryId);

    @FormUrlEncoded @POST("/forum_entry/{topic_id}") Observable<ForumArea> createForumEntry(@Path(
        "topic_id") String topicId,
        @Field("subject") String entrySubject,
        @Field("content") String entryContent);

    @GET("/user/{user_id}") Observable<UserItem> getUser(@Path("user_id") String userId);

    @GET("/studip/settings") Observable<Settings> getSettings();
  }

  public static class UserNotFoundException extends RuntimeException {
    RetrofitError cause;

    UserNotFoundException(RetrofitError cause) {
      this.cause = cause;
    }
  }
}