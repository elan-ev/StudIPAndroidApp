/*
 * Copyright (c) 2017 ELAN e.V.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 */

package de.elanev.studip.android.app.data.net.services;

import java.util.List;

import de.elanev.studip.android.app.StudIPConstants;
import de.elanev.studip.android.app.authorization.data.entity.SettingsEntity;
import de.elanev.studip.android.app.contacts.data.entity.ContactGroup;
import de.elanev.studip.android.app.contacts.data.entity.ContactGroupEntity;
import de.elanev.studip.android.app.contacts.data.entity.ContactGroups;
import de.elanev.studip.android.app.courses.data.entity.Course;
import de.elanev.studip.android.app.courses.data.entity.CourseItem;
import de.elanev.studip.android.app.courses.data.entity.Courses;
import de.elanev.studip.android.app.courses.data.entity.DocumentFolders;
import de.elanev.studip.android.app.courses.data.entity.ForumArea;
import de.elanev.studip.android.app.courses.data.entity.ForumAreas;
import de.elanev.studip.android.app.courses.data.entity.ForumCategories;
import de.elanev.studip.android.app.courses.data.entity.ForumCategory;
import de.elanev.studip.android.app.courses.data.entity.ForumEntries;
import de.elanev.studip.android.app.courses.data.entity.ForumEntry;
import de.elanev.studip.android.app.courses.data.entity.Recording;
import de.elanev.studip.android.app.data.datamodel.Institutes;
import de.elanev.studip.android.app.data.datamodel.InstitutesContainer;
import de.elanev.studip.android.app.data.datamodel.NewsItemWrapper;
import de.elanev.studip.android.app.data.datamodel.Semester;
import de.elanev.studip.android.app.data.datamodel.SemesterWrapper;
import de.elanev.studip.android.app.data.datamodel.User;
import de.elanev.studip.android.app.data.datamodel.UserItem;
import de.elanev.studip.android.app.messages.data.entity.MessageEntities;
import de.elanev.studip.android.app.messages.data.entity.MessageEntity;
import de.elanev.studip.android.app.messages.data.entity.MessageEntityWrapper;
import de.elanev.studip.android.app.messages.data.entity.MessageFolders;
import de.elanev.studip.android.app.news.data.entity.NewsEntity;
import de.elanev.studip.android.app.news.data.entity.NewsEntityList;
import de.elanev.studip.android.app.planner.data.entity.EventEntity;
import de.elanev.studip.android.app.planner.data.entity.Events;
import de.elanev.studip.android.app.user.data.entity.UserEntity;
import de.elanev.studip.android.app.user.data.entity.UserEntityWrapper;
import retrofit2.Retrofit;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;
import timber.log.Timber;


/**
 * API service providing methodes to interact with the legacy implementation of the Stud.IP REST
 * API aka. Rest.IP
 *
 * @author joern
 */
public class StudIpLegacyApiService {

  //region LOCAL CONSTANTS -------------------------------------------------------------------------
  //endregion --------------------------------------------------------------------------------------

  //region INSTANCE VARIABLES ----------------------------------------------------------------------
  private RestIPLegacyService mService;
  //endregion --------------------------------------------------------------------------------------

  //region CONSTRUCTOR
  // -----------------------------------------------------------------------------------------------
  public StudIpLegacyApiService(Retrofit retrofit) {

    // Create an instance of our RestIPLegacyService API interface.
    this.mService = retrofit.create(RestIPLegacyService.class);
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
        .flatMap(forumCategories -> Observable.from(forumCategories.forumCategories));
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
        .flatMap(forumEntries -> Observable.from(forumEntries.entries))
        .flatMap(new Func1<ForumEntry, Observable<ForumEntry>>() {
          @Override public Observable<ForumEntry> call(ForumEntry entry) {
            return Observable.zip(Observable.just(entry), getUser(entry.userId), (entry1, user) -> {
              entry1.user = user;
              return entry1;
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
    //    User u = getUserFromContentProvider(userId);
    //    if (u != null) {
    //      return Observable.just(u);
    //    }

    return mService.getUser(userId)
        .flatMap(userItem -> Observable.just(userItem.user))
        .onErrorReturn(
            throwable -> new User(null, null, null, null, "Deleted", "User", null, null, null, null,
                null, null, null, null, 0));
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
   * Gets various settings of the API and the underlying Stud.IP installation.
   *
   * @return An {@link Observable} wrapping a {@link SettingsEntity} object containing various setting
   * information.
   */
  public Observable<SettingsEntity> getSettings() {
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
  public Observable<List<Recording>> getRecordings(String courseId) {
    Observable<CourseItem> courseObservable = mService.getCourse(courseId);

    return courseObservable.flatMap(new Func1<CourseItem, Observable<List<Recording>>>() {
      @Override public Observable<List<Recording>> call(CourseItem course) {
        if ((course != null) && (course.course != null) && (course.course.getCourseAdditionalData()
            != null)) {

          return Observable.just(course.course.getCourseAdditionalData()
              .getRecordings());

        } else {
          return Observable.empty();
        }
      }
    });
  }

  /**
   * Gets information of the currently signed in user.
   *
   * @return An {@link User} wrapped in qn {@link Observable} containing information about the
   * currently signed in user.
   */
  public Observable<UserEntity> getCurrentUser() {
    return mService.getCurrentUserInfo()
        .map(userEntityWrapper -> userEntityWrapper.getUserEntity());
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
  public Observable<List<EventEntity>> getEvents() {
    return mService.getEvents()
        .map(events -> events.eventEntities);
  }

  public Observable<Course> getCourse(final String courseId) {
    return mService.getCourse(courseId)
        .flatMap(courseItem -> Observable.defer(() -> Observable.just(courseItem.course)))
        .flatMap(course -> getSemester(course.getSemesterId()).flatMap(semester -> {
          course.setSemester(semester);
          return Observable.defer(() -> Observable.just(course));
        }));
  }

  private Observable<Semester> getSemester(String semesterId) {
    return mService.getSemester(semesterId)
        .flatMap(semesterWrapper -> Observable.defer(
            () -> Observable.just(semesterWrapper.getSemester())));
  }

  public Observable<List<EventEntity>> getEvents(final String courseId) {
    return mService.getEvents(courseId)
        .flatMap(events -> Observable.defer(() -> Observable.just(events.eventEntities)));
  }

  /**
   * Get the users {@link MessageEntities} in the specified Stud.IP messages inbox folder.
   *
   * @param offset Offset number of the message pagination.
   * @param limit  The limit of entries until it paginates.
   * @return An {@link Observable} containing {@link MessageEntities} from the specified inbox folder.
   */
  public Observable<List<MessageEntity>> getInboxMessages(int offset, int limit) {
    Observable<String> inboxFoldersObs = mService.getMessageInbox(offset, limit)
        .flatMap(
            messageFolders -> Observable.defer(() -> Observable.from(messageFolders.getFolders())));

    return inboxFoldersObs.flatMap(s -> mService.getMessagesInboxFolder(s, offset, limit)
        // Unwrap messages
        .flatMap(new Func1<MessageEntities, Observable<MessageEntity>>() {
          @Override public Observable<MessageEntity> call(MessageEntities messageEntities) {
            return Observable.from(messageEntities.getMessages());
          }
        })
        .flatMap(message -> Observable.zip(getUserEntity(message.getSenderId()),
            getUserEntity(message.getReceiverId()), (sender, receiver) -> {
              message.setSender(sender);
              message.setReceiver(receiver);

              return message;
            })))
        .toList();
  }

  /**
   * Takes a user id as argument and returns an {@link Observable} wrapping the user correspondig
   * to the {@link User}.
   *
   * @param userId String id identifying the user to load the info for.
   * @return An {@link Observable} wrapping an {@link User} object corresponding to the passed
   * user id.
   */
  public Observable<UserEntity> getUserEntity(final String userId) {

    return mService.getUserEntity(userId)
        .flatMap(userEntityWrapper -> Observable.defer(
            () -> Observable.just(userEntityWrapper.getUserEntity())))
        .onErrorReturn(throwable -> null)
        .filter(userEntity -> userEntity != null);
  }

  /**
   * Get the users {@link MessageEntities} in the specified Stud.IP messages outbox folder.
   *
   * @param offset Offset number of the== message pagination.
   * @param limit  The limit of entries until it paginates.
   * @return An {@link Observable} containing {@link MessageEntities} from the specified outbox folder.
   */
  public Observable<List<MessageEntity>> getOutboxMessages(int offset, int limit) {
    Observable<String> outboxFoldersObs = mService.getMessageOutbox(offset, limit)
        .flatMap(
            messageFolders -> Observable.defer(() -> Observable.from(messageFolders.getFolders())));

    return outboxFoldersObs.flatMap(s -> mService.getMessagesOutboxFolder(s, offset, limit)
        // Unwrap message
        .flatMap(new Func1<MessageEntities, Observable<MessageEntity>>() {
          @Override public Observable<MessageEntity> call(MessageEntities messageEntities) {
            return Observable.from(messageEntities.getMessages());
          }
        })
        .flatMap(message -> Observable.zip(getUserEntity(message.getSenderId()),
            getUserEntity(message.getReceiverId()), (sender, receiver) -> {
              message.setSender(sender);
              message.setReceiver(receiver);

              return message;
            })))
        .toList();
  }

  /**
   * Sends a message to the user specified by the receiverId.
   *
   * @param receiverId The ID of the user to send the message to.
   * @param subject    A String as subject for the message.
   * @param message    The message String.
   * @return The newly created {@link MessageEntity}
   */
  public Observable<MessageEntity> sendMessage(final String receiverId, final String subject,
      final String message) {

    return mService.sendMessage(receiverId, subject, message)
        .map(MessageEntityWrapper::getMessageEntity);
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

  public Observable<MessageEntity> getMessage(final String messageId) {
    return mService.getMessage(messageId)
        .map(MessageEntityWrapper::getMessageEntity)
        .flatMap(message -> Observable.zip(getUserEntity(message.getSenderId()),
            getUserEntity(message.getReceiverId()), (sender, receiver) -> {
              message.setSender(sender);
              message.setReceiver(receiver);

              return message;
            }))
        .doOnNext(messageEntity -> {
          Observable<Void> v = mService.setMessageRead(messageEntity.getMessageId());
          Timber.d(v.toString());
        });
  }

  /**
   * Gets the {@link ContactGroups} of the user.
   *
   * @return A list of {@link ContactGroups}
   */
  public Observable<List<ContactGroupEntity>> getContactGroups() {
    Observable<ContactGroup> contactGroupObs = mService.getContactGroups()
        .flatMap(contactGroups -> Observable.from(contactGroups.getGroups()));

    Observable<List<UserEntity>> usersObs = contactGroupObs.flatMap(
        contactGroup -> Observable.from(contactGroup.getMembers())
            .flatMap(this::getUserEntity)
            .toList());

    Observable<ContactGroupEntity> contEntityObs = Observable.zip(contactGroupObs, usersObs,
        (contactGroup, users) -> {
          ContactGroupEntity entity = new ContactGroupEntity();
          entity.setGroupId(contactGroup.getGroupId());
          entity.setName(contactGroup.getName());
          entity.setMembers(users);
          return entity;
        });

    return contEntityObs.toList();
  }

  public Observable<List<NewsEntity>> getNews() {

    final Observable<NewsEntity> globalNews = getNewsGlobal();
    final Observable<NewsEntity> institutesNews = getCurrentUser().flatMap(
        userEntity -> getNewsInstitutes(userEntity.getUserId()));

    return Observable.merge(globalNews, institutesNews)
        .toSortedList((newsEntity, newsEntity2) -> newsEntity2.getDate()
            .compareTo(newsEntity.getDate()));
  }

  public Observable<NewsEntity> getNewsGlobal() {
    return getNewsForRange(StudIPConstants.STUDIP_NEWS_GLOBAL_RANGE);
  }

  /**
   * Gets a list of the users {@link Courses}.
   *
   * @return A list of the users {@link Courses}.
   */
  public Observable<List<Course>> getCourses() {
    return mService.getCourses()
        .flatMap(courses -> Observable.from(courses.courses))
        .flatMap(course -> getSemester(course.getSemesterId()).flatMap(
            semester -> Observable.defer(() -> {
              course.setSemester(semester);
              return Observable.just(course);
            })))
        .toList();
  }

  public Observable<NewsEntity> getNewsForRange(final String range) {
    // FIXME: Add actual paging mechanism
    return mService.getNewsEntityForRange(range, 0, 100)
        .flatMap(news -> (Observable.from(news.getNewsEntities())
            .filter(newsEntity -> newsEntity.getDate() + newsEntity.getExpire()
                >= System.currentTimeMillis() / 1000)
            .flatMap(newsEntity -> getUserEntity(newsEntity.getUserId()).flatMap(userEntity -> {
              newsEntity.setAuthor(userEntity);
              newsEntity.setRange(range);

              return Observable.defer(() -> Observable.just(newsEntity));
            }))));
  }

  public Observable<NewsEntity> getNewsInstitutes(final String userId) {
    return mService.getInstitutes(userId)
        .flatMap(institutesContainer -> {
          final Observable studyInstitutes = Observable.from(institutesContainer.getInstitutes()
              .getStudy());
          final Observable workInstitutes = Observable.from(institutesContainer.getInstitutes()
              .getWork());

          return Observable.mergeDelayError(
              Observable.defer((Func0<Observable<Institutes.Institute>>) () -> studyInstitutes),
              Observable.defer((Func0<Observable<Institutes.Institute>>) () -> workInstitutes))
              .flatMap(institute -> getNewsForRange(institute.getInstituteId()));
        });
  }

  public Observable<NewsEntity> getNewsItem(final String id) {
    return mService.getNewsItem(id)
        .flatMap(newsItem -> getUserEntity(newsItem.news.getNewsId()).flatMap(user -> {
          newsItem.news.setAuthor(user);

          return Observable.defer(() -> Observable.just(newsItem.news));
        }));
  }
  //endregion --------------------------------------------------------------------------------------

  //region INTERFACES ------------------------------------------------------------------------------
  public interface RestIPLegacyService {
    /* Forums */
    @GET("courses/{course_id}/forum_categories") Observable<ForumCategories> getForumCategories(
        @Path("course_id") String courseId);

    @GET("forum_category/{category_id}/areas") Observable<ForumAreas> getForumAreas(
        @Path("category_id") String categoryId, @Query("offset") int offset,
        @Query("limit") int limit);

    @GET("forum_entry/{topic_id}/children") Observable<ForumEntries> getForumTopicEntries(
        @Path("topic_id") String topicId, @Query("offset") int offset, @Query("limit") int limit);

    @FormUrlEncoded @POST("forum_entry/{topic_id}") Observable<ForumArea> createForumEntry(
        @Path("topic_id") String topicId, @Field("subject") String entrySubject,
        @Field("content") String entryContent);

    /* User */
    @GET("user/{user_id}") Observable<UserItem> getUser(@Path("user_id") String userId);

    /* User */
    @GET("user/{user_id}") Observable<UserEntityWrapper> getUserEntity(
        @Path("user_id") String userId);

    @GET("user") Observable<UserEntityWrapper> getCurrentUserInfo();

    /* Events */
    @GET("events") Observable<Events> getEvents();

    @GET("courses/{course_id}/events") Observable<Events> getEvents(
        @Path("course_id") String courseId);

    /* General */
    @GET("studip/settings") Observable<SettingsEntity> getSettings();

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

    @GET("messages/in/{folder_id}") Observable<MessageEntities> getMessagesInboxFolder(
        @Path("folder_id") String folderId, @Query("offset") int offset, @Query("limit") int limit);

    @GET("messages/out/{folder_id}") Observable<MessageEntities> getMessagesOutboxFolder(
        @Path("folder_id") String folderId, @Query("offset") int offset, @Query("limit") int limit);

    @PUT("messages/{message_id}/read/") Observable<Void> setMessageRead(
        @Path("message_id") String messageId);

    @FormUrlEncoded @POST("messages") Observable<MessageEntityWrapper> sendMessage(
        @Field("user_id") String receiverId, @Field("subject") String subject,
        @Field("message") String message);

    @DELETE("messages/{message_id}") Observable<Void> deleteMessage(
        @Path("message_id") String messageId);

    @GET("messages/{message_id}") Observable<MessageEntityWrapper> getMessage(
        @Path("message_id") String messageId);

    /* Institutes */
    @GET("user/{user_id}/institutes") Observable<InstitutesContainer> getInstitutes(
        @Path("user_id") String userId);

    /* Contacts */
    @GET("contacts/groups") Observable<ContactGroups> getContactGroups();

    /* Courses */
    @GET("courses") Observable<Courses> getCourses();

    /* News */
    @GET("news/range/{range}") Observable<NewsEntityList> getNewsEntityForRange(
        @Path("range") String range, @Query("offset") int offset, @Query("limit") int limit);

    @GET("news/{id}") Observable<NewsItemWrapper> getNewsItem(@Path("id") String id);

    /* Semesters */
    @GET("semesters/{id}") Observable<SemesterWrapper> getSemester(@Path("id") final String id);
  }
  //endregion --------------------------------------------------------------------------------------

  //region INNER CLASSES ---------------------------------------------------------------------------

  //endregion --------------------------------------------------------------------------------------
}